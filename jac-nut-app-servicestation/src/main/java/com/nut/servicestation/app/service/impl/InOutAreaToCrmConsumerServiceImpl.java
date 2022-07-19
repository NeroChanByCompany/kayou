package com.nut.servicestation.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.RedisComponent;
import com.nut.servicestation.app.dao.AreaStationDao;
import com.nut.servicestation.app.dao.CarDao;
import com.nut.servicestation.app.dao.WorkOrderDao;
import com.nut.servicestation.app.domain.WorkOrder;
import com.nut.servicestation.app.pojo.AreaStationPojo;
import com.nut.servicestation.app.pojo.AreaStayInfoPojo;
import com.nut.servicestation.app.pojo.CarRedisPojo;
import com.nut.servicestation.app.pojo.InOutToCrmPojo;
import com.nut.servicestation.app.service.InOutAreaToCrmConsumerService;
import com.nut.servicestation.app.service.UptimeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuBing
 * @Classname InOutAreaToCrmConsumerServiceImpl
 * @Description TODO
 * @Date 2021/7/7 19:17
 */
@Slf4j
@Service
public class InOutAreaToCrmConsumerServiceImpl implements InOutAreaToCrmConsumerService {

    /**
     * redis中存储的车辆信息的key
     */
    private static final String CAR_REDIS_KEY = "car_sync";

    /**
     * redis中存储的电子围栏服务站的key
     */
    private static final String AREA_STATION_MAPPING_REDIS_KEY = "area_station_mapping";

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private CarDao carDao;

    @Autowired
    private AreaStationDao areaStationDao;

    @Autowired
    private WorkOrderDao workOrderDao;

    @Autowired
    private UptimeService uptimeService;

    @Value("${database_name}")
    private String DatabaseName;

    @Override
    public void inOutAreaToCrmConsumer(AreaStayInfoPojo pojo, String terminalId) {

        //从redis获取车辆信息
        CarRedisPojo carRedisPojo = JsonUtil.fromJson((String) redisComponent.hget(CAR_REDIS_KEY, terminalId), CarRedisPojo.class);
        //查询mysql信息
        if (carRedisPojo == null) {
            carRedisPojo = carDao.queryCarByAutoTerminal(terminalId);
        }

        //无此车辆信息
        if (carRedisPojo == null) {
            log.info("[InOutAreaToCrmConsumerService][inOutAreaToCrmConsumer] This car does not exist!");
            return;
        }
        String stationCode = this.getStationCodeByAreaId(pojo.getAreaId());
        log.info("[InOutAreaToCrmConsumerService][inOutAreaToCrmConsumer] stationCode value:{}", stationCode);
        if(StringUtils.isEmpty(stationCode)){
            log.info("[InOutAreaToCrmConsumerService][inOutAreaToCrmConsumer] stationCode is empty!");
            return;
        }

        InOutToCrmPojo inOutToCrmPojo = new InOutToCrmPojo();
        inOutToCrmPojo.setChassisNo(carRedisPojo.getCarVin());
        inOutToCrmPojo.setIdentifyNum(stationCode);
        // 是否为进站
        boolean isInToFlag = false;
        if (pojo.getEndTime() != null && !(pojo.getEndTime().equals(0L))) {
            inOutToCrmPojo.setInOutTime(DateUtil.timeStr(pojo.getEndTime() * 1000));
            inOutToCrmPojo.setMoveClass("1");
        } else {
            inOutToCrmPojo.setInOutTime(DateUtil.timeStr(pojo.getStartTime() * 1000));
            inOutToCrmPojo.setMoveClass("0");
            isInToFlag = true;
        }
        String inOutTime = inOutToCrmPojo.getInOutTime();
        log.info("[InOutAreaToCrmConsumerService][inOutAreaToCrmConsumer] inOutToCrmPojo:{}", inOutToCrmPojo);
        this.syncInOutAreaToCrm(inOutToCrmPojo);

        if (isInToFlag) {
            // 进站时同步crm 进站时间
            // 根据底盘号和服务站code查询待接车前的工单
            // 状态值
            List<Integer> woStatusList = Arrays.asList(ServiceStationEnum.TO_BE_ACCEPTED.code(),
                    ServiceStationEnum.REFUSE_APPLYING.code(), ServiceStationEnum.TO_TAKE_OFF.code(),
                    ServiceStationEnum.TO_RECEIVE.code(), ServiceStationEnum.MODIFY_APPLYING_ACCEPT.code(),
                    ServiceStationEnum.MODIFY_APPLYING_TAKEOFF.code(), ServiceStationEnum.MODIFY_APPLYING_RECEIVE.code(),
                    ServiceStationEnum.CLOSE_APPLYING_TAKEOFF.code(), ServiceStationEnum.CLOSE_APPLYING_RECEIVE.code(),
                    ServiceStationEnum.CLOSE_TAKEOFF.code(), ServiceStationEnum.CLOSE_RECEIVE.code());
            List<WorkOrder> woList = workOrderDao.selectByVinStationCodeStatus(carRedisPojo.getCarVin(), stationCode, ServiceStationVal.STATION_SERVICE, woStatusList);
            // 判断是否存在工单
            if (woList != null && !woList.isEmpty()) {
                for (WorkOrder woOrder : woList) {
                    HttpCommandResultWithData result = new HttpCommandResultWithData();
                    result.setResultCode(ECode.SUCCESS.code());
                    result.setMessage(ECode.SUCCESS.message());
                    // 需求急，借用mileage字段保存格式化时间
                    uptimeService.trigger(result, woOrder.getWoCode(), ServiceStationVal.WEB_SERVICE_MAINTENANCEINBOUNDTIME, "", inOutTime);
                }
            }
        }
    }

