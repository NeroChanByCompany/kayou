package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 网点列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-28 20:13
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel(value = "使用网点列表Form")
public class CouponBranchListForm extends BaseForm {

    @NotNull(message = "优惠券ID不能为空")
    @NotBlank(message = "优惠券ID不能为空")
    @ApiModelProperty(name = "infoId",notes = "优惠券Id",dataType = "String")
    private String infoId;

    @NotNull(message = "地区ID不能为空")
    @NotBlank(message = "地区ID不能为空")
    @ApiModelProperty(name = "areaCode",notes = "地区Id",dataType = "String")
    private String areaCode;

}
