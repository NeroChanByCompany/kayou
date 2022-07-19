package com.nut.servicestation.app.form;


import com.nut.common.base.BaseForm;
import lombok.Data;

/**
 * 积分添加模型
 */
@Data
public class IntegralForReceiveForm extends BaseForm {
    /**
     * 用户ucId
     */
    private String ucId;

    /**
     * 操作动作
     * 0：添加积分，暂定默认为添加动作
     */
    private Integer operationId;

    /**
     * 添加积分类型，暂定为预约服务并接车后进行积分添加
     */
    private Integer addType;



}
