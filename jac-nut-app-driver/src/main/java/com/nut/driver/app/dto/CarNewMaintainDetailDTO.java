package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;
/**
 * @author liuBing
 * @Classname CarNewMaintainDetailDTO
 * @Description TODO
 * @Date 2021/6/24 10:34
 */
@Data
public class CarNewMaintainDetailDTO {
    /**
     * 总数
     */
    private long total;
    /**
     * 总页数
     */
    private Integer page_total;
    /**
     * 数据集合
     */
    private List<CarNewMaintainItemlDTO> list;
}
