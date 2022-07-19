package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

/**
 * @author liuBing
 * @Classname QueryDriverMaintenanceListDTO
 * @Description TODO
 * @Date 2021/6/24 14:45
 */
@Data
public class QueryDriverMaintenanceListDTO {
    /**
     * 保养列表dto
     */
    List<QueryDriverMaintenanceDTO> list;
}
