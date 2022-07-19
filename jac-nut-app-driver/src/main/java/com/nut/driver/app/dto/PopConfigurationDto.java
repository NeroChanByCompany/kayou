package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-28 09:42
 * @Version: 1.0
 */
@Data
public class PopConfigurationDto {
    private String id;

    private String title;

    private String picUrl;

    private String linkUrl;

    private String displayTime;

    private String endTime;

    private String stopTime;
}
