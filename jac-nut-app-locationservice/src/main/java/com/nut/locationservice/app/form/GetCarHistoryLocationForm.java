package com.nut.locationservice.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author liuBing
 * @Classname GetCarHistoryLocationForm
 * @Description TODO
 * @Date 2021/6/16 10:44
 */
@Data
@Accessors(chain = true)
@ToString
@NutFormValidator
public class GetCarHistoryLocationForm extends BaseForm implements Serializable {
    private static final long serialVersionUID = 2705240244258896389L;

    @ApiModelProperty(name = "terminalId", notes = "终端ID", dataType = "Long")
    @NotNull(message = "车辆信息不能为空")
    private Long terminalId;
    @ApiModelProperty(name = "queryDate", notes = "查询时间", dataType = "Long")
    @NotNull(message = "查询时间不能为空")
    private Long queryDate;
    @ApiModelProperty(name = "index", notes = "索引", dataType = "int")
    @NotNull(message = "索引不能为空")
    private int index;
    @ApiModelProperty(name = "accessTocken", notes = "token", dataType = "Long")
    @NotNull(message = "token不能为空")
    private Long accessTocken;

}
