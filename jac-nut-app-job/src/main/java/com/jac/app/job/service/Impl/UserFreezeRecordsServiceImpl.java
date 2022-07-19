package com.jac.app.job.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jac.app.job.entity.UserFreezeRecordsEntity;
import com.jac.app.job.mapper.UserFreezeRecordsMapper;
import com.jac.app.job.service.UserFreezeRecordsService;
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
public class UserFreezeRecordsServiceImpl extends ServiceImpl<UserFreezeRecordsMapper, UserFreezeRecordsEntity> implements UserFreezeRecordsService {
}
