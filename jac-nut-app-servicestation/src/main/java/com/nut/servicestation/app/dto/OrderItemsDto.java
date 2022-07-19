package com.nut.servicestation.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderItemsDto {
    private List<OrderItemListDto> repairList;
    private List<OrderItemListDto> maintainList;

}
