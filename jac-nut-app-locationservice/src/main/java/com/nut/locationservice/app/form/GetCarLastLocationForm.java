package com.nut.locationservice.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Description: 根据通信号获取车辆末次位置信息
 *               只返回经纬度信息，不包含其他额外信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.form
 * @Author: yzl
 * @CreateTime: 2021-06-17 11:08
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
@ToString
@NutFormValidator
public class GetCarLastLocationForm extends BaseForm implements Serializable {
    /**
     * 通信号集合
     */
    @NotNull(message = "通信号不能为空")
    @ApiModelProperty(name = "terminalIdList", notes = "终端ID列表", dataType = "List<Long>")
    private List<Long> terminalIdList;
}
