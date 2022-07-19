package com.nut.locationservice.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.protobuf.InvalidProtocolBufferException;
import com.nut.locationservice.app.dao.ExportCarInfoFileDao;
import com.nut.locationservice.app.dto.BaseData;
import com.nut.locationservice.app.dto.FaultSummaryDto;
import com.nut.locationservice.app.dto.QueryFaultSummaryDto;
import com.nut.locationservice.app.form.QueryFaultSummaryForm;
import com.nut.locationservice.app.pojo.*;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.HttpUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: 故障汇总报表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service
 * @Author: yzl
 * @CreateTime: 2021-06-17 10:24
 * @Version: 1.0
 */
@Service
@Slf4j
public class FaultSummaryServiceImpl {

    @Autowired
    private ExportCarInfoFileDao exportCarInfoFileDao;

    @Value("${database_name}")
    private String DatabaseName;

    private CompletionService<byte[]> completionService;
    private ExecutorService threadPool;

    @Value("${local.cloud.getFaultOriginalUrl}")
    private String getFaultOriginalUrl;

    private static final int splitNum = 5000;
    private static final Integer DATA_TYPE_40 = 40;
    private static final Integer DATA_TYPE_2 = 2;

    @PostConstruct
    public void init() {
        this.threadPool = Executors.newSingleThreadExecutor();
        this.completionService = new ExecutorCompletionService(this.threadPool);
        log.info("ExcutorTestService.init finished……");
    }

    @PreDestroy
    public void destroy() {
        threadPool.shutdown();
        log.info("ExcutorTestService.destroy finished……");
    }


    public List<QueryFaultSummaryDto> queryFaultSummary(QueryFaultSummaryForm form) {
        if (form.getDateStr() != null && !form.getDateStr().isEmpty()) {
            form.setBeginTime(setTimeValue(form.getDateStr() + " 00:00:00"));
            form.setEndTime(setTimeValue(form.getDateStr() + " 23:59:59"));
        }
        //查询系统类型数据
        List<QuerySystemTypePojo> carFaultPojos = exportCarInfoFileDao.getSystemType(this.DatabaseName);

        Map<Integer, String> systemTypeMap = new HashMap<>();
        List<Integer> systemTypeVals = new ArrayList<>();
        for (QuerySystemTypePojo systemTypePojo : carFaultPojos) {
            if (systemTypePojo.getSystemSource().length() > 2) {
                systemTypeMap.put(Integer.valueOf(systemTypePojo.getSystemSource().substring(2), 16), systemTypePojo.getSystemType());
                systemTypeVals.add(Integer.valueOf(systemTypePojo.getSystemSource().substring(2), 16));
            }
        }

        List<QueryFaultSummaryDto> summaryDtos = FaultSummaryDto.convert(queryCloud(form, systemTypeVals));
        for (QueryFaultSummaryDto faultSummaryDto : summaryDtos) {
            faultSummaryDto.setSystemType(systemTypeMap.get(faultSummaryDto.getSystemSource()));
        }
        return summaryDtos;
    }

