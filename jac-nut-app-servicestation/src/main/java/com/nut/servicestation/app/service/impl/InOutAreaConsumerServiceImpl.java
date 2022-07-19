package com.nut.servicestation.app.service.impl;

import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.RedisComponent;
import com.nut.servicestation.app.dao.CarDao;
import com.nut.servicestation.app.pojo.AreaStayInfoPojo;
import com.nut.servicestation.app.pojo.CarRedisPojo;
import com.nut.servicestation.app.service.InOutAreaConsumerService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname InOutAreaConsumerServiceImpl
 * @Description TODO 消费进出服务半径电子围栏信息
 * @Date 2021/7/7 17:49
 * @author liuBing
 */
@Slf4j
@Service
public class InOutAreaConsumerServiceImpl implements InOutAreaConsumerService {


    /**
     * redis中存储的车辆信息的key
     */
    private static final String CAR_REDIS_KEY = "car_sync";
    /**
     * 进出站数据缓存的前缀
     */
    private static final String REDIS_KEY = "ss_car_inout";


    @Autowired
    private CarDao carDao;
    @Autowired
    private RedisComponent redisComponent;


    /**
     * 进出服务半径电子围栏信息处理
     *
     * @param pojo       进出围栏信息
     * @param terminalId 通信号
     * @throws Exception
     */
    @SneakyThrows
    @Override
    public void inOutAreaConsumer(AreaStayInfoPojo pojo, String terminalId) {
        /*
         * 获取车辆ID信息
         *
         * 1、先取redis缓存中的信息
         * 2、缓存中不存在时再查询mysql
         */
        //获取redis信息
        CarRedisPojo carRedisPojo = JsonUtil.fromJson((String) redisComponent.hget(CAR_REDIS_KEY, terminalId), CarRedisPojo.class);
        //查询mysql信息
        if (carRedisPojo == null) {
            carRedisPojo = carDao.queryCarByAutoTerminal(terminalId);
        }

        //无此车辆信息
        if (carRedisPojo == null) {
            log.info("[InOutAreaConsumerService][inOutAreaConsumer] This car does not exist");
            return;
        }

        //缓存KEY生成
        //格式：前缀_围栏ID_通信号_进入时间戳_当前时间戳
        String key = (pojo.getStartTime() * 1000) + "_" + System.currentTimeMillis();
        log.info("[InOutAreaConsumerService][inOutAreaConsumer] This key is : {}{}{}", pojo.getAreaId(), terminalId, key);
        //缓存对象数据格式转换
        carRedisPojo.setAreaId(pojo.getAreaId());
        carRedisPojo.setInTime(pojo.getStartTime() * 1000);
        if (0 != pojo.getEndTime()) {
            carRedisPojo.setOutTime(pojo.getEndTime() * 1000);
        }
        carRedisPojo.setStayLen(pojo.getStayTime());
        carRedisPojo.setTerId(Long.valueOf(terminalId));
        redisComponent.hset(REDIS_KEY,key,JsonUtil.toJson(carRedisPojo));
    }
}
