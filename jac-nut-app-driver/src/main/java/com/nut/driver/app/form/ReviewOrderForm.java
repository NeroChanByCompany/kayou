package com.nut.driver.app.form;;

import com.nut.common.annotation.EmojiForbid;
import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Description: 评价服务站
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form;
 * @Author: yzl
 * @CreateTime: 2021-06-23 16:32
 * @Version: 1.0
 */
@NutFormValidator
@Data
@ApiModel(value = "工单评价Form")
public class ReviewOrderForm extends BaseForm {

    /**
     * 工单号
     */
    @NotBlank(message = "工单号不能为空")
    @ApiModelProperty(value = "woCode", notes = "工单号", dataType = "String")
    private String woCode;

    /**
     * 服务站ID
     */
    @NotBlank(message = "服务站ID不能为空")
    @ApiModelProperty(value = "stationId", notes = "服务站ID", dataType = "String")
    private String stationId;

    /**
     * 其他建议
     */
    @EmojiForbid(message = "不能输入Emoji表情！")
    @Length(max = 140, message = "文字长度限制140个字")
    @ApiModelProperty(value = "content", notes = "其他建议", dataType = "String")
    private String content;

    /**
     * 总体满意程度星级
     */
    @NotBlank(message = "请对服务站进行评分")
    @Pattern(regexp = RegexpUtils.ONE__TO_FIVE_NATURAL_NUMBER, message = "总体满意程度星级格式不正确")
    @ApiModelProperty(value = "wholeStar", notes = "总体满意程度星级", dataType = "String")
    private String wholeStar;

    /**
     * 评价标签
     */
    @Length(max = 500, message = "评价标签长度限制500个字")
    @ApiModelProperty(value = "reviewLabel", notes = "评价标签", dataType = "String")
    private String reviewLabel;

    /**
     * 其他补充标签
     */
    @EmojiForbid(message = "不能输入Emoji表情！")
    @Length(max = 140, message = "文字长度限制140个字")
    @ApiModelProperty(value = "otherLabel", notes = "其他补充标签", dataType = "String")
    private String otherLabel;

    /**
     * 是否自费（1：是；其他：免费）
     */
    @ApiModelProperty(value = "selfPay", notes = "是否自费（1：是；其他：免费）", dataType = "String")
    private String selfPay;

    /**
     * 自费费用（1位小数）
     */
    @ApiModelProperty(value = "cost", notes = "自费费用（1位小数）", dataType = "String")
    private String cost;

    /**
     * 是否再次光临
     */
    @Pattern(regexp = "^[01]?$", message = "是否再次光临格式不正确")
    @ApiModelProperty(value = "comeAgain", notes = "是否再次光临", dataType = "String")
    private String comeAgain;

    /**
     * 不满意的流程
     */
    @ApiModelProperty(value = "discontent", notes = "不满意的流程", dataType = "String")
    private String discontent;

}