    private Long setTimeValue(String value) {// 秒级别
        Long date = 0L;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = df.parse(value).getTime() / 1000;
        } catch (ParseException e) {
            log.error("异常", e);
        }
        return date;
    }

    /**
     * 查询位置云信息
     *
     * @param condition
     * @return
     */
    @SneakyThrows
    private List<QueryFaultSummaryPojo> queryCloud(QueryFaultSummaryForm condition, List<Integer> systemTypeVals) {
        List<QueryFaultSummaryPojo> dataList = new ArrayList<>();
        if (condition.getBeginTime() != null && condition.getEndTime() != null) {
            try {
                log.info("---------------------query faultSummaryPojo----------- start");
                long queryStart = System.currentTimeMillis() / 1000;
                List<QueryFaultSummaryPojo> faultSummaryPojos = queryFaultSummaryInfo();
                long queryEnd = System.currentTimeMillis() / 1000;
                log.info("-----------query faultSummaryPojo-is end and spend time ----" + (queryEnd - queryStart));
                if (faultSummaryPojos != null && !faultSummaryPojos.isEmpty()) {
                    // 封装通讯号信息数据
                    Map<Long, QueryFaultSummaryPojo> dataMaps = new HashMap<>();
                    List<Long> communicationIds = setDataMaps(faultSummaryPojos, dataMaps);
                    // 从位置云拿数据并解析
                    if (communicationIds != null && !communicationIds.isEmpty()) {
                        //位置云一次请求最大的终端条数为5000
                        List<List<Long>> mglist = new ArrayList<>();
                        if (communicationIds.size() > splitNum) {
                            int splitCount = communicationIds.size() % splitNum == 0 ? (communicationIds.size() / splitNum) : (communicationIds.size() / splitNum + 1);
                            Stream.iterate(0, n -> n + 1).limit(splitCount).forEach(i -> {
                                mglist.add(communicationIds.stream().skip(i * splitNum).limit(splitNum).collect(Collectors.toList()));
                            });
                        } else {
                            mglist.add(communicationIds);
                        }
                        for (List<Long> tidList : mglist) {
                            long start = System.currentTimeMillis() / 1000;
                            //int faultType = (condition.getSystemType() == null || Integer.parseInt(condition.getSystemType()) < 0) ? -1 : Integer.parseInt(condition.getSystemType());
                            //调用位置云故障原始统计接口
                            List<FaultOriginalPojo> faultOriginalPojoList = getFaultOriginalList(tidList, -1, condition.getBeginTime().longValue(), condition.getEndTime().longValue());
                            long end = System.currentTimeMillis() / 1000;
                            log.info("调用位置云故障原始统计接口耗时：{}秒", (end - start));
                            /**拼接数据*/
                            this.composeSummaryFault(faultSummaryPojos);

                            if (faultOriginalPojoList != null && !faultOriginalPojoList.isEmpty()) {
                                log.info("FaultSummaryServiceImpl:cloudData:" + faultOriginalPojoList.size());
                                Map<String, QueryCarFaultPojo> sfMap = new HashMap();
                                List<QueryCarFaultPojo> carFaultList = exportCarInfoFileDao.getCarFaultList(this.DatabaseName);
                                for (QueryCarFaultPojo carFaultPojo : carFaultList) {
                                    String key = Integer.valueOf(carFaultPojo.getSystemSource().substring(2), 16)
                                            + "_" + carFaultPojo.getSpn()
                                            + "_" + carFaultPojo.getFmi()
                                            + "_" + carFaultPojo.getSymbolCode();
                                    sfMap.put(key, carFaultPojo);
                                }

                                long begin = System.currentTimeMillis() / 1000;
                                for (FaultOriginalPojo faultOriginalPojo : faultOriginalPojoList) {
                                    List<FaultInfoPojo> faultList = faultOriginalPojo.getFaultList();
                                    if (faultList != null && !faultList.isEmpty()) {
                                        for (FaultInfoPojo faultInfoPojo : faultList) {
                                            if ("-2".equals(condition.getSystemType())) {
                                                //用来判断数据库没有的系统类型
                                                if (!systemTypeVals.contains(faultInfoPojo.getAddr())) {
                                                    setCloudInfo(dataList, dataMaps, sfMap, faultInfoPojo, faultOriginalPojo.getTerminalId());
                                                }
                                            } else {
                                                setCloudInfo(dataList, dataMaps, sfMap, faultInfoPojo, faultOriginalPojo.getTerminalId());
                                            }
                                        }
                                    }
                                }
                                long endR = System.currentTimeMillis() / 1000;
                                log.info("故障汇总数据拼装耗时：{}秒", (endR - begin));
                            }
                        }
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                log.info("轨迹数据获取或转换异常", e);
            } catch (Exception e) {
                log.info("车辆获取或转换异常", e);
            }
        }

        return dataList;
    }

    /**
     * 数据库查询
     *
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private List<QueryFaultSummaryPojo> queryFaultSummaryInfo() {
        Map<String, Object> conMaps = new HashMap<>(16);
        conMaps.put("DatabaseName", DatabaseName);
        return exportCarInfoFileDao.queryFaultSummaryList(conMaps);
    }

    private List<Long> setDataMaps(List<QueryFaultSummaryPojo> faultSummaryPojos,
                                   Map<Long, QueryFaultSummaryPojo> dataMaps) {
        List<Long> communicationIds = new ArrayList<Long>();
        for (int index = 0; index < faultSummaryPojos.size(); index++) {
            if (faultSummaryPojos.get(index) != null && faultSummaryPojos.get(index).getCommId() != null) {
                Long commId = faultSummaryPojos.get(index).getCommId().longValue();
                if (commId != null && commId.longValue() != 0) {
                    communicationIds.add(commId);
                    dataMaps.put(commId, faultSummaryPojos.get(index));
                }
            }
        }
        return communicationIds;
    }

    /**
     * 调用位置云故障原始统计接口
     *
     * @return
     */
    private List<FaultOriginalPojo> getFaultOriginalList(List<Long> terminalIds, int type, Long startTime, Long endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("terminalIds", terminalIds);
        map.put("type", type);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        List<FaultOriginalPojo> pojos = HttpUtil.postJsonRequest(getFaultOriginalUrl, map, new TypeReference<HttpCommandResultWithData<List<FaultOriginalPojo>>>() {
        });
        return pojos == null ? new ArrayList<>() : pojos;
    }

    private void setCloudInfo(List<QueryFaultSummaryPojo> dataList, Map<Long, QueryFaultSummaryPojo> dataMaps,
                              Map<String, QueryCarFaultPojo> sfMap, FaultInfoPojo vo, Long tid) throws CloneNotSupportedException {
        QueryFaultSummaryPojo bean = dataMaps.get(tid);
        if (bean == null) {
            return;
        }
        QueryFaultSummaryPojo rBean = (QueryFaultSummaryPojo) bean.clone();
        rBean.setSpn(vo.getSpn() + "");
        rBean.setFmi(vo.getFmi() + "");
        rBean.setSystemSource(vo.getAddr());
        String mapKey = vo.getAddr() + "_" + vo.getSpn() + "_" + vo.getFmi();
        //先赋初始值（防止根据spn+fmi+faultType未找到故障数据
        rBean.setBreakdownDis("未知");
        rBean.setFaultSolutions("");
        rBean.setTypeModel("其他");
        rBean.setSymbolCode("");

        Set<String> keys = sfMap.keySet();
        List<String> keyList = keys.stream().filter(f -> f.contains(mapKey)).sorted((s1, s2) -> s2.compareTo(s1)).collect(Collectors.toList());//根据spn+fmi+faultType 过滤数据
        if (CollectionUtils.isNotEmpty(keyList)) {
            for (String key : keyList) {
                String symbolCode = sfMap.get(key).getSymbolCode();
                //先赋初始值（防止根据spn+fmi+faultType+symbol 未找到数据）
                setFaultData(sfMap, rBean, key);
                if (StringUtils.isEmpty(symbolCode) && !"null".equals(symbolCode)) {//有标识码
                    if (StringUtils.isNotEmpty(getSymbol5Code(rBean.getProdCode(), symbolCode))) {
                        setFaultData(sfMap, rBean, key);
                        break;
                    }
                }
            }
        }

        rBean.setOccurDate(DateUtil.timeStampToDate(vo.getBegin() * 1000));
        rBean.setDuration(formatLongToTimeString(vo.getCon()));
        rBean.setBLoction(vo.getBLng() * 0.000001 + "," + vo.getBLat() * 0.000001);
        rBean.setELoction(vo.getELng() * 0.000001 + "," + vo.getELat() * 0.000001);
        //set  解决办法
        dataList.add(rBean);
    }

    /**
     * 将秒转换为时分秒格式
     *
     * @param time
     * @return
     */
    private String formatLongToTimeString(int time) {
        long second = new Long(time);
        long hour = 0;
        long minute = 0;

        if (second >= 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        return (hour + "小时" + minute + "分钟" + second + "秒");
    }

    private String getSymbol5Code(String prodCode, String symbolCode) {
        String symbol5Code = "";
        if (StringUtils.isEmpty(symbolCode) || "null".equals(symbolCode)) {
            return symbol5Code;
        }
        String[] symbolCodes = symbolCode.split(",");
        if (symbolCodes.length == 1) {
            return symbolCodes[0];
        }
        for (String code : symbolCodes) {
            Map<String, Object> param = new HashMap<>(16);
            param.put("prodCode", prodCode);
            param.put("tboxSymbolCode", code);
            param.put("DatabaseName", DatabaseName);
            Map<String, Object> symbol5CodeMap = exportCarInfoFileDao.getSymbolCode(param);
            if (symbol5CodeMap != null && symbol5CodeMap.size() > 0) {
                symbol5Code = (String) symbol5CodeMap.get("tboxSymbolCode");
            }
            if (StringUtils.isNotEmpty(symbol5Code)) {
                break;
            }
        }
        return symbol5Code;
    }

    private void setFaultData(Map<String, QueryCarFaultPojo> sfMap, QueryFaultSummaryPojo rBean, String key) {
        String symbolCode = sfMap.get(key).getSymbolCode();
        setBreakdown(sfMap, rBean, key);
        rBean.setSymbolCode(getSymbol5Code(rBean.getProdCode(), symbolCode));
    }

    /**
     * 设置故障名称，故障描述，解决办法
     *
     * @param sfMap
     * @param rBean
     * @param mapKey
     */
    private void setBreakdown(Map<String, QueryCarFaultPojo> sfMap, QueryFaultSummaryPojo rBean, String mapKey) {
        rBean.setBreakdownDis(sfMap.get(mapKey).getFaultDes() != null && !sfMap.get(mapKey).getFaultDes().equals("null") ? sfMap.get(mapKey).getFaultDes() : "未知");
        rBean.setTypeModel(sfMap.get(mapKey).getTypeModel() != null && !sfMap.get(mapKey).getTypeModel().equals("null") ? sfMap.get(mapKey).getTypeModel() : "其他");
        rBean.setFaultSolutions(sfMap.get(mapKey).getFaultSolutions() != null && !sfMap.get(mapKey).getFaultSolutions().equals("null") ? sfMap.get(mapKey).getFaultSolutions() : "");
    }

    public void composeSummaryFault(List<QueryFaultSummaryPojo> queryFaultSummaryPojoList) throws Exception {

        //获取dataType = 40
        List<BaseData> engineTypeList = this.queryList(DATA_TYPE_40);
        //获取dataType = 2
        List<BaseData> carModelList = this.queryList(DATA_TYPE_2);
        queryFaultSummaryPojoList.stream().forEach(fault -> {
            //拼接发动机型号
            engineTypeList.stream().forEach(engineType -> {
                if (engineType.getCode().equals(fault.getEType())) {
                    fault.setEngineType(engineType.getValue());
                }
            });
            //拼接车辆型号
            carModelList.stream().forEach(carModel -> {
                try {
                    if (fault.getCarType() != null && carModel.getId().compareTo(BigInteger.valueOf(fault.getCarType())) == 0) {
                        fault.setCarModel(carModel.getValue());
                    }
                } catch (Exception e) {
                    //未匹配，不做任何处理
                }

            });
            //set销售状态状态
            if (fault.getAakstatus() == null) {
                fault.setAakstatusValue("未知");
            } else if (0 == fault.getAakstatus()) {
                fault.setAakstatusValue("已出库");
            } else if (1 == fault.getAakstatus()) {
                fault.setAakstatusValue("未出库");
            } else {
                fault.setAakstatusValue("未知");
            }
        });
    }

    public List<BaseData> queryList(int type) {
        Map<String, Object> map = new HashedMap();
        map.put("type", type);
        map.put("DatabaseName", this.DatabaseName);
        return exportCarInfoFileDao.queryBasicDataList(map);
    }

}
