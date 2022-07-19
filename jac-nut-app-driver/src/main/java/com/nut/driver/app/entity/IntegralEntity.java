package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-15 19:03:14
 */
@Data
@TableName("integral")
public class IntegralEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据主键
     */
    @TableId
    private Long id;
    /**
     * 业务主键
     */
    private String ucId;
    /**
     * 积分数量
     */
    private Long integralCounts;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 上次积分总数
     */
    private Long lastIntegralCounts;
    /**
     * 上次积分操作数量
     */
    private Long lastIntegralOperatedCounts;
    /**
     * 积分状态,0正常，1冻结
     */
    private Integer integralState;
    /**
     * 上次积分操作类型。0添加，1减少，2冻结，3解冻，4添加积分用户
     */
    private Integer lastOperateType;
    /**
     * 用户手机号，后期会为业务主键
     */
    private String phone;

}
