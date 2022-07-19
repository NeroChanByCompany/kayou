package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liuBing
 * @Classname CarMaintainEntity
 * @Description TODO
 * @Date 2021/6/23 17:45
 */
@Data
@TableName("car_maintain")
public class CarMaintainEntity {

    /**
     * 主键id
     */
    private Long id;

    /**
     *底盘号
     */
    private String carVin;
    /**
     *车牌号
     */
    private String carNumber;
    /**
     *协议类型（0：非协议车，1：协议车）
     */
    private String protocolType;
    /**
     *保养项目
     */
    private String mtcItem;
    /**
     *保养类别
     */
    private String mtcType;
    /**
     *上一次保养里程
     */
    private String lastMaintainMileage;
    /**
     *上一次保养时间
     */
    private Date lastMaintainTime;
    /**
     *下次保养里程
     */
    private BigDecimal nextMaintainMileage;
    /**
     *下次保养时间
     */
    private Date nextMaintainTime;

    /**
     *走保里程（单位：KM）
     */
    private String maiMileage;
    /**
     *走保时间(月份间隔)
     */
    private String maiMonth;
    /**
     *首保里程(单位：KM)
     */
    private String fstMileage;
    /**
     *首保时间(月份间隔)
     */
    private String fstMonth;
    /**
     *执行间隔里程(单位：KM)
     */
    private String invMileage;
    /**
     *执行间隔时间(月份间隔)
     */
    private String invMonth;
    /**
     *总成类别(示例：发动机，通用检查包，干燥筒…...)
     */
    private String assyType;
    /**
     *配件号
     */
    private String partNum;
    /**
     *配件名称
     */
    private String partName;
    /**
     *使用数量
     */
    private String count;
    /**
     *计量单位
     */
    private String unitofmmt;
    /**
     * 是否校验更新数据 1：不需要  2：需要
     */
    private Integer isCheck;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 更新时间
     */
    private Date updateDate;

}
