package com.nut.servicestation.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 配件查询接口
 */
@Data
@NutFormValidator
public class PartForm extends BaseForm {
    /**
     * 配件代码
     */
    private String partCode;
    /**
     * 配件名称
     */
    private String partName;
    /**
     * 配件唯一码
     */
    private String partOnlyCode;
    /**
     * vin
     */
    @NotBlank(message = "vin不能为空")
    private String vin;
    /**
     * 服务商代码
     */
    private String dealerCode;
    /**
     * 服务商简称
     */
    private String dealerShortname;

    private String items;

}
