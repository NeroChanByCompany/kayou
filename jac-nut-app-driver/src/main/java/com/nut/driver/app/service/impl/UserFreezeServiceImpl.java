package com.nut.driver.app.service.impl;

import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.driver.app.form.AppFreezeForm;
import com.nut.driver.app.form.AppFreezeTokenForm;
import com.nut.driver.app.service.UserFreezeService;
import com.nut.driver.app.service.UserService;
import com.nut.driver.common.component.TokenComponent;
import com.nut.driver.common.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author liuBing
 * @Classname UserFreezeServiceImpl
 * @Description TODO
 * @Date 2021/9/14 16:51
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class,readOnly = true)
public class UserFreezeServiceImpl implements UserFreezeService {

    @Resource
    UserService userService;

    /**
     * 1、校验加密key
     * 2、拉黑用户，并加入黑名单
     * @param form
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freeze(AppFreezeForm form) {
        if (!CommonConstants.FREEZE_KEY.equals(form.getKey())){
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "接口校验失败,请重试");
        }
         userService.freezeUser(form);
    }
    /**
     * 1、校验加密key
     * 2、激活用户
     * @param form
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activate(AppFreezeForm form) {
        if (!CommonConstants.FREEZE_KEY.equals(form.getKey())){
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "接口校验失败,请重试");
        }
        userService.activateUser(form);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freezeByUcId(AppFreezeTokenForm form) {
        if (!CommonConstants.FREEZE_KEY.equals(form.getKey())){
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "接口校验失败,请重试");
        }
        userService.freezeByUcId(form);
    }
}
