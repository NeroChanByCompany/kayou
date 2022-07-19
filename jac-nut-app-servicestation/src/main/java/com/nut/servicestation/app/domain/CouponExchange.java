package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 优惠券兑换
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.dao
 * @Author: yzl
 * @CreateTime: 2021-08-02 16:34
 * @Version: 1.0
 */
@Data
public class CouponExchange {
    private Long id;
    private Long infoId;
    private String exchangeBranchType;
    private String exchangeBranchId;
    private Date createTime;
}
