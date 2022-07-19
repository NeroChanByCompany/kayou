package com.nut.driver.app.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuBing
 * @Classname MaintainDetailsPojo
 * @Description TODO 车辆详情
 * @Date 2021/8/25 19:44
 */
@Data
@Accessors(chain = true)
public class MaintainDetailsPojo {
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 底盘号
     */
    private String vin;
    /**
     * 下次保养剩余里程
     */
    private Double remainingMileage;
    /**
     * 下次保养总数
     */
    private String nextCount;
    /**
     * 下次保养时间
     */
    private String nextDate;
    /**
     * 是否未保养（1：需要保养）
     */
    private Integer maintainType;
    /**
     * 保养项名称
     */
    private String itemsName;
}
