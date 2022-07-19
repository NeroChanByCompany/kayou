package com.nut.servicestation.app.service.impl;

import com.nut.common.constant.OperateCodeEnum;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.LonLatUtil;
import com.nut.common.utils.NumberFormatUtil;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.client.TspBusinessClient;
import com.nut.servicestation.app.dao.AccessoriesCodeDao;
import com.nut.servicestation.app.dao.ServiceAarNoticeDao;
import com.nut.servicestation.app.dao.WoInfoDao;
import com.nut.servicestation.app.domain.AccessoriesCode;
import com.nut.servicestation.app.dto.ServiceAarNoticeDto;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.dto.WoInfoDto;
import com.nut.servicestation.app.dto.WoProcessDto;
import com.nut.servicestation.app.entity.CarGasInfo;
import com.nut.servicestation.app.form.WoInfoForm;
import com.nut.servicestation.app.pojo.WoInfoPojo;
import com.nut.servicestation.app.pojo.WoProcessPojo;
import com.nut.servicestation.app.service.UserService;
import com.nut.servicestation.app.service.WoInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@Service("WoInfoService")
public class WoInfoServiceImpl implements WoInfoService {

    private static final String YES = "1";
    private static final String NO = "0";
    // 原因件标识标记
    private static final String ACCESSORIESCODE1 = "P51,P41,P151,P141,P101,P91";
    // 新件标识标记
    private static final String ACCESSORIESCODE2 = "P52,P42,P152,P142,P102,P92";
    @Value("${database_name}")
    private String DatabaseName;
    @Autowired
    private ServiceAarNoticeDao serviceAarNoticeMapper;
    @Value("${queryNoticeType:返修,活动}")
    private String queryNoticeType;
    @Autowired
    private WoInfoDao woInfoMapper;
    @Autowired
    private UserService queryUserInfoService;
    /** 进出站 最大拒绝接单次数 */
    @Value("${refuse_max_times:3}")
    private Integer refuseMaxTimes;

    /** 外出救援 最大拒绝接单次数 */
    @Value("${refuse_max_times_rescue:1}")
    private Integer refuseMaxTimesRescue;

    /** 最大申请修改次数 */
    @Value("${modify_max_times:2}")
    private Integer modifyMaxTimes;

    /** 最大申请关闭次数 */
    @Value("${close_max_times:2}")
    private Integer closeMaxTimes;

    @Autowired
    private AccessoriesCodeDao accessoriesCodeMapper;

    @Autowired
    private TspBusinessClient tspBusinessClient;

