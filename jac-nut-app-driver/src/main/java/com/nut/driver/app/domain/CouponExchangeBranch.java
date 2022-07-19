package com.nut.driver.app.domain;

import lombok.Data;

/**
 * @Description: 网点信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.domain
 * @Author: yzl
 * @CreateTime: 2021-06-28 20:20
 * @Version: 1.0
 */
@Data
public class CouponExchangeBranch {
    private Long exchangeBranchId;
    private String exchangeBranchType;
    private String branchName;
    private String branchAddress;
    private String branchPhone;
}
