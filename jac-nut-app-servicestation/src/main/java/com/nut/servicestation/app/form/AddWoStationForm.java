package com.nut.servicestation.app.form;

import com.nut.common.annotation.EmojiForbid;
import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 服务站建单接口
 */
@Data
@NutFormValidator
public class AddWoStationForm extends BaseForm {
    /**
     * 报修人姓名
     */
    @NotBlank(message = "报修人姓名不能为空")
    @EmojiForbid(message = "请输入正确的报修人姓名")
    @Length(max = 20, message = "报修人姓名最多可输入20个字或字符")
    private String appoUserName;
    /**
     * 报修人电话
     */
    @NotBlank(message = "报修人电话不能为空")
    @Pattern(regexp = RegexpUtils.MOBILE_PHONE_REGEXP, message = "报修人电话格式不正确")
    private String appoUserPhone;
    /**
     * 车辆ID
     */
    @NotBlank(message = "车辆ID不能为空")
    private String carId;
    /**
     * 维修项目 逗号分隔
     */
    @Length(max = 1000, message = "维修项目最多可输入1000个字")
    private String repairItem;
    /**
     * 保养项目 逗号分隔
     */
    @Length(max = 1000, message = "保养项目最多可输入1000个字")
    private String maintainItem;
    /**
     * 预约到站时间，格式yyyy-mm-dd HH:mm:ss
     */
    @NotBlank(message = "预约到站时间不能为空")
    @Pattern(regexp = RegexpUtils.DATE_BARS_REGEXP_HOUR_MIN_SECOND, message = "预约到站时间格式不正确")
    private String appoArriveTime;
    /**
     * 故障描述
     */
    @EmojiForbid(message = "故障描述含有非法字符")
    @Length(max = 300, message = "故障描述最多可输入300个字或字符")
    private String userComment;
    /**
     * 行驶里程，单位km
     */
    @NotBlank(message = "行驶里程不能为空")
    @Length(max = 10, message = "行驶里程超出长度限制")
    @Pattern(regexp = RegexpUtils.NON_NEGATIVE_DECIMAL_LESS_THAN_10B, message = "行驶里程格式不正确")
    private String mileage;

}
