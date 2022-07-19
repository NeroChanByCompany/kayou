package com.nut.driver.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-30 09:10
 * @Version: 1.0
 */
@Data
public class QueryTrackDto implements Serializable {
    private String lons;//经度编码集合

    private String lats;//维度编码集合

    private String speeds;//速度编码集合

    private String firstTime;//首点时间戳

    private String times;//时间编码集合

    private String instantOils;//瞬时油耗编码集合

    private String directions;//方向编码集合

    private String levels;//点对应级别编码集合

    private String standardMileage; //标准里程

    private String addr; //当前车辆位置

    private String status;  //车辆当前状态
}
