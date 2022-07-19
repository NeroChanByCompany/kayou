package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Description: APP端登录
 */
@Data
@ApiModel("APP端登出Entity")
@NutFormValidator
public class AppLogoutForm extends BaseForm {


}
