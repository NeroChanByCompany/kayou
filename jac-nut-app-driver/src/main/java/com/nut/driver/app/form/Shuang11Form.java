package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.driver.app.pojo.AppVersion;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 双十一活动积分发放
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-11-02 16:44
 * @Version: 1.0
 */
@Data
@NutFormValidator
public class Shuang11Form extends BaseForm {

    @NotNull(message = "活动类型不能为空")
    @NotBlank(message = "活动类型不能为空")
    // 0：浏览国六  1:浏览配件  2:浏览整车
    private Integer shuang11Type;

}
