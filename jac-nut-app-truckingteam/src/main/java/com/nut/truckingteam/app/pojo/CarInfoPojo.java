package com.nut.truckingteam.app.pojo;

import lombok.Data;

/**
 * @Description: 车辆信息数据
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.truckingteam.app.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-27 14:53
 * @Version: 1.0
 */
@Data
public class CarInfoPojo {
    private String carId; // 车辆id
    private String carNumber; // 车牌号
    private String mainDriver; // 主驾驶员名称
    private String subDriver; // 副驾驶员名称
    private String teamId; // 所在车队ID
    private String teamName; // 所在车队名称
    private String vin; // vin
    private String curCarTeamId; // 司机版当前车辆所在车队ID
    private String curCarId; // 司机版当前车辆ID
}
