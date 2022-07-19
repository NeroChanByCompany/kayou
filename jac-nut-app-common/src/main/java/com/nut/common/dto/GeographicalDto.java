package com.nut.common.dto;

import lombok.Data;

/**
 * @Description: 所在地理位置的信息描述
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.dto
 * @Author: yzl
 * @CreateTime: 2021-06-22 10:28
 * @Version: 1.0
 */
@Data
public class GeographicalDto {
    // 所在省
    private String province;
    // 所在城市
    private String city;
    // 所在区县
    private String dist;
    // 所在区域
    private String area;
    // 所在乡镇
    private String town;
    // 所在村
    private String village;
    // 地址
    private String address;
    // 方向
    private String direction;
    // 距离
    private String distance;

}
