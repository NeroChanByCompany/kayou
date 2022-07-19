package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 结束维修接口
 * Created by Administrator on 2018/5/10.
 */
@Data
@NutFormValidator
public class EndRepairForm extends BaseForm {

    /** 工单号 */
    @NotBlank(message = "工单号不能为空")
    private String woCode;
    /**
     * 轨迹点丢失强制允许结束标识
     * 1：不进行轨迹点完整性校验（前端轨迹点丢失时使用）
     */
    private String forceEnd;
    //兼容老版本
    private String versionCode;

    /**
     * 返站时里程
     */
    @NotNull(message = "里程数不能为空")
    @Pattern(regexp = RegexpUtils.INTEGE_FLOAT, message = "里程数格式不正确")
    private String endMileage;

    /**
     * 返站时地址
     */
    @NotNull(message = "返站地址不可为空")
    private String endAddress;

    /**
     * 一次实际往返里程
     */
    @Pattern(regexp = RegexpUtils.INTEGE_FLOAT, message = "里程数格式不正确")
    private String appOutMileage;

}
