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
 * @Description: 申请关闭工单
 */
@Data
@NutFormValidator
public class ApplyCloseWoForm extends BaseForm {
    /**
     * 工单号
     */
    @NotNull(message = "工单号不能为空！")
    @NotBlank(message = "工单号不能为空！")
    private String woCode;
    /**
     * 申请关闭类型
     * 1、缺配件
     * 2、无技术能力
     * 3、无法安排人/车
     * 4、保内自行处理
     * 5、保外另行处理
     * 6、其它
     */
    @NotNull(message = "关闭类型不能为空！")
    @NotBlank(message = "关闭类型不能为空！")
    @Pattern(regexp = RegexpUtils.ONE__TO_SIX_NATURAL_NUMBER, message = "关闭类型不正确！")
    private String closeType;
    /**
     * 申请关闭原因
     */
    @NotNull(message = "申请关闭原因不能为空！")
    @NotBlank(message = "申请关闭原因不能为空！")
    @Length(max = 300, message = "关闭原因最多可输入300个字或字符！")
    private String closeReason;
    /**
     * 申请关闭时手机定位经度（3位整数6位小数）
     */
    @Length(max = 20, message = "经度长度超出限制")
    private String lon;
    /**
     * 申请关闭时手机定位纬度（3位整数6位小数）
     */
    @Length(max = 20, message = "纬度长度超出限制")
    private String lat;
    /**
     * 救援轨迹点最大序列号
     */
    private Integer rpMaxIndex;
    /**
     * 轨迹点丢失强制允许结束标识
     * 1：不进行轨迹点完整性校验（前端轨迹点丢失时使用）
     */
    private String forceEnd;
//    @NotNull(message = "设备Id不能为空")
//    @NotBlank(message = "设备Id不能为空")
    private String deviceId;

}
