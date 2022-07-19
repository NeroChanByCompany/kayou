package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 扫一扫/根据车辆查询工单
 */
@Data
@NutFormValidator
public class ScanWoForm extends BaseForm {

    /**
     * 底盘号
     */
    @NotNull(message = "底盘号不能为空")
    @Length(min = 1, max = 8, message = "底盘号最多8位")
    private String chassisNum;

}