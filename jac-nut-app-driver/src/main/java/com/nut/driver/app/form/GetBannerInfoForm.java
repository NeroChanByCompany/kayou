package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 车队/司机APP首页广告轮播
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-28 15:34
 * @Version: 1.0
 */
@Data
@NutFormValidator
public class GetBannerInfoForm extends BaseForm {
    @NotNull(message = "APP类型不能为空")
    @NotBlank(message = "APP类型不能为空")
    @ApiModelProperty(name = "appType", notes = "app类型", dataType = "String")
    private String appType;

    @Override
    public String getAppType() {
        return appType;
    }

    @Override
    public void setAppType(String appType) {
        this.appType = appType;
    }
}
