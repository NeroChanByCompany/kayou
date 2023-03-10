package com.nut.driver.app.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.nut.app.driver.dto.MyOrderDetailDto;
import com.nut.driver.app.form.MyOrderDetailForm;
import com.nut.driver.app.domain.Car;
import com.nut.driver.app.entity.CarEntity;
import com.nut.driver.app.pojo.MyOrderDetailPojo;
import com.nut.common.constant.OperateCodeEnum;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.client.TruckingTeamClient;
import com.nut.driver.app.dao.*;
import com.nut.driver.app.dto.CarDto;
import com.nut.driver.app.dto.MyOrdersDto;
import com.nut.driver.app.entity.MaintainVehicleTubeEntity;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.form.GetDriverCarsForm;
import com.nut.driver.app.form.MyOrdersForm;
import com.nut.driver.app.pojo.ServiceStationDetailPojo;
import com.nut.driver.app.service.MyOrderService;
import com.nut.driver.common.utils.PageUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.app.driver.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-23 20:07
 * @Version: 1.0
 */
@Slf4j
@Service
public class MyOrderServiceImpl extends DriverBaseService implements MyOrderService {

    @Autowired
    private WorkOrderDao workOrderDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private WorkOrderOperateDao workOrderOperateDao;

    @Autowired
    private MaintainVehicleTubeDao maintainVehicleTubeDao;

    @Autowired
    private CarDao carDao;

    @Autowired
    private TruckingTeamClient truckingTeamClient;

    @Autowired
    private PageUtil pageUtil;

    @Value("${database_name}")
    private String DbName;

    @Autowired
    private ServiceStationDao serviceStationDao;

    @Override
    @SneakyThrows
    public Map myOrders(MyOrdersForm form) {
        UserEntity user = userDao.selectByPrimaryKey(form.getAutoIncreaseId());
        if (user == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "????????????????????????");
        }
        int woStatus = 0;
        if (StringUtil.isNotEmpty(form.getQueryTag())) {
            // ????????????????????????
            if (form.getQueryTag().equals("1")){
                woStatus = ServiceStationEnum.WORK_DONE.code();
            }else if (form.getQueryTag().equals("2")){
                // ????????????????????????==?????????????????????2.4.2??????????????????
                woStatus = ServiceStationEnum.TO_AUDIT.code();
            }else {
                woStatus = 0;
            }
        }
        // ???????????????????????????????????????
        List<String> chassisNums = new ArrayList<>();
        // ?????????????????????????????????????????????????????? ??? ????????? ?????????????????????
        List<CarDto> carDtos = callGetDriverCars(form.getAutoIncreaseId());
        if (carDtos != null && !carDtos.isEmpty()) {
            chassisNums = carDtos.stream().map(CarDto::getVin).collect(Collectors.toList());
        }

