package com.nut.driver.app.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName("flt_fleet_car_mapping")
public class FltFleetCarMapping {
    private Long id;

    private Long teamId;

    private String carId;

    private Integer createType;

    private Long createUserId;

    private String tbossUserId;

    private Date createTime;

    private Date updateTime;
}