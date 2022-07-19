package com.nut.driver.app.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuBing
 * @Classname MaintainPojo
 * @Description TODO
 * @Date 2021/6/23 17:20
 */
@Data
@Accessors(chain = true)
@Deprecated
public class OldMaintainPojo {

    /**
     * 主键
     */
    private Long id;
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
     * 按里程保养项目总数
     */
    private String maintainItemCount;
    /**
     * 下次保养时间
     */
    private String nextDate;
    /**
     * 按时间保养项目总数
     */
    private String maintainItemTmCount;
    /**
     * 下次保养里程
     */
    private String nextMileage;
    /**
     * 车辆协议类型（0：非协议车，1：协议车）
     */
    private Integer protocolType;
    /**
     * 是否未保养（0：待保养，1：未保养）
     */
    private Integer maintainType;

}
