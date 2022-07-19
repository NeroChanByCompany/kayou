package com.nut.locationservice.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 查询位置云车辆状态接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.form
 * @Author: yzl
 * @CreateTime: 2021-06-17 11:25
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
@ToString
@NutFormValidator
public class GetOnlineStatusForm extends BaseForm {

    /**
     * 通信号数组
     */
    @ApiModelProperty(name = "tidList", notes = "通讯ID列表", dataType = "List<Long>")
    private List<Long> tidList;

    /**
     * vin数组
     */
    @ApiModelProperty(name = "vinList", notes = "底盘号列表", dataType = "List<String>")
    private List<String> vinList;

    /**
     * 查询方式（true：按通信号，false：按vin。）
     */
    @NotNull(message = "查询方式不能为空")
    @ApiModelProperty(name = "sourceFlag", notes = "查询方式", dataType = "Boolean")
    private Boolean sourceFlag;

    /**
     * 是否是高级查询（true：包含今日里程和今日油耗，false：普通查询。）
     */
    @ApiModelProperty(name = "flagStatus", notes = "查询方式", dataType = "Boolean")
    private Boolean flagStatus;

}
