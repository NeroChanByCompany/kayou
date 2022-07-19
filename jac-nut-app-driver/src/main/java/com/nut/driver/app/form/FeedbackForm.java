package com.nut.driver.app.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 意见反馈
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-28 10:03
 * @Version: 1.0
 */
@Data
@ApiModel(value = "意见反馈Form")
public class FeedbackForm {
    @ApiModelProperty(name = "id",notes = "",dataType = "Integer")
    private Integer id;
    @ApiModelProperty(name = "message",notes = "反馈信息",dataType = "String")
    private String message;
    @ApiModelProperty(name = "phone",notes = "电话",dataType = "String")
    private String phone;
    @ApiModelProperty(name = "createTime",notes = "创建时间",dataType = "Date")
    private Date createTime;
    @ApiModelProperty(name = "updateTime",notes = "更新时间",dataType = "Date")
    private Date updateTime;
    @ApiModelProperty(name = "createUser",notes = "创建者",dataType = "String")
    private String createUser;
    @ApiModelProperty(name = "updateUser",notes = "更新者",dataType = "String")
    private String updateUser;
}
