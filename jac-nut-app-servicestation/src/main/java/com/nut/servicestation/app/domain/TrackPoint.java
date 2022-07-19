package com.nut.servicestation.app.domain;

import lombok.Data;

/**
 * @Description: 纠偏轨迹点
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.domain
 * @Author: yzl
 * @CreateTime: 2021-08-26 10:46
 * @Version: 1.0
 */
@Data
public class TrackPoint {

    private String latitude;

    private String longitude;

    private String coord_type_input;

    private Long loc_time;

}
