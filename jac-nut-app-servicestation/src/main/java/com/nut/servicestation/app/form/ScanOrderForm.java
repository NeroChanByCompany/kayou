package com.nut.servicestation.app.form;

import com.nut.common.base.BaseForm;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @Description: 扫码查单
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.form
 * @Author: yzl
 * @CreateTime: 2021-08-20 10:20
 * @Version: 1.0
 */
@Data
public class ScanOrderForm extends BaseForm {

    /**
     * 底盘号
     */
    @NotNull(message = "底盘号不能为空")
    private String chassisNum;
}
