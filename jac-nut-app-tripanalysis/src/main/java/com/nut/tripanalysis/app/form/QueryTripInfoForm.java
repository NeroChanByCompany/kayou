package com.nut.tripanalysis.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 查询行程详细信息接口
 */
@Data
@NutFormValidator
public class QueryTripInfoForm extends BaseForm {

    @NotBlank(message = "通信号不能为空")
    private String terminalId;

    @NotBlank(message = "行程开始时间不能为空")
    private Long startTime;

    @NotBlank(message = "行程ID不能为空")
    private String tripId;

}
