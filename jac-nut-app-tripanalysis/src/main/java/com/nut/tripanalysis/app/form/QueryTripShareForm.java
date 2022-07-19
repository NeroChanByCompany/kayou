package com.nut.tripanalysis.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NutFormValidator
public class QueryTripShareForm extends BaseForm {

    @NotBlank(message = "通信号不能为空")
    private String terminalId;

    @NotNull(message = "时间不能为空")
    private Long startTime;

}
