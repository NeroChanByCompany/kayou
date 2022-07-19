package com.nut.driver.app.dto;

import lombok.Data;

/**
 * 车队司机列表接口返回DTO
 */
@Data
public class FleetDriversDto extends AbstractPersonnelDto {
    private Integer bound;

    @Override
    public String toString() {
        return "FleetDriversDto{" +
                "bound=" + bound +
                "} " + super.toString();
    }

}
