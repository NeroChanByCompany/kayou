package com.nut.driver.app.form;


import com.nut.common.annotation.EmojiForbid;
import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Description: 服务预约
 */
@Data
@NutFormValidator
public class ApplyOrderForm extends BaseForm {
    /**
     * 工单类型
     */
    @NotNull(message = "工单类型不能为空")
    private Integer woType;

    /**
     * 车辆位置
     */
    private String carLocation;

    /**
     * 车辆经度
     */
    private String carLon;

    /**
     * 车辆纬度
     */
    private String carLat;

    /**
     * 预约人
     */
    @NotBlank(message = "请您输入预约人")
    @Length(max = 20, message = "预约人最多可输入20个字")
    private String appoUserName;
    /**
     * 预约人电话 必须11位数字，不能为非数字字符
     */
    @NotBlank(message = "请您输入联系电话")
    @Pattern(regexp = RegexpUtils.MOBILE_PHONE_REGEXP, message = "请您输入正确的联系电话")
    private String appoUserPhone;
    /**
     * 预约车辆id
     */
    @NotBlank(message = "请选择您要预约的车辆")
    private String carId;
    /**
     * 服务站id
     */
    @NotBlank(message = "请选择您要预约的服务站")
    private String stationId;
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
     * 预约到站时间 进出站工单必填，格式yyyy-mm-dd HH:mm:ss
     */
    @NotBlank(message = "请选择您的预约时间")
    @Pattern(regexp = RegexpUtils.DATE_BARS_REGEXP_HOUR_MIN_SECOND, message = "预约到站时间格式不正确")
    private String appoArriveTime;
    /**
     * 故障描述 故障描述最多可输入100个字或字符
     */
    @EmojiForbid(message = "请您输入正确的描述！")
    @Length(max = 100, message = "描述最多可输入100字")
    private String userComment;
}