        //???token????????????????????????????????????????????????????????????maintain_vehicle_tube????????????????????????????????????
        // ???token??????????????????????????????
        //?????????????????? ------------
        List<MaintainVehicleTubeEntity> maintainVehicleTubeList = maintainVehicleTubeDao.queryMaintainVehicleTubeListByMobile(user.getPhone());
        for (MaintainVehicleTubeEntity tube:maintainVehicleTubeList){
            chassisNums.add(tube.getChassisNo());
        }
        //?????????????????? --------------
        Page<MyOrdersDto> page = getMyOrdersDtos(form, user, woStatus, chassisNums);
        if (page.getResult() != null && page.getResult().size() > 0) {
            // ??????????????????
            for (MyOrdersDto dto : page.getResult()) {
                dto.setStatus(ServiceStationEnum.getAppMessage(dto.getWoStatus()));
            }
        }
        //???????????????
        Long evaluateNum = workOrderDao.queryEvaluateNumORauditNum(form.getAutoIncreaseId(), user.getPhone(), ServiceStationEnum.WORK_DONE.code(), chassisNums);
        //???????????????
        Long auditNum = workOrderDao.queryEvaluateNumORauditNum(form.getAutoIncreaseId(), user.getPhone(), ServiceStationEnum.TO_AUDIT.code(), chassisNums);
        Map map = new HashMap(5);
        map.put("evaluateNum", evaluateNum);
        map.put("auditNum", auditNum);
        map.put("total", page.getTotal());
        map.put("page_total", page.getPages());
        map.put("list", page.getResult());
        log.info("myOrders end return:{}",map);
        return map;
    }

    @Override
    public HttpCommandResultWithData myOrderDetail(MyOrderDetailForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        MyOrderDetailPojo pojo = workOrderDao.queryWorkOrderDetail(form.getWoCode());
        ServiceStationDetailPojo stationPojo = serviceStationDao.getServiceStationDetail(DbName, pojo.getStationId());
        MyOrderDetailDto dto = null;
        if (pojo != null) {
            dto = new MyOrderDetailDto();
            dto.setWoCode(pojo.getWoCode());
            dto.setCarLocation(pojo.getCarLocation());
            dto.setCarLon(pojo.getCarLon());
            dto.setCarLat(pojo.getCarLat());
            dto.setStationId(pojo.getStationId());
            dto.setStationName(pojo.getStationName());
            // ???????????????????????????
            dto.setIsReviewer(0);
            dto.setIsAppoUser(0);
            UserEntity user = userDao.selectByPrimaryKey(form.getAutoIncreaseId());
            if (user != null && StringUtil.isEq(user.getPhone(), pojo.getSendToRepairPhone())) {
                dto.setIsReviewer(1);
            }
            if (user != null && StringUtil.isEq(user.getPhone(), pojo.getPhone())) {
                dto.setIsAppoUser(1);
            }
            dto.setName(pojo.getName());
            dto.setPhone(pojo.getPhone());
            dto.setOrderTime(pojo.getOrderTime());
            dto.setCreateTime(pojo.getCreateTime());
            dto.setWoType(pojo.getWoType());
            dto.setMaintenanceItem(pojo.getMaintenanceItem());
            dto.setRepairItem(pojo.getRepairItem());
            dto.setFeedback(pojo.getFeedback());
            dto.setFiles(pojo.getFiles());
            dto.setRateStatus(pojo.getRateStatus());
            dto.setVin(pojo.getVin());
            dto.setCarNumber(pojo.getCarNumber());
            dto.setTeamId(pojo.getTeamId());
            dto.setTeamName(pojo.getTeamName());
            dto.setDescription(pojo.getDescription());
            dto.setCarType(pojo.getCarType());
            if (pojo.getWoStatus() != null) {
                int status = pojo.getWoStatus();
                dto.setWoStatus(status);
                String defaultReason = "??????????????????????????????????????????????????????";
                if (status == ServiceStationEnum.CLOSE_INSPECT.code() || status == ServiceStationEnum.CLOSE_REPAIR.code()
                        || status == ServiceStationEnum.CLOSE_TAKEOFF.code()
                        || status == ServiceStationEnum.CLOSE_RECEIVE.code()) {
                    // ????????????
                    dto.setReason(StringUtil.isNotEmpty(pojo.getCloseReason()) ? pojo.getCloseReason() : defaultReason);
                } else if (status == ServiceStationEnum.CLOSE_REFUSED.code()) {
                    // ????????????
                    dto.setReason(StringUtil.isNotEmpty(pojo.getRefuseReason()) ? pojo.getRefuseReason() : defaultReason);
                } else if (status == ServiceStationEnum.CLOSE_RESCUE.code()) {
                    // ??????????????????
                    String abortRescueReason = workOrderOperateDao.queryCancelRescueReason(form.getWoCode(), OperateCodeEnum.OP_ABORT_RESCUE.code());
                    dto.setReason(StringUtil.isNotEmpty(abortRescueReason) ? abortRescueReason : defaultReason);
                } else if (status == ServiceStationEnum.CANCEL_ORDER.code()) {
                    // ??????????????????
                    dto.setReason(StringUtil.isNotEmpty(pojo.getCancelReason()) ? pojo.getCancelReason() : "?????????????????????");
                }
            }
            /*** ????????????????????????----??????*/
            //??????????????????
            dto.setWoStatusValue(ServiceStationEnum.getMessage(dto.getWoStatus()));
            //????????????
            if (pojo.getRejectRemarks() != null && !pojo.getRejectRemarks().equals("")) {
                dto.setRejectRemarks(pojo.getRejectRemarks());
            }
            MaintainVehicleTubeEntity tube = maintainVehicleTubeDao.getInfoByClassisNo(pojo.getVin());
            if (tube != null) {
                //???????????? ???????????????????????????????????????????????????
                if (user.getPhone().equals(tube.getMobile())) {
                    dto.setIsAuditer(1);
                    dto.setAuditPhone(pojo.getPhone());
                } else {
                    //???????????????????????????????????????
                    dto.setIsAuditer(0);
                    dto.setAuditPhone(tube.getMobile());
                }
            } else {
                dto.setIsAuditer(2);
                dto.setAuditPhone(user.getPhone());
            }
            dto.setAuditPhone(stationPojo.getPhone());
            /*** ????????????????????????----??????*/
        }
        result.setData(dto);
        result.setResultCode(ECode.SUCCESS.code());
        return result;
    }

    /**
     * ?????????????????????
     */
    private List<CarDto> callGetDriverCars(Long driverId) {
        GetDriverCarsForm com = new GetDriverCarsForm();
        com.setDriverId(driverId);
        com.setReturnAll("1");
        List<CarDto> ret = null;
        try {
            HttpCommandResultWithData result = truckingTeamClient.getDriverCars(com);
            PagingInfo pagingInfo = null;
            if (result != null) {
                pagingInfo = JsonUtil.fromJson(JsonUtil.toJson(result.getData()), PagingInfo.class);
            }
            if (pagingInfo != null) {
                ret = JsonUtil.toList(JsonUtil.toJson(pagingInfo.getList()), CarDto.class);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ret;
    }

    private Page<MyOrdersDto> getMyOrdersDtos(MyOrdersForm form, UserEntity user, int woStatus, List<String> chassisNums) {
        pageUtil.getPage(form);
        Page<MyOrdersDto> myOrdersDtos = workOrderDao.queryWorkOrderList(form.getAutoIncreaseId(), user.getPhone(), woStatus, chassisNums);
        Set<String> vins = new HashSet<>();
        for (MyOrdersDto ordersDto1 : myOrdersDtos) {
            vins.add(ordersDto1.getVin());
        }

        Map<String, Car> map = getSaleDateByVin(vins);

        Page<MyOrdersDto> result = new Page<>();
        for (MyOrdersDto ordersDto : myOrdersDtos) {
            if (!map.containsKey(ordersDto.getVin())) {
                continue;
            }
            if (isDisplay(ordersDto.getOrderTime(), map.get(ordersDto.getVin()).getSalesDate())) {
                result.add(ordersDto);
            }
        }

        result.setTotal(result.size());
        result.setPages(myOrdersDtos.getPages());
        return result;
    }

    private Map<String, Car> getSaleDateByVin(Set<String> vins) {
        if (vins == null || vins.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Car> result = new HashMap<>();
        List<Car> carInfoPojoList = carDao.queryCarNumberByCarVinList(vins);
        for (Car car : carInfoPojoList) {
            if (car.getMbSalesStatus() == null || car.getMbSalesStatus() != 1) {
                // 1 ???????????????
                log.warn("?????????????????????????????????????????????????????????????????????, vin: {}, ????????????: {}", car.getCarVin(), car.getSalesStatus());
                continue;
            }
            if (car.getSalesDate() == null) {
                List<Date> mappingDate = carDao.findMappingDateByCarId(car.getId());
                if (mappingDate == null || mappingDate.isEmpty() || mappingDate.get(0) == null) {
                    log.warn("?????????????????????????????????????????????????????????????????????????????????????????????, vin: {}, ????????????: {}", car.getCarVin(), car.getSalesStatus());
                    continue;
                } else {
                    car.setSalesDate(mappingDate.get(0));
                }
            }
            result.put(car.getCarVin(), car);
        }
        return result;
    }

    private boolean isDisplay(String orderTime, Date saleDate) {
        if (saleDate == null) {
            return false;
        }

        try {
            Date orderDate = DateUtils.parseDate(orderTime, "yyyy-MM-dd HH:mm:ss");
            return orderDate.after(saleDate);
        } catch (Exception ex) {
            log.error("?????????????????????????????????", ex);
        }
        return false;
    }

}
