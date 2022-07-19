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
 * @Description: 轨迹抽析
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-30 09:13
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("轨迹实体")
public class QueryTrackForm extends BaseForm {
    @NotNull(message = "车辆ID不能为空")
    @NotBlank(message = "车辆ID不能为空白")
    @ApiModelProperty(name = "carId" , notes = "车辆ID" , dataType = "String")
    private String carId;

    @Pattern(message = "beginDate格式不合法", regexp = RegexpUtils.DATE_BARS_REGEXP_HOUR_MIN_SS)
    @ApiModelProperty(name = "beginDate" , notes = "统计开始日期" , dataType = "String")
    @NotBlank(message = "beginDate不能为空")
    private String beginDate;

    @Pattern(message = "endDate格式不合法", regexp = RegexpUtils.DATE_BARS_REGEXP_HOUR_MIN_SS)
    @ApiModelProperty(name = "endDate" , notes = "统计结束日期" , dataType = "String")
    @NotBlank(message = "endDate不能为空")
    private String endDate;

    @NotNull(message = "zoom不能为空")
    @NotBlank(message = "zoom不能为空白")
    @Pattern(message = "zoom必须是0~18之间的整数", regexp = RegexpUtils.NATURAL_NUMBER)
    @ApiModelProperty(name = "zoom" , notes = "地图缩放级别" , dataType = "String")
    private String zoom;
}
