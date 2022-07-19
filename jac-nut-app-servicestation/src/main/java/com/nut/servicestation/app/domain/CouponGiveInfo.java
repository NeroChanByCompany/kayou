package com.nut.servicestation.app.domain;

import lombok.Data;

/**
 * @Description: 优惠券列表信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.domain
 * @Author: yzl
 * @CreateTime: 2021-08-03 13:57
 * @Version: 1.0
 */
@Data
public class CouponGiveInfo {
    private String couponId;
    private String couponName;
    private String endDate;
    private String vin;
    private String createTime;
    private String couponStatus;
}
