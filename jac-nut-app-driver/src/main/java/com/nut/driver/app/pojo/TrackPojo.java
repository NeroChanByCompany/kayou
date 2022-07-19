package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-30 09:23
 * @Version: 1.0
 */
@Data
public class TrackPojo {
    private int longitude;

    private int latitude;

    private int speed;

    private long gpsDate;

    private int direction;

    private int status;

    private double standardMileage;
}
