package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author: jiangcm
 * @Description: 维修项列表
 * @Date: Created in 2018/5/7 17:53
 * @Modified By:
 */
@Data
@NutFormValidator
public class RepairRecordsForm extends BaseForm {
    /**
     * 工单号
     */
    @NotNull(message = "工单号不能为空")
    @NotBlank(message = "工单号不能为空")
    private String woCode;


}

