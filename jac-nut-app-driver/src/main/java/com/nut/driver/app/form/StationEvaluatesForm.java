package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Description: 评价列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.form
 * @Author: yzl
 * @CreateTime: 2021-06-23 14:43
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel
public class StationEvaluatesForm extends BaseForm {
    /**
     * 服务站ID
     */
    @NotNull(message = "服务站ID不能为空")
    @Pattern(regexp = RegexpUtils.ZERO_NEGATIVE_INTEGERS_REGEXP, message = "服务站ID数据类型不正确")
    @ApiModelProperty(value = "stationId", notes = "服务站ID", dataType = "String")
    private String stationId;

    /**
     * 司机ID
     */
    @NotNull(message = "司机ID不能为空")
    @NotBlank(message = "司机ID不能为空")
    @ApiModelProperty(value = "driverId", notes = "司机ID", dataType = "String")
    private String driverId;

    /**
     * 查询类型
     */
    @NotNull(message = "查询类型不能为空")
    @Pattern(regexp = RegexpUtils.ONE__TO_TWO_NATURAL_NUMBER, message = "查询类型只能为1或者2")
    @ApiModelProperty(value = "flag", notes = "查询类型", dataType = "String")
    private String flag;

    /**
     * 工单号
     */
    @ApiModelProperty(value = "woCode", notes = "工单号", dataType = "String")
    private String woCode;

    /**
     * 车辆vin码
     */
    @ApiModelProperty(value = "vin", notes = "车辆vin码", dataType = "String")
    private String vin;

}
