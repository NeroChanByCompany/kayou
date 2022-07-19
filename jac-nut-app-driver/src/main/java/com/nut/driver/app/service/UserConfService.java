package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.driver.app.dao.UserConfDao;
import com.nut.driver.app.entity.UserConfEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.PrivateKey;
import java.util.Map;

/**
 * 用户配置表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-27 15:47:51
 */
public interface UserConfService extends IService<UserConfEntity> {

   UserConfEntity findByUserId(Long userId, String topic, String key);

    void insert(UserConfEntity userConfEntity);

     void update(UserConfEntity userConfEntity);

}

