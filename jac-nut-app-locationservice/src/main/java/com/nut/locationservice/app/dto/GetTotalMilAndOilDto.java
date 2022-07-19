package com.nut.locationservice.app.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.dto
 * @Author: yzl
 * @CreateTime: 2021-06-17 13:36
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class GetTotalMilAndOilDto {

    /**
     * 在线车辆数
     */
    private Long count;
    /**
     * 百公里油耗
     */
    private Double oilPerHud;
    /**
     * 总里程
     */
    private Double totalMil;
    /**
     * 总油耗
     */
    private Double totalOil;

}