    @Override
    public HttpCommandResultWithData core(WoInfoForm command, boolean isFromTboss) {
        log.info("[core]start");
        HttpCommandResultWithData<Map> result = new HttpCommandResultWithData<>();
        WoInfoDto info = null;
        //查询当前服务站抢单信息
        Long stationId = woInfoMapper.queryStationById(DatabaseName,command.getUserId());
        if (stationId != null){
            //开始查询抢单信息
            WoInfoPojo pojo = woInfoMapper.queryWorkOrderStation(DatabaseName,stationId,command.getWoCode());
            if (pojo != null){
                if(pojo.getChassisNum() != null){
                    String str = pojo.getChassisNum();
                    String chassisNum = pojo.getChassisNum();
                    if(chassisNum.length() > 8){
                        str = chassisNum.substring(chassisNum.length()-8,chassisNum.length()-1);
                    }
                    //调用tsp接口查询排放标准
                    HttpCommandResultWithData<CarGasInfo> carGasInfo = tspBusinessClient.getByChassisNum(str);
                    CarGasInfo o = carGasInfo.getData();
                    CarGasInfo carGasInfo1 = new CarGasInfo();
                    if(o != null){
                        carGasInfo1 = o;
                    }
                    if(carGasInfo1.getGasOutValue() != null){
                        pojo.setGasOutValue(carGasInfo1.getGasOutValue());
                    }
                    List<String> workOrderText = woInfoMapper.queryWorkOrderText();
                    if(workOrderText.size() > 0){
                        pojo.setWorkOderText(workOrderText.get(0));
                    }
                }
                // 查询公告车型
                String gg = woInfoMapper.queryGgByCarId(DatabaseName, pojo.getCarId());
                log.info("[queryInfo]gonggao:{}", gg);
                pojo.setGonggao(gg);
                // 查询故障码
                String fault = woInfoMapper.queryLatestSFault(pojo.getCarId());
                log.info("[queryInfo]fault:{}", fault);
                pojo.setFaultCode(fault);
                String mileage = pojo.getMileage();
                if (StringUtils.isNotBlank(mileage) && ".".equals(mileage.substring(mileage.length()-1))){
                    mileage = mileage.substring(0,mileage.lastIndexOf("."));
                    pojo.setMileage(mileage);
                }
                //处理公里数包含.的问题
                info = pojoToDto(pojo, command.getUserId(), isFromTboss);
            }
        }
        //如果不是抢单信息就去查询是否是正常工单
        if (info == null){
            info  = queryInfo(command.getWoCode(), command.getUserId(), isFromTboss);
            if (info == null) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("未获取到该工单信息");
                return result;
            }
        }
        try {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("info", info);
            if (command.getNoProcess() == null || command.getNoProcess() != 1) {
                resultMap.put("process", queryProcess(command.getWoCode(), info.getAssignTo(), isFromTboss));
            }
            List<ServiceAarNoticeDto> servicesList = queryServiceAarNotices(info.getStationCode(), info.getChassisNum(), info.getWoStatus());
            if (servicesList != null) {
                resultMap.put("serviceActivities", servicesList);
            }
            result.setResultCode(ECode.SUCCESS.code());
            result.setMessage(ECode.SUCCESS.message());
            result.setData(resultMap);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询工单详情失败");
            log.error(e.getMessage(), e);
        }
        log.info("[core]end");
        return result;
    }
    /**
     * 查询服务活动
     * @param stationCode
     * @param vin
     * @param woStatus
     * @return
     */
    public List<ServiceAarNoticeDto> queryServiceAarNotices(String stationCode, String vin, Integer woStatus) {
        // 检查中、维修中才返回
        if (woStatus == ServiceStationEnum.INSPECTING.code() || woStatus == ServiceStationEnum.REPAIRING.code()) {
            List<ServiceAarNoticeDto> servicesList = serviceAarNoticeMapper.queryServiceAarNotices(stationCode, vin, Arrays.asList(queryNoticeType.split(",")));
            if (servicesList == null) {
                servicesList = new ArrayList<>();
            }
            return servicesList;
        }
        return null;
    }
    /**
     * 查询工单基础信息
     *
     * @param woCode 工单号
     * @return 工单信息
     */
    public WoInfoDto queryInfo(String woCode, String userId, boolean isFromTboss) {
        log.info("[queryInfo]start");

        // 查询工单信息
        List<WoInfoPojo> queryList = woInfoMapper.queryWoInfoByWoCode(DatabaseName, woCode);
        if (queryList == null || queryList.isEmpty()) {
            log.info("[queryInfo]work order doesn't exist");
            return null;
        }
        WoInfoPojo queryWo = queryList.get(0);
        if(queryWo.getChassisNum() != null){
            String str = queryWo.getChassisNum();
            String chassisNum = queryWo.getChassisNum();
            if(chassisNum.length() > 8){
                str = chassisNum.substring(chassisNum.length()-8,chassisNum.length());
            }
            //调用tsp接口查询排放标准
            HttpCommandResultWithData<CarGasInfo> carGasInfo = tspBusinessClient.getByChassisNum(str);
            CarGasInfo o = carGasInfo.getData();
            CarGasInfo carGasInfo1 = new CarGasInfo();
            if(o != null){
                carGasInfo1 = o;
            }
            if(carGasInfo1.getGasOutValue() != null){
                queryWo.setGasOutValue(carGasInfo1.getGasOutValue());
            }
            List<String> workOrderText = woInfoMapper.queryWorkOrderText();
            if(workOrderText.size()>0 && !workOrderText.isEmpty()){
                for(int i=0;i<workOrderText.size();i++){
                    if(workOrderText.get(i) != null){
                        queryWo.setWorkOderText(workOrderText.get(i));
                    }
                }
            }
        }
        log.info("[queryInfo]carId:{}", queryWo.getCarId());
        // 查询公告车型
        String gg = woInfoMapper.queryGgByCarId(DatabaseName, queryWo.getCarId());
        log.info("[queryInfo]gonggao:{}", gg);
        queryWo.setGonggao(gg);

        // 查询故障码
        String fault = woInfoMapper.queryLatestSFault(queryWo.getCarId());
        log.info("[queryInfo]fault:{}", fault);
        queryWo.setFaultCode(fault);

        String mileage = queryWo.getMileage();
        if (StringUtils.isNotBlank(mileage) && ".".equals(mileage.substring(mileage.length()-1))){
            mileage = mileage.substring(0,mileage.lastIndexOf("."));
            queryWo.setMileage(mileage);
        }
        WoInfoDto result = pojoToDto(queryWo, userId, isFromTboss);
        log.info("[queryInfo]end");
        return result;
    }
    /**
     * 查询工单服务流程
     *
     * @param woCode      工单号
     * @param account     维修账号
     * @param isFromTboss 调用方为TBOSS
     * @return 服务流程数组
     */
    public List<WoProcessDto> queryProcess(String woCode, String account, boolean isFromTboss) throws IOException {
        log.info("[queryProcess]start");

        // 查询工单流程
        Map<String, Object> param = new HashMap<>(2);
        param.put("woCode", woCode);
        param.put("isFromTboss", isFromTboss);
        List<WoProcessPojo> queryList = woInfoMapper.queryWoProcessByWoCode(param);
        log.info("[queryProcess]queryList.size:{}", queryList.size());

        // 收集检查过程和维修过程的operateId
        List<String> operateIds = queryList.stream()
                .filter(e -> e.getOperateCode() == OperateCodeEnum.OP_INSPECT.code() || e.getOperateCode() == OperateCodeEnum.OP_REPAIR.code())
                .map(WoProcessPojo::getOperateId).collect(Collectors.toList());
        log.info("[queryProcess]operateIds:{}", String.join(",", operateIds));

        // 查询出关联图片
        List<WoProcessPojo> photos = new ArrayList<>();
        if (operateIds.size() > 0) {
            Map<String, Object> photoParam = new HashMap<>(2);
            photoParam.put("woCode", woCode);
            photoParam.put("list", operateIds);
            photos = woInfoMapper.queryWoPhotoByWoCodeAndOperateIn(photoParam);
        }
        log.info("[queryProcess]photos.size:{}", photos.size());

        // 根据operateId填充对应图片的url
        for (WoProcessPojo process : queryList) {
            if (process.getOperateCode() == OperateCodeEnum.OP_INSPECT.code() || process.getOperateCode() == OperateCodeEnum.OP_REPAIR.code()) {
                // 检查过程和维修过程需要替换图片
                String operateId = process.getOperateId();
                String originalJson = process.getTextJson();
                log.info("[queryProcess]operateId:{}||originalJson:{}", operateId, originalJson);

                List<String> urls = photos.stream().filter(e -> StringUtil.isEq(e.getOperateId(), operateId))
                        .map(WoProcessPojo::getUrl).collect(Collectors.toList());
                Map<String, Object> map = JsonUtil.toMap(originalJson);
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getValue() instanceof String
                            && String.valueOf(entry.getValue()).equals(ServiceStationVal.PHOTO_URL_PLACEHOLDER)) {
                        // 替换成相应url集合
                        entry.setValue(urls);
                        break;
                    }
                }

                // 处理配件码，维修过程912
                if (process.getOperateCode() == OperateCodeEnum.OP_REPAIR.code()) {
                    List<AccessoriesCode> accessoriesCodeList = accessoriesCodeMapper.selectListByWoCode(woCode, operateId);
                    if (accessoriesCodeList != null && !accessoriesCodeList.isEmpty()) {
                        List<String> acce1List = accessoriesCodeList.stream().filter(acc -> StringUtil.containMark(ACCESSORIESCODE1, acc.getAcceType())).map(acc -> acc.getAcceCode()).collect(Collectors.toList());
                        List<String> acce2List = accessoriesCodeList.stream().filter(acc -> StringUtil.containMark(ACCESSORIESCODE2, acc.getAcceType())).map(acc -> acc.getAcceCode()).collect(Collectors.toList());
                        Map<String, Object> map2 = new LinkedHashMap<>();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            map2.put(entry.getKey(), entry.getValue());
                            if (StringUtil.containMark(entry.getKey(), "维修过程拍照")) {
                                if (acce1List != null && !acce1List.isEmpty()) {
                                    map2.put("原因件标识码：", acce1List);
                                }
                                if (acce2List != null && !acce2List.isEmpty()) {
                                    map2.put("新件标识码：", acce2List);
                                }
                            }
                        }
                        map = map2;
                    }
                }

                String newJson = JsonUtil.toJson(map);
                log.info("[queryProcess]newJson:{}", newJson);
                process.setTextJson(newJson);
            }

            if ((process.getOperateCode() == OperateCodeEnum.OP_SCAN_RECEIVE.code()
                    || process.getOperateCode() == OperateCodeEnum.OP_TIMEOUT_STATION.code())
                    && isFromTboss) {
                // TBoss的接车记录显示特别
                process.setTextJson(process.getTextJsonTBoss());
            }
        }

        // 将多个连续的维修过程合并成一个
        queryList = mergeRepairRecords(queryList, account);

        List<WoProcessDto> result = pojoToDto(queryList);
        log.info("[queryProcess]end");
        return result;
    }
    /**
     * 类型转换
     * (WoProcessPojo -> WoProcessDto)
     */
    private WoProcessDto pojoToDto(WoProcessPojo pojo) {
        log.info("[pojoToDto]pojo:{}", pojo.toString());
        WoProcessDto dto = new WoProcessDto();
        dto.setOperateCode(pojo.getOperateCode());
        dto.setTitle(pojo.getTitle());
        // 隐藏评价账号
        if (pojo.getOperateCode() == OperateCodeEnum.OP_COMMENT.code()) {
            String textJSON = pojo.getTextJson();
            try {
                Map<String, Object> textMap = JsonUtil.toMap(textJSON);
                String textAcc = (String) textMap.get("评价账号：");
                if (StringUtil.isNotEmpty(textAcc)) {
                    int textLen = textAcc.length();
                    if (textLen == 1) {
                        textMap.put("评价账号：", "*");
                    } else if (textLen > 1 && textLen <= 4) {
                        StringBuffer newText = new StringBuffer();
                        newText.append(textAcc.substring(0, 1));
                        for (int i = 0; i < textLen - 1; i++) {
                            newText.append("*");
                        }
                        textMap.put("评价账号：", newText.toString());
                    } else {
                        StringBuffer newText = new StringBuffer();
                        newText.append(textAcc.substring(0, 2));
                        for (int i = 0; i < textLen - 4; i++) {
                            newText.append("*");
                        }
                        newText.append(textAcc.substring(textLen - 2, textLen));
                        textMap.put("评价账号：", newText.toString());
                    }
                }
                dto.setDetailJson(JsonUtil.toJson(textMap));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                dto.setDetailJson(pojo.getTextJson());
            }
        } else {
            dto.setDetailJson(pojo.getTextJson());
        }

        dto.setTimesRescueNumber(pojo.getTimesRescueNumber());
        return dto;
    }
    /**
     * 类型转换
     * (WoProcessPojo -> WoProcessDto)
     */
    private List<WoProcessDto> pojoToDto(List<WoProcessPojo> pojo) {
        return pojo.stream().map(this::pojoToDto).collect(Collectors.toList());
    }
    /**
     * 将多个*连续的*维修过程合并成一个
     */
    private List<WoProcessPojo> mergeRepairRecords(List<WoProcessPojo> processes, String account) throws IOException {
        List<WoProcessPojo> result = new ArrayList<>();

        // 开始连续出现维修过程的标识
        boolean repairRecordBegin = false;
        // 存储连续维修过程json的buffer
        List<String> jsonBuffer = new ArrayList<>();
        // 记录连续维修过程的最后一次维修时间
        String lastRepairTime = "";
        for (WoProcessPojo process : processes) {
            log.info("[mergeRepairRecords]operateCode:{}", process.getOperateCode());
            // 按顺序遍历
            if (process.getOperateCode() == OperateCodeEnum.OP_REPAIR.code()) {
                // 当前遍历到维修过程
                if (!repairRecordBegin) {
                    repairRecordBegin = true;
                }
                jsonBuffer.add(process.getTextJson());
                lastRepairTime = process.getCreateTime();
            } else {
                // 当前遍历到不是维修过程
                if (repairRecordBegin) {
                    repairRecordBegin = false;
                    String mergedJson = mergeRepairJsons(jsonBuffer, account, lastRepairTime);
                    // 生成合并后的维修过程
                    WoProcessPojo mergedProcess = new WoProcessPojo();
                    mergedProcess.setOperateCode(OperateCodeEnum.OP_REPAIR.code());
                    mergedProcess.setTitle(OperateCodeEnum.OP_REPAIR.message());
                    mergedProcess.setTextJson(mergedJson);
                    result.add(mergedProcess);
                    // buffer使用完清空
                    jsonBuffer.clear();
                    lastRepairTime = "";
                }
                result.add(process);
            }
        }

        // 如果循环结束后jsonBuffer中仍有数据，说明列表以维修过程为结束
        if (jsonBuffer.size() > 0) {
            String mergedJson = mergeRepairJsons(jsonBuffer, account, lastRepairTime);
            // 生成合并后的维修过程
            WoProcessPojo mergedProcess = new WoProcessPojo();
            mergedProcess.setOperateCode(OperateCodeEnum.OP_REPAIR.code());
            mergedProcess.setTitle(OperateCodeEnum.OP_REPAIR.message());
            mergedProcess.setTextJson(mergedJson);
            result.add(mergedProcess);
        }

        log.info("[mergeRepairRecords]result.size:{}", result.size());
        return result;
    }
    /**
     * 合并多个json
     */
    private String mergeRepairJsons(List<String> jsons, String account, String time) throws IOException {
        log.info("[mergeRepairJsons]jsons.size:{}", jsons.size());
        Map<String, Object> mergedMap = new LinkedHashMap<>();
        int i = 1;
        for (String json : jsons) {
            Map<String, Object> map = JsonUtil.toMap(json);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                // 多个维修记录的时候，“处理方式”、“故障描述”、“维修过程拍照”等key会重复，需要加编号
                String numberedKey = entry.getKey().replace("：", i + "：");
                mergedMap.put(numberedKey, entry.getValue());
            }
            i++;
        }
        mergedMap.put("维修账号：", account);
        mergedMap.put("维修结束时间：", time);
        String mergedJson = JsonUtil.toJson(mergedMap);
        log.info("[mergeRepairJsons]mergedJson:{}", mergedJson);
        return mergedJson;
    }
    /**
     * 类型转换
     * (WoInfoPojo -> WoInfoDto)
     */
    private WoInfoDto pojoToDto(WoInfoPojo pojo, String userId, boolean isFromTboss) {
        log.debug("[pojoToDto]pojo:{}", pojo.toString());
        UserInfoDto userInfoDto = null;
        if (!isFromTboss) {
            userInfoDto = queryUserInfoService.getUserInfoByUserId(userId, false);
            if (userInfoDto == null) {
                return null;
            }
        }

        WoInfoDto dto = new WoInfoDto();
        //排放标准加入返回值
        dto.setGasOutValue(pojo.getGasOutValue());
        if(pojo.getGasOutValue() != null){
            if(pojo.getGasOutValue().equals("国六")){
                dto.setWorkOderText(pojo.getWorkOderText());
            }
        }
        dto.setWoCode(pojo.getWoCode());
        dto.setCarNumber(pojo.getCarNumber());
        dto.setAppoStationName(pojo.getAppoStationName());
        dto.setAppoArriveTime(pojo.getAppoArriveTime());
        dto.setAppoType(pojo.getAppoType());
        dto.setWoType(pojo.getWoType());
        dto.setWoStatus(pojo.getWoStatus());
        dto.setTimeCreate(pojo.getTimeCreate());
        dto.setTimeInspectBegin(pojo.getTimeInspectBegin());
        dto.setAppoUserName(pojo.getAppoUserName());
        dto.setAppoUserPhone(pojo.getAppoUserPhone());
        dto.setCarLon(pojo.getCarLon());
        dto.setCarLat(pojo.getCarLat());
        dto.setCarLocation(pojo.getCarLocation());
        // 车与服务站距离数据库保存单位为米，转换为千米返回
        if (pojo.getCarDistance() != null) {
            dto.setCarDistance(NumberFormatUtil.formatNumber(pojo.getCarDistance() / 1000d, 1));
        }
        dto.setRepairItem(pojo.getRepairItem());
        dto.setMaintainItem(pojo.getMaintainItem());
        dto.setOperatorId(pojo.getOperatorId());
        // OperatorName与OperatorId赋值一样
        dto.setOperatorName(pojo.getOperatorName());
        dto.setUserComment(pojo.getUserComment());
        dto.setCarSaleDate(pojo.getCarSaleDate());
        dto.setMileage(pojo.getMileage() == null ? 0D :Double.parseDouble(pojo.getMileage()));
        dto.setChassisNum(pojo.getChassisNum());
        dto.setGonggao(pojo.getGonggao());
        dto.setEngineType(pojo.getEngineType());
        dto.setEngineModel(pojo.getEngineModel());
        dto.setFaultCode(pojo.getFaultCode());
        dto.setSeriseName(pojo.getSeriseName());
        dto.setStationId(pojo.getStationId());
        dto.setStationCode(pojo.getStationCode());
        // 转换经纬度的格式
        dto.setStationLon(LonLatUtil.convertLonLat(pojo.getStationLon()));
        dto.setStationLat(LonLatUtil.convertLonLat(pojo.getStationLat()));
        dto.setAreaCode(pojo.getAreaCode());
        dto.setShipperName(pojo.getShipperName());
        dto.setShipperPhone(pojo.getShipperPhone());
        dto.setRegisteredPhone(pojo.getRegisteredPhone());
        if (pojo.getIsEmergency() != null) {
            dto.setIsEmergency(String.valueOf(pojo.getIsEmergency()));
        }
        if (pojo.getBreakStatus() != null) {
            dto.setBreakStatus(String.valueOf(pojo.getBreakStatus()));
        }
        dto.setLoadDescription(pojo.getLoadDescription());

        int limit = 99;
        if (pojo.getWoType() == ServiceStationVal.STATION_SERVICE) {
            limit = refuseMaxTimes;
        } else if (pojo.getWoType() == ServiceStationVal.OUTSIDE_RESCUE) {
            limit = refuseMaxTimesRescue;
        }
        if (pojo.getRefuseTimes() != null && pojo.getRefuseTimes() >= limit) {
            dto.setCanRefuse(NO);
        } else {
            dto.setCanRefuse(YES);
        }
        // 判断修改要管理员权限
        if (userInfoDto != null && ServiceStationVal.JOB_TYPE_ADMIN != userInfoDto.getRoleCode()) {
            dto.setCanModify(NO);
        } else {
            if (pojo.getModifyTimes() != null && pojo.getModifyTimes() >= modifyMaxTimes) {
                dto.setCanModify(NO);
            } else {
                dto.setCanModify(YES);
            }
        }

        if (pojo.getCloseTimes() != null && pojo.getCloseTimes() >= closeMaxTimes) {
            dto.setCanClose(NO);
        } else {
            dto.setCanClose(YES);
        }
        dto.setAssignTo(pojo.getAssignTo());
        dto.setRescueStaffName(pojo.getRescueStaffName());
        dto.setRescueStaffPhone(pojo.getRescueStaffPhone());
        dto.setRescueStaffNum(pojo.getRescueStaffNum());
        dto.setRescueCarDevice(pojo.getRescueCarDevice());
        dto.setNonServiceMarkAct(pojo.getNonServiceMarkAct());
        dto.setNonServiceMarkTime(pojo.getNonServiceMarkTime());
        dto.setRescueIsTransferring(StringUtil.isEmpty(pojo.getRescueIsTransferring()) ? NO : pojo.getRescueIsTransferring());
        if (pojo.getWoType() == ServiceStationVal.STATION_SERVICE) {
            dto.setRescueType(0);
        } else {
            dto.setRescueType(pojo.getRescueType());
        }
        /** 工单状态是检查中或者维修中，展示保养计划*/
        if (Objects.nonNull(pojo.getWoStatus())
                && (pojo.getWoStatus() == ServiceStationEnum.INSPECTING.code()
                || pojo.getWoStatus() == ServiceStationEnum.REPAIRING.code() )) {
            dto.setIsShowMaintain(1);
        }else {
            dto.setIsShowMaintain(0);
        }
        // 协议工单标识
        dto.setProtocolMark(pojo.getProtocolMark());
        // 结算单
        dto.setBillSubStatus(pojo.getBillSubStatus());
        if (pojo.getBillReject() != null) {
            dto.setBillReject(pojo.getBillReject());
        }
        dto.setEstimateTime(pojo.getEstimateTime());
        dto.setEstimateFee(pojo.getEstimateFee());

        return dto;
    }
}
