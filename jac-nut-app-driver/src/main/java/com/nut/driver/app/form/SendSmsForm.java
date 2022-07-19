package com.nut.driver.app.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 发送短信接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.form
 * @Author: yzl
 * @CreateTime: 2021-06-16 09:17
 * @Version: 1.0
 */
@Data
@ApiModel("APP发送短信Entity")
public class SendSmsForm {

    @ApiModelProperty(name = "phone",notes = "手机号",dataType = "String")
    private String phone; // 手机号（必填）
    @ApiModelProperty(name = "content",notes = "短信内容",dataType = "String")
    private String content; // 短信内容（必填）
    @ApiModelProperty(name = "sign",notes = "签名",dataType = "String")
    private String sign; // 签名（可选）

}
