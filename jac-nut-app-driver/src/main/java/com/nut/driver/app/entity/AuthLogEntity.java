package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author liuBing
 * @Classname authLogEntity
 * @Description TODO 用户实名认证日志表
 * @Date 2021/8/10 11:24
 */
@Data
@Accessors(chain = true)
@TableName(value = "auth_log")
public class AuthLogEntity {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 实名认证是否成功（0：成功，1：失败）
     */
    private String isAuth;
    /**
     * 登记物联卡号
     */
    private String msisdn;
    /**
     * 登记人姓名
     */
    private String name;
    /**
     * 登记人身份证号
     */
    private String idCard;
    /**
     * 登记结果接收手机号
     */
    private String phone;
    /**
     * 登记时间
     */
    private Long registerTime;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 底盘号
     */
    private String carVin;
    /**
     * 失败原因
     */
    private String msg;
    /**
     * 创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
