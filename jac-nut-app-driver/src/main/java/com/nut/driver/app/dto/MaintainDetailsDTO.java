package com.nut.driver.app.dto;

import com.nut.driver.app.pojo.MaintainDetailsPojo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author liuBing
 * @Classname MaintainDetailsDTO
 * @Description TODO
 * @Date 2021/8/25 20:01
 */
@Data
@Accessors(chain = true)
public class MaintainDetailsDTO {

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
    private List<MaintainDetailsPojo> list;
}
