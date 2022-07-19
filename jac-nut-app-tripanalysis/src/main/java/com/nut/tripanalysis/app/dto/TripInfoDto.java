package com.nut.tripanalysis.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询行程详细信息接口--dto
 */
@Data
public class TripInfoDto implements Serializable {

    /**
     * 开始时间（毫秒）
     */
    private long startTime;
    /**
     * 开始位置
     */
    private String startLocal;
    /**
     * 结束时间（毫秒）
     */
    private long endTime;
    /**
     * 结束位置
     */
    private String endLocal;
    /**
     * 行程打分
     */
    private double tripScore;
    /**
     * 行程里程（km）
     */
    private double tripLen;
    /**
     * 平均油耗（L/100km）
     */
    private double avgOil;
    /**
     * 平均速度（km/h）
     */
    private double avgSpeed;
    /**
     * 行程时长（毫秒）
     */
    private long tripTime;
    /**
     * 行程总油耗（L）
     */
    private double tripOil;
    /**
     * 怠速油耗（L）
     */
    private double tripIdleOil;
    /**
     * 油耗级别
     */
    private int oilLevel;
    /**
     * 同车型平均油耗（L/100km）
     */
    private double avgOilCarModel;
    /**
     * 历史平均油耗（L/100km）
     */
    private double avgOilHistory;
    /**
     * 油耗建议
     */
    private String oilSuggest;
    /**
     * 驾驶等级
     */
    private int driveLevel;
    /**
     * 怠速时长（毫秒）
     */
    private long zeroUpTime;
    /**
     * 慢速时长（毫秒）
     */
    private long slowTime;
    /**
     * 低速时长（毫秒）
     */
    private long lowTime;
    /**
     * 中速时长（毫秒）
     */
    private long midTime;
    /**
     * 高速时长（毫秒）
     */
    private long highTime;
    /**
     * 超速时长（毫秒）
     */
    private long overTime;
    /**
     * 怠速时长百分比
     */
    private long zeroUpPer;
    /**
     * 慢速时长百分比
     */
    private long slowPer;
    /**
     * 低速时长百分比
     */
    private long lowPer;
    /**
     * 中速时长百分比
     */
    private long midPer;
    /**
     * 高速时长百分比
     */
    private long highPer;
    /**
     * 超速时长百分比
     */
    private long overPer;
    /**
     * 急加速次数
     */
    private int fastupCount;
    /**
     * 急减速次数
     */
    private int fastlowCount;
    /**
     * 急转弯次数
     */
    private int crookCount;
    /**
     * 超速次数
     */
    private int speedingCount;

    /**
     * 超长怠速
     */
    private int idleTimeoutCount;
    /**
     * 二挡起步
     */
    private int sndGearStartCount;
    /**
     * 冷车运行
     */
    private int coldRunCount;
    /**
     * 夜晚开车
     */
    private int nightRunCount;
    /**
     * 低挡高速
     */
    private int lowGrHighSpdCount;
    /**
     * 全油门
     */
    private int fullThrottleCount;
    /**
     * 大油门
     */
    private int roughAccCount;
    /** 空挡滑行 */
    private int neutralGearCoastCount;
    /** 熄火滑行 */
    private int stallCoastCount;
    /** 怠速空调 */
    private int idleAirConditionCount;

    /**
     * 驾驶建议
     */
    private String driveSuggest;
    /**
     * 车辆ID
     */
    private String carId;

    /**
     * 0-800转速区间范围 半角-分隔上下限
     */
    private String rpmLowMidRange;
    /**
     * 0-800低转速区间时间 毫秒
     */
    private long rpmLowMidTime;
    /**
     * 0-800低转速区间百分比
     */
    private long rpmLowMidPer;

    /**
     * 800-1100转速区间范围 半角-分隔上下限
     */
    private String rpmMidRange;
    /**
     * 800-1100转速区间时间 毫秒
     */
    private long rpmMidTime;
    /**
     * 800-1100转速区间百分比
     */
    private long rpmMidPer;

    /**
     * 1100-1700转速区间范围 半角-分隔上下限
     */
    private String rpmMidHighRange;
    /**
     * 1100-1700转速区间时间 毫秒
     */
    private long rpmMidHighTime;
    /**
     * 1100-1700转速区间百分比
     */
    private long rpmMidHighPer;

    /**
     * 1700-2000转速区间范围 半角-分隔上下限
     */
    private String rpmHighRange;
    /**
     * 1700-2000转速区间时间 毫秒
     */
    private long rpmHighTime;
    /**
     * 1700-2000转速区间百分比
     */
    private long rpmHighPer;

    /**
     * 2000-2300转速区间范围 半角-分隔上下限
     */
    private String rpmSuperHighRange;
    /**
     * 2000-2300转速区间时间 毫秒
     */
    private long rpmSuperHighTime;
    /**
     * 2000-2300转速区间百分比
     */
    private long rpmSuperHighPer;

    /**
     * 大于等于2300转速区间范围 半角-分隔上下限
     */
    private String rpmSuperHighRange2;
    /**
     * 大于等于2300转速区间时间 毫秒
     */
    private long rpmSuperHighTime2;
    /**
     * 大于等于2300转速区间百分比
     */
    private long rpmSuperHighPer2;

    /**
     * 制动里程 单位km
     */
    private double breakLen;


    /**
     * 引擎超转数次数
     **/
    private int engineOverSpeedNumber;

    /**
     * 开始经度
     */
    private long startLon;
    /**
     * 结束经度
     */
    private long endLon;
    /**
     * 开始纬度
     */
    private long startLat;
    /**
     * 结束纬度
     */
    private long endLat;

}
