package com.nut.driver.app.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description: APP端返回登录信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.dto
 * @Author: yzl
 * @CreateTime: 2021-06-16 14:42
 * @Version: 1.0
 */
@Data
@ApiModel("app登录具体实体")
@Accessors(chain = true)
public class AppLoginDTO implements Serializable {
    private static final long serialVersionUID = 7846945921177544266L;
    @ApiModelProperty(name = "id",notes = "登录id",dataType = "Long")
    private Long id;

    // 用户ID
    @ApiModelProperty(name = "userId",notes = "用户id",dataType = "String")
    private String userId;

    // 昵称
    @ApiModelProperty(name = "userId",notes = "用户id",dataType = "String")
    private String nickname;

    // token凭证
    @ApiModelProperty(name = "token",notes = "登录token",dataType = "String")
    private String token;

    //判断登录种类  0默认 1显示新手引导
    @ApiModelProperty(name = "loginType",notes = "判断登录种类  0默认 1显示新手引导",dataType = "int")
    private int loginType;
    @ApiModelProperty(name = "popupEntity",notes = "是否弹出",dataType = "PopupDto")
    private PopupDTO popupEntity = new PopupDTO();
}
