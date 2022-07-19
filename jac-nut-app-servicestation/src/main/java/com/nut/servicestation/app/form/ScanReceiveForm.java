package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Description: 扫码接车
 */
@Data
@NutFormValidator
public class ScanReceiveForm extends BaseForm {
    @NotBlank(message = "底盘号不能为空")
    @Pattern(regexp = RegexpUtils.EIGHT_CHASSIS_NUMBER, message = "请输入8位底盘号")
    private String chassisNum;

    @NotBlank(message = "工单号不能为空")
    private String woCode;

    @NotBlank(message = "手机经度不能为空")
    private String phoneLon;

    @NotBlank(message = "手机纬度不能为空")
    private String phoneLat;

    /** 指派人员ID*/
    private String assignTo;

    /**
     * 手机识别码
     */
    private String phoneId;

    /**
     * 故障车当前里程数
     */
    private String milage4fault;

    /**
     * 送修人姓名
     */
    @Length(message = "送修人姓名最多可输入20个字", max = 20)
//    @NotBlank(message = "送修人姓名不能为空")
    private String shipperName;

    /**
     * 送修人电话
     */
    @Pattern(regexp = RegexpUtils.MOBILE_PHONE_REGEXP, message = "手机号格式不正确")
//    @NotBlank(message = "送修人电话不能为空")
    private String shipperTel;

    /**
     * 救援轨迹点最大序列号
     */
    private Integer rpMaxIndex;

    /** 操作唯一标识 */
    private String operateId;

    /** 照片总数量 */
    private Integer photoNum;

}

