package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.domain
 * @Author: yzl
 * @CreateTime: 2021-06-28 10:06
 * @Version: 1.0
 */
@Data
public class BannerInfo {
    private Integer id;

    private String appType;

    private String bannerType;

    private String bannerName;

    private String bannerStatus;

    private Integer bannerIndex;

    private String imgPath;

    private String bannerLink;

    private Date createTime;

    private String delFlag;
}
