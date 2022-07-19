package com.nut.driver.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nut.driver.app.dao.UserFreezeRecordsDao;
import com.nut.driver.app.entity.UserFreezeRecordsEntity;
import com.nut.driver.app.service.UserFreezeRecordsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author liuBing
 * @Classname UserFreezeRecordsServiceImpl
 * @Description TODO
 * @Date 2021/10/11 13:52
 */
@Slf4j
@Service
public class UserFreezeRecordsServiceImpl extends ServiceImpl<UserFreezeRecordsDao, UserFreezeRecordsEntity> implements UserFreezeRecordsService {
}
