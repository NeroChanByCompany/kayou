package com.nut.tripanalysis.app.pojo;

import lombok.Data;

import java.util.List;

@Data
public class TripInfoByDatePojo {

    /**
     * 总页数
     */
    private Long page_total;
    /**
     * 数据总量
     */
    private Long total;
    /**
     * 当日里程
     */
    private String dayLen;

    /**
     * 当日油耗
     */
    private String dayOil;

    /**
     * 非正常行程信息
     */
    private String textDescription;

    /**
     * 正常行程信息
     */
    private List<TripInfoPojo> list;

}
