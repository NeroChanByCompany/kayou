package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import io.swagger.annotations.Api;
import lombok.Data;

/**
 * @Description: 油耗异常接收参数
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.form
 * @Author: yzl
 * @CreateTime: 2021-07-23 17:41
 * @Version: 1.0
 */
@Data
public class OilFaultForm extends BaseForm {

    private String chassisNum;

    private String dateStr;

}
