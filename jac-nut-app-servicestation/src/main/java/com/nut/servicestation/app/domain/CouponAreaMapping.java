package com.nut.servicestation.app.domain;

import lombok.Data;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.domain
 * @Author: yzl
 * @CreateTime: 2021-07-27 14:34
 * @Version: 1.0
 */
@Data
public class CouponAreaMapping {
    private Long id;

    private Long infoId;

    private String giveOrExchange;

    private String province;

    private String city;

    private String ticket;

}
