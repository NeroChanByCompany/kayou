package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 车辆参数
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-29 17:51
 * @Version: 1.0
 */
@Data
public class CarParmDto {
    private String carId;//车辆id
    private String seriseName;//整车平台
    private String carTypeName;//车辆类别
    private String vin;//vin码
    private String gearBoxModel;//变速箱型号
    private String engineModel;//发动机型号
    private String rearxleAratio;//后桥速比
    private String tireModel;//轮胎型号
}
