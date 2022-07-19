package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.driver.app.entity.UserListenerLogEntity;

/**
 * @author liuBing
 * @Classname userListenerLogService
 * @Description TODO 用户动作监听
 * @Date 2021/9/13 11:13
 */
public interface UserListenerLogService extends IService<UserListenerLogEntity> {
    /**
     * 用户当前操作动作监听
      * @param entity 用户
     */
    void saveActionListener(UserListenerLogEntity entity);

}
