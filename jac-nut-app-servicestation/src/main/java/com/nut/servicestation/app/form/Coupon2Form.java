package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * @Description: 优惠券发放
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.form
 * @Author: yzl
 * @CreateTime: 2021-07-26 10:09
 * @Version: 1.0
 */
@Data
@NutFormValidator
public class Coupon2Form extends BaseForm implements Serializable {

    // 优惠券适用类型：2-指定底盘号
    private String applicable;

    // 优惠券名称
    @NotBlank(message = "优惠券名称不能为空")
    @NotNull(message = "优惠券名称不能为空")
    private String couponName;

    // 优惠券内容
    @NotBlank(message = "优惠券内容不能为空")
    @NotNull(message = "优惠券内容不能为空")
    private String couponMessage;

    // 优惠券面额
    private String couponValue;

    // 优惠券有效期类型：1：不限时间；3：自定义时间
    @NotBlank(message = "优惠券有效期类型不能为空")
    @NotNull(message = "优惠券有效期类型不能为空")
    private String couponValid;

    // 自定义开始时间
    private String startTime;

    // 自定义结束时间
    private String endTime;

    // 指定底盘号
    @NotBlank(message = "底盘号不能为空")
    @NotNull(message = "底盘号不能为空")
    private String vin;

    private List<String> stationList;

    // 0-自定义服务站；1-全选服务站
    private String isAll;

    private String serviceCode;
}
