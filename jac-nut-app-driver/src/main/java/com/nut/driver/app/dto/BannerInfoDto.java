package com.nut.driver.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: banner页信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-28 15:38
 * @Version: 1.0
 */
@Data
public class BannerInfoDto implements Serializable {
    // ID
    private Integer id;

    // APP端版本
    private String appType;

    // 类型
    private String bannerType;

    // 名称
    private String bannerName;

    // 位置
    private Integer bannerIndex;

    // 图片路径
    private String imgPath;

    // 链接
    private String bannerLink;
}
