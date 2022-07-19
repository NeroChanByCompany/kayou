package com.nut.driver.app.form;


import com.nut.common.annotation.EmojiForbid;
import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @Description: 获取服务站列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.form
 * @Author: yzl
 * @CreateTime: 2021-06-23 10:37
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("服务站实体")
public class QueryStationForm extends BaseForm {
    /**
     * 距离
     */
    @ApiModelProperty(name = "distance" , notes = "距离" , dataType = "String")
    private String distance;

    /**
     * 服务站等级（星级：共5星等级）
     */
    @ApiModelProperty(name = "level" , notes = "服务站等级" , dataType = "String")
    private String level;
    @EmojiForbid(message = "不能输入Emoji表情！")

    @ApiModelProperty(name = "keyWord" , notes = "关键字" , dataType = "String")
    private String keyWord;

    /**
     * 服务站点所属城市id
     */
    @ApiModelProperty(name = "id" , notes = "服务站点所属城市id" , dataType = "String")
    private String id;

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

    /**
     * 服务站类型
     * 0：全部
     * 1：核心服务站
     *
     */
    @ApiModelProperty(name = "centralFlag" , notes = "服务站类型" , dataType = "String")
    private String centralFlag;

    /**
     * 1：司机端，2：车主端
     */
    @Pattern(regexp ="^[1|2]", message = "客户端标记不正确,必须为1或2")
    @ApiModelProperty(name = "clientFlg" , notes = "客户端标记" , dataType = "String")
    private String clientFlg;

    /**
     * 排序
     * 0：距离优先
     * 1：星级优先
     */
    @ApiModelProperty(name = "sortType" , notes = "排序" , dataType = "Integer")
    private Integer sortType = 0;
}
