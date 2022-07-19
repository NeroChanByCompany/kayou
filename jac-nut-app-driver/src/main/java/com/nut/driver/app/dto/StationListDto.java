package com.nut.driver.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 服务站列表DTO
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.dto
 * @Author: yzl
 * @CreateTime: 2021-06-23 10:45
 * @Version: 1.0
 */
@Data
public class StationListDto implements Serializable {
    // 服务站ID
    private String id;
    // 服务站名称
    private String name;
    // 服务站推荐指数
    private String level;
    // 距离
    private String distance;
    // 地址
    private String address;
    // 经度
    private Double longitude;
    // 纬度
    private Double latitude;
    // 服务站图片url
    private String photo;
    // 服务范围（不能建单：0 - 进站维修：1 - 外出救援：2 - 外出救援和进站维修：3）
    private Integer authority;
    // 资质（不是为空）
    private String aptitude;
    // 专属（不是为空）
    private String exclusive;

    private String telephone;
}
