package com.nut.driver.app.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuBing
 * @Classname CarReportDTO
 * @Description TODO 车辆报表数据统计dto
 * @Date 2021/6/19 17:40
 */
@Data
@Accessors(chain = true)
public class CarReportDTO {
    /**
     * 出勤车辆
     */
    private int carTotal;
    /**
     * 总里程
     */
    private String totalMileage;
    /**
     * 车均里程
     */
    private String avgMileage;
    /**
     * 平均速度
     */
    private String avgSpeed;
    /**
     * 总油耗
     */
    private String totalOil;
    /**
     * 平均油耗
     */
    private String avgOil;
    /**
     * 怠速油耗
     */
    private String idleOil;
    /**
     * 行驶时长
     */
    private String totalTime;
    /**
     * 驾驶时长
     */
    private String drivingTime;
    /**
     * 怠速时长
     */
    private String idleTime;
    /**
     * 超速次数
     */
    private int overSpeedCount;
    /**
     * 急加速次数
     */
    private int rapidAccelerationCount;
    /**
     * 急减速次数
     */
    private int rapidDecelerationCount;
    /**
     * 急转弯次数
     */
    private int suddenTurnCount;
    /**
     * 超长怠速次数
     */
    private int longIdlingCount;
    /**
     * 冷车运行次数
     */
    private int coldCarCount;
    /**
     * 夜晚开车次数
     */
    private int nightDrivingCount;
    /**
     * 引擎高转速次数
     */
    private int engineHighSpeedCount;
    /**
     * 全油门次数
     */
    private int fullThrottleCount;
    /**
     * 大油门次数
     */
    private int bigThrottleCount;
    /**
     * 空挡滑行次数
     */
    private int coastingCount;
    /**
     * 熄火滑行次数
     */
    private int coastingEngineOffCount;
}
