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
 *               只供服务站附近车辆查询使用
 *               其他额外信息返回
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.form
 * @Author: yzl
 * @CreateTime: 2021-06-17 11:17
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
@ToString
@NutFormValidator
public class GetCarLocationToStationForm extends BaseForm implements Serializable {

    /**
     * 通信号集合
     */
    @NotNull(message = "通信号不能为空")
    @ApiModelProperty(name = "terminalIdList", notes = "终端ID列表", dataType = "List<Long>")
    private List<Long> terminalIdList;

    /**
     * 服务站经度
     */
    @NotNull(message = "服务站经度不能为空")
    @ApiModelProperty(name = "lon", notes = "服务站经度", dataType = "Double")
    private Double lon;
    /**
     * 服务站纬度
     */
    @NotNull(message = "服务站纬度不能为空")
    @ApiModelProperty(name = "lat", notes = "服务站纬度", dataType = "Double")
    private Double lat;
    /**
     * 查询范围半径
     */
    @NotNull(message = "查询范围不能为空")
    @ApiModelProperty(name = "range", notes = "查询范围半径", dataType = "int")
    private int range;

}
