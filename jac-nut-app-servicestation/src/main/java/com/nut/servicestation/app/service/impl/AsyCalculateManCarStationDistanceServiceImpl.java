package com.nut.servicestation.app.service.impl;

import com.nut.common.utils.LonLatUtil;
import com.nut.common.utils.StringUtil;
import com.nut.locationservice.app.dto.CarLocationOutputDto;
import com.nut.servicestation.app.dao.WorkOrderDao;
import com.nut.servicestation.app.domain.WorkOrder;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.service.AddWoStationService;
import com.nut.servicestation.app.service.AsyCalculateManCarStationDistanceService;
import com.nut.servicestation.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@Service("AsyCalculateManCarStationDistanceService")
public class AsyCalculateManCarStationDistanceServiceImpl implements AsyCalculateManCarStationDistanceService {


    private String usedPhotoType = "P30,P81,P40A,P50A,P60,P61,P62,P70,P71,P72,P90A,P100A,P110,P120,P121,P122,P130,P131,P132,P133,P134,P135,P140A,P150A,P160,P161,P162,P170,P171,P172,P180,P190,P191,P192,P193,P194,P195";

    @Autowired
    private AddWoStationService addWoStationService;
    @Autowired
    private UserService queryUserInfoService;
    @Autowired
    private WorkOrderDao workOrderMapper;


    @Override
    @Async
    public void calculateDistance(String type, String woCode, String lon, String lat, String userId) {
        if (!StringUtil.containMark(usedPhotoType, type)) {
            log.info("[calculateDistance]no calculate:" + type);
            return;
        }
        UserInfoDto userInfoDto = queryUserInfoService.getUserInfoByUserId(userId, false);
        if (userInfoDto == null || StringUtil.isEmpty(userInfoDto.getServiceStationLat()) || StringUtil.isEmpty(userInfoDto.getServiceStationLon())) {
            log.info("[calculateDistance]user no exist:" + woCode);
            return;
        }
        /* 查询工单信息 */
        Map<String, String> param = new HashMap<>(2);
        param.put("woCode", woCode);
        WorkOrder workOrder = workOrderMapper.selectByWoCode(param);
        if (workOrder == null) {
            log.info("[calculateDistance]wo no exist:" + woCode);
            return;
        }
        /* 获取车辆位置上报信息 */
        CarLocationOutputDto carLocationOutputDto = addWoStationService.getCarUpInfo(workOrder.getChassisNum());

        Double carLat = null;
        Double carLon = null;
        Double manCarDis = null;
        Double manStationDis = null;
        Double carStationDis = null;

        if (carLocationOutputDto == null || carLocationOutputDto.getUpTime() == null || carLocationOutputDto.getLat() == null || carLocationOutputDto.getLon() == null) {
            log.info("[calculateDistance getCarUpInfo() No information is reported on the vehicle location]");
        } else {
            carLat = carLocationOutputDto.getLat();
            carLon = carLocationOutputDto.getLon();
        }
        if (lon != null && lat != null && carLat != null && carLat != null) {
            try {
                // 人 车距离
                manCarDis = LonLatUtil.getDistance(Double.valueOf(lat), Double.valueOf(lon), carLat, carLon);
                // 车 站距离
                carStationDis = LonLatUtil.getDistance(carLat, carLon, Double.valueOf(userInfoDto.getServiceStationLat()), Double.valueOf(userInfoDto.getServiceStationLon()));
            } catch (Exception e) {
                log.error("[calculateDistance] lon lat error", e);
            }
        }
        if (lon != null && lat != null) {
            try {
                // 人 站距离
                manStationDis = LonLatUtil.getDistance(Double.valueOf(lat), Double.valueOf(lon), Double.valueOf(userInfoDto.getServiceStationLat()), Double.valueOf(userInfoDto.getServiceStationLon()));
            } catch (Exception e) {
                log.error("[calculateDistance] lon lat error", e);
            }
        }

        log.info("[calculateDistance]{},{},{},{}", woCode, manCarDis, manStationDis, carStationDis);
        // 更新
        WorkOrder updateWorkOrder = new WorkOrder();
        updateWorkOrder.setId(workOrder.getId());
        if (manCarDis != null) {
            updateWorkOrder.setManCarDistance(manCarDis.longValue() + "");
        }
        if (manStationDis != null) {
            updateWorkOrder.setManStationDistance(manStationDis.longValue() + "");
        }
        if (carStationDis != null) {
            updateWorkOrder.setCarStationDistance(carStationDis.longValue() + "");
        }
        if (manCarDis != null || manStationDis != null || carStationDis != null) {
            workOrderMapper.updateByPrimaryKeySelective(updateWorkOrder);
        }
    }
}
