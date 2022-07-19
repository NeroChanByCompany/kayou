package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author liuBing
 * @Classname UploadUserPicUrlForm
 * @Description TODO
 * @Date 2021/6/17 15:55
 */
@Data
@Accessors(chain = true)
@NutFormValidator
@ApiModel("APP更换头像Entity")
public class UploadUserPicUrlForm extends BaseForm {
    @NotNull(message = "头像地址不能为空")
    @NotBlank(message = "头像地址不能为空")
    @ApiModelProperty(name = "userPicUrl",notes = "头像地址",dataType = "String")
    private String userPicUrl;
}
