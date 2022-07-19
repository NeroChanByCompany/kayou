package com.nut.driver.app.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.dozermapper.core.Mapper;
import com.nut.driver.app.client.TspBusinessClient;
import com.nut.driver.app.client.TspClient;
import com.nut.driver.app.client.ZhixiaoClient;
import com.nut.driver.app.entity.*;
import com.nut.driver.app.pojo.*;
import com.nut.driver.app.service.AuthLogService;
import com.nut.driver.common.component.AppVersionComponent;
import com.nut.driver.common.utils.PageUtil;
import com.github.pagehelper.Page;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.domain.Car;
import com.nut.driver.app.domain.FltFleet;
import com.nut.driver.app.dto.*;
import com.nut.common.constant.AppTypeEnum;
import com.nut.driver.app.domain.FltCarOwnerMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.assembler.ReverseGeoCodingClient;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.enums.OnlineStatusEnum;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.JsonUtil;
import com.nut.driver.common.constants.FleetRoleEnum;
import com.nut.locationservice.app.form.GetOnlineStatusForm;
import com.nut.driver.app.client.LocationServiceClient;
import com.nut.driver.app.dao.*;
import com.nut.driver.app.form.QueryMaintainItemListForm;
import com.nut.driver.app.form.QueryMaintenanceItemForm;
import com.nut.driver.app.form.UserCarsForm;
import com.nut.driver.app.form.*;
import com.nut.driver.app.service.CarService;
import com.nut.driver.app.service.IntegralService;
import com.nut.driver.common.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;
import com.nut.driver.app.dto.CarOnlineStatusDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author liuBing
 * @Classname CarServiceImpl
 * @Description TODO
 * @Date 2021/6/24 17:30
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class CarServiceImpl implements CarService {

    private static final String SEPARATOR = ",";
    private static final String YES = "1";
    private static final int Y = 1;
    private static final int N = 0;

    /**
     * 车牌号审核状态：待审核
     */
    private static final int STS_TO_BE_CHECKED = 1;

    @Autowired
    private ProductDeliveryVehicleInfoDao productDeliveryVehicleInfoDao;

    @Autowired
    private ReverseGeoCodingClient queryGeographicalService;

    @Autowired
    private CarDao carDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private FltFleetDao fltFleetDao;

    @Autowired
    private MaintainVehicleTubeDao maintainVehicleTubeDao;

    @Autowired
    private ServiceStationDao serviceStationDao;

    @Autowired
    private FltFleetCarMappingDao fltFleetCarMappingDao;

    @Autowired
    private WorkOrderDao workOrderDao;

    @Autowired
    private MaintainStationTubeDao maintainStationTubeDao;

    @Autowired
    private LocationServiceClient locationServiceClient;

    @Autowired
    private PageUtil pageUtil;

    @Autowired
    private MaintainItemInfoDao maintainItemInfoDao;

    @Autowired
    private CustomMaintainInfoDao customMaintainInfoDao;

    @Autowired
    private ZhixiaoClient zhixiaoClient;

    @Autowired
    TspClient tspClient;
    @Autowired
    TspBusinessClient tspBusinessClient;

    @Autowired
    private AuthLogService authLogService;

    @Autowired
    private Mapper convert;

    @Autowired
    private AppVersionComponent appVersionComponent;

    @Autowired
    private CustomMaintainItemDao customMaintainItemDao;
    @Value("${database_name:jac_tsp_dev}")
    private String DbName;

    @Value("${workOrderNotAllowAreaCode:dummy}")
    private String areaCodes;

    @Value("${interceptHours:24}")
    private int interceptHours;

    @Value("${interceptMessage:该车辆在24小时内创建过工单，若要继续建单请联系客服创建（400-800-9933）！}")
    private String interceptMessage;

    @Value("${guoQingStart}")
    private String guoQingStart;

    @Value("${guoQingEnd}")
    private String guoQingEnd;

    @Value("${shuang11Start}")
    private String shuang11Start;

    @Value("${shuang11End}")
    private String shuang11End;


    @Override
    public UserCarsDTO query(UserCarsForm form) {

        // 查询相关车辆ID
        List<CarRolePojo> carRelations = carDao.selectUserRelatedCar(form.getAutoIncreaseId());
        log.info("[query]carRelations.size:{}", carRelations.size());
        if (carRelations.isEmpty()) {
            return new UserCarsDTO();
        }
        // 全部车辆ID
        List<String> carIds = carRelations.stream().map(CarRolePojo::getCarId).distinct().collect(Collectors.toList());
        log.info("[query]carIds.size:{}", carIds.size());

        // 查询车辆基础信息
        List<CarInfoPojo> cars = carDao.selectByCarIdIn(carIds);
        log.info("[query]cars.size:{}", cars.size());
        List<String> chassisNumList = cars.stream().map(CarInfoPojo::getCarVin).collect(Collectors.toList());

        // 查询车辆最近一条创建的工单
        Map<String, WorkOrderInfoPojo> woMap = null;
        List<String> checkCarList = null;
        List<String> checkStationList = null;
        if (CollectionUtils.isNotEmpty(chassisNumList)) {
            // 查询协议车辆
            checkCarList = maintainVehicleTubeDao.queryCarVinListByVins(chassisNumList);
            if (StringUtils.isNotEmpty(form.getStationId())) {
                // 服务站信息查询
                ServiceStationInfoPojo station = serviceStationDao.queryServiceStationInfo(
                        DbName, form.getStationId(), ServiceStationVal.JOB_TYPE_ADMIN);
                List<String> areaCodeList = Arrays.asList(areaCodes.split(","));
                String areaCode = null != station && null != station.getPovince() ? String.valueOf(station.getPovince()) : null;
                if (StringUtils.isNotEmpty(areaCode) && CollectionUtils.isNotEmpty(areaCodeList) && areaCodeList.contains(areaCode)) {
                    List<WorkOrderInfoPojo> woList = workOrderDao.queryLatelyWorkOrderByVin(chassisNumList);
                    if (CollectionUtils.isNotEmpty(woList)) {
                        woMap = woList.stream().collect(Collectors.toMap(workOrder -> workOrder.getCarVin(), entity -> entity));
                    }
                }
                if (null != station && StringUtils.isNotEmpty(station.getServiceCode())) {
                    // 查询协议车辆与所选服务站是否匹配
                    checkStationList = maintainStationTubeDao.queryCarVinListByVinsAndServiceCode(chassisNumList, station.getServiceCode());
                }
            }
        }

        // 查询已经绑定的数据
        List<String> exclusiveCars = null;
        if (form.getExclusiveTeamId() != null) {
            exclusiveCars = getExclusiveCarIds(form.getExclusiveTeamId());
        }

        // 查询动态信息
        UserCarsDTO data = new UserCarsDTO();
        List<UserCarsDTO.CarInfo> carList;
        if (MapUtils.isEmpty(woMap)) {
            woMap = new HashMap<>();
        }
        if (CollectionUtils.isEmpty(checkCarList)) {
            checkCarList = new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(checkStationList)) {
            checkStationList = new ArrayList<>();
        }
        if (YES.equals(form.getStaticOnly())) {
            carList = pojoToDto(cars, exclusiveCars, woMap, checkCarList, checkStationList, form.getStationId());
        } else {
            Map<String, CarOnlineStatusDto> outMap = new HashMap<>();
            // 调用位置云
            HttpCommandResultWithData cloudResult = null;
            cloudResult = getRealTimeInfo(chassisNumList, outMap);

            if (cloudResult.getResultCode() != ECode.SUCCESS.code()) {
                ExceptionUtil.result(cloudResult.getResultCode(), cloudResult.getMessage());
            }
            // 补全位置信息
            carList = completeDynamicInfo(cars, outMap, exclusiveCars, woMap, checkCarList, checkStationList, form.getStationId());
        }

        // 筛选
        List<String> carIdKept = filterByRoleAndTeam(carRelations, form);
        Predicate<UserCarsDTO.CarInfo> filter = e -> carIdKept.contains(e.getCarId());
        if (StringUtils.isNotEmpty(form.getOnlineStatus())) {
            List<String> conditionStatus = Arrays.asList(form.getOnlineStatus().split(SEPARATOR));
            filter = filter.and(e -> e.getStatus() != null && conditionStatus.contains(e.getStatus().toString()));
        }
        if (StringUtils.isNotEmpty(form.getKeyword())) {
            filter = filter.and(e -> StringUtils.isNotEmpty(e.getCarNumber()) && e.getCarNumber().contains(form.getKeyword())
                    || StringUtils.isNotEmpty(e.getChassisNum()) && e.getChassisNum().contains(form.getKeyword()));
        }
        carList = carList.stream().filter(filter).collect(Collectors.toList());

        // 确定总里程、总在线数
        if (!YES.equals(form.getListOnly())) {
            List<Integer> onlineStatus = Arrays.asList(OnlineStatusEnum.OL_STS_STILL.code(), OnlineStatusEnum.OL_STS_MOVING.code());
            long onlineCount = carList.stream()
                    .filter(e -> onlineStatus.contains(e.getStatus()))
                    .count();
            data.setOnlineNum((int) onlineCount);
            double mileageSum = carList.stream()
                    .mapToDouble(e -> StringUtils.isEmpty(e.getMileage()) ? 0 : Double.valueOf(e.getMileage()))
                    .sum();
            data.setTotalMileage(MathUtil.scale(mileageSum, 1).toString());
        }
        // 分页
        if (YES.equals(form.getReturnAll())) {
            data.setTotal(carList.size());
            data.setPage_total(carList.isEmpty() ? 0 : 1);
            data.setList(carList);
        } else {
            PagingInfo<UserCarsDTO.CarInfo> pagingInfo = pageUtil.paging(carList, form.getPage_number(), form.getPage_size());
            data.setTotal(pagingInfo.getTotal());
            data.setPage_total(pagingInfo.getPage_total());
            data.setList(dataAddRole(pagingInfo.getList(), carRelations));
        }

        // 逆地理
        completeGeoPositionInfo(data.getList());

        // 交车单-是否有待添加车辆
        data.setDeliveryStatus(productDeliveryVehicleInfoDao.queryDeliveryCarListCount(form.getAutoIncreaseId()) > 0 ? true : false);
        // 24小时内创建过工单的提示文案
        data.setOrderStatusMessage(interceptMessage);
        log.info("userCars end return:{}", data);
        return data;
    }

    @Override
    public PagingInfo<MaintainItemListoDTO> queryMaintainItemList(QueryMaintainItemListForm form) {
        PagingInfo<MaintainItemListoDTO> pagePojo;
        pageUtil.getPage(form);
        Page<MaintainItemListoDTO> dtoList = maintainItemInfoDao.queryMaintainItemListForPage(form);
        pagePojo = pageUtil.convertPagingToPage(dtoList);
        log.info("queryMaintainItemList end return:{}", pagePojo);
        return pagePojo;
    }

    @Override
    public QueryMaintenanceItemListDTO queryMaintainItemInfo(QueryMaintenanceItemForm form) {
        log.info("CustomMaintainItemService---queryMaintainItemInfo:---begin-----");
        log.info("CustomMaintainItemService---queryMaintainItemInfo:请求参数:{}", form.toString());
        // 查询自定义保养项目列表
        //
        CustomMaintainInfoEntity customMaintainInfo = customMaintainInfoDao.selectByPrimaryKey(Long.parseLong(form.getMaintainId()));

        if (customMaintainInfo == null || customMaintainInfo.getStatus() == 0 || StringUtil.isNotEq(customMaintainInfo.getUserId(), form.getUserId())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "未发现此保养信息!");
        }

        // 保养项目列表
        List<CustomMaintainItemEntity> customMaintainItemList = queryItemListByMaintainId(customMaintainInfo.getId());
        if (customMaintainItemList == null || customMaintainItemList.size() == 0) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "未发现此保养项目列表！");
        }
        QueryMaintenanceItemListDTO listDto = new QueryMaintenanceItemListDTO();
        // 保养项目id列表查询名称

        List<MaintenanceItemEntity> maintainItemList =
                maintainItemInfoDao.queryMaintainItemList(customMaintainItemList
                        .stream()
                        .map(CustomMaintainItemEntity::getMaintainItemId).collect(Collectors.toList()))
                        .stream()
                        .map(maintainItemInfo -> {
                            MaintenanceItemEntity maintenanceItem = new MaintenanceItemEntity();
                            maintenanceItem.setMaintainItemId(maintainItemInfo.getMaintainItemId());
                            maintenanceItem.setMaintainItemName(maintainItemInfo.getMaintainItemName());
                            return maintenanceItem;
                        }).collect(Collectors.toList());

        listDto.setMaintainItemList(maintainItemList);

        log.info("queryMaintenanceItem end return:{}", listDto);
        return listDto;
    }


    @Autowired
    private FltCarOwnerMappingDao fltCarOwnerMappingMapper;
    @Autowired
    private IntegralService integralService;
    @Autowired
    private CommonCustomMaintainServiceImpl commonCustomMaintainService;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public HttpCommandResultWithData addByChassisNumber(UserCarAddForm form) throws Exception {
        log.info("[addByChassisNumber]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询车辆信息
        CarEntity car = carDao.selectByChassisNumber(form.getVin());
        if (car == null) {
            result.setResultCode(ECode.ADD_VEHICLE_NOT_FOUND.code());
            result.setMessage("系统不存在此车辆");
            return result;
        }
        form.setCarId(car.getId());

        // 校验车辆是否已经添加
        FltCarOwnerMapping fltCarOwnerMapping = fltCarOwnerMappingMapper.selectByCarIdAndUserId(form.getCarId(), form.getAutoIncreaseId());
        if (fltCarOwnerMapping != null) {
            result.setResultCode(ECode.ADD_VEHICLE_ADDED.code());
            result.setMessage("已添加此车辆");
            return result;
        }

        // 校验车辆是否已经被其他人添加
        List<FltCarOwnerMapping> fltCarOwnerMappingList = fltCarOwnerMappingMapper.selectByCarId(form.getCarId());
        if (CollectionUtils.isNotEmpty(fltCarOwnerMappingList)) {
            result.setResultCode(ECode.ADD_VEHICLE_OTHER_ADDED.code());
            result.setMessage("此车已经被其他人绑定");
            return result;
        }

        // 车与车主绑定关系，插入数据库
        int carBinged = insertRecord(form);

        if (carBinged < 1) {
            log.info("[----用户加车，添加积分----]Start");

            // 获取用户手机号
            String phone = userDao.findPhoneByUcid(form.getUserId());
            log.info("手机号为：{}", phone);
            if (phone == null) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("手机号获取失败");
                return result;
            }


            HttpCommandResultWithData integralResult = integralService.addAction(form.getUserId(), 2);

            /**
             * 活动期间添加积分 持续时间2021/09/20-2021/09/26
             * 2021/9/15 13:45 YZL
             */
            integralService.midAutumnByRuleId(form.getUserId(), 75);
            /**
             * 活动期间添加积分 持续时间2021/10/01-2021/10/07
             * 2021/9/15 13:45 YZL
             */
            integralService.FestivalByRuleId(form.getUserId(), 85, guoQingStart, guoQingEnd);


            /**
             * 针对最新版本用户，发放活动积分
             * 修改时间：2021/11/02
             * 双十一邀请活动发放积分：2021/11/08-2021/11/18
             */
            HttpCommandResultWithData resultWithData = zhixiaoClient.getByToPhone(phone);
            log.info("调用直销商城，获取直销商城返回数据：{}", resultWithData);
            if (resultWithData.getResultCode() == 200 && resultWithData.getData() != null) {
                try {
                    JSONObject formZhixiao = (JSONObject) JSONObject.toJSON(resultWithData.getData());
                    Integer type = Integer.parseInt(formZhixiao.getString("fromDriverType")) - 1;
                    String fromPhone = formZhixiao.getString("fromPhone");
                    String versionType = formZhixiao.getString("appSystemType");
                    String version = formZhixiao.getString("appVersion");
                    switch (versionType){
                        case "android":versionType = "1";break;
                        case "ios":versionType = "2";break;
                    }
                    AppVersion appVersion = new AppVersion();
                    log.info("APP类型：{},苹果/安卓：{},当前内部版本号：{}", type, versionType, version);
                    appVersion.setActionCode0(type.toString());
                    appVersion.setType(versionType);
                    appVersion.setVersion(version);
                    log.info("appVersion:{}", appVersion);
                    if (appVersionComponent.checkVersion(appVersion)) {
                    log.info("tyep: {},fromPhone: {}", type, fromPhone);
                    String ucid = userDao.ucIdByPhoneType(fromPhone, type.toString());
                    integralService.FestivalByRuleId(ucid, 95, shuang11Start, shuang11End);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
            /** *************************************** */



            if (integralResult.getResultCode() != ECode.SUCCESS.code()) {
                //result.fillResult(ECode.CLIENT_ERROR);//即便积分添加失败也继续接下来操作
                log.info("[----用户加车，添加积分----]ErrResult:" + JsonUtil.toJson(integralResult));

            } else {
                log.info("[----用户加车，添加积分----]Success");
            }
            log.info("[----用户加车，添加积分----]End");

            // 调用直销商城的数据
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phoneNumber", phone);
            jsonObject.put("appType", form.getAppType());
            log.info("Json:{}", jsonObject);
            try {
                HttpCommandResultWithData result1 = zhixiaoClient.bindSignal(jsonObject);
                if (result1.getResultCode() != 200) {
                    log.error("直销商城发放积分失败，error{}", result1.getMessage());
                }
            }catch (Exception e){
                log.error(e.getMessage());
            }


        } else {
            log.info("{}已被绑定，无法添加再次添加积分", form.getVin());
        }

        log.info("[addByChassisNumber]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData delete(UserCarDeleteForm form) {
        log.info("[delete]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        FltCarOwnerMapping existEntity = fltCarOwnerMappingMapper.selectByCarIdAndUserId(form.getCarId(), form.getAutoIncreaseId());
        if (existEntity != null) {
            fltCarOwnerMappingMapper.deleteByPrimaryKey(existEntity.getId());
            CarEntity entity = carDao.selectCar(existEntity.getCarId());
            if(entity != null){
                if(entity.getCarVin() != null){
                    authLogService.deleteAuthlog(entity.getCarVin());
                }
            }
        }
        // 异步更新自定义保养
        commonCustomMaintainService.updateUserCustomMaintain(form.getAutoIncreaseId());
        log.info("[delete]end");
        return result;
    }

//    @Override
//    public HttpCommandResultWithData getRealTimeInfo(List<String> chassisNumList, Map<String, CarOnlineStatusDto> outMap) throws JsonProcessingException {
//        HttpCommandResultWithData result = new HttpCommandResultWithData();
//        result.setResultCode(ECode.SUCCESS.code());
//        if (chassisNumList == null || chassisNumList.isEmpty()) {
//            return result;
//        }
//        GetOnlineStatusForm cloudCommand = new GetOnlineStatusForm();
//        cloudCommand.setVinList(chassisNumList);
//        cloudCommand.setSourceFlag(false);
//        log.info("[getRealTimeInfo]call getOnlineStatus start");
//        log.info("[getRealTimeInfo]param: {}", JsonUtil.toJson(cloudCommand));
//        HttpCommandResultWithData cloudResult = locationServiceClient.getOnlineStatus(cloudCommand);
//        log.info("[getRealTimeInfo]reutrn: {}", JsonUtil.toJson(cloudResult));
//        log.info("[getRealTimeInfo]call getOnlineStatus end");
//        if (cloudResult.getResultCode() == ECode.FALLBACK.code()) {
//            // feign降级处理
//            result.setResultCode(ECode.CLIENT_ERROR.code());
//            result.setMessage(ECode.FALLBACK.message());
//            return result;
//        } else if (cloudResult.getResultCode() != ECode.SUCCESS.code()) {
//            return cloudResult;
//        }
//
//        HttpCommandResultWithData cloudResult1 = locationServiceClient.getOnlineStatus(cloudCommand);
//        if (cloudResult1.getData() != null && outMap != null) {
//            Map<String, Object> map = null;
//            try {
//                map = JsonUtil.toMap(JsonUtil.toJson(cloudResult1.getData()));
//            } catch (IOException e) {
//                log.error(e.getMessage(), e);
//            }
//            if (map != null) {
//                for (Map.Entry<String, Object> entry : map.entrySet()) {
//                    CarOnlineStatusDto carOnlineStatusDto = null;
//                    try {
//                        carOnlineStatusDto = JsonUtil.fromJson(JsonUtil.toJson(entry.getValue()), CarOnlineStatusDto.class);
//                    } catch (JsonProcessingException e) {
//                        log.error(e.getMessage(), e);
//                    }
//                    if (carOnlineStatusDto == null) {
//                        continue;
//                    }
//                    outMap.put(entry.getKey(), carOnlineStatusDto);
//                }
//            }
//        }
//        log.info("[getRealTimeInfo]end");
//        return result;
//    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int insertRecord(UserCarAddForm form) {
        log.info("[insertRecord]start");
        FltCarOwnerMapping insRecord = new FltCarOwnerMapping();
        insRecord.setCarId(form.getCarId());
        insRecord.setUserId(form.getAutoIncreaseId());
        insRecord.setCreateType(AppTypeEnum.APP_C.code());
        Date now = new Date();
        insRecord.setCreateTime(now);
        insRecord.setUpdateTime(now);
        fltCarOwnerMappingMapper.insertSelective(insRecord);

        int carBinged = carDao.queryCarBinding(form.getVin());
        log.info("[insertRecord]end");
        Map map = new HashMap();
        map.put("carVin", form.getVin());
        carDao.insertCarBinding(map);
        return carBinged;

    }

    /**
     * 类型转换
     */
    private List<UserCarsDTO.CarInfo> pojoToDto(List<CarInfoPojo> pojos, List<String> exclusiveCars,
                                                Map<String, WorkOrderInfoPojo> woMap, List<String> checkCarList, List<String> checkStationList, String stationId) {


        return pojos.stream().map(e -> pojoToDto(e, exclusiveCars, woMap.get(e.getCarVin()), checkCarList, checkStationList, stationId)).collect(Collectors.toList());
    }

    /**
     * 类型转换
     */
    private UserCarsDTO.CarInfo pojoToDto(CarInfoPojo pojo, List<String> exclusiveCars,
                                          WorkOrderInfoPojo woPojo, List<String> checkCarList, List<String> checkStationList, String stationId) {
        UserCarsDTO.CarInfo dto = new UserCarsDTO.CarInfo();
        dto.setCarId(pojo.getCarId());
        dto.setCarNumber(pojo.getCarNumber());
        dto.setChassisNum(pojo.getCarVin());
        if (exclusiveCars != null) {
            if (exclusiveCars.contains(pojo.getCarId())) {
                dto.setBound(Y);
            } else {
                dto.setBound(N);
            }
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(pojo.getAutoTerminal())) {
            dto.setHasNetwork("1");
        } else {
            dto.setHasNetwork("0");
        }
        if (pojo.getExtInfoOk() == null || pojo.getExtInfoOk() != 1) {
            dto.setExtInfoOk("0");
        } else {
            dto.setExtInfoOk("1");
        }


        dto.setOrderStatus(false);
        // 判断24小时内车辆是否创建过工单
        if (null != woPojo && null != woPojo.getCreateTime() && (System.currentTimeMillis() - woPojo.getCreateTime().getTime()) / 1000 < interceptHours * 3600) {
            dto.setOrderStatus(true);
        }
        //新加增添车辆是否可以在所选服务站建单v2.4.2后添加的字段  ==========开始
        //是否可以创建工单
        dto.setCreateOrderStatus(true);
        //是否为协议车辆
        if (checkCarList.contains(pojo.getCarVin())) {
            dto.setAgreementCar(true);
            if (StringUtils.isNotEmpty(stationId) && !checkStationList.contains(pojo.getCarVin())) {
                //协议车辆和协议服务站不对应，不能创建工单
                dto.setCreateOrderStatus(false);
            }
        } else {
            dto.setAgreementCar(false);
        }
        //新加增添车辆是否可以在所选服务站建单v2.4.2后添加的字段  ==========结束
        return dto;
    }

    /**
     * 查询已经与车队绑定的车辆
     */
    private List<String> getExclusiveCarIds(Long teamId) {
        List<FltFleetCarMappingEntity> queryList = fltFleetCarMappingDao.selectByTeamId(teamId);
        return queryList.stream().map(FltFleetCarMappingEntity::getCarId).collect(Collectors.toList());
    }

    /**
     * 调用位置云接口查询车辆状态
     */
    @Override
    public HttpCommandResultWithData getRealTimeInfo(List<String> chassisNumList, Map<String, CarOnlineStatusDto> outMap) {

        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        if (chassisNumList == null || chassisNumList.isEmpty()) {
            return result;
        }
        GetOnlineStatusForm cloudCommand = new GetOnlineStatusForm();
        cloudCommand.setVinList(chassisNumList);
        cloudCommand.setSourceFlag(false);

        HttpCommandResultWithData cloudResult = locationServiceClient.getOnlineStatus(cloudCommand);
        if (cloudResult.getData() != null && outMap != null) {
            Map<String, Object> map = null;
            try {
                map = JsonUtil.toMap(JsonUtil.toJson(cloudResult.getData()));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            if (map != null) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    CarOnlineStatusDto carOnlineStatusDTO = null;
                    try {
                        carOnlineStatusDTO = JsonUtil.fromJson(JsonUtil.toJson(entry.getValue()), CarOnlineStatusDto.class);
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                    }
                    if (carOnlineStatusDTO == null) {
                        continue;
                    }
                    outMap.put(entry.getKey(), carOnlineStatusDTO);
                }
            }
        }
        log.info("[getRealTimeInfo]end");
        return result;
    }

    /**
     * @Description：${todo}
     * @author YZL
     * @data 2021/6/29 10:16
     */
    @Override
    public Car findByChassisNumber(String classisNumber) {
        Car car = carDao.selectByChassisNumber2(classisNumber);
        if (car != null) {
            car.setCarBrand(carDao.findBrandNameById(car.getCarBrand()));
        }
        log.info("userCarInfo end return:{}", car);
        return car;
    }

    @Override
    public UserCarDetailDto detail(UserCarDetailForm form) {
        // 查询车辆基础信息
        CarEntity car = carDao.selectByPrimaryKey(form.getCarId());
        if (car == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "未查询到车辆信息");
        }
        UserCarDetailDto data = new UserCarDetailDto();
        data.setCarNumber(car.getCarNumber());
        if (org.apache.commons.lang.StringUtils.isNotBlank(car.getAutoTerminal())) {
            data.setHasNetwork("1");
        } else {
            data.setHasNetwork("0");
        }
        data.setChassisNum(car.getCarVin());

        // 查询动态信息
        Map<String, CarLocationOutputDto> outMap = new HashMap<>(16);
        HttpCommandResultWithData cloudResult = getCarLocationInfo(Collections.singletonList(car.getCarVin()), outMap);
        if (cloudResult.getResultCode() != ECode.SUCCESS.code()) {
            ExceptionUtil.result(cloudResult.getResultCode(), cloudResult.getMessage());
        }
        // 查询车辆在线状态
        Map<String, CarOnlineStatusDto> outOnlineMap = new HashMap<>(16);
        cloudResult = getRealTimeInfo(Collections.singletonList(car.getCarVin()), outOnlineMap);
        if (cloudResult.getResultCode() != ECode.SUCCESS.code()) {
            ExceptionUtil.result(cloudResult.getResultCode(), cloudResult.getMessage());
        }
        completeDynamicInfo(data, outMap, outOnlineMap);
        if (StringUtils.isNotBlank(car.getCarSeriesName())) {
            data.setCarSeriesName(car.getCarSeriesName());
        } else {
            data.setCarSeriesName("未知");
        }

        if (YES.equals(form.getRealtimeFlag())) {
            log.info("[detail]real time info only");
            return data;
        }

        // 查询相关用户
        List<CarRolePojo> relatedUsers = carDao.selectCarRelatedUserByCarId(form.getCarId());
        if (relatedUsers.stream().anyMatch(e -> e.getUserId().equals(form.getAutoIncreaseId())
                && e.getRole().equals(FleetRoleEnum.OWNER.code()))) {
            data.setRole(FleetRoleEnum.OWNER.code());
        } else {
            Integer role = relatedUsers.stream()
                    .filter(e -> e.getUserId().equals(form.getAutoIncreaseId()))
                    .map(CarRolePojo::getRole)
                    .max(Comparator.comparingInt(FleetRoleEnum::getWeight))
                    .orElse(null);
            data.setRole(role);
        }

        // 查询车队信息
        List<Long> teamIds = relatedUsers.stream()
                .filter(e -> e.getTeamId() != null && e.getUserId().equals(form.getAutoIncreaseId()))
                .map(CarRolePojo::getTeamId)
                .distinct()
                .collect(Collectors.toList());
        List<UserCarDetailDto.TeamInfo> teamList = new ArrayList<>();
        if (teamIds.size() > 0) {
            List<FltFleet> teams = fltFleetDao.selectByIdIn(teamIds);
            for (FltFleet t : teams) {
                UserCarDetailDto.TeamInfo team = new UserCarDetailDto.TeamInfo();
                team.setTeamId(t.getId());
                team.setName(t.getName());
                String role = relatedUsers.stream()
                        .filter(e -> t.getId().equals(e.getTeamId()) && e.getUserId().equals(form.getAutoIncreaseId()))
                        .map(e -> String.valueOf(e.getRole()))
                        .collect(Collectors.joining(SEPARATOR));
                team.setRole(role);
                teamList.add(team);
            }
        }
        data.setTeamList(teamList);

        // 查询交车单ID
        Map<String, Object> param = new HashedMap(2);
        param.put("carId", form.getCarId());
        param.put("userId", form.getAutoIncreaseId());
        data.setDeliveryId(productDeliveryVehicleInfoDao.queryDeliveryIdByCarIdAndUserId(param));

        return data;

    }

    @Override
    public CarParmDto getCarParmInfo(QueryCarParmForm form) {
        CarParmDto dto = carDao.queryCarParm(form);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CarBasicInfoEntity getCarBasicInfo(CarBasicInfoForm form) {
        Map<String, Object> param = new HashMap<>();
        param.put("carVin", form.getCarVin());
        log.info("getCarBasicInfo 调用tsp参数param：{}", param);
        CarBasicInfoEntity carBasicInfo = tspBusinessClient.getCarBasicInfo(param).getData();
        if (Objects.isNull(carBasicInfo)) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "暂无当前车辆信息");
        }
        carBasicInfo.setOrderNo(UUID.randomUUID().toString().replaceAll("-", ""));
        AuthLogEntity entity = new AuthLogEntity().setCarVin(form.getCarVin()).setOrderNo(carBasicInfo.getOrderNo()).setCreateTime(new Date()).setUpdateTime(new Date());
        authLogService.saveOrUpdate(entity, new UpdateWrapper<AuthLogEntity>().eq("car_vin", entity.getCarVin()));
        log.info("getCarBasicInfo 对接tsp获取车辆基础信息回调：{}", carBasicInfo);
        return carBasicInfo;
    }

    /**
     * 补全动态信息
     */
    private void completeDynamicInfo(UserCarDetailDto data, Map<String, CarLocationOutputDto> map, Map<String, CarOnlineStatusDto> outOnlineMap) {
        if (map.get(data.getChassisNum()) != null) {
            CarLocationOutputDto carLocationOutputDto = map.get(data.getChassisNum());
            // 车速
            if (carLocationOutputDto.getSpeed() != null) {
                data.setSpeed(carLocationOutputDto.getSpeed().toString());
            }
            // 发动机转速:单位转/分
            if (carLocationOutputDto.getRotation() != null) {
                data.setRotation(carLocationOutputDto.getRotation().toString());
            }
            // 百公里油耗
            if (carLocationOutputDto.getAvgOil() != null) {
                data.setAvgOil(carLocationOutputDto.getAvgOil());
            }
            // 总里程
            if (carLocationOutputDto.getMileageEngine() != null) {
                data.setMileageEngine(carLocationOutputDto.getMileageEngine());
            }
            // 尿素箱液位
            if (carLocationOutputDto.getUreaLevel() != null) {
                BigDecimal b = new BigDecimal(carLocationOutputDto.getUreaLevel() / 100);
                double df = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                data.setUreaLevel(df);
            }

            // 油箱液位:单位%
            if (carLocationOutputDto.getOilValue() != null) {
                data.setFuelPercent(carLocationOutputDto.getOilValue().toString());
            }
            // 水温:单位摄氏度
            if (carLocationOutputDto.getCoolingWaterTem() != null) {
                data.setCoolingWaterTem(carLocationOutputDto.getCoolingWaterTem().toString());
            }
            // 机油压力:单位千帕
            if (carLocationOutputDto.getOilPressure() != null) {
                data.setOilPressure(carLocationOutputDto.getOilPressure().toString());
            }
            // 环境温度:单位摄氏度
            if (carLocationOutputDto.getAtmosphericTem() != null) {
                data.setAtmosphericTem(carLocationOutputDto.getAtmosphericTem().toString());
            }
            // 位置
            if (carLocationOutputDto.getLon() != null) {
                data.setLon(carLocationOutputDto.getLon().toString());
            }
            if (carLocationOutputDto.getLat() != null) {
                data.setLat(carLocationOutputDto.getLat().toString());
            }
            if (StringUtil.isNotEmpty(data.getLon()) && StringUtil.isNotEmpty(data.getLat())) {
                data.setLocation(queryGeographicalService.getPosition(data.getLat(), data.getLon()));
            }
        }
        // 当车辆状态为静止时，速度必须为0，当车辆状态为离线、断连时，车速、转速必须为0；
        if (outOnlineMap.get(data.getChassisNum()) != null) {
            CarOnlineStatusDto carOnlineStatusDto = outOnlineMap.get(data.getChassisNum());
            Integer onlineStatus = carOnlineStatusDto.getOnLineStatus();
            data.setOnlineStatus(onlineStatus);
            if (onlineStatus != null && data.getSpeed() == null) {
                if (onlineStatus != OnlineStatusEnum.OL_STS_MOVING.code()) {
                    data.setSpeed("0");
                }
                if (onlineStatus == OnlineStatusEnum.OL_STS_OFFLINE.code() || onlineStatus == OnlineStatusEnum.OL_STS_ABSENT.code()) {
                    data.setRotation("0");
                }
            }
            // 今日里程
            if (carOnlineStatusDto.getDayMil() != null) {
                data.setMileage(carOnlineStatusDto.getDayMil().toString());
            }
            // 总里程
            /*if (carOnlineStatusDto.getStandardMileage() != null) {
                data.setTotalMileage(carOnlineStatusDto.getStandardMileage().toString());
            }*/
        }
    }

    /**
     * 补全动态信息
     */
    private List<UserCarsDTO.CarInfo> completeDynamicInfo(List<CarInfoPojo> cars, Map<String, CarOnlineStatusDto> map, List<String> exclusiveCars,
                                                          Map<String, WorkOrderInfoPojo> woMap, List<String> checkCarList, List<String> checkStationList, String stationId) {
        log.info("[completeDynamicInfo]start");
        List<UserCarsDTO.CarInfo> carList = new ArrayList<>();
        for (CarInfoPojo car : cars) {
            UserCarsDTO.CarInfo dto = pojoToDto(car, exclusiveCars, woMap.get(car.getCarVin()), checkCarList, checkStationList, stationId);
            if (map.get(car.getCarVin()) != null) {
                CarOnlineStatusDto carOnlineStatusDto = map.get(car.getCarVin());
                // 车辆状态
                if (carOnlineStatusDto.getOnLineStatus() != null) {
                    dto.setStatus(carOnlineStatusDto.getOnLineStatus());
                } else {
                    dto.setStatus(OnlineStatusEnum.OL_STS_OFFLINE.code());
                }
                // 今日里程
                if (carOnlineStatusDto.getDayMil() != null) {
                    dto.setMileage(carOnlineStatusDto.getDayMil().toString());
                }

                // 总里程
                if (carOnlineStatusDto.getStandardMileage() != null) {
                    dto.setTotalMileage(carOnlineStatusDto.getStandardMileage().toString());
                }

//                //总里程20210128添加
//                if (carOnlineStatusDto.getMileageEngine() != null) {
//                    dto.setTotalMileage(carOnlineStatusDto.getMileageEngine().toString());
//                }
                // 当前速度
                /*if (dto.getStatus() != OnlineStatusEnum.OL_STS_MOVING.code() || carOnlineStatusDto.getSpeed() == null) {
                    dto.setSpeed("0");
                } else {
                    dto.setSpeed(carOnlineStatusDto.getSpeed().toString());
                }*/
                if (carOnlineStatusDto.getSpeed() != null) {
                    dto.setSpeed(carOnlineStatusDto.getSpeed().toString());
                }
                // 位置
                dto.setLon(carOnlineStatusDto.getLon());
                dto.setLat(carOnlineStatusDto.getLat());
                // 逆地理信息在筛选分页之后再补全
            } else {
                // 车辆状态是筛选条件之一，从位置云获取不到车辆数据时需设默认值
                dto.setStatus(OnlineStatusEnum.OL_STS_OFFLINE.code());
                dto.setSpeed("0");
            }
            carList.add(dto);
        }
        log.info("[completeDynamicInfo]end");
        return carList;
    }


    /**
     * 管理角色、车队初步筛选
     */
    private List<String> filterByRoleAndTeam(List<CarRolePojo> carRelations, UserCarsForm form) {
        log.info("[filterByRoleAndTeam]start");
        if (StringUtils.isNotEmpty(form.getRole())) {
            List<String> conditionRoles = Arrays.asList(form.getRole().split(SEPARATOR));
            carRelations = carRelations.stream()
                    .filter(e -> conditionRoles.contains(String.valueOf(e.getRole())))
                    .collect(Collectors.toList());
        }
        if (StringUtils.isNotEmpty(form.getTeam())) {
            List<String> conditionTeams = Arrays.asList(form.getTeam().split(SEPARATOR));
            carRelations = carRelations.stream()
                    .filter(e -> e.getTeamId() != null && conditionTeams.contains(e.getTeamId().toString()))
                    .collect(Collectors.toList());
        }
        // 初步筛选后的车辆ID去重
        log.info("[filterByRoleAndTeam]end");
        return carRelations.stream().map(CarRolePojo::getCarId).distinct().collect(Collectors.toList());
    }

    private List<UserCarsDTO.CarInfo> dataAddRole(List<UserCarsDTO.CarInfo> carInfos, List<CarRolePojo> carRelations) {
        Map<String, Integer> carIdRoleMap = new HashMap<>();
        for (CarRolePojo carRolePojo : carRelations) {
            carIdRoleMap.put(carRolePojo.getCarId(), carRolePojo.getRole());
        }
        for (UserCarsDTO.CarInfo carInfos1 : carInfos) {
            carInfos1.setRole(carIdRoleMap.get(carInfos1.getCarId()));
        }
        return carInfos;
    }


    /**
     * 补全逆地理信息
     */
    private void completeGeoPositionInfo(List<UserCarsDTO.CarInfo> carList) {
        log.info("[completeGeoPositionInfo]start");
        if (carList != null) {
            for (UserCarsDTO.CarInfo dto : carList) {
                if (StringUtils.isNotEmpty(dto.getLon()) && StringUtils.isNotEmpty(dto.getLat())) {
                    dto.setLocation(queryGeographicalService.getPosition(dto.getLat(), dto.getLon()));
                }
            }
        }
        log.info("[completeGeoPositionInfo]end");
    }


    protected List<CustomMaintainItemEntity> queryItemListByMaintainId(Long maintainId) {
        return customMaintainItemDao.selectItemListByMaintainId(maintainId);
    }


    /**
     * 调用位置云接口查询末次位置
     */
    public HttpCommandResultWithData getCarLocationInfo(List<String> chassisNumList, Map<String, CarLocationOutputDto> outMap) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());

        if (chassisNumList == null || chassisNumList.isEmpty()) {
            return result;
        }
        GetCarLocationForm cloudCommand = new GetCarLocationForm();
        cloudCommand.setVins(String.join(SEPARATOR, chassisNumList));
        HttpCommandResultWithData cloudResult = locationServiceClient.carLocation(cloudCommand);
        if (cloudResult.getResultCode() == ECode.FALLBACK.code()) {
            // feign降级处理
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(ECode.FALLBACK.message());
            return result;
        } else if (cloudResult.getResultCode() != ECode.SUCCESS.code()) {
            return cloudResult;
        }
        if (cloudResult.getData() != null && outMap != null) {
            Map<String, Object> map = null;
            try {
                map = JsonUtil.toMap(JsonUtil.toJson(cloudResult.getData()));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            if (map != null) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    CarLocationOutputDto carLocationOutputDto = null;
                    try {
                        carLocationOutputDto = JsonUtil.fromJson(JsonUtil.toJson(entry.getValue()), CarLocationOutputDto.class);
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                    }
                    if (carLocationOutputDto == null) {
                        continue;
                    }
                    outMap.put(entry.getKey(), carLocationOutputDto);
                }
            }
        }
        return result;
    }
}
