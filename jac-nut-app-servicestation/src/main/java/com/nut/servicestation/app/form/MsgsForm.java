package com.nut.servicestation.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Description: 消息列表
 */
@Data
@NutFormValidator
public class MsgsForm extends BaseForm {

    /**
     * 消息展示分类 1：车队消息，2：个人消息，3：工单消息，4：通知类消息
     */
    @NotBlank(message = "消息展示分类不能为空")
    private String pushShowType;
}
