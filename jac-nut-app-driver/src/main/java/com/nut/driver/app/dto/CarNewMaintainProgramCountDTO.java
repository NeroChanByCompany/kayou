package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @author liuBing
 * @Classname CarNewMaintainProgramCountDTO
 * @Description TODO
 * @Date 2021/6/24 10:35
 */
@Data
public class CarNewMaintainProgramCountDTO {
    /**
     * 更换项目数
     */
    private Integer replaceProgramCount;

    /**
     *检查项目数
     */
    private Integer checkProgramCount;

    /**
     * 润滑项目数
     */
    private Integer lubricateProgramCount;
}
