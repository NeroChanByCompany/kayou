package com.nut.servicestation.app.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.domain
 * @Author: yzl
 * @CreateTime: 2021-07-27 09:58
 * @Version: 1.0
 */
@Data
public class BranchInfo implements Serializable {

    private String branchId;
    private String branchName;
    private String branchAddress;
    private String branchCode;
    private String phone;

}
