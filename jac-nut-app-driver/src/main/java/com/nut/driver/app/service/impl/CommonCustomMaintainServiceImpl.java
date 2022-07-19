package com.nut.driver.app.service.impl;

import com.nut.common.utils.StringUtil;
import com.nut.driver.app.dao.CarDao;
import com.nut.driver.app.dao.UserDao;
import com.nut.driver.app.domain.User;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.pojo.CarRolePojo;
import com.nut.driver.app.service.CommonCustomMaintainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommonCustomMaintainServiceImpl implements CommonCustomMaintainService {

    @Autowired
    private UserDao userMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CarDao carMapper;

    @Override
    @Async
    public void updateUserCustomMaintain(Long userId) {
        log.info("[updateUserCustomMaintain]start");
        if (userId != null) {
            UserEntity user = userMapper.selectByPrimaryKey(userId);
            if (user != null && StringUtil.isNotEmpty(user.getUcId())) {
                List<CarRolePojo> cars = carMapper.selectUserRelatedCar(userId);
                List<String> carIds = cars.stream().map(CarRolePojo::getCarId).collect(Collectors.toList());
                if (carIds.isEmpty()) {
                    carIds = null;
                }
                //获得需要更改状态的maintanceIds
                String maintanceIds = userMapper.getCustomIdsByUserIdNew(userId, carIds);
                log.info("[updateUserCustomMaintain] maintanceIds :" + maintanceIds);
                if (StringUtil.isNotEmpty(maintanceIds)) {
                    String[] customIdList = maintanceIds.split(",");
                    userMapper.deleteUserCustomMaintanceCommon(Arrays.asList(customIdList));
                    for (String customId : customIdList) {
                        //redis信息删除
                        redisTemplate.delete(StringUtil.concat("CUSTOM_MAINTAIN:", user.getUcId(), ":", customId));
                    }
                }
            }
        }
        log.info("[updateUserCustomMaintain]end");
    }
}
