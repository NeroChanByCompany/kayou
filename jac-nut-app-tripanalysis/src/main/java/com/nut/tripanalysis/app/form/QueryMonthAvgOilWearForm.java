package com.nut.tripanalysis.app.form;



import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 查询本月油耗排行榜接口
 */
@Data
public class QueryMonthAvgOilWearForm extends BaseForm {

    /**
     * 获取车队版我的车辆排行请传车型ID
     */
    @NotBlank(message = "车型不能为空")
    @NotNull(message = "车型不能为空")
    private String modelId;

}
