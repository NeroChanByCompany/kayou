package com.nut.driver.app.form;

import com.baomidou.mybatisplus.annotation.TableId;
import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.driver.app.pojo.AuthStatePojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author liuBing
 * @Classname authLogEntity
 * @Description TODO 用户实名认证日志表
 * @Date 2021/8/10 11:24
 */
@Data
@ApiModel("用户实名认证状态变更参数接收")
@Accessors(chain = true)

public class AuthStateForm extends BaseForm {

    /**
     * 认证信息集合
     */
    @ApiModelProperty(name = "authStatePojos" , notes = "认证信息集合" , dataType = "list",required = false)
    private List<AuthStatePojo> authStatePojos;
}
