package com.nut.servicestation.app.form;

import lombok.Data;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.form
 * @Author: yzl
 * @CreateTime: 2021-07-27 10:15
 * @Version: 1.0
 */
@Data
public class BranchAddBranchListForm {

    /**
     *经销商或是服务站ID
     */
    private String branchId;
    /**
     * 经销商或是服务站类型
     */
    private String branchType;

}
