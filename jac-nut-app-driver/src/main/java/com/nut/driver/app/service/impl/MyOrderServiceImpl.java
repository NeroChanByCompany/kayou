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
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "未查询到用户信息");
        }
        int woStatus = 0;
        if (StringUtil.isNotEmpty(form.getQueryTag())) {
            // 只查询待评价工单
            if (form.getQueryTag().equals("1")){
                woStatus = ServiceStationEnum.WORK_DONE.code();
            }else if (form.getQueryTag().equals("2")){
                // 只查询待审核工单==新加的逻辑：在2.4.2后添加的逻辑
                woStatus = ServiceStationEnum.TO_AUDIT.code();
            }else {
                woStatus = 0;
            }
        }
        // 根据底盘号列表和预约人查询
        List<String> chassisNums = new ArrayList<>();
        // 车辆绑定的司机，车辆所在车队的管理员 和 预约人 能看到预约工单
        List<CarDto> carDtos = callGetDriverCars(form.getAutoIncreaseId());
        if (carDtos != null && !carDtos.isEmpty()) {
            chassisNums = carDtos.stream().map(CarDto::getVin).collect(Collectors.toList());
        }

        //当token是车管账号时除了原有逻辑查询外，还要查出maintain_vehicle_tube表关联的车辆所创建的工单
        // 当token是普通账号时逻辑不变
        //新增逻辑开始 ------------
        List<MaintainVehicleTubeEntity> maintainVehicleTubeList = maintainVehicleTubeDao.queryMaintainVehicleTubeListByMobile(user.getPhone());
        for (MaintainVehicleTubeEntity tube:maintainVehicleTubeList){
            chassisNums.add(tube.getChassisNo());
        }
        //新增逻辑结束 --------------
        Page<MyOrdersDto> page = getMyOrdersDtos(form, user, woStatus, chassisNums);
        if (page.getResult() != null && page.getResult().size() > 0) {
            // 进行状态转换
            for (MyOrdersDto dto : page.getResult()) {
                dto.setStatus(ServiceStationEnum.getAppMessage(dto.getWoStatus()));
            }
        }
        //待评价数量
        Long evaluateNum = workOrderDao.queryEvaluateNumORauditNum(form.getAutoIncreaseId(), user.getPhone(), ServiceStationEnum.WORK_DONE.code(), chassisNums);
        //待审核数量
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
            // 当前用户是否可评价
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
                String defaultReason = "服务站已取消预约，请更换其他服务站。";
                if (status == ServiceStationEnum.CLOSE_INSPECT.code() || status == ServiceStationEnum.CLOSE_REPAIR.code()
                        || status == ServiceStationEnum.CLOSE_TAKEOFF.code()
                        || status == ServiceStationEnum.CLOSE_RECEIVE.code()) {
                    // 关闭原因
                    dto.setReason(StringUtil.isNotEmpty(pojo.getCloseReason()) ? pojo.getCloseReason() : defaultReason);
                } else if (status == ServiceStationEnum.CLOSE_REFUSED.code()) {
                    // 拒单原因
                    dto.setReason(StringUtil.isNotEmpty(pojo.getRefuseReason()) ? pojo.getRefuseReason() : defaultReason);
                } else if (status == ServiceStationEnum.CLOSE_RESCUE.code()) {
                    // 取消救援原因
                    String abortRescueReason = workOrderOperateDao.queryCancelRescueReason(form.getWoCode(), OperateCodeEnum.OP_ABORT_RESCUE.code());
                    dto.setReason(StringUtil.isNotEmpty(abortRescueReason) ? abortRescueReason : defaultReason);
                } else if (status == ServiceStationEnum.CANCEL_ORDER.code()) {
                    // 取消预约原因
                    dto.setReason(StringUtil.isNotEmpty(pojo.getCancelReason()) ? pojo.getCancelReason() : "您已取消预约。");
                }
            }
            /*** 增加新的返回字段----开始*/
            //中文工单状态
            dto.setWoStatusValue(ServiceStationEnum.getMessage(dto.getWoStatus()));
            //驳回备注
            if (pojo.getRejectRemarks() != null && !pojo.getRejectRemarks().equals("")) {
                dto.setRejectRemarks(pojo.getRejectRemarks());
            }
            MaintainVehicleTubeEntity tube = maintainVehicleTubeDao.getInfoByClassisNo(pojo.getVin());
            if (tube != null) {
                //车管账号 登陆的手机号是否和审核的手机号相等
                if (user.getPhone().equals(tube.getMobile())) {
                    dto.setIsAuditer(1);
                    dto.setAuditPhone(pojo.getPhone());
                } else {
                    //不相等显示的是审核人手机号
                    dto.setIsAuditer(0);
                    dto.setAuditPhone(tube.getMobile());
                }
            } else {
                dto.setIsAuditer(2);
                dto.setAuditPhone(user.getPhone());
            }
            dto.setAuditPhone(stationPojo.getPhone());
            /*** 增加新的返回字段----结束*/
        }
        result.setData(dto);
        result.setResultCode(ECode.SUCCESS.code());
        return result;
    }

    /**
     * 司机版车辆列表
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
                // 1 是已经销售
                log.warn("车辆为未销售，查询工单时对应的工单信息将被忽略, vin: {}, 销售状态: {}", car.getCarVin(), car.getSalesStatus());
                continue;
            }
            if (car.getSalesDate() == null) {
                List<Date> mappingDate = carDao.findMappingDateByCarId(car.getId());
                if (mappingDate == null || mappingDate.isEmpty() || mappingDate.get(0) == null) {
                    log.warn("车辆销售时间和绑定时间都为空，查询工单时对应的工单信息将被忽略, vin: {}, 销售状态: {}", car.getCarVin(), car.getSalesStatus());
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
            log.error("时间转换的时候发生错误", ex);
        }
        return false;
    }

}
