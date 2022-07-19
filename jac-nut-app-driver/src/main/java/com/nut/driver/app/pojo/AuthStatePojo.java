package com.nut.driver.app.pojo;

import com.nut.common.annotation.NutFormValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author liuBing
 * @Classname AuthStatePojo
 * @Description TODO
 * @Date 2021/9/2 11:26
 */
@Data
@ApiModel(value = "AuthStatePojo", description = "用户认证状态变更参数")
@Accessors(chain = true)

public class AuthStatePojo {
    /**
     * 登记物联卡号
     */
    @ApiModelProperty(name = "simId" , notes = "登记物联卡号" , dataType = "String",required = false)
    private String simId;
    /**
     * 登记人身份证号
     */
    @ApiModelProperty(name = "iccId" , notes = "iccId不能为空" , dataType = "String",required = false)
    private String iccId;
    /**
     * 底盘号
     */
    @ApiModelProperty(name = "carVin" , notes = "车辆底盘号" , dataType = "String",required = false)
    private String carVin;
}
