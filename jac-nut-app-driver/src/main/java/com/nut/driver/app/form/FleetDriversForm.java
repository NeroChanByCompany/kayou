package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 车队司机列表接口
 */
@Data
@NutFormValidator
@ApiModel("app车队司机列表Entity")
public class FleetDriversForm extends BaseForm {
    /**
     * 车队ID
     */
    @NotNull(message = "车队ID不能为空")
    @ApiModelProperty(name = "teamId" , notes = "车队ID" , dataType = "Long")
    private Long teamId;
    /**
     * 搜索关键字
     */
    @ApiModelProperty(name = "keyword" , notes = "关键字" , dataType = "String")
    private String keyword;
    /**
     * 返回全部数据（1：是）
     */
    @ApiModelProperty(name = "returnAll" , notes = "是否返回全部数据" , dataType = "String")
    private String returnAll;
    /**
     * 排除的车辆ID
     */
    @ApiModelProperty(name = "exclusiveCarId" , notes = "排除的车辆ID" , dataType = "String")
    private String exclusiveCarId;


    @Override
    public String toString() {
        return "FleetDriversCommand{" +
                "teamId=" + teamId +
                ", keyword='" + keyword + '\'' +
                ", returnAll='" + returnAll + '\'' +
                ", exclusiveCarId='" + exclusiveCarId + '\'' +
                "} " + super.toString();
    }

}
