package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 查询评价列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.dto
 * @Author: yzl
 * @CreateTime: 2021-06-23 14:51
 * @Version: 1.0
 */
@Data
public class StationEvaluatesDto {
    private String ratedId;

    private String driverId;

    private String woCode;

    private String userName;

    private String carType;

    private Integer rate;

    private String date;

    private String content;
}
