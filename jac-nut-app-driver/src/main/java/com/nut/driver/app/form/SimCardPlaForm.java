package com.nut.driver.app.form;/**
 * Created by Administrator on 2021/11/24.
 */

import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @version v1.0.0
 * @desc
 * @auther wangshuai
 * @create 2021/11/24
 * @Company esv
 */
@Data
@ApiModel("Sim卡流量查询Form")
public class SimCardPlaForm extends BaseForm {

    @ApiModelProperty(name = "simNumber",notes = "Sim卡号",dataType = "String")
    private String simNumber;
}
