package com.nut.servicestation.app.form;

import com.nut.common.annotation.EmojiForbid;
import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 服务站取消救援接口
 */
@Data
@NutFormValidator
public class RescueCancelForm extends BaseForm {
    /**
     * 工单号
     */
    @NotBlank(message = "工单号不能为空")
    private String woCode;
    /**
     * 取消原因
     */
    @NotBlank(message = "取消原因不能为空")
    @Length(max = 300, message = "取消原因最多可输入300个字或字符")
    @EmojiForbid(message = "取消原因含有非法字符")
    private String reason;
    /**
     * 取消救援经度（3位整数6位小数）
     */
    @NotBlank(message = "取消救援经度不能为空")
    @Length(max = 20, message = "取消救援经度长度超出限制")
    private String lon;
    /**
     * 取消救援纬度（3位整数6位小数）
     */
    @NotBlank(message = "取消救援纬度不能为空")
    @Length(max = 20, message = "取消救援纬度长度超出限制")
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

}
