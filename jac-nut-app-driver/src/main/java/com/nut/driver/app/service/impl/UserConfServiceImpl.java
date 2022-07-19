package com.nut.driver.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nut.driver.app.dao.UserConfDao;
import com.nut.driver.app.entity.UserConfEntity;
import com.nut.driver.app.service.UserConfService;

@Service("userConfService")
@Slf4j
public class UserConfServiceImpl extends ServiceImpl<UserConfDao, UserConfEntity> implements UserConfService {

    @Autowired
    private UserConfDao userConfDao;

    public UserConfEntity findByUserId(Long userId, String topic, String key) {
        return userConfDao.findByUserId(userId, topic, key);
    }

    public void insert(UserConfEntity userConfEntity) {
        userConfDao.insert(userConfEntity);
    }

    public void update(UserConfEntity userConfEntity) {
        userConfDao.update(userConfEntity);
    }
}
