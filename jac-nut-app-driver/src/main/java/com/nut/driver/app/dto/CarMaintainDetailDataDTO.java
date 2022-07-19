package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @author liuBing
 * @Classname CarMaintainDetailDataDTO
 * @Description TODO
 * @Date 2021/6/24 10:31
 */
@Data
public class CarMaintainDetailDataDTO {
    /**
     * 车辆基础信息
     */
    private CarNewMaintainDTO carDto;

    private CarNewMaintainDetailDTO maintainDetailDto;

    private CarNewMaintainProgramCountDTO carNewMaintainProgramCountDto;
}
