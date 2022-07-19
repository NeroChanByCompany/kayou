package com.jac.app.job.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jac.app.job.entity.UserFreezeConfigEntity;
import com.jac.app.job.mapper.UserFreezeConfigMapper;
import com.jac.app.job.service.UserFreezeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author liuBing
 * @Classname UserFreezeConfigServiceImpl
 * @Description TODO
 * @Date 2021/10/11 13:54
 */
@Slf4j
@Service
public class UserFreezeConfigServiceImpl extends ServiceImpl<UserFreezeConfigMapper, UserFreezeConfigEntity> implements UserFreezeConfigService {
}
