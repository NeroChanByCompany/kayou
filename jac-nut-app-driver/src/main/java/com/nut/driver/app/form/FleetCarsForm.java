package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 车队车辆列表接口
 */
@Data
@NutFormValidator
@ApiModel("app车队车辆列表")
public class FleetCarsForm extends BaseForm {
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
     * 排除的司机ID
     */
    @ApiModelProperty(name = "exclusiveDriverId" , notes = "排除的司机ID" , dataType = "Long")
    private Long exclusiveDriverId;


    @Override
    public String toString() {
        return "FleetCarsCommand{" +
                "teamId=" + teamId +
                ", keyword='" + keyword + '\'' +
                ", returnAll='" + returnAll + '\'' +
                ", exclusiveDriverId=" + exclusiveDriverId +
                "} " + super.toString();
    }

}
