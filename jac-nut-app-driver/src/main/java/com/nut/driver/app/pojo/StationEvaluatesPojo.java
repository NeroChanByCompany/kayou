package com.nut.driver.app.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 查询评价列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-23 14:54
 * @Version: 1.0
 */
@Data
public class StationEvaluatesPojo {
    private String ratedId;

    private String driverId;

    private String woCode;

    private String userName;

    private String carType;

    private Integer rate;

    private Date date;

    private String content;

}
