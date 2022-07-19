package com.nut.tripanalysis.app.pojo;

import lombok.Data;

@Data
public class TripInfoPojo implements java.io.Serializable{

    /**
     * 车辆Id
     */
    private String carId;

    private String terminalId;
    /**
     *行程ID
     */
    private String tripId;
    /**
     *行程开始时间（毫秒）
     */
    private Long startTime;
    /**
     *行程结束时间（毫秒）
     */
    private Long endTIme;
    /**
     *行程打分
     */
    private Integer tripScore;
    /**
     *行程里程(km)
     */
    private String tripLen;
    /**
     *平均油耗（L/100km）
     */
    private String avgOil;
    /**
     *行程油耗
     */
    private String tripOil;
    /**
     *是否为跨天行程
     */
    private boolean beyondFlag = false;
    /**
     *此行程中今天的里程
     */
    private String tripLenToday;
    /**
     *此行程中今天的油耗
     */
    private String tripOilToday;


}
