package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nut.common.enums.UserListenerEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author liuBing
 * @Classname UserListenerLog
 * @Description TODO 用户日志监听
 * @Date 2021/9/13 10:52
 */
@Data
@Accessors(chain = true)
@TableName("user_listener_log")
public class UserListenerLogEntity {
    /**
     * '自增id'
     */
    private Long id;

    /**
     * '用户唯一身份标识'
     */
    private String ucId;
    /**
     * 用户类型 0-用户app、1-车队app、2-服务app
     */
    private Integer appType;
    /**
     * 登录类型 0-密码、1-短信
     */
    private Integer loginType;
    /**
     * '登录ip'
     *
     */
    private String loginIp;

    /**
     * '登录token'
     */
    private String token;
    /**
     * '登录时间'
     */
    private Date loginTime;
    /**
     * '登出时间'
     */
    private Date logoutTime;
    /**
     * '注销时间'
     */
    private Date cancellationTime;
    /**
     * '创建时间'
     */
    private Date createTime;
    /**
     * '更新时间'
     */
    private Date updateTime;

    /**
     * 用户动作 0 登录 1 登出 2 登出
     */
    private Integer action;


    /**
     * 用户设备类型 1 安卓 2 ios
     */
    private Integer versionType;

    /**
     * 版本号
     */
    private String version;
}