    private void syncInOutAreaToCrm(InOutToCrmPojo inOutToCrmPojo) {
    }

    private String getStationCodeByAreaId(Long areaId) {
        if(areaId == null){
            log.info("[InOutAreaToCrmConsumerService][getStationCodeByAreaId] areaId is null!");
            return null;
        }
        //判断redis中是否有key
        Boolean isExist = redisComponent.hasKey(AREA_STATION_MAPPING_REDIS_KEY);
        log.info("[InOutAreaToCrmConsumerService][getStationCodeByAreaId] redis haskey:{}", isExist);
        //存在key
        if(isExist){
            AreaStationPojo areaStation = JsonUtil.fromJson((String) redisComponent.hget(AREA_STATION_MAPPING_REDIS_KEY, String.valueOf(areaId)), AreaStationPojo.class);
            log.info("[InOutAreaToCrmConsumerService][getStationCodeByAreaId] redis AreaStationPojo value:{}", areaStation);
            // redis查询到服务站code并返回
            if(areaStation != null && StringUtils.isNotEmpty(areaStation.getStationCode())){
                return areaStation.getStationCode();
            }
            //查询mysql
            List<AreaStationPojo> areaStationList = areaStationDao.queryAreaStation(DatabaseName, areaId);
            if(CollectionUtils.isNotEmpty(areaStationList) && areaStationList.get(0) != null){
                log.info("[InOutAreaToCrmConsumerService][getStationCodeByAreaId] mysql AreaStationPojo value:{}", areaStationList.get(0));
                String stationCode = areaStationList.get(0).getStationCode();
                if(StringUtils.isNotEmpty(stationCode)){
                    //将数据补充到redis中
                    AreaStationPojo areaStationPojo = new AreaStationPojo();
                    areaStationPojo.setAreaId(areaId);
                    areaStationPojo.setStationCode(stationCode);
                    try {
                        redisComponent.hset(AREA_STATION_MAPPING_REDIS_KEY, String.valueOf(areaId), JsonUtil.toJson(areaStationPojo));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                    }
                    return stationCode;
                }
            }
            //不存在key
        }else {
            //mysql查询所有
            List<AreaStationPojo> areaStationList = areaStationDao.queryAreaStation(DatabaseName, null);
            log.info("[InOutAreaToCrmConsumerService][getStationCodeByAreaId] areaStationList size:{}", areaStationList.size());
            if(CollectionUtils.isNotEmpty(areaStationList)){
                String stationCodeTmp = null;
                Map<String, Object> redisMap = new HashMap<>(16);
                for(AreaStationPojo pojo : areaStationList){
                    if(pojo.getAreaId().equals(areaId)){
                        stationCodeTmp = pojo.getStationCode();
                    }
                    try {
                        redisMap.put(String.valueOf(pojo.getAreaId()), JsonUtil.toJson(pojo));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                    }
                }
                //初始化redis
                redisComponent.hmset(AREA_STATION_MAPPING_REDIS_KEY, redisMap);
                return stationCodeTmp;
            }
        }
        return null;

    }
}
