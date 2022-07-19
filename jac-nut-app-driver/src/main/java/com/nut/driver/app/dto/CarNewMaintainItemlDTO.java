package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @author liuBing
 * @Classname CarNewMaintainItemlDTO
 * @Description TODO
 * @Date 2021/6/24 10:35
 */
@Data
public class CarNewMaintainItemlDTO {
    /**
     *保养项目
     */
    private String mtcItem;
    /**
     *剩余保养里程
     */
    private String remainMaintainMeliage;
    /**
     *下次保养里程
     */
    private String nextMaintainMileage;
    /**
     *下次保养时间
     */
    private String nextMaintainTime;
    /**
     *上次保养里程
     */
    private String lastMaintainMileage;
    /**
     *上次保养时间
     */
    private String lastMaintainTime;
    /**
     *车辆保养项目Id
     */
    private String carMaintainId;
    /**
     *即将到期：1，已经过期：2，默认（其他）：0
     */
    private Integer bgColor;
    /**
     * 车辆协议类型（0：非协议车，1：协议车）
     */
    private String protocolType;
}
