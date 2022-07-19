package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.driver.app.form.AppFreezeForm;
import com.nut.driver.app.form.AppFreezeTokenForm;
import com.nut.driver.app.form.AppLoginForm;

/**
 * @author liuBing
 * @Classname UserFreezeService
 * @Description TODO
 * @Date 2021/9/14 16:51
 */
public interface UserFreezeService {
    /**
     * @Author liuBing
     * @Description //TODO 将当前用户拉入黑名单，并删除token
     * @Date 16:53 2021/9/14
     * @Param [form]
     * @return void
     **/
    void freeze(AppFreezeForm form);
    /**
     * @Author liuBing
     * @Description //TODO 将当前加入黑名单的用户激活
     * @Date 16:53 2021/9/14
     * @Param [form]
     * @return void
     **/
    void activate(AppFreezeForm form);
    /**
     * @Author liuBing
     * @Description //TODO 将当前用户拉入黑名单，并删除token ucId版本
     * @Date 16:53 2021/9/14
     * @Param [form]
     * @return void
     **/
    void freezeByUcId(AppFreezeTokenForm form);
}
