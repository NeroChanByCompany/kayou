package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @author liuBing
 * @Classname UserCarsForm
 * @Description TODO
 * @Date 2021/6/24 17:33
 */
@Data
@NutFormValidator
public class UserCarsForm extends BaseForm {
    /**
     * 搜索关键字
     */
    @ApiModelProperty(name = "keyword",notes = "搜索关键字",dataType = "String")
    private String keyword;
    /**
     * 返回全部数据（1：是）
     */
    @ApiModelProperty(name = "returnAll",notes = "返回全部数据（1：是）",dataType = "String")
    private String returnAll;
    /**
     * 车辆状态（逗号分隔）
     */
    @ApiModelProperty(name = "onlineStatus",notes = "车辆状态",dataType = "String")
    private String onlineStatus;
    /**
     * 管理角色（逗号分隔）
     */
    @ApiModelProperty(name = "role",notes = "管理角色",dataType = "String")
    private String role;
    /**
     * 车队（逗号分隔）
     */
    @ApiModelProperty(name = "team",notes = "车队",dataType = "String")
    private String team;
    /**
     * 只返回列表数据
     */
    @ApiModelProperty(name = "listOnly",notes = "只返回列表数据",dataType = "String")
    private String listOnly;
    /**
     * 只返回静态数据
     */
    @ApiModelProperty(name = "staticOnly",notes = "只返回静态数据",dataType = "String")
    private String staticOnly;
    /**
     * 排除的车队ID
     */
    @ApiModelProperty(name = "exclusiveTeamId",notes = "排除的车队Id",dataType = "Long")
    private Long exclusiveTeamId;

    /**
     * 服务站ID
     */
    @ApiModelProperty(value = "stationId",notes = "服务站ID",dataType = "String")
    @Pattern(regexp = RegexpUtils.POSITIVE_INTEGER_REGEXP, message = "服务站ID格式不正确")
    private String stationId;
}
