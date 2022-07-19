package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户联系人列表接口
 */
@Data
@ApiModel("app查询司机列表Entity")
public class UserContactsForm extends BaseForm {
    /**
     * 搜索关键字
     */
    @ApiModelProperty(name = "keyword" , notes = "关键字" , dataType = "String")
    private String keyword;
    /**
     * 排除的车队ID
     */
    @ApiModelProperty(name = "exclusiveTeamId" , notes = "排除的车队ID" , dataType = "Long")
    private Long exclusiveTeamId;
    /**
     * 排除的车队角色
     */
    @ApiModelProperty(name = "exclusiveRole" , notes = "排除的车队角色" , dataType = "Integer")
    private Integer exclusiveRole;


    @Override
    public String toString() {
        return "UserContactsCommand{" +
                "keyword='" + keyword + '\'' +
                ", exclusiveTeamId=" + exclusiveTeamId +
                ", exclusiveRole=" + exclusiveRole +
                "} " + super.toString();
    }

}
