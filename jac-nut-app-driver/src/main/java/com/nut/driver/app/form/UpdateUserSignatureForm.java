package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @Description: 用户签名
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-28 09:02
 * @Version: 1.0
 */
@Data
@NutFormValidator
public class UpdateUserSignatureForm extends BaseForm {
    /**
     * 签名
     */
    @ApiModelProperty(name = "signature",notes = "签名",dataType = "String")
    @Length(max = 50, message = "签名太长了")
    private String signature;
}
