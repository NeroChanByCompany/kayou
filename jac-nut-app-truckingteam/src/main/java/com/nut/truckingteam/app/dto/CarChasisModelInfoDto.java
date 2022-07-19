package com.nut.truckingteam.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 车辆信息DTO
 */
@Data
public class CarChasisModelInfoDto implements Serializable {
    private String simNo;//sim卡号
    private String carModel;//车型码
    private String chasisNum;//底盘号
    private String engineModel;//发动机型号

}