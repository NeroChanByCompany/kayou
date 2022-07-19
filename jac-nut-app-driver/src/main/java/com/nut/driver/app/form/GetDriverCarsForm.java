package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: 根据用户ID 查询作为司机的车辆和管理车队的车辆接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.truckingteam.app
 * @Author: yzl
 * @CreateTime: 2021-06-27 13:47
 * @Version: 1.0
 */
@NutFormValidator
@Data
public class GetDriverCarsForm extends BaseForm {
    @NotNull(message = "用户ID不能为空")
    private Long driverId; // 用户ID

    private String returnAll; // 返回所有结果（1：不需要分页）

    private String key; //搜索车牌号或VIN后八位
}
