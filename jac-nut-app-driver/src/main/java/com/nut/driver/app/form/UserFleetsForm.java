package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户车队列表接口
 */
@Data
@ApiModel("app车队列表Entity")
public class UserFleetsForm extends BaseForm {
    /**
     * 车队名称
     */
    @ApiModelProperty(name = "name" , notes = "车队名称" , dataType = "String")
    private String name;
    /**
     * 返回全部数据（1：是）
     */
    @ApiModelProperty(name = "returnAll" , notes = "是否返回全部数据" , dataType = "String")
    private String returnAll;


    @Override
    public String toString() {
        return "UserFleetsCommand{" +
                "name='" + name + '\'' +
                ", returnAll='" + returnAll + '\'' +
                "} " + super.toString();
    }

}
