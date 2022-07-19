package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 获取配件列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-28 16:29
 * @Version: 1.0
 */
@Data
@ApiModel("配件列表实体")
public class QuerySpareStockForm extends BaseForm {
    /**
     * 搜索类型
     * 0：按备件代码搜索
     * 1：按设备名称搜索
     */
    @ApiModelProperty(name = "searchType" , notes = "搜索类型" , dataType = "Integer")
    private Integer searchType;

    /**
     * 搜索条件
     */
    @ApiModelProperty(name = "searchInfo" , notes = "搜索条件" , dataType = "String")
    private String searchInfo;

    /**
     * 经度
     */
    @ApiModelProperty(name = "lon" , notes = "经度" , dataType = "String")
    private String lon;

    /**
     * 纬度
     */
    @ApiModelProperty(name = "lat" , notes = "纬度" , dataType = "String")
    private String lat;
}
