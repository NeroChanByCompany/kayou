package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import lombok.Data;

/**
 * @Description: 查询签到统计
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-08-09 16:07
 * @Version: 1.0
 */
@Data
public class QuerySignNumberForm extends BaseForm {
    private Integer year;

    private Integer month;

    private String ucId;

}
