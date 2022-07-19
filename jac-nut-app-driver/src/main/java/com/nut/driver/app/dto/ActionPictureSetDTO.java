package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 版本信息接口返回dto
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.dto
 * @Author: yzl
 * @CreateTime: 2021-06-18 13:51
 * @Version: 1.0
 */
@Data
public class ActionPictureSetDTO {
    private Long id;
    private String actionCode;
    private String actionName;
    private String pictureUrl;
    private String order;
    private String inVersion;
    private String outVersion;
    private String describe;
    private String fileSize;
    private int flag;
}
