package com.nut.tripanalysis.app.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CarAnalyseEntity {
    private Long id;

    private String carId;

    private String carNum;

    private Date statisDate;

    private Float mileage;

    private Float oilwear;

    private Float oilwearAvg;

    private Float mileageTotal;

    private Float oilwearTotal;

    private Float oilwearAvgTotal;

    private Float speedAvg;

    private Integer isActive;

    private Date createTime;

    private Long timeTotal;

    private Long idleTime;

    private String mainDriver;

    private String subDriver;

    private String routeId;

    private Long teamId;

    private String routeName;

    private String startPointName;

    private String endPointName;

    private Long speedSum;

    private Integer speedCount;

    private Long statisTimestamp;

    /* 超长怠速 */
    private int idleTimeoutCount;
    /* 二挡起步 */
    private int sndGearStartCount;
    /* 冷车运行 */
    private int coldRunCount;
    /* 夜晚开车 */
    private int nightRunCount;
    /* 低挡高速 */
    private int lowGrHighSpdCount;
    /* 全油门 */
    private int fullThrottleCount;
    /* 大油门 */
    private int roughAccCount;

    private String rpmLowRange;

    private long rpmLowTime;

    private long rpmLowPer;

    /* 0-800转速区间范围 半角-分隔上下限 */
    private String rpmLowMidRange;
    /* 600-800转速区间时间 毫秒 */
    private long rpmLowMidTime;
    /* 600-800转速区间百分比 */
    private long rpmLowMidPer;

    /* 800-1100转速区间范围 半角-分隔上下限 */
    private String rpmMidRange;
    /* 800-1100转速区间时间 毫秒 */
    private long rpmMidTime;
    /* 800-1100转速区间百分比 */
    private long rpmMidPer;

    /* 1100-1900转速区间范围 半角-分隔上下限 */
    private String rpmMidHighRange;
    /* 800-1100转速区间时间 毫秒 */
    private long rpmMidHighTime;
    /* 800-1100转速区间百分比 */
    private long rpmMidHighPer;

    /* 1100-1900转速区间范围 半角-分隔上下限 */
    private String rpmHighRange;
    /* 1100-1900转速区间时间 毫秒 */
    private long rpmHighTime;
    /* 1100-1900转速区间百分比 */
    private long rpmHighPer;

    /* 1100-1900转速区间范围 半角-分隔上下限 */
    private String rpmSuperHighRange;
    /* 1100-1900转速区间时间 毫秒 */
    private long rpmSuperHighTime;
    /* 1100-1900转速区间百分比 */
    private long rpmSuperHighPer;

    /* 制动里程 */
    private double breakLen;

}