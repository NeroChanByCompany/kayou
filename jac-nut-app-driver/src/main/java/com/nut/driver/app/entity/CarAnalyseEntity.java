package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author liuBing
 * @Classname CarAnalyseEntity
 * @Description TODO 统计分析-车辆维度表
 * @Date 2021/6/19 17:16
 */
@Data
@Accessors(chain = true)
@TableName("car_analyse")
public class CarAnalyseEntity {

    /**
     * 数据ID
     */
    private Long id;
    /**
     * 车辆唯一标识ID
     */
    private String carId;
    /**
     * 统计日期
     */
    private Date statisDate;
    /**
     * 当天的行程里程
     */
    private float mileage;
    /**
     * 车牌号码
     */
    private String carNum;
    /**
     * 当天的总油耗
     */
    private float oilwear;
    /**
     * 当天的百公里油耗
     */
    private float oilwearAvg;
    /**
     * 行驶油耗
     */
    private float runOil;
    /**
     * 怠速油耗
     */
    private float idlingOil;
    /**
     * 截止到统计日期的总行程
     */
    private float mileageTotal;
    /**
     * 截止到统计日期的总油耗
     */
    private float oilwearTotal;
    /**
     * 截止到统计日期的百公里油耗
     */
    private float oilwearAvgTotal;
    /**
     * 平均速度
     */
    private float speedAvg;
    private float isActive;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 行驶时长（毫秒）
     */
    private Long timeTotal;
    /**
     * 怠速时长（毫秒）
     */
    private Long idleTime;
    /**
     * 主驾驶名
     */
    private String mainDriver;
    /**
     * 车队id
     */
    private Long teamId;
    /**
     * 速度之和
     */
    private Long speedSum;
    /**
     * 速度个数
     */
    private Integer speedCount;
    /**
     * 固化时间戳
     */
    private Long statisTimestamp;
    /**
     * 超速次数
     */
    private Integer overspeedCnt;
    /**
     * 急加速次数
     */
    private Integer rapidAccelerationCnt;
    /**
     * 急减速次数
     */
    private Integer rapidDecelerationCnt;
    /**
     * 急转弯次数
     */
    private Integer sharpTurnCnt;
    /**
     * 超长怠速次数
     */
    private Integer idleTimeoutCnt;
    /**
     * 二挡起步次数
     */
    private Integer sndGearStartCnt;
    /**
     * 冷车运行次数
     */
    private Integer coldRunCnt;
    /**
     * 夜晚开车次数
     */
    private Integer nightRunCnt;
    /**
     * 低挡高速次数
     */
    private Integer lowHrHighSpdCnt;
    /**
     * 全油门次数
     */
    private Integer fullThrottleCnt;
    /**
     * 大油门次数
     */
    private Integer roughThrottleCnt;
    /**
     * 空挡滑行
     */
    private Integer neutralGearCoastCnt;

    /**
     * 熄火滑行
     */
    private Integer stallCoastCnt;
    /**
     * 怠速空调
     */
    private Integer idleAirConditionCnt;

}
