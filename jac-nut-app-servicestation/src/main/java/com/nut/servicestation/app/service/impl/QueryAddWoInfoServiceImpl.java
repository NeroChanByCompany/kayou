package com.nut.servicestation.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.StringUtil;
import com.nut.locationservice.app.dto.CarOnlineStatusDTO;
import com.nut.servicestation.app.dao.AppointmentItemsDao;
import com.nut.servicestation.app.dao.CarDao;
import com.nut.servicestation.app.dao.WorkOrderDao;
import com.nut.servicestation.app.domain.AppointmentItems;
import com.nut.servicestation.app.domain.Car;
import com.nut.servicestation.app.dto.OrderCarDto;
import com.nut.servicestation.app.dto.OrderItemListDto;
import com.nut.servicestation.app.dto.OrderItemsDto;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.form.OrderCarForm;
import com.nut.servicestation.app.form.OrderItemsForm;
import com.nut.servicestation.app.pojo.WorkOrderInfoPojo;
import com.nut.servicestation.app.service.AddWoStationService;
import com.nut.servicestation.app.service.QueryAddWoInfoService;
import com.nut.servicestation.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/*
 *  @author wuhaotian 2021/7/5
 */
@Slf4j
@Service("QueryAddWoInfoService")
public class QueryAddWoInfoServiceImpl implements QueryAddWoInfoService {


    /**
     * 预约项目类型
     */
    private static final String APPOINTMENT_ITEM_TYPE_MAINTAIN = "2";
    private static final String APPOINTMENT_ITEM_TYPE_REPAIR = "1";

    private static final String YES = "1";
    private static final String NO = "0";

    @Value("${workOrderNotAllowAreaCode:dummy}")
    private String areaCodes;

    @Autowired
    private AppointmentItemsDao appointmentItemsMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CarDao carMapper;

    @Autowired
    private AddWoStationService addWoStationService;

    @Autowired
    private WorkOrderDao workOrderMapper;

    @Value("${interceptHours:24}")
    private int interceptHours;

    @Value("${interceptMessage:该车辆在24小时内创建过工单，若要继续建单请联系客服创建（0719-8885555）！}")
    private String interceptMessage;
    
    
    @Override
    public HttpCommandResultWithData orderItems(OrderItemsForm command) {
        log.info("[orderItems]start");
        HttpCommandResultWithData<OrderItemsDto> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询预约项目
        List<AppointmentItems> queryList = appointmentItemsMapper.selectAllItems();
        List<OrderItemListDto> repairList = queryList.stream()
                .filter(e -> APPOINTMENT_ITEM_TYPE_REPAIR.equals(e.getItemType()))
                .map(e -> {
                    OrderItemListDto dto = new OrderItemListDto();
                    dto.setItemId(e.getId());
                    dto.setItemName(e.getItemName());
                    return dto;
                })
                .sorted(Comparator.comparing(OrderItemListDto::getItemId))
                .collect(Collectors.toList());
        List<OrderItemListDto> maintainList = queryList.stream()
                .filter(e -> APPOINTMENT_ITEM_TYPE_MAINTAIN.equals(e.getItemType()))
                .map(e -> {
                    OrderItemListDto dto = new OrderItemListDto();
                    dto.setItemId(e.getId());
                    dto.setItemName(e.getItemName());
                    return dto;
                })
                .sorted(Comparator.comparing(OrderItemListDto::getItemId))
                .collect(Collectors.toList());;
        OrderItemsDto data = new OrderItemsDto();
        data.setRepairList(repairList);
        data.setMaintainList(maintainList);
        result.setData(data);
        log.info("[orderItems]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData orderCar(OrderCarForm command) throws JsonProcessingException {
        log.info("[orderCar]start");
        HttpCommandResultWithData<OrderCarDto> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 获取用户信息和工单信息
        UserInfoDto userInfo = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfo == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("未获取到用户信息");
            return result;
        }

        Car car = carMapper.selectCarByCarVin(command.getChassisNum());
        if (car == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("车辆不存在！");
            return result;
        }
        OrderCarDto data = new OrderCarDto();
        data.setCarId(car.getId());
        data.setCarNumber(car.getCarNumber());

        // 调用位置云接口获取里程
//        CarLocationOutputDto dto = addWoStationService.getCarUpInfo(command.getChassisNum());
//        if (dto != null && dto.getMileage() != null) {
//            data.setMileage(dto.getMileage().toString());
//        }

        List<String> carVins = new ArrayList<>();
        carVins.add(car.getCarVin());
        Map<String, CarOnlineStatusDTO> outMap = new HashMap<>(16);
        HttpCommandResultWithData cloudResult = addWoStationService.getRealTimeInfo(carVins, outMap);
        if (cloudResult.getResultCode() == ECode.SUCCESS.code()) {
            CarOnlineStatusDTO dto = outMap.get(car.getCarVin());
            if (dto != null) {
                data.setMileage(dto.getStandardMileage() == null? null: dto.getStandardMileage().toString());
            }
        }

        // 判断是否有本站未完成工单
        // 在途工单包括：待接受，待出发，待接车，检查中，维修中，拒单申请审核中，修改申请审核中-待接受，
        //               修改申请审核中-待出发，修改申请审核中-待接车，关闭申请审核中-待出发，关闭申请审核中-待接车，
        //                关闭申请审核中-检查，关闭申请审核中-维修
        List<Integer> woStatusList = Arrays.asList(ServiceStationEnum.TO_BE_ACCEPTED.code(), ServiceStationEnum.TO_TAKE_OFF.code(),
                ServiceStationEnum.TO_RECEIVE.code(), ServiceStationEnum.INSPECTING.code(), ServiceStationEnum.REPAIRING.code(),
                ServiceStationEnum.REFUSE_APPLYING.code(), ServiceStationEnum.MODIFY_APPLYING_ACCEPT.code(),
                ServiceStationEnum.MODIFY_APPLYING_TAKEOFF.code(), ServiceStationEnum.MODIFY_APPLYING_RECEIVE.code(),
                ServiceStationEnum.CLOSE_APPLYING_TAKEOFF.code(), ServiceStationEnum.CLOSE_APPLYING_RECEIVE.code(),
                ServiceStationEnum.CLOSE_APPLYING_INSPECT.code(), ServiceStationEnum.CLOSE_APPLYING_REPAIR.code());
        Map<String, Object> param = new HashMap<>(10);
        param.put("woStatusList", woStatusList);
        param.put("chassisNum", command.getChassisNum());
        param.put("stationId", userInfo.getServiceStationId());
        Long woCount = workOrderMapper.countWoByStatus(param);
        if (woCount > 0) {
            data.setOnlineWo(YES);
        } else {
            data.setOnlineWo(NO);
        }

        data.setOrderStatus(false);
        data.setOrderStatusMessage("");
        // 判断24小时内车辆是否创建过工单
        List<String> areaCodeList = Arrays.asList(areaCodes.split(","));
        if (StringUtil.isNotEmpty(userInfo.getAreaCode())
                && CollectionUtils.isNotEmpty(areaCodeList)
                && areaCodeList.contains(userInfo.getAreaCode())) {
            // 查询车辆最近一条创建的工单
            WorkOrderInfoPojo wo = workOrderMapper.queryLatelyWorkOrderByVin(command.getChassisNum());
            if (null != wo && null != wo.getCreateTime()
                    && (new Date().getTime() - wo.getCreateTime().getTime()) / 1000 < interceptHours * 3600) {
                data.setOrderStatus(true);
                data.setOrderStatusMessage(interceptMessage);
            }
        }

        result.setData(data);
        log.info("[orderCar]end");
        return result;
    }
}
