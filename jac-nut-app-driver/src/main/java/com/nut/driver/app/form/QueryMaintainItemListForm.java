package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuBing
 * @Classname QueryMaintainItemListForm
 * @Description TODO
 * @Date 2021/6/25 16:22
 */

@Data
public class QueryMaintainItemListForm extends BaseForm {

    // 保养项目名
    @ApiModelProperty(name = "maintainItemName", notes = "保养项目名", dataType = "String")
    private String maintainItemName;
}
