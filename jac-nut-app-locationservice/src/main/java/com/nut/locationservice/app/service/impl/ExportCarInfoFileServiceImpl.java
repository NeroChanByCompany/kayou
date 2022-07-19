package com.nut.locationservice.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.nut.location.protocol.common.LCLocationData;
import com.nut.location.protocol.common.LCStatusType;
import com.nut.location.protocol.common.LCVehicleStatusData;
import com.nut.locationservice.app.dao.ExportCarInfoFileDao;
import com.nut.locationservice.app.QueryTracesConverter;
import com.nut.locationservice.app.dto.BaseData;
import com.nut.locationservice.app.dto.QueryFaultSummaryDto;
import com.nut.locationservice.app.dto.QueryTracesDto;
import com.nut.locationservice.app.form.QueryFaultSummaryForm;
import com.nut.locationservice.app.pojo.CarInfoPojo;
import com.nut.locationservice.app.pojo.QueryCommonCarPojo;
import com.nut.locationservice.app.pojo.QueryTracesPojo;
import com.nut.locationservice.app.service.ExportCarInfoFileService;
import com.nut.locationservice.common.constants.CloudConstants;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.locationservice.common.util.CloudUtil;
import com.nut.locationservice.common.util.ListUtil;
import com.nut.locationservice.common.util.NumberFormatUtil;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.ExportExcelUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-17 09:18
 * @Version: 1.0
 */
@Slf4j
@Service
public class ExportCarInfoFileServiceImpl implements ExportCarInfoFileService {

    private static final int FILE_TYPE_FAULT = 1;

    private static final int FILE_TYPE_TRACK = 2;

    @Autowired
    private CloudServiceImpl cloudServie;

    @Autowired
    private FaultSummaryServiceImpl faultSummaryServiceImpl;


    @Value("${file.storage.url}")
    public String fileStorageUrl;

    private static final int BUFFER = 8192;

    @Value("${track.core.pool.size:5}")
    private int trackCorePoolSize;

    @Value("${track.maximum.pool.size:10}")
    private int trackMaximumPoolSize;

    @Value("${track.blocking.queue.size:2000}")
    private int trackBlockingQueueSize;

    @Value("${database_name}")
    private String DatabaseName;

    @Autowired
    private ExportCarInfoFileDao exportCarInfoFileDao;

    @Override
    @SneakyThrows
    public HttpCommandResultWithData generateCarFaultInfoFile(String procDate) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        log.info("[ExportCarInfoFileService]generateCarFaultInfoFile start");
        long startMillis = System.currentTimeMillis();
        //查询数据
        QueryFaultSummaryForm dataCommand = new QueryFaultSummaryForm();
        dataCommand.setDateStr(procDate);
        long queryStartMillis = System.currentTimeMillis();
        List<QueryFaultSummaryDto> dataList = faultSummaryServiceImpl.queryFaultSummary(dataCommand);
        log.info("查询数据完成，共{}条，耗时：{}ms", dataList.size(), System.currentTimeMillis() - queryStartMillis);
        if (dataList == null || dataList.size() == 0) {
            result.setResultCode(ECode.SUCCESS.code());
            result.setMessage("查询数据为空！");
            return result;
        }
        //生成excel并上传
        long excelStartMillis = System.currentTimeMillis();
        Map<String, Object> resultMap = this.exportAndUploadFaultExcel(procDate, dataList);
        if (resultMap == null || resultMap.size() == 0) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("生成excel出错！");
            return result;
        }
        try {
            log.info("生成excel并上传完成，返回：{}，耗时：{}ms", JsonUtil.toJson(resultMap), System.currentTimeMillis() - excelStartMillis);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        //插入数据
        long insertStartMillis = System.currentTimeMillis();
        this.insertCarFaultFile(procDate, resultMap);
        log.info("新增文件数据完成，耗时：{}ms", System.currentTimeMillis() - insertStartMillis);
        log.info("总耗时：{}ms", System.currentTimeMillis() - startMillis);
        log.info("[ExportCarInfoFileService]generateCarFaultInfoFile end");
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        return result;
    }

    @SneakyThrows
    public HttpCommandResultWithData generateCarTrackInfoFile(String procDate) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        log.info("[ExportCarInfoFileService]generateCarTrackInfoFile start");
        long startMillis = System.currentTimeMillis();
        //查询数据

        List<CarInfoPojo> carList = this.queryCarList();
        List<Map<String, Object>> excelList = new ArrayList<>();

        ExecutorService executor = new ThreadPoolExecutor(
                trackCorePoolSize,
                trackMaximumPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(trackBlockingQueueSize),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        for (CarInfoPojo pojo : carList) {
            executor.execute(() -> {
                List<QueryTracesPojo> dataList = new ArrayList<>();
                long queryStartMillis = System.currentTimeMillis();
                try {
                    if (StringUtil.isNotEmpty(pojo.getVin()) && pojo.getTerminalId() != null) {
                        dataList = queryTrackList(pojo.getVin(), pojo.getTerminalId(), procDate);
                    }
                    log.info("车辆{}，{} 查询数据完成，共{}条，耗时：{}ms", pojo.getVin(), pojo.getTerminalId(), dataList.size(), System.currentTimeMillis() - queryStartMillis);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                if (CollectionUtils.isNotEmpty(dataList)) {
                    //生成excel并上传
                    long excelStartMillis = System.currentTimeMillis();
                    Map<String, Object> resultMap = exportAndUploadTrackExcel(pojo.getVin(), dataList);
                    if (resultMap != null && resultMap.size() > 0) {
                        excelList.add(resultMap);
                        try {
                            log.info("生成excel并上传完成，返回：{}，耗时：{}ms", JsonUtil.toJson(resultMap), System.currentTimeMillis() - excelStartMillis);
                        } catch (JsonProcessingException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            });
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(8, TimeUnit.HOURS)) {
                log.info("车辆行程定时导出多线程未执行完，强制关闭！");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        // 打包
        if (CollectionUtils.isEmpty(excelList)) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("没有excel数据");
            return result;
        }
        long packStartMillis = System.currentTimeMillis();
        Map<String, Object> packFileMap = this.packFile(procDate, excelList);
        try {
            log.info("打包文件完成，返回：{}，耗时：{}ms", JsonUtil.toJson(packFileMap), System.currentTimeMillis() - packStartMillis);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        //插入数据
        if (packFileMap != null && !packFileMap.isEmpty()) {
            long insertStartMillis = System.currentTimeMillis();
            this.insertCarTrackFile(procDate, packFileMap);
            log.info("新增文件数据完成，耗时：{}ms", System.currentTimeMillis() - insertStartMillis);
            File packFile = new File((String) packFileMap.get("fileName"));
            if (packFile.exists()) {
                packFile.delete();
            }
        }

        if (CollectionUtils.isNotEmpty(excelList)) {
            for (Map<String, Object> excelMap : excelList) {
                File excelFile = new File((String) excelMap.get("fileName"));
                if (excelFile.exists()) {
                    excelFile.delete();
                }
            }
        }


        log.info("总耗时：{}ms", System.currentTimeMillis() - startMillis);
        log.info("[ExportCarInfoFileService]generateCarTrackInfoFile end");
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        return result;
    }

    private void insertCarFaultFile(String procDate, Map<String, Object> resultMap) {
        resultMap.put("procDate", procDate);
        resultMap.put("fileType", FILE_TYPE_FAULT);
        exportCarInfoFileDao.insertExportCarFile(resultMap);
    }

    /**
     * 查询1000辆车
     *
     * @return
     */
    private List<CarInfoPojo> queryCarList() {
        return exportCarInfoFileDao.queryCarList();
    }

    /**
     * 查询车辆轨迹
     *
     * @param vin
     * @param terminalId
     * @param procDate
     * @return
     * @throws Exception
     */
    private List<QueryTracesPojo> queryTrackList(String vin, long terminalId, String procDate) throws Exception {
        List<QueryTracesPojo> list = new ArrayList<>();
        QueryCommonCarPojo queryCommonCarPojo = exportCarInfoFileDao.queryCommonCar(this.DatabaseName, vin);
        String engineType = "";
        if (null != queryCommonCarPojo && null != queryCommonCarPojo.getCommId()) {
            engineType = queryCommonCarPojo.getEngineType();
            // 默认油量处理
            if (StringUtil.isEmpty(queryCommonCarPojo.getOilCapacity())) {
                queryCommonCarPojo.setOilCapacity(CloudConstants.DEFAULT_OILCAPACITY);
            }
        } else {
            return list;
        }

        List<BaseData> faulist = new ArrayList<>();
        if (!(StringUtil.isEmpty(engineType) || !this.isNumericZidai(engineType))) {
            faulist = faultSummaryServiceImpl.queryList(Integer.valueOf(engineType));
        }

        long startDate = DateUtil.strTimeChangeLong(procDate + " 00:00:00") / 1000;
        long endDate = DateUtil.strTimeChangeLong(procDate + " 23:59:59") / 1000;
        List<LCLocationData.LocationData> locationData = cloudServie.queryByteTrack(terminalId, startDate, endDate, false, 0);

        // 循环轨迹接口返回的结果,并进行分值
        if (!ListUtil.isEmpty(locationData)) {
            for (LCLocationData.LocationData gpslist : locationData) {
                if (gpslist != null) {
                    QueryTracesPojo forPojo = new QueryTracesPojo();
                    // gps时间
                    forPojo.setGpsdate(DateUtil.timeStr(gpslist.getGpsDate() * 1000));
                    // 车辆速度
                    forPojo.setVelocity(String.valueOf(NumberFormatUtil.getDoubleValueData(new Double(
                                    gpslist.getSpeed()),
                            1)));
                    // 经纬度
                    forPojo.setLat(NumberFormatUtil.getLatitudeOrLongitude(gpslist.getLatitude()));
                    forPojo.setLng(NumberFormatUtil.getLatitudeOrLongitude(gpslist.getLongitude()));
                    // 剩余油量
                    // 剩余油/汽量
                    if (null != queryCommonCarPojo.getFuel() && queryCommonCarPojo.getFuel() == 0) {
                        forPojo.setOilwear(String.valueOf(CloudUtil.getResGas(gpslist)));
                    } else {
                        // 剩余油量=燃油液位/100*油箱容积
                        Double resOil = NumberFormatUtil.formatNumber(CloudUtil.getResOil(gpslist) * 0.01 * 0.01 * Double.valueOf(queryCommonCarPojo.getOilCapacity()).doubleValue(), 2);
                        if (resOil != null) {
                            forPojo.setOilwear(resOil.toString());
                        }
                    }
                    forPojo.setTotolmileage(String.valueOf(gpslist.getStandardMileage()));
                    forPojo.setTotalFuelConsumption(String.valueOf(gpslist.getStandardFuelCon()));
                    // 状态
                    forPojo.setStatue(CloudUtil.getDoorStatus(gpslist.getStatus())
                            + CloudUtil.getAccStatus(gpslist.getStatus()) + CloudUtil.getGpsStatus(gpslist.getStatus()));
                    // 报警
                    forPojo.setAlarm(CloudUtil.getAlarm(gpslist.getAlarm()));
                    // 故障
                    if (StringUtil.isEmpty(engineType) || !this.isNumericZidai(engineType)) {
                        forPojo.setFault("");
                    } else {
                        String fault = CloudUtil.getFault(gpslist.getBreakdownAddition().getBreakdownList(), faulist, engineType);
                        forPojo.setFault(fault);
                    }
                    // 接收时间
                    forPojo.setReceiveDate(DateUtil.timeStr(gpslist.getReceiveDate() * 1000));
                    // 方向
                    forPojo.setDirection(CloudUtil.getDirection(gpslist.getDirection()));
                    // 终端里程
                    forPojo.setMileage(String.valueOf(NumberFormatUtil.getDoubleValueData(gpslist.getMileage() * 0.001,
                            1)));
                    // 地址
                    forPojo.setAddress(forPojo.getLng() + ";" + forPojo.getLat());
                    // 导出明细增加字段
                    // 高程
                    forPojo.setHeight(NumberFormatUtil.getDoubleValueData(new Double(gpslist.getHeight()), 1));
                    // 载重AMT(KG)
                    forPojo.setTcuLoad(gpslist.getTcuLoad());
                    // 载重VECU(KG)
                    forPojo.setVecuLoad(gpslist.getVecuLoad());
                    forPojo = this.getVehicleStatusAdditionForTrack(gpslist, forPojo);
                    forPojo = getcarMessageData(gpslist, forPojo);
                    list.add(forPojo);

                }
            }
        }
        return list;
    }

    private Map<String, Object> exportAndUploadFaultExcel(String procDate, List<QueryFaultSummaryDto> faultList) {
        List<QueryFaultSummaryDto> dataList = new ArrayList<>();
        //每辆车用空行分隔
        String chassisNum = null;
        for (QueryFaultSummaryDto d : faultList) {
            if (chassisNum != null && !chassisNum.equals(d.getChassisNum())) {
                dataList.add(new QueryFaultSummaryDto());
            }
            dataList.add(d);
            chassisNum = d.getChassisNum();
        }

        //导出
        Map<String, Object> returnMap = new HashMap<>(16);
        ExportExcelUtil exportExcel = new ExportExcelUtil(fileStorageUrl);
        String[] titleArr = new String[]{"序号", "底盘号", "车牌号", "AAK销售状态", "终端ID(部标机)", "终端ID(T-box)", "所属经销商", "所属客户", "联系方式", "车辆型号	", "系统类别", "名称及型号	", "车架号", "故障名称", "SPN", "FMI", "标识码	", "解决办法", "故障发生时间", "持续时长", "起始位置", "结束位置"};
        String[][] dataArr = new String[dataList.size()][];
        int indexNum = 0;
        for (int i = 0; i < dataList.size(); i++) {
            QueryFaultSummaryDto dto = dataList.get(i);
            String[] tmp = new String[titleArr.length];
            if (StringUtil.isNotEmpty(dto.getChassisNum())) {
                indexNum++;
                tmp[0] = dto.getOccurDate();
            } else {
                tmp[0] = "";
            }

            tmp[1] = dto.getChassisNum();
            tmp[2] = dto.getPlateNum();
            tmp[3] = dto.getAakstatusValue();
            tmp[4] = dto.getBdTerCode();
            tmp[5] = dto.getFkTerCode();
            tmp[6] = dto.getTName();
            tmp[7] = dto.getBusinessName();
            tmp[8] = dto.getLinkTelpone();
            tmp[9] = dto.getCarModel();
            tmp[10] = dto.getSystemType();
            tmp[11] = dto.getTypeModel();
            tmp[12] = dto.getStructureNum();
            tmp[13] = dto.getBreakdownDis();
            tmp[14] = dto.getSpn();
            tmp[15] = dto.getFmi();
            tmp[16] = dto.getSymbolCode();
            tmp[17] = dto.getFaultSolutions();
            tmp[18] = dto.getOccurDate();
            tmp[19] = dto.getDuration();
            tmp[20] = "";
            if (StringUtils.isNotBlank(dto.getBLoction())) {
                String[] bLocation = dto.getBLoction().split(",");
                tmp[20] = "经度:" + bLocation[0] + " 纬度:" + bLocation[1];
            }
            tmp[21] = "";
            if (StringUtils.isNotBlank(dto.getELoction())) {
                String[] eLocation = dto.getELoction().split(",");
                tmp[21] = "经度:" + eLocation[0] + " 纬度:" + eLocation[1];
            }
            dataArr[i] = tmp;
        }
        String fileName = procDate + "故障数据.xlsx";
        String resultUrl = exportExcel.commonGenerateExcel(titleArr, 100, dataArr, procDate + "故障数据", fileName);
        log.info("调用公共方法生成并上传excel返回:{}", resultUrl);
        if (StringUtils.isNotBlank(resultUrl)) {
            try {
                Map map = JsonUtil.toMap(resultUrl);
                if ("200".equals(map.get("status").toString()) && map.get("data") != null) {
                    Map resultMap = (LinkedHashMap) map.get("data");
                    if (resultMap != null && resultMap.get("fullPath") != null) {
                        returnMap.put("fullPath", (String) resultMap.get("fullPath"));
                        returnMap.put("fileSize", (Integer) resultMap.get("size"));
                        returnMap.put("fileName", fileName);
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return returnMap;
    }

    private Map<String, Object> exportAndUploadTrackExcel(String vin, List<QueryTracesPojo> trackList) {
        List<QueryTracesDto> queryTracesDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(trackList)) {
            trackList.stream().forEach(o -> {
                queryTracesDtoList.add(QueryTracesConverter.queryTracesConverter(o));
            });
        }
        //导出
        Map<String, Object> returnMap = new HashMap<>(16);
        ExportExcelUtil exportExcel = new ExportExcelUtil(fileStorageUrl);
        String[] titleArr = new String[]{"序号", "接收时间", "GPS时间", "经度", "纬度", "方向", "速度(KM/H)", "车辆状态", "整车里程(KM)", "终端里程(KM)", "剩余油/气量(L)", "故障", "报警", "地址", "高程（M）", "小计里程（KM）", "发动机转速（r/min）", "发动机累计转数（r）", "发动机累计运行时长", "冷却液温度（度）", "油/汽量总消耗（L）", "发动机燃油消耗率（L/h）", "平均燃油消耗率（KM/h）", "瞬时油耗率（KM/L）", "转矩控制模式", "驾驶指令百分比扭矩（%）", "发动机实际扭矩百分比（%）", "加速踏板低怠速开关", "加速踏板Kickdown开关", "加速踏板开度（%）", "当前速度负载百分比（%）", "名义摩擦力矩百分比（%）", "驻车制动器开关", "车轮车速（KM/h）", "巡航控制开关状态", "制动开关", "离合器开关", "巡航控制设置开关", "巡航控制减速开关", "巡航控制恢复开关", "巡航控制加速开关", "巡航控制设置速度（KM/h）", "巡航控制状态", "转出轴转速（r）", "载重AMT(kg)", "载重VECU"};
        String[][] dataArr = new String[queryTracesDtoList.size()][];
        for (int i = 0; i < queryTracesDtoList.size(); i++) {
            QueryTracesDto dto = queryTracesDtoList.get(i);
            String[] tmp = new String[titleArr.length];
            tmp[0] = dto.getGpsdate();
            tmp[1] = dto.getReceiveDate();
            tmp[2] = dto.getGpsdate();
            tmp[3] = String.valueOf(dto.getLng());
            tmp[4] = String.valueOf(dto.getLat());
            tmp[5] = dto.getDirection();
            tmp[6] = dto.getVelocity();
            tmp[7] = dto.getStatue();
            tmp[8] = dto.getTotolmileage();
            tmp[9] = dto.getMileage();
            tmp[10] = dto.getOilwear();
            tmp[11] = dto.getFault();
            tmp[12] = dto.getAlarm();
            tmp[13] = dto.getAddress();
            tmp[14] = String.valueOf(dto.getHeight());
            tmp[15] = dto.getDailyMileage();
            tmp[16] = String.valueOf(dto.getRotation());
            tmp[17] = String.valueOf(dto.getCumulativeTurningNumber());
            tmp[18] = String.valueOf(dto.getCumulativeRunningTime());
            tmp[19] = String.valueOf(dto.getCoolingWaterTem());
            tmp[20] = dto.getTotalFuelConsumption();
            tmp[21] = String.valueOf(dto.getFuelConsumptionRate());
            tmp[22] = String.valueOf(dto.getAverageFuelConsumption());
            tmp[23] = String.valueOf(dto.getRealTimeOilConsumption());
            tmp[24] = dto.getEngineTorMode();
            tmp[25] = String.valueOf(dto.getDriverEnginePercentTor());
            tmp[26] = String.valueOf(dto.getActualEnginePercentTor());
            tmp[27] = dto.getAccPedalLowIdleSwitch();
            tmp[28] = dto.getAccPedalKickdownSwitch();
            tmp[29] = dto.getAccPedalPos();
            tmp[30] = String.valueOf(dto.getPercentLoadAtCurrentSpd());
            tmp[31] = String.valueOf(dto.getNominalFrictionPercentTrq());
            tmp[32] = dto.getParkingBrakeSwitch();
            tmp[33] = String.valueOf(dto.getWheelBasedVehicleSpd());
            tmp[34] = dto.getCruiseCtrlActive();
            tmp[35] = dto.getBrakeSwitch();
            tmp[36] = dto.getClutchSwitch();
            tmp[37] = dto.getCruiseCtrlSetSwitch();
            tmp[38] = dto.getCruiseCtrlCoastSwitch();
            tmp[39] = dto.getCruiseCtrlResumeSwitch();
            tmp[40] = dto.getCruiseCtrlAccSwitch();
            tmp[41] = String.valueOf(dto.getCruiseCtrlSetSpd());
            tmp[42] = dto.getCruiseCtrlStates();
            tmp[43] = String.valueOf(dto.getOutPutRoateSpeed());
            tmp[44] = String.valueOf(dto.getTcuLoad());
            tmp[45] = String.valueOf(dto.getVecuLoad());
            dataArr[i] = tmp;
        }
        String fileName = vin + "行驶数据.xlsx";
        String resultUrl = exportExcel.commonGenerateExcel(titleArr, 100, dataArr, vin + "行驶数据", fileName);
        log.info("调用公共方法生成并上传excel返回:{}", resultUrl);
        if (StringUtils.isNotBlank(resultUrl)) {
            try {
                Map map = JsonUtil.toMap(resultUrl);
                if ("200".equals(map.get("status").toString()) && map.get("data") != null) {
                    Map resultMap = (LinkedHashMap) map.get("data");
                    if (resultMap != null && resultMap.get("fullPath") != null) {
                        returnMap.put("fullPath", (String) resultMap.get("fullPath"));
                        returnMap.put("fileSize", (Integer) resultMap.get("size"));
                        returnMap.put("fileName", fileName);
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return returnMap;
    }

    private Map<String, Object> packFile(String procDate, List<Map<String, Object>> excelList) {
        String zipFileName = procDate + "车辆行驶数据";
        String zipFileFullName = zipFileName + ".zip";
        File zipFile = new File(zipFileFullName);
        FileOutputStream out = null;
        ZipOutputStream zipOut = null;
        try {
            out = new FileOutputStream(zipFile);
            CheckedOutputStream cos = new CheckedOutputStream(out, new CRC32());
            zipOut = new ZipOutputStream(cos);
            for (Map<String, Object> fileMap : excelList) {
                BufferedInputStream bis = null;
                try {
                    bis = new BufferedInputStream(new FileInputStream(new File((String) fileMap.get("fileName"))));
                    ZipEntry entry = new ZipEntry(zipFileName + File.separator + fileMap.get("fileName"));
                    zipOut.putNextEntry(entry);
                    int count;
                    byte[] data = new byte[BUFFER];
                    while ((count = bis.read(data, 0, BUFFER)) != -1) {
                        zipOut.write(data, 0, count);
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    if (null != bis) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (null != zipOut) {
                try {
                    zipOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out = null;
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("name", zipFileFullName);
        param.add("file", new FileSystemResource(zipFile));
        RestTemplate restTemplate = new RestTemplate();
        String httpResult = restTemplate.postForObject(fileStorageUrl, param, String.class);
        log.info("车辆行程压缩文件上传完成，返回：{}", httpResult);
        Map<String, Object> returnMap = new HashMap<>(16);
        if (StringUtils.isNotBlank(httpResult)) {
            try {
                Map map = JsonUtil.toMap(httpResult);
                if ("200".equals(map.get("status").toString()) && map.get("data") != null) {
                    Map resultMap = (LinkedHashMap) map.get("data");
                    if (resultMap != null && resultMap.get("fullPath") != null) {
                        returnMap.put("fullPath", (String) resultMap.get("fullPath"));
                        returnMap.put("fileSize", (Integer) resultMap.get("size"));
                        returnMap.put("fileName", zipFileFullName);
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return returnMap;
    }

    private void insertCarTrackFile(String procDate, Map<String, Object> resultMap) {
        resultMap.put("procDate", procDate);
        resultMap.put("fileType", FILE_TYPE_TRACK);
        exportCarInfoFileDao.insertExportCarFile(resultMap);
    }

    public boolean isNumericZidai(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 车辆状态附加信息additionAlarm
     *
     * @param locationData
     * @param remoteDiagnosis
     * @return
     */
    public QueryTracesPojo getVehicleStatusAdditionForTrack(LCLocationData.LocationData locationData,
                                                            QueryTracesPojo remoteDiagnosis) {
        try {
            LCLocationData.VehicleStatusAddition statusAddition = locationData.getStatusAddition();
            if (statusAddition != null) {
                List<LCVehicleStatusData.VehicleStatusData> VehicleStatusDataList = statusAddition.getStatusList();
                if (VehicleStatusDataList != null) {
                    for (LCVehicleStatusData.VehicleStatusData data : VehicleStatusDataList) {
                        switch (data.getTypes().getNumber()) {
                            // 车辆当前转速
                            case LCStatusType.StatusType.rotation_VALUE:
                                double rotation_VALUE = new Double(data.getStatusValue()).doubleValue();
                                remoteDiagnosis.setRotation(formatNumber(rotation_VALUE / 100, 2));
                                break;
                            // 冷却液液位（百分比）,改为改为 coolingWaterTem 0x14
                            case LCStatusType.StatusType.coolingWaterTem_VALUE:
                                // 发动机冷却水温度
                                double coolingWaterTem_VALUE = new Double(data.getStatusValue()).doubleValue();
                                remoteDiagnosis.setCoolingWaterTem(formatNumber(coolingWaterTem_VALUE / 100, 2));
                                break;
                            // 发动机燃油消耗率（毫升/小时）
                            case LCStatusType.StatusType.fuelConsumptionRate_VALUE:
                                double fuelConsumptionRate_VALUE = new Double(data.getStatusValue()).doubleValue();
                                // (升/小时)
                                remoteDiagnosis.setFuelConsumptionRate(formatNumber(fuelConsumptionRate_VALUE / 100 / 1000,
                                        2));
                                break;
                            // 平均燃油消耗率（千米/升）
                            case LCStatusType.StatusType.averageFuelConsumption_VALUE:
                                double averageFuelConsumption_VALUE = new Double(data.getStatusValue()).doubleValue();
                                remoteDiagnosis.setAverageFuelConsumption(formatNumber(averageFuelConsumption_VALUE / 100,
                                        2));
                                break;
                            // 发动机累计运行时间（秒）
                            case LCStatusType.StatusType.cumulativeRunningTime_VALUE:
                                long cumulativeRunningTime_VALUE = new Long(data.getStatusValue());
                                double time = formatNumber(cumulativeRunningTime_VALUE / 100L, 2);
                                remoteDiagnosis.setCumulativeRunningTime((long) time);
                                String timeStr = this.convert1((long) time);
                                remoteDiagnosis.setCumulativeRunningTimeStr(timeStr);
                                break;
                            // 发动机累计转数（单位:1000rpm）
                            case LCStatusType.StatusType.cumulativeTurningNumber_VALUE:
                                double cumulativeTurningNumber_VALUE = data.getStatusValue();
                                remoteDiagnosis.setCumulativeTurningNumber(formatNumber(cumulativeTurningNumber_VALUE / 100,
                                        2));
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return remoteDiagnosis;
    }

    public String convert1(long time) {
        long day = time / 86400;
        long h = (time - day * 86400) / 3600;
        long m = (time - day * 86400 - h * 3600) / 60;
        return day + "天" + h + "时" + m + "分";
    }

    public double formatNumber(double src, int validBit) {
        double result = 0;
        try {
            BigDecimal b = new BigDecimal(src);
            result = b.setScale(validBit, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 车辆状态附加信息additionAlarm
     *
     * @param locationData
     * @param remoteDiagnosis
     * @return
     */
    public QueryTracesPojo getcarMessageData(LCLocationData.LocationData locationData, QueryTracesPojo remoteDiagnosis) {
        try {
            remoteDiagnosis.setAccPedalLowIdleSwitch("");
            LCLocationData.VehicleStatusAddition statusAddition = locationData.getStatusAddition();
            if (statusAddition != null) {
                List<LCVehicleStatusData.VehicleStatusData> VehicleStatusDataList = statusAddition.getStatusList();
                if (VehicleStatusDataList != null) {
                    for (LCVehicleStatusData.VehicleStatusData data : VehicleStatusDataList) {
                        switch (data.getTypes().getNumber()) {
                            case LCStatusType.StatusType.engineTorMode_VALUE:// 转矩控制模式
                                int engineTorMode_VALUE = new Long(data.getStatusValue() / 100).intValue();
                                switch (engineTorMode_VALUE) {
                                    case 0:
                                        remoteDiagnosis.setEngineTorMode("低怠速调节器/无请求");
                                        break;
                                    case 1:
                                        remoteDiagnosis.setEngineTorMode("加速踏板");
                                        break;
                                    case 2:
                                        remoteDiagnosis.setEngineTorMode("巡航控制");
                                        break;
                                    case 3:
                                        remoteDiagnosis.setEngineTorMode("PTO调节器");
                                        break;
                                    case 4:
                                        remoteDiagnosis.setEngineTorMode("车速调节器");
                                        break;
                                    case 5:
                                        remoteDiagnosis.setEngineTorMode("ASR控制");
                                        break;
                                    case 6:
                                        remoteDiagnosis.setEngineTorMode("变速器控制");
                                        break;
                                    case 7:
                                        remoteDiagnosis.setEngineTorMode("ABS控制");
                                        break;
                                    case 8:
                                        remoteDiagnosis.setEngineTorMode("转矩限定");
                                        break;
                                    case 9:
                                        remoteDiagnosis.setEngineTorMode("高速调节器");
                                        break;
                                    case 10:
                                        remoteDiagnosis.setEngineTorMode("制动系统");
                                        break;
                                    case 11:
                                        remoteDiagnosis.setEngineTorMode("遥控加速器");
                                        break;
                                }
                                break;
                            case LCStatusType.StatusType.driverEnginePercentTor_VALUE:// 驾驶员需求发动机转矩百分比
                                int driverEnginePercentTor_VALUE = new Long(data.getStatusValue()).intValue();
                                remoteDiagnosis.setDriverEnginePercentTor(formatNumber(driverEnginePercentTor_VALUE / 100,
                                        2));
                                break;
                            case LCStatusType.StatusType.actualEnginePercentTor_VALUE:// 实际发动机转矩百分比
                                int actualEnginePercentTor_VALUE = new Long(data.getStatusValue()).intValue();
                                remoteDiagnosis.setActualEnginePercentTor(formatNumber(actualEnginePercentTor_VALUE / 100,
                                        2));
                                break;
                            case LCStatusType.StatusType.accPedalLowIdleSwitch_VALUE:// 加速踏板低怠速开关
                                int accPedalLowIdleSwitch_VALUE = new Long(data.getStatusValue()).intValue();
                                if (accPedalLowIdleSwitch_VALUE == 0) {
                                    remoteDiagnosis.setAccPedalLowIdleSwitch("不处于低怠速模式");
                                } else {
                                    remoteDiagnosis.setAccPedalLowIdleSwitch("处于低怠速模式");
                                }
                                break;
                            case LCStatusType.StatusType.accPedalKickdownSwitch_VALUE:
                                int accPedalKickdownSwitch_VALUE = new Long(data.getStatusValue() / 100).intValue();
                                if (accPedalKickdownSwitch_VALUE == 0) {
                                    remoteDiagnosis.setAccPedalKickdownSwitch("被动");
                                } else if (accPedalKickdownSwitch_VALUE == 1) {
                                    remoteDiagnosis.setAccPedalKickdownSwitch("主动");
                                }
                                break;
                            case LCStatusType.StatusType.accPedalPos_VALUE:// 加速踏板开度
                                int accPedalPos_VALUE = new Long(data.getStatusValue() / 100).intValue();
                                remoteDiagnosis.setAccPedalPos(String.valueOf(accPedalPos_VALUE));
                                break;
                            case LCStatusType.StatusType.percentLoadAtCurrentSpd_VALUE:// 当前速度下，负载百分比
                                int percentLoadAtCurrentSpd_VALUE = new Long(data.getStatusValue()).intValue();
                                remoteDiagnosis.setPercentLoadAtCurrentSpd(formatNumber(percentLoadAtCurrentSpd_VALUE / 100,
                                        2));
                                break;
                            case LCStatusType.StatusType.nominalFrictionPercentTrq_VALUE:// 名义摩擦力矩百分比
                                int nominalFrictionPercentTrq_VALUE = new Long(data.getStatusValue()).intValue();
                                remoteDiagnosis.setNominalFrictionPercentTrq(formatNumber(nominalFrictionPercentTrq_VALUE / 100,
                                        2));
                                break;
                            case LCStatusType.StatusType.parkingBrakeSwitch_VALUE:// 驻车制动器开关
                                int parkingBrakeSwitch_VALUE = new Long(data.getStatusValue() / 100).intValue();
                                if (parkingBrakeSwitch_VALUE == 0) {
                                    remoteDiagnosis.setParkingBrakeSwitch("未操作");
                                } else if (parkingBrakeSwitch_VALUE == 1) {
                                    remoteDiagnosis.setParkingBrakeSwitch("操作");
                                }
                                break;
                            case LCStatusType.StatusType.wheelBasedVehicleSpd_VALUE:// 车轮车速
                                Long wheelBasedVehicleSpd_VALUE = data.getStatusValue();
                                remoteDiagnosis.setWheelBasedVehicleSpd(formatNumber(wheelBasedVehicleSpd_VALUE / 100,
                                        2));
                                break;
                            case LCStatusType.StatusType.cruiseCtrlActive_VALUE:
                                int cruiseCtrlActive_VALUE = new Long(data.getStatusValue() / 100).intValue();
                                if (cruiseCtrlActive_VALUE == 0) {
                                    remoteDiagnosis.setCruiseCtrlActive("未激活");
                                } else {
                                    remoteDiagnosis.setCruiseCtrlActive("激活");
                                }
                                break;
                            case LCStatusType.StatusType.brakeSwitch_VALUE:
                                int brakeSwitch_VALUE = new Long(data.getStatusValue() / 100).intValue();
                                if (brakeSwitch_VALUE == 0) {
                                    remoteDiagnosis.setBrakeSwitch("未操作");
                                } else {
                                    remoteDiagnosis.setBrakeSwitch("操作");
                                }
                                break;
                            case LCStatusType.StatusType.clutchSwitch_VALUE:// 离合器开关
                                int clutchSwitch_VALUE = new Long(data.getStatusValue() / 100).intValue();
                                if (clutchSwitch_VALUE == 0) {
                                    remoteDiagnosis.setClutchSwitch("未分离");
                                } else {
                                    remoteDiagnosis.setClutchSwitch("分离");
                                }
                                break;
                            case LCStatusType.StatusType.cruiseCtrlSetSwitch_VALUE:// 巡航控制设置开关
                                int cruiseCtrlSetSwitch_VALUE = new Long(data.getStatusValue() / 100).intValue();
                                if (cruiseCtrlSetSwitch_VALUE == 0) {
                                    remoteDiagnosis.setCruiseCtrlSetSwitch("无按钮按下");
                                } else {
                                    remoteDiagnosis.setCruiseCtrlSetSwitch("退出巡航");
                                }
                                break;
                            case LCStatusType.StatusType.cruiseCtrlCoastSwitch_VALUE:
                                int cruiseCtrlCoastSwitch_VALUE = new Long(data.getStatusValue() / 100).intValue();
                                if (cruiseCtrlCoastSwitch_VALUE == 0) {
                                    remoteDiagnosis.setCruiseCtrlCoastSwitch("未按下");
                                } else {
                                    remoteDiagnosis.setCruiseCtrlCoastSwitch("按下");
                                }
                                break;
                            case LCStatusType.StatusType.cruiseCtrlResumeSwitch_VALUE:// 巡航控制恢复开关
                                int cruiseCtrlResumeSwitch_VALUE = new Long(data.getStatusValue() / 100).intValue();
                                if (cruiseCtrlResumeSwitch_VALUE == 0) {
                                    remoteDiagnosis.setCruiseCtrlResumeSwitch("未按下");
                                } else {
                                    remoteDiagnosis.setCruiseCtrlResumeSwitch("按下");
                                }
                                break;
                            case LCStatusType.StatusType.cruiseCtrlAccSwitch_VALUE:// 巡航控制加速开关
                                int cruiseCtrlAccSwitch_VALUE = new Long(data.getStatusValue() / 100).intValue();
                                if (cruiseCtrlAccSwitch_VALUE == 0) {
                                    remoteDiagnosis.setCruiseCtrlAccSwitch("未按下");
                                } else {
                                    remoteDiagnosis.setCruiseCtrlAccSwitch("按下");
                                }
                                break;
                            case LCStatusType.StatusType.cruiseCtrlSetSpd_VALUE:// 巡航控制设置速度
                                long cruiseCtrlSetSpd_VALUE = new Long(data.getStatusValue() / 100).intValue();
                                remoteDiagnosis.setCruiseCtrlSetSpd(formatNumber(cruiseCtrlSetSpd_VALUE, 2));
                                break;
                            case LCStatusType.StatusType.cruiseCtrlStates_VALUE:// 巡航控制状态
                                int cruiseCtrlStates_VALUE = new Long(data.getStatusValue() / 100).intValue();
                                switch (cruiseCtrlStates_VALUE) {
                                    case 0:
                                        remoteDiagnosis.setCruiseCtrlStates("关闭/禁止");
                                        break;
                                    case 1:
                                        remoteDiagnosis.setCruiseCtrlStates("保持");
                                        break;
                                    case 2:
                                        remoteDiagnosis.setCruiseCtrlStates("加速");
                                        break;
                                    case 3:
                                        remoteDiagnosis.setCruiseCtrlStates("减速/滑行");
                                        break;
                                    case 4:
                                        remoteDiagnosis.setCruiseCtrlStates("恢复");
                                        break;
                                    case 5:
                                        remoteDiagnosis.setCruiseCtrlStates("设置");
                                        break;
                                    case 6:
                                        remoteDiagnosis.setCruiseCtrlStates("加速踏板取代");
                                        break;
                                }
                                break;
                            case LCStatusType.StatusType.TripDistanceDD_VALUE:// 小计里程
                                long tripDistanceDD_VALUE = data.getStatusValue();
                                remoteDiagnosis.setDailyMileage(String.valueOf(formatNumber(tripDistanceDD_VALUE / 100, 2)));
                                break;
                            case LCStatusType.StatusType.realTimeOilConsumption_VALUE:// 实时油耗
                                int realTimeOilConsumption_VALUE = new Long(data.getStatusValue()).intValue();
                                remoteDiagnosis.setRealTimeOilConsumption(formatNumber(realTimeOilConsumption_VALUE / 100,
                                        2));
                                break;
                            case LCStatusType.StatusType.totalFuelConsumption_VALUE:
                                int totalFuelConsumption_VALUE = new Long(data.getStatusValue()).intValue();
                                remoteDiagnosis.setTotalOil(formatNumber(totalFuelConsumption_VALUE / 100, 2));
                                break;
                            case LCStatusType.StatusType.outPutRoateSpeed_VALUE:// 输出轴速度
                                int outPutRoateSpeed_VALUE = new Long(data.getStatusValue() / 100).intValue();
                                remoteDiagnosis.setOutPutRoateSpeed(outPutRoateSpeed_VALUE);
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return remoteDiagnosis;
    }

}
