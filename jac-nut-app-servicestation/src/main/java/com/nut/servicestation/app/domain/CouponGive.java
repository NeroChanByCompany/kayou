package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.domain
 * @Author: yzl
 * @CreateTime: 2021-07-27 14:29
 * @Version: 1.0
 */
@Data
public class CouponGive {
    private Long id;

    private Long infoId;

    private String giveBranchType;

    private Long giveBranchId;

    private String giveBranchName;

    private Date createTime;

    private String povince;

    private String city;

    private String povinceName;

    private String cityName;

    private String giveTicket;
}
