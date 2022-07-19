package com.nut.driver.app.pojo;

import lombok.Data;

@Data
public class FltStatisticalFleetDataPojo {
    private Long teamId;

    private String teamName;

    private String autoTerminal;

    private float mileage;

    private float oilwear;

    private float runOil;

    private float idlingOil;

    private Long timeTotal = 0L;

    private Long idleTime = 0L;

    private Long speedSum = 0L;

    private Integer speedCount = 0;
    /**
     * 超速
     */
    private Integer overSpeedCnt = 0;
    /**
     * 急加速
     */
    private Integer raCnt = 0;
    /**
     * 急减速
     */
    private Integer rdCnt = 0;
    /**
     * 急转弯
     */
    private Integer sharpTurnCnt = 0;
    /**
     * 超长怠速
     */
    private Integer idleTimeoutCnt = 0;
    /**
     * 冷车启动
     */
    private Integer coldRunCnt = 0;
    /**
     * 夜晚开车
     */
    private Integer nightRunCnt = 0;
    /**
     * 引擎高转速
     */
    private Integer lowGrHighSpdCnt = 0;
    /**
     * 全油门
     */
    private Integer fullThrottleCnt = 0;
    /**
     * 大油门
     */
    private Integer roughThrottleCnt = 0;
    /**
     * 空挡滑行
     */
    private Integer neutralGearCoastCnt= 0;
    /**
     * 熄火滑行
     */
    private Integer stallCoastCnt = 0;

    private Integer totalCount = 0;

}
