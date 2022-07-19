package com.nut.locationservice.app.form;

import com.nut.common.annotation.NutFormValidator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description: 根据终端ID 时间 获取对应的地理 坐标
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.form
 * @Author: yzl
 * @CreateTime: 2021-06-16 17:54
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
@ToString
@NutFormValidator
public class GetCarLocationForm  implements Serializable {
    // TODO: 2021/6/22 有坑

    @ApiModelProperty(name = "vins", notes = "车辆底盘号", dataType = "String")
    @NotNull(message = "vins不能为空")
    @NotBlank(message = "vins不能为空白")
    private String vins;

    /**
     * 业务来源，eol:未下线车辆体检
     */
    @ApiModelProperty(name = "carSource", notes = "业务来源", dataType = "String")
    private String carSource;

}
