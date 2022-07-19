package com.nut.driver.app.domain;
import lombok.Data;

import java.util.Date;

@Data
public class FltFleetLineRouteCar {
    private Long id;

    private Long lineId;

    private Long carId;

    private String createUserId;

    private Date createTime;

    private Date updateTime;

}
