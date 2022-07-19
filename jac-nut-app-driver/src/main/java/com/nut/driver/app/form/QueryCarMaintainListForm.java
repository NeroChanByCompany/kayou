package com.nut.driver.app.form;


import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuBing
 * @Classname QueryCarMaintainListCommand
 * @Description TODO
 * @Date 2021/6/23 17:07
 */
@Data
@ApiModel("推荐保养列表接口")
public class QueryCarMaintainListForm extends BaseForm {
    /**
     * 车牌号/底盘号
     */
    @ApiModelProperty(name = "keyword",notes = "车牌号/底盘号",dataType = "String")
    private String keyword;
}
