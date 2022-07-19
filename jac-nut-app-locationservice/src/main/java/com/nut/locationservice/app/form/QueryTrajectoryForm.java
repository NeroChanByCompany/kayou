package com.nut.locationservice.app.form;

import com.nut.common.annotation.NutFormValidator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.form
 * @Author: yzl
 * @CreateTime: 2021-06-17 13:43
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
@ToString
@NutFormValidator
public class QueryTrajectoryForm {
    /**
     * 通信号集合
     */
    @NotNull(message = "通信号不能为空")
    @ApiModelProperty(name = "terminalId", notes = "终端Id", dataType = "Long")
    private Long terminalId;

    @NotNull(message = "开始时间不能为空")
    @ApiModelProperty(name = "startTime", notes = "开始时间", dataType = "Long")
    private Long startTime;

    @NotNull(message = "结束时间不能为空")
    @ApiModelProperty(name = "endTime", notes = "结束时间", dataType = "Long")
    private Long endTime;
}
