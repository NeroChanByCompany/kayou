package com.nut.servicestation.app.form;

import lombok.Data;

import java.util.List;

/**
 * @Description: 网点列表查询
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.form
 * @Author: yzl
 * @CreateTime: 2021-07-27 10:15
 * @Version: 1.0
 */
@Data
public class BranchAddForm extends BranchListForm{

    /**
     * 经销商ID
     */
    List<BranchAddBranchListForm> branchList;

    /**
     *  是否为全选：0：否1：是 默认为全选
     */
    private String isAll = "1";

}
