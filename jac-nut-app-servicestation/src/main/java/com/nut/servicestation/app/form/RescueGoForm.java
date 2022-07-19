package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 外出救援立即出发
 *
 * @author Sunyu
 * @date 2018/5/14
 */
@Data
@NutFormValidator
public class RescueGoForm extends BaseForm {
    /**
     * 工单号
     */
    @NotBlank(message = "工单号不能为空")
    private String woCode;

    /**
     * 外出人员姓名
     */
    @NotBlank(message = "请填写外出人员姓名")
    @Length(max = 20, message = "外出人员姓名最多可输入20个字或字符")
    private String name;

    /**
     * 外出人员手机
     */
    @NotNull(message = "请填写外出人员手机号")
    @Pattern(regexp = RegexpUtils.MOBILE_PHONE_REGEXP, message = "手机号格式错误")
    private String phone;

    /**
     * 外出人数
     */
    @NotNull(message = "请选择外出人数")
    @Pattern(regexp = "^[1-3]$", message = "外出人数格式错误")
    private String num;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 外出时手机定位经度
     */
    @NotNull(message = "经度不能为空")
    @Pattern(regexp = RegexpUtils.LON_REGEXP, message = "经度格式不正确")
    private String lon;

    /**
     * 外出时手机定位纬度
     */
    @NotNull(message = "纬度不能为空")
    @Pattern(regexp = RegexpUtils.LAT_REGEXP, message = "纬度格式不正确")
    private String lat;

    /**
     * 外出车辆gps设备号
     */
    private String gpsDeviceNo;

    /**
     * 外出时里程
     */
    @NotNull(message = "里程数不能为空")
    @Pattern(regexp = RegexpUtils.INTEGE_FLOAT, message = "里程数格式不正确")
    private String startMileage;

    /**
     * 外出时地址
     */
    @NotNull(message = "出发地址不可为空")
    private String startAddress;

    /** 操作唯一标识 */
    @NotBlank(message = "操作唯一标识不能为空")
    private String operateId;

    /** 照片总数量 */
    @NotNull(message = "照片总数量不能为空")
    private Integer photoNum;

}