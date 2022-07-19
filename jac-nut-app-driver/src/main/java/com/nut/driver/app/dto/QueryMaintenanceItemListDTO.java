package com.nut.driver.app.dto;

import com.nut.driver.app.entity.MaintenanceItemEntity;
import lombok.Data;

import java.util.List;

/**
 * @author liuBing
 * @Classname QueryMaintenanceItemListDto
 * @Description TODO
 * @Date 2021/6/27 13:48
 */
@Data
public class QueryMaintenanceItemListDTO{

    private List<MaintenanceItemEntity> maintainItemList;
}
