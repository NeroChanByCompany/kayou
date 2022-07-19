package com.nut.servicestation.app.form;


import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Description: 上传外出救援轨迹点接口
 */
@Data
public class UploadRescueRoutePointForm extends BaseForm {
    /**
     * 工单号
     */
    @NotBlank(message = "工单号不能为空")
    private String woCode;
    /**
     * 轨迹点序号
     */
    @NotBlank(message = "轨迹点序号不能为空")
    @Pattern(regexp = RegexpUtils.NUMBER_CHECK, message = "轨迹点序号只能为数字")
    private String index;
    /**
     * 轨迹点纬度
     */
    @NotBlank(message = "轨迹点纬度不能为空")
//    @Pattern(regexp = "^(\\d{1,3})(\\.\\d+)?$", message = "轨迹点纬度格式不正确")
    private String latitude;
    /**
     * 轨迹点经度
     */
    @NotBlank(message = "轨迹点经度不能为空")
//    @Pattern(regexp = "^(\\d{1,3})(\\.\\d+)?$", message = "轨迹点经度格式不正确")
    private String longitude;
    /**
     * 轨迹点时间
     */
    @NotBlank(message = "轨迹点时间不能为空")
    @Pattern(regexp = RegexpUtils.NUMBER_CHECK, message = "轨迹点时间只能为数字")
    private String time;
    /**
     * 退出登录标识（1:发生退出登录）
     */
    @Pattern(regexp = "^[01]?$", message = "退出登录标识格式不正确")
    private String isLogout;
    /**
     * 定位精度
     */
    private String radius;

}
