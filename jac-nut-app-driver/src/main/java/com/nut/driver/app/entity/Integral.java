package com.nut.driver.app.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 积分实体类
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.entity
 * @Author: yzl
 * @CreateTime: 2021-06-15 19:22
 * @Version: 1.0
 */
@Data
public class Integral {

    /**
     * 数据主键
     */
    private Long id;

    /**
     * 业务主键
     */
    private String ucId;

    /**
     * 积分数量
     */
    private Integer integralCounts;

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
    private Integer lastIntegralCounts;

    /**
     * 上次积分操作数量
     */
    private Integer lastIntegralOperatedCounts;

    /**
     * 积分状态
     */
    private Integer integralState;

    /**
     * 上次操作积分状态
     * 0:创建积分用户
     * 1：添加积分
     * 2：扣除积分
     * 3：冻结积分
     * 4：积分解冻
     */
    private Integer lastOperateType;

    /**
     * 用户手机号
     * 二期该字段可能为业务主键
     */
    private String phone;

}
