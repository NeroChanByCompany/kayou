package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.dto
 * @Author: yzl
 * @CreateTime: 2021-06-23 14:13
 * @Version: 1.0
 */
@Data
public class ServiceStationDetailDto {
    private String name;//服务站名称

    private String address;//服务站地址

    private String phone;//电话

    private Double lat;//纬度

    private Double lon;//经度

    private String level;//服务站推荐指数

    private String introduce;//简介

    private String photo;// 图片

    /**
     * 服务范围（不能建单：0 - 进站维修：1 - 外出救援：2 - 外出救援和进站维修：3）
     */
    private Integer authority;

    /**
     * 资质
     */
    private String aptitude;
    /**
     * 专属
     */
    private String exclusive;
}
