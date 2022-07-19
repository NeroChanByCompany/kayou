package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @author liuBing
 * @Classname CarMaintainNewDetailPojo
 * @Description TODO
 * @Date 2021/6/24 10:42
 */
@Data
public class CarMaintainNewDetailPojo {

    /**
     *车辆保养项目Id
     */
    private long carMaintainId;

    /**
     *底盘号
     */
    private String carVin;
    /**
     *车牌号
     */
    private String carNumber;
    /**
     *保养项目类型
     */
    private String protocolType;
    /**
     *保养项目
     */
    private String mtcItem;
    /**
     *保养项目类别
     */
    private String mtcType;
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
     * 已行驶总里程
     */
    private String totalmileage;
    /**
     * 剩余保养里程
     */
    private String remainMaintainMeliage;

    /**
     * 背景颜色
     */
    private Integer bgColor;

    /**
     * 是否校验更新保养数据
     */
    private Integer isCheck;
}
