package com.nut.driver.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nut.driver.app.dao.UserFreezeConfigDao;
import com.nut.driver.app.entity.UserFreezeConfigEntity;
import com.nut.driver.app.service.UserFreezeConfigService;
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
public class UserFreezeConfigServiceImpl extends ServiceImpl<UserFreezeConfigDao, UserFreezeConfigEntity> implements UserFreezeConfigService {
}
