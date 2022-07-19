package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Description: 供司机app/车队app/车队web调用的司机车队相关推送接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-28 10:49
 * @Version: 1.0
 */
@NutFormValidator
@Data
public class PushDriverOwnerMsgForm extends BaseForm {
    @NotBlank(message = "stype不能为空")
    private String stype;

    private Long managerId;
    private String carId;
    private String carNum;
    private String driverId;
    private String oldCarNum;
    private String oldTeamName;
    private String receiverId;
    private String senderId;
    private Long teamId;
    private String teamName;
    private List<String> carIdList;
    private String role;
}
