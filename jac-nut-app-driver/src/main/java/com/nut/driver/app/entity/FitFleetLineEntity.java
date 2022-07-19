package com.nut.app.driver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("fit_fleet_line")
public class FitFleetLineEntity {
    private Long id;

    private String startCityCode;

    private String startCityName;

    private String endCityCode;

    private String endCityName;

    private String createUserId;

    private String oilConsLimit;

    private String carids;

    private String fleetId;

    private Date createTime;

    private Date updateTime;

    private String maxSpeed;
}
