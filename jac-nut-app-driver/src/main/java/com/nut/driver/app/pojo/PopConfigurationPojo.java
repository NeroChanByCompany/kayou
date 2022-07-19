package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-28 09:45
 * @Version: 1.0
 */
@Data
public class PopConfigurationPojo {
    private Long id;

    private String title;

    private String picUrl;

    private String linkUrl;

    private String displayTime;

    private String endTime;

    private String stopTime;
}
