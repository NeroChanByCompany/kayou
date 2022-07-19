package com.nut.truckingteam.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 根据车队TEAMID MODELID查询 车队所有车辆接口
 * Created by mal on 2017/9/25.
 */
@Data
@NutFormValidator
public class GetTeamCarsOilWearForm extends BaseForm {

    @NotNull(message = "车型ID不能为空")
    @NotBlank(message = "车型ID不能为空")
    private String modelId;

}
