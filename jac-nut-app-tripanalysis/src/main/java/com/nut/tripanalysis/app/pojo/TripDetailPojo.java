
package com.nut.tripanalysis.app.pojo;

import lombok.Data;

/**
 * 行程分析详情dto
 *
 */
@Data
public class TripDetailPojo {
    /**
     * 通信号
     */
    private long terminalId;
    /**
     * 行程得分
     */
    private int score;
    /**
     * 行程评星
     */
    private int  level;
    /**
     * 油耗得分
     */
    private int oilScore;
    /**
     * 油耗评星
     */
    private int  oilLevel;
    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 结束时间
     */
    private long endTime;
    /**
     * 行驶时长（秒）
     */
    private int workTime;
    /**
     * 行驶里程（米）
     */
    private int mileage;
    /**
     * 平均速度（Km/h）
     */
    private int avgSpeed;
    /**
     * 油耗（升）
     */
    private double oil;
    /**
     * 平均油耗（L/100Km）
     */
    private double avgOil;
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
//    /**
//     * 开始海拔
//     */
//    private long startHeight;
//    /**
//     * 结束海拔
//     */
//    private long endHeight;
//
//    /**
//     * <pre>
//     * 空挡滑行里程 单位:M
//     * </pre>
//     **/
//    private int idlingMileage;
//
//    /**
//     * <pre>
//     * 在挡滑行里程 单位:M
//     * </pre>
//     **/
//    private int paringRangeMileage;
//
//    /**
//     * <pre>
//     * 急踩油门次数
//     * </pre>
//     **/
//    private int sharpStepOnAcceleratorFrequency;
//
    /**
     * <pre>
     * 空挡滑行次数
     * </pre>
     **/
    private int idlingFrequency;
//
//    /**
//     * <pre>
//     * 在挡滑行次数
//     * </pre>
//     **/
//    private int paringRangeFrequency;


    /**
     * <pre>
     * 停车怠速时长 单位: 秒
     * </pre>
     **/
    private int parkingIdleTime;

//    /**
//     * <pre>
//     * 制动次数
//     * </pre>
//     **/
//    private int brakeNumber;
//
    /**
     * <pre>
     * 制动累计里程(制动总距离) 单位: 秒
     * </pre>
     **/
    private int cumulativeMileage;
//
//    /**
//     * <pre>
//     * 制动时长 单位:S
//     * </pre>
//     **/
//    private int brakingTime;
//
//    /**
//     * <pre>
//     * 整车估算载荷 单位:0.1吨
//     * </pre>
//     **/
//    private int vehicleEstimatedLoad;
//
//    /**
//     * <pre>
//     * 巡航里程(定速巡航距离) 单位:M
//     * </pre>
//     **/
//    private int cruiseRange;
//
//    /**
//     * <pre>
//     * 超速行驶次数
//     * </pre>
//     **/
//    private int overSpeedTimes;
//
//    /**
//     * <pre>
//     * 空挡滑行油耗 单位:0.1L
//     * </pre>
//     **/
//    private int idlingFuelConsumption;
//
    /**
     * <pre>
     * 停车怠速油耗 单位:0.1L
     * </pre>
     **/
    private int parkingIdleFuelConsumption;
//
//    /**
//     * <pre>
//     * 超速行驶距离 单位:M
//     * </pre>
//     **/
//    private int overSpeedDistance;
//
//    /**
//     * <pre>
//     * 超速行驶油量 单位:0.1L
//     * </pre>
//     **/
//    private int overSpeedFuelConsumption;
//
//    /**
//     * <pre>
//     * 不含怠速平均车速 单位:KM/H
//     * </pre>
//     **/
//    private int averageSpeedExcludeIdleTime;
//
//    /**
//     * <pre>
//     * 含怠速平均车速 单位:KM/H
//     * </pre>
//     **/
//    private int averageSpeedIncludeIdleTime;
//
//    /**
//     * <pre>
//     * 最大车速 单位:KM/H
//     * </pre>
//     **/
//    private int maxSpeed;
//
//    /**
//     * <pre>
//     * 停车通电时长 单位:分
//     * </pre>
//     **/
//    private int parkingPowerOnTime;
//
//    /**
//     * <pre>
//     * 城市行驶里程 单位:M
//     * </pre>
//     **/
//    private int cityDrivingMileage;
//
//    /**
//     * <pre>
//     * 城市行驶时长 单位:分
//     * </pre>
//     **/
//    private int cityDrivingTime;
//
//    /**
//     * <pre>
//     * 城市行驶油量 单位:0.1L
//     * </pre>
//     **/
//    private int cityDrivingFuelConsumption;
//
//    /**
//     * <pre>
//     * 非城市行驶里程 单位:M
//     * </pre>
//     **/
//    private int ruralDrivingMileage;
//
//    /**
//     * <pre>
//     * 非城市行驶时长 单位:分
//     * </pre>
//     **/
//    private int ruralDrivingTime;
//
//    /**
//     * <pre>
//     * 非城市行驶油量 单位:0.1L
//     * </pre>
//     **/
//    private int ruralDrivingFuelConsumption;
//
//    /**
//     * <pre>
//     * 经济行驶里程 单位:M
//     * </pre>
//     **/
//    private int ecoDrivingMileage;
//
//    /**
//     * <pre>
//     * 经济行驶时长 单位:分
//     * </pre>
//     **/
//    private int ecoDrivingTime;
//
//    /**
//     * <pre>
//     * 经济行驶油量 单位:0.1L
//     * </pre>
//     **/
//    private int ecoDrivingFuelConsumption;
//
//    /**
//     * <pre>
//     * 上坡行驶距离 单位:M
//     * </pre>
//     **/
//    private int upSlopeDrivingMileage;
//
//    /**
//     * <pre>
//     * 上坡行驶时长 单位:分
//     * </pre>
//     **/
//    private int upSlopeDrivingTime;
//
//    /**
//     * <pre>
//     * 上坡行驶油量 单位:0.1L
//     * </pre>
//     **/
//    private int upSlopeDrivingFuelConsumption;
//
//    /**
//     * <pre>
//     * 下坡行驶距离 单位:M
//     * </pre>
//     **/
//    private int downSlopeDrivingMileage;
//
//    /**
//     * <pre>
//     * 下坡行驶时长 单位:分
//     * </pre>
//     **/
//    private int downSlopeDrivingTime;
//
//    /**
//     * <pre>
//     * 下坡行驶油量 单位:0.1L
//     * </pre>
//     **/
//    private int downSlopeDrivingFuelConsumption;
//
//    /**
//     * <pre>
//     * 平路行驶距离 单位:M
//     * </pre>
//     **/
//    private int levelRoadDrivingMileage;
//
//    /**
//     * <pre>
//     * 平路行驶时长 单位:分
//     * </pre>
//     **/
//    private int levelRoadDrivingTime;
//
//    /**
//     * <pre>
//     * 平路行驶油量 单位:0.1L
//     * </pre>
//     **/
//    private int levelRoadDrivingFuelConsumption;
//
//    /**
//     * <pre>
//     * 冷引擎激烈驾驶距离 单位:M
//     * </pre>
//     **/
//    private int coldEngineSharpDrivingMileage;
//
//    /**
//     * <pre>
//     * 冷引擎激烈驾驶时长 单位:S
//     * </pre>
//     **/
//    private int coldEngineSharpDrivingTime;
//
//    /**
//     * <pre>
//     * 冷引擎激烈驾驶油量 单位:0.1L
//     * </pre>
//     **/
//    private int coldEngineSharpDrivingFuelConsumption;
//
//    /**
//     * <pre>
//     * 定速巡航时长 单位:S
//     * </pre>
//     **/
//    private int cruiseControlDrivingTime;
//
//    /**
//     * <pre>
//     * 定速巡航油量 单位:0.1L
//     * </pre>
//     **/
//    private int cruiseControlDrivingFuelConsumption;
//
//    /**
//     * <pre>
//     * 引擎超转数行驶距离 单位:M
//     * </pre>
//     **/
//    private int engineOverSpeedDrivingMileage;
//
//    /**
//     * <pre>
//     * 引擎超转数行驶时长 单位:S
//     * </pre>
//     **/
//    private int engineOverSpeedDrivingTime;
//
//    /**
//     * <pre>
//     * 引擎超转数行驶油量 单位:0.1L
//     * </pre>
//     **/
//    private int engineOverSpeedDrivingFuelConsumption;
//
//    /**
//     * <pre>
//     * 换挡次数
//     * </pre>
//     **/
//    private int shiftGearsFrequency;
//
//    /**
//     * <pre>
//     * 激烈减速时长 单位:0.1S
//     * </pre>
//     **/
//    private int sharpDownSpeedTime;
//
//    /**
//     * <pre>
//     * 激烈减速距离 单位:M
//     * </pre>
//     **/
//    private int sharpDownSpeedMileage;
//
//    /**
//     * <pre>
//     * 激烈减速次数
//     * </pre>
//     **/
//    private int sharpDownSpeedFrequency;
//
//    /**
//     * <pre>
//     * 激烈加速时长 单位:0.1S
//     * </pre>
//     **/
//    private int sharpUpSpeedTime;
//
//    /**
//     * <pre>
//     * 激烈加速距离 单位:M
//     * </pre>
//     **/
//    private int sharpUpSpeedMileage;
//
//    /**
//     * <pre>
//     * 激烈加速次数
//     * </pre>
//     **/
//    private int sharpUpSpeedFrequency;
//
//    /**
//     * <pre>
//     * 激烈加速油耗 单位:0.1L
//     * </pre>
//     **/
//    private int sharpUpSpeedFuelConsumption;
//
//    /**
//     * <pre>
//     * 急踩油门行驶距离 单位:M
//     * </pre>
//     **/
//    private int sharpStepOnAcceleratorMileage;
//
//    /**
//     * <pre>
//     * 急踩油门时长 单位:0.1S
//     * </pre>
//     **/
//    private int sharpStepOnAcceleratorTime;
//
//    /**
//     * <pre>
//     * 急踩油门油量 单位:0.1L
//     * </pre>
//     **/
//    private int sharpStepOnAcceleratorFuelConsumption;
//
//    /**
//     * <pre>
//     * 车辆停车次数
//     * </pre>
//     **/
//    private int vehicleParkFrequency;
//
//    /**
//     * <pre>
//     * 车辆启步次数
//     * </pre>
//     **/
//    private int vehicleStartFrequency;
//
//    /**
//     * <pre>
//     * 冷车启步次数
//     * </pre>
//     **/
//    private int vehicleColdStartFrequency;
//
//    /**
//     * <pre>
//     * 加油量百分比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int fuelChargePercent;
//
//    /**
//     * <pre>
//     * 加油数量 单位:0.1L
//     * </pre>
//     **/
//    private int fuelChargeNumber;
//
//    /**
//     * <pre>
//     * 漏油量百分比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int fuelLeakPercent;
//
//    /**
//     * <pre>
//     * 漏油数量 单位:0.1L
//     * </pre>
//     **/
//    private int fuelLeakNumber;
//
//    /**
//     * <pre>
//     * 车辆起步时长 单位:0.1S
//     * </pre>
//     **/
//    private int vehicleStartTime;
//
//    /**
//     * <pre>
//     * 车辆起步耗油量 单位:0.1L
//     * </pre>
//     **/
//    private int vehicleStartFuelConsumption;
//
//    /**
//     * <pre>
//     * 油门稳定性次数
//     * </pre>
//     **/
//    private int acceleratorStabilityFrequency;
//
//    /**
//     * <pre>
//     * 油门稳定性时长 单位:S
//     * </pre>
//     **/
//    private int acceleratorStabilityTime;
//
//    /**
//     * <pre>
//     * 离合开关时长 单位:S
//     * </pre>
//     **/
//    private int clutchSwitchTime;
//
//    /**
//     * <pre>
//     * 最大引擎转速 单位:RPM
//     * </pre>
//     **/
//    private int maxEngineRotation;
//
//    /**
//     * <pre>
//     * 平均引擎转速 单位:RPM
//     * </pre>
//     **/
//    private int averageEngineRotation;
//
//    /**
//     * <pre>
//     * 平均油门开度 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int averageAccelerator;
//
//    /**
//     * <pre>
//     * 车速区间01(0-10KM/H)累计里程 单位:KM
//     * </pre>
//     **/
//    private int speedRange01Mileage;

    /**
     * <pre>
     * 车速区间01(0-10KM/H)累计时长 单位: 秒
     * </pre>
     **/
    private int speedRange01Time;
//
//    /**
//     * <pre>
//     * 车速区间01(0-10KM/H)累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int speedRange01FuelConsumption;
//
//    /**
//     * <pre>
//     * 车速区间02(10-20KM/H)累计里程 单位:KM
//     * </pre>
//     **/
//    private int speedRange02Mileage;

    /**
     * <pre>
     * 车速区间02(10-20KM/H)累计时长 单位:秒
     * </pre>
     **/
    private int speedRange02Time;
//
//    /**
//     * <pre>
//     * 车速区间02(10-20KM/H)累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int speedRange02FuelConsumption;
//
//    /**
//     * <pre>
//     * 车速区间03(20-30KM/H)累计里程 单位:KM
//     * </pre>
//     **/
//    private int speedRange03Mileage;

    /**
     * <pre>
     * 车速区间03(20-30KM/H)累计时长 单位: 秒
     * </pre>
     **/
    private int speedRange03Time;
//
//    /**
//     * <pre>
//     * 车速区间03(20-30KM/H)累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int speedRange03FuelConsumption;
//
//    /**
//     * <pre>
//     * 车速区间04(30-40KM/H)累计里程 单位:KM
//     * </pre>
//     **/
//    private int speedRange04Mileage;

    /**
     * <pre>
     * 车速区间04(30-40KM/H)累计时长 单位: 秒
     * </pre>
     **/
    private int speedRange04Time;
//
//    /**
//     * <pre>
//     * 车速区间04(30-40KM/H)累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int speedRange04FuelConsumption;
//
//    /**
//     * <pre>
//     * 车速区间05(40-50KM/H)累计里程 单位:KM
//     * </pre>
//     **/
//    private int speedRange05Mileage;

    /**
     * <pre>
     * 车速区间05(40-50KM/H)累计时长 单位: 秒
     * </pre>
     **/
    private int speedRange05Time;
//
//    /**
//     * <pre>
//     * 车速区间05(40-50KM/H)累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int speedRange05FuelConsumption;
//
//    /**
//     * <pre>
//     * 车速区间06(50-60KM/H)累计里程 单位:KM
//     * </pre>
//     **/
//    private int speedRange06Mileage;

    /**
     * <pre>
     * 车速区间06(50-60KM/H)累计时长 单位: 秒
     * </pre>
     **/
    private int speedRange06Time;

//    /**
//     * <pre>
//     * 车速区间06(50-60KM/H)累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int speedRange06FuelConsumption;
//
//    /**
//     * <pre>
//     * 车速区间07(60-70KM/H)累计里程 单位:KM
//     * </pre>
//     **/
//    private int speedRange07Mileage;

    /**
     * <pre>
     * 车速区间07(60-70KM/H)累计时长 单位: 秒
     * </pre>
     **/
    private int speedRange07Time;

//    /**
//     * <pre>
//     * 车速区间07(60-70KM/H)累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int speedRange07FuelConsumption;
//
//    /**
//     * <pre>
//     * 车速区间08(70-80KM/H)累计里程 单位:KM
//     * </pre>
//     **/
//    private int speedRange08Mileage;

    /**
     * <pre>
     * 车速区间08(70-80KM/H)累计时长 单位: 秒
     * </pre>
     **/
    private int speedRange08Time;

//    /**
//     * <pre>
//     * 车速区间08(70-80KM/H)累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int speedRange08FuelConsumption;
//
//    /**
//     * <pre>
//     * 车速区间09(80-90KM/H)累计里程 单位:KM
//     * </pre>
//     **/
//    private int speedRange09Mileage;

    /**
     * <pre>
     * 车速区间09(80-90KM/H)累计时长 单位: 秒
     * </pre>
     **/
    private int speedRange09Time;

//    /**
//     * <pre>
//     * 车速区间09(80-90KM/H)累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int speedRange09FuelConsumption;
//
//    /**
//     * <pre>
//     * 车速区间10(90-100KM/H)累计里程 单位:KM
//     * </pre>
//     **/
//    private int speedRange10Mileage;

    /**
     * <pre>
     * 车速区间10(90-100KM/H)累计时长 单位: 秒
     * </pre>
     **/
    private int speedRange10Time;

//    /**
//     * <pre>
//     * 车速区间10(90-100KM/H)累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int speedRange10FuelConsumption;
//
//    /**
//     * <pre>
//     * 车速区间11(100-110KM/H)累计里程 单位:KM
//     * </pre>
//     **/
//    private int speedRange11Mileage;

    /**
     * <pre>
     * 车速区间11(100-110KM/H)累计时长 单位: 秒
     * </pre>
     **/
    private int speedRange11Time;

//    /**
//     * <pre>
//     * 车速区间11(100-110KM/H)累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int speedRange11FuelConsumption;
//
//    /**
//     * <pre>
//     * 车速区间12(110-120KM/H)累计里程 单位:KM
//     * </pre>
//     **/
//    private int speedRange12Mileage;

    /**
     * <pre>
     * 车速区间12(110-120KM/H)累计时长 单位: 秒
     * </pre>
     **/
    private int speedRange12Time;

//    /**
//     * <pre>
//     * 车速区间12(110-120KM/H)累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int speedRange12FuelConsumption;
//
//    /**
//     * <pre>
//     * 超区间车速(>120KM/H)累计里程 单位:KM
//     * </pre>
//     **/
//    private int exceedSpeedRangeMileage;

    /**
     * <pre>
     * 超区间车速(>120KM/H)累计时长 单位: 秒
     * </pre>
     **/
    private int exceedSpeedRangeTime;

//    /**
//     * <pre>
//     * 超区间车速(>120KM/H)累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int exceedSpeedRangeFuelConsumption;
//
//    /**
//     * <pre>
//     * 辅助制动里程 单位:M
//     * </pre>
//     **/
//    private int assistBrakeMileage;
//
//    /**
//     * <pre>
//     * 辅助制动时长 单位:分
//     * </pre>
//     **/
//    private int assistBrakeTime;
//
//    /**
//     * <pre>
//     * 疲劳驾驶次数
//     * </pre>
//     **/
//    private int fatigueDrivingFrequency;
//
//    /**
//     * <pre>
//     * 停车轰油门时长 单位:S
//     * </pre>
//     **/
//    private int parkingStepOnAcceleratorTime;
//
//    /**
//     * <pre>
//     * 停车轰油门油耗 单位:0.1L
//     * </pre>
//     **/
//    private int parkingStepOnAcceleratorFuelConsumption;
//
//    /**
//     * <pre>
//     * 发动机工作时长 单位:S
//     * </pre>
//     **/
//    private int engineWorkTime;
//
//    /**
//     * <pre>
//     * 行驶过程中手刹使用次数
//     * </pre>
//     **/
//    private int drivingHandbrakeUseFrequency;
//
//    /**
//     * <pre>
//     * 行驶过程中手刹使用时长 单位:S
//     * </pre>
//     **/
//    private int drivingHandbrakeUseTime;
//
//    /**
//     * <pre>
//     * 行驶过程中手刹使用里程 单位:M
//     * </pre>
//     **/
//    private int drivingHandbrakeUseMileage;
//
//    /**
//     * <pre>
//     * 停车怠速状态下手刹使用次数
//     * </pre>
//     **/
//    private int parkingIdleHandbrakeUseFrequency;
//
//    /**
//     * <pre>
//     * 停车怠速状态下手刹使用时长 单位:S
//     * </pre>
//     **/
//    private int parkingIdleHandbrakeUseTime;
//
//    /**
//     * <pre>
//     * 挡位01使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear01UseRatio;
//
//    /**
//     * <pre>
//     * 挡位01累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear01Mileage;
//
//    /**
//     * <pre>
//     * 挡位01累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear01FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位01累计时长 单位:分
//     * </pre>
//     **/
//    private int gear01Time;
//
//    /**
//     * <pre>
//     * 挡位01累计次数
//     * </pre>
//     **/
//    private int gear01Frequency;
//
//    /**
//     * <pre>
//     * 挡位02使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear02UseRatio;
//
//    /**
//     * <pre>
//     * 挡位02累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear02Mileage;
//
//    /**
//     * <pre>
//     * 挡位02累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear02FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位02累计时长 单位:分
//     * </pre>
//     **/
//    private int gear02Time;
//
//    /**
//     * <pre>
//     * 挡位02累计次数
//     * </pre>
//     **/
//    private int gear02Frequency;
//
//    /**
//     * <pre>
//     * 挡位03使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear03UseRatio;
//
//    /**
//     * <pre>
//     * 挡位03累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear03Mileage;
//
//    /**
//     * <pre>
//     * 挡位03累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear03FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位03累计时长 单位:分
//     * </pre>
//     **/
//    private int gear03Time;
//
//    /**
//     * <pre>
//     * 挡位03累计次数
//     * </pre>
//     **/
//    private int gear03Frequency;
//
//    /**
//     * <pre>
//     * 挡位04使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear04UseRatio;
//
//    /**
//     * <pre>
//     * 挡位04累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear04Mileage;
//
//    /**
//     * <pre>
//     * 挡位04累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear04FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位04累计时长 单位:分
//     * </pre>
//     **/
//    private int gear04Time;
//
//    /**
//     * <pre>
//     * 挡位04累计次数
//     * </pre>
//     **/
//    private int gear04Frequency;
//
//    /**
//     * <pre>
//     * 挡位05使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear05UseRatio;
//
//    /**
//     * <pre>
//     * 挡位05累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear05Mileage;
//
//    /**
//     * <pre>
//     * 挡位05累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear05FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位05累计时长 单位:分
//     * </pre>
//     **/
//    private int gear05Time;
//
//    /**
//     * <pre>
//     * 挡位05累计次数
//     * </pre>
//     **/
//    private int gear05Frequency;
//
//    /**
//     * <pre>
//     * 挡位06使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear06UseRatio;
//
//    /**
//     * <pre>
//     * 挡位06累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear06Mileage;
//
//    /**
//     * <pre>
//     * 挡位06累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear06FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位06累计时长 单位:分
//     * </pre>
//     **/
//    private int gear06Time;
//
//    /**
//     * <pre>
//     * 挡位06累计次数
//     * </pre>
//     **/
//    private int gear06Frequency;
//
//    /**
//     * <pre>
//     * 挡位07使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear07UseRatio;
//
//    /**
//     * <pre>
//     * 挡位07累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear07Mileage;
//
//    /**
//     * <pre>
//     * 挡位07累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear07FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位07累计时长 单位:分
//     * </pre>
//     **/
//    private int gear07Time;
//
//    /**
//     * <pre>
//     * 挡位07累计次数
//     * </pre>
//     **/
//    private int gear07Frequency;
//
//    /**
//     * <pre>
//     * 挡位08使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear08UseRatio;
//
//    /**
//     * <pre>
//     * 挡位08累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear08Mileage;
//
//    /**
//     * <pre>
//     * 挡位08累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear08FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位08累计时长 单位:分
//     * </pre>
//     **/
//    private int gear08Time;
//
//    /**
//     * <pre>
//     * 挡位08累计次数
//     * </pre>
//     **/
//    private int gear08Frequency;
//
//    /**
//     * <pre>
//     * 挡位09使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear09UseRatio;
//
//    /**
//     * <pre>
//     * 挡位09累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear09Mileage;
//
//    /**
//     * <pre>
//     * 挡位09累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear09FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位09累计时长 单位:分
//     * </pre>
//     **/
//    private int gear09Time;
//
//    /**
//     * <pre>
//     * 挡位09累计次数
//     * </pre>
//     **/
//    private int gear09Frequency;
//
//    /**
//     * <pre>
//     * 挡位10使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear10UseRatio;
//
//    /**
//     * <pre>
//     * 挡位10累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear10Mileage;
//
//    /**
//     * <pre>
//     * 挡位10累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear10FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位10累计时长 单位:分
//     * </pre>
//     **/
//    private int gear10Time;
//
//    /**
//     * <pre>
//     * 挡位10累计次数
//     * </pre>
//     **/
//    private int gear10Frequency;
//
//    /**
//     * <pre>
//     * 挡位11使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear11UseRatio;
//
//    /**
//     * <pre>
//     * 挡位11累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear11Mileage;
//
//    /**
//     * <pre>
//     * 挡位11累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear11FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位11累计时长 单位:分
//     * </pre>
//     **/
//    private int gear11Time;
//
//    /**
//     * <pre>
//     * 挡位11累计次数
//     * </pre>
//     **/
//    private int gear11Frequency;
//
//    /**
//     * <pre>
//     * 挡位12使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear12UseRatio;
//
//    /**
//     * <pre>
//     * 挡位12累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear12Mileage;
//
//    /**
//     * <pre>
//     * 挡位12累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear12FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位12累计时长 单位:分
//     * </pre>
//     **/
//    private int gear12Time;
//
//    /**
//     * <pre>
//     * 挡位12累计次数
//     * </pre>
//     **/
//    private int gear12Frequency;
//
//    /**
//     * <pre>
//     * 挡位13使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear13UseRatio;
//
//    /**
//     * <pre>
//     * 挡位13累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear13Mileage;
//
//    /**
//     * <pre>
//     * 挡位13累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear13FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位13累计时长 单位:分
//     * </pre>
//     **/
//    private int gear13Time;
//
//    /**
//     * <pre>
//     * 挡位13累计次数
//     * </pre>
//     **/
//    private int gear13Frequency;
//
//    /**
//     * <pre>
//     * 挡位14使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear14UseRatio;
//
//    /**
//     * <pre>
//     * 挡位14累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear14Mileage;
//
//    /**
//     * <pre>
//     * 挡位14累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear14FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位14累计时长 单位:分
//     * </pre>
//     **/
//    private int gear14Time;
//
//    /**
//     * <pre>
//     * 挡位14累计次数
//     * </pre>
//     **/
//    private int gear14Frequency;
//
//    /**
//     * <pre>
//     * 挡位15使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear15UseRatio;
//
//    /**
//     * <pre>
//     * 挡位15累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear15Mileage;
//
//    /**
//     * <pre>
//     * 挡位15累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear15FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位15累计时长 单位:分
//     * </pre>
//     **/
//    private int gear15Time;
//
//    /**
//     * <pre>
//     * 挡位15累计次数
//     * </pre>
//     **/
//    private int gear15Frequency;
//
//    /**
//     * <pre>
//     * 挡位16使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gear16UseRatio;
//
//    /**
//     * <pre>
//     * 挡位16累计里程 单位:KM
//     * </pre>
//     **/
//    private int gear16Mileage;
//
//    /**
//     * <pre>
//     * 挡位16累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gear16FuelConsumption;
//
//    /**
//     * <pre>
//     * 挡位16累计时长 单位:分
//     * </pre>
//     **/
//    private int gear16Time;
//
//    /**
//     * <pre>
//     * 挡位16累计次数
//     * </pre>
//     **/
//    private int gear16Frequency;
//
//    /**
//     * <pre>
//     * 其它挡位(包含倒档，空挡)使用占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int gearOtherUseRatio;
//
//    /**
//     * <pre>
//     * 其它挡位(包含倒档，空挡)累计里程 单位:KM
//     * </pre>
//     **/
//    private int gearOtherMileage;
//
//    /**
//     * <pre>
//     * 其它挡位(包含倒档，空挡)累计油耗 单位:0.1L
//     * </pre>
//     **/
//    private int gearOtherFuelConsumption;
//
//    /**
//     * <pre>
//     * 其它挡位(包含倒档，空挡)累计时长 单位:分
//     * </pre>
//     **/
//    private int gearOtherTime;
//
//    /**
//     * <pre>
//     * 其它挡位(包含倒档，空挡)累计次数
//     * </pre>
//     **/
//    private int gearOtherFrequency;
//
//    /**
//     * <pre>
//     * 发动机转速区间01(500-800RPM)里程 单位:KM
//     * </pre>
//     **/
//    private int engineSpeedRange01Mileage;

    /**
     * <pre>
     * 发动机转速区间01(500-800RPM)时长 单位:秒
     * </pre>
     **/
    private int engineSpeedRange01Time;
//
//    /**
//     * <pre>
//     * 发动机转速区间01(500-800RPM)油耗 单位:0.1L
//     * </pre>
//     **/
//    private int engineSpeedRange01FuelConsumption;

    /**
     * <pre>
     * 发动机转速区间01(500-800RPM)使用占比 单位:百分比，范围:0~100
     * </pre>
     **/
    private int engineSpeedRange01UseRatio;
//
//    /**
//     * <pre>
//     * 发动机转速区间01(500-800RPM)里程占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int engineSpeedRange01MileageRatio;
//
//    /**
//     * <pre>
//     * 发动机转速区间01(500-800RPM)油耗占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int engineSpeedRange01FuelConsumptionRatio;
//
//    /**
//     * <pre>
//     * 发动机转速区间02(800-1100RPM)里程 单位:KM
//     * </pre>
//     **/
//    private int engineSpeedRange02Mileage;

    /**
     * <pre>
     * 发动机转速区间02(800-1100RPM)时长 单位:秒
     * </pre>
     **/
    private int engineSpeedRange02Time;

//    /**
//     * <pre>
//     * 发动机转速区间02(800-1100RPM)油耗 单位:0.1L
//     * </pre>
//     **/
//    private int engineSpeedRange02FuelConsumption;

    /**
     * <pre>
     * 发动机转速区间02(800-1100RPM)使用占比 单位:百分比，范围:0~100
     * </pre>
     **/
    private int engineSpeedRange02UseRatio;

//    /**
//     * <pre>
//     * 发动机转速区间02(800-1100RPM)里程占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int engineSpeedRange02MileageRatio;
//
//    /**
//     * <pre>
//     * 发动机转速区间02(800-1100RPM)油耗占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int engineSpeedRange02FuelConsumptionRatio;
//
//    /**
//     * <pre>
//     * 发动机转速区间03(1100-1400RPM)里程 单位:KM
//     * </pre>
//     **/
//    private int engineSpeedRange03Mileage;

    /**
     * <pre>
     * 发动机转速区间03(1100-1400RPM)时长 单位:秒
     * </pre>
     **/
    private int engineSpeedRange03Time;

//    /**
//     * <pre>
//     * 发动机转速区间03(1100-1400RPM)油耗 单位:0.1L
//     * </pre>
//     **/
//    private int engineSpeedRange03FuelConsumption;

    /**
     * <pre>
     * 发动机转速区间03(1100-1400RPM)使用占比 单位:百分比，范围:0~100
     * </pre>
     **/
    private int engineSpeedRange03UseRatio;

//    /**
//     * <pre>
//     * 发动机转速区间03(1100-1400RPM)里程占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int engineSpeedRange03MileageRatio;
//
//    /**
//     * <pre>
//     * 发动机转速区间03(1100-1400RPM)油耗占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int engineSpeedRange03FuelConsumptionRatio;
//
//    /**
//     * <pre>
//     * 发动机转速区间04(1400-1700RPM)里程 单位:KM
//     * </pre>
//     **/
//    private int engineSpeedRange04Mileage;

    /**
     * <pre>
     * 发动机转速区间04(1400-1700RPM)时长 单位:秒
     * </pre>
     **/
    private int engineSpeedRange04Time;

//    /**
//     * <pre>
//     * 发动机转速区间04(1400-1700RPM)油耗 单位:0.1L
//     * </pre>
//     **/
//    private int engineSpeedRange04FuelConsumption;

    /**
     * <pre>
     * 发动机转速区间04(1400-1700RPM)使用占比 单位:百分比，范围:0~100
     * </pre>
     **/
    private int engineSpeedRange04UseRatio;

//    /**
//     * <pre>
//     * 发动机转速区间04(1400-1700RPM)里程占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int engineSpeedRange04MileageRatio;
//
//    /**
//     * <pre>
//     * 发动机转速区间04(1400-1700RPM)油耗占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int engineSpeedRange04FuelConsumptionRatio;
//
//    /**
//     * <pre>
//     * 发动机转速区间05(1700-2000RPM)里程 单位:KM
//     * </pre>
//     **/
//    private int engineSpeedRange05Mileage;

    /**
     * <pre>
     * 发动机转速区间05(1700-2000RPM)时长 单位:秒
     * </pre>
     **/
    private int engineSpeedRange05Time;

//    /**
//     * <pre>
//     * 发动机转速区间05(1700-2000RPM)油耗 单位:0.1L
//     * </pre>
//     **/
//    private int engineSpeedRange05FuelConsumption;

    /**
     * <pre>
     * 发动机转速区间05(1700-2000RPM)使用占比 单位:百分比，范围:0~100
     * </pre>
     **/
    private int engineSpeedRange05UseRatio;

//    /**
//     * <pre>
//     * 发动机转速区间05(1700-2000RPM)里程占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int engineSpeedRange05MileageRatio;
//
//    /**
//     * <pre>
//     * 发动机转速区间05(1700-2000RPM)油耗占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int engineSpeedRange05FuelConsumptionRatio;
//
//    /**
//     * <pre>
//     * 发动机转速区间06(2000-2300RPM)里程 单位:KM
//     * </pre>
//     **/
//    private int engineSpeedRange06Mileage;

    /**
     * <pre>
     * 发动机转速区间06(2000-2300RPM)时长 单位:秒
     * </pre>
     **/
    private int engineSpeedRange06Time;

//    /**
//     * <pre>
//     * 发动机转速区间06(2000-2300RPM)油耗 单位:0.1L
//     * </pre>
//     **/
//    private int engineSpeedRange06FuelConsumption;

    /**
     * <pre>
     * 发动机转速区间06(2000-2300RPM)使用占比 单位:百分比，范围:0~100
     * </pre>
     **/
    private int engineSpeedRange06UseRatio;

//    /**
//     * <pre>
//     * 发动机转速区间06(2000-2300RPM)里程占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int engineSpeedRange06MileageRatio;
//
//    /**
//     * <pre>
//     * 发动机转速区间06(2000-2300RPM)油耗占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int engineSpeedRange06FuelConsumptionRatio;
//
//    /**
//     * <pre>
//     * 超区间发动机转速(>2300RPM)里程 单位:KM
//     * </pre>
//     **/
//    private int exceedEngineSpeedRangeMileage;

    /**
     * <pre>
     * 超区间发动机转速(>2300RPM)时长 单位:秒
     * </pre>
     **/
    private int exceedEngineSpeedRangeTime;

//    /**
//     * <pre>
//     * 超区间发动机转速(>2300RPM)油耗 单位:0.1L
//     * </pre>
//     **/
//    private int exceedEngineSpeedRangeFuelConsumption;

    /**
     * <pre>
     * 超区间发动机转速(>2300RPM)使用占比 单位:百分比，范围:0~100
     * </pre>
     **/
    private int exceedEngineSpeedRangeUseRatio;

//    /**
//     * <pre>
//     * 超区间发动机转速(>2300RPM)里程占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int exceedEngineSpeedRangeMileageRatio;
//
//    /**
//     * <pre>
//     * 超区间发动机转速(>2300RPM)油耗占比 单位:百分比，范围:0~100
//     * </pre>
//     **/
//    private int exceedEngineSpeedRangeFuelConsumptionRatio;
//
//    /**
//     * <pre>
//     * 二氧化碳排放量统计 单位:0.01KG
//     * </pre>
//     **/
//    private int carbonDioxideEmissions;
//
//    /**
//     * <pre>
//     * 急转弯次数（急转向次数）
//     * </pre>
//     **/
//    private int sharpCurveFrequency;
//
//    /**
//     * <pre>
//     * 急转向行驶距离 单位:M
//     * </pre>
//     **/
//    private int sharpCurveMileage;

    /**
     * <pre>
     * 引擎超转数次数
     * </pre>
     **/
    private int engineOverSpeedNumber;
//    /**
//     * <pre>
//     * 全油门时长 单位:S
//     * </pre>
//     **/
//    private int fullAcceleratorTime;
//    /**
//     * <pre>
//     * 大油门时长 单位:S
//     * </pre>
//     **/
//    private int largeAcceleratorTime;
    /**
     * <pre>
     * 东风-熄火滑行次数
     * </pre>
     **/
    private int stallingSlideFrequency;

//    /**
//     * <pre>
//     * 东风-熄火滑行里程 单位:M
//     * </pre>
//     **/
//    private int stallingSlideMileage;
//
//    /**
//     * <pre>
//     * 东风-熄火滑行油耗 单位:ML
//     * </pre>
//     **/
//    private int stallingSlideConsumption;
//
//    /**
//     * <pre>
//     * 东风-熄火滑行时长 单位:MS
//     * </pre>
//     **/
//    private int stallingSlideTime;
    /**
     * <pre>
     * 东风-夜晚开车次数
     * </pre>
     **/
    private int inNightFrequency;
//    /**
//     * <pre>
//     * 东风-夜晚时长 单位:S
//     * </pre>
//     **/
//    private int inNightTime;
    /**
     * <pre>
     * 东风-急转弯次数
     * </pre>
     **/
    private int sharpTurnFrequency;
//    /**
//     * <pre>
//     * 大油门里程 单位:S
//     * </pre>
//     **/
//    private int largeAcceleratorDistance;
//
//    /**
//     * <pre>
//     * 大油门油耗 单位:ML
//     * </pre>
//     **/
//    private int largeAcceleratorFuelConsumption;

    /**
     * <pre>
     * 大油门次数
     * </pre>
     **/
    private int largeAcceleratorFrequency;

//    /**
//     * <pre>
//     * 全油门里程 单位:S
//     * </pre>
//     **/
//    private int fullAcceleratorDistance;
//
//    /**
//     * <pre>
//     * 全油门油耗 单位:ML
//     * </pre>
//     **/
//    private int fullAcceleratorFuelConsumption;

    /**
     * <pre>
     * 全油门次数
     * </pre>
     **/
    private int fullAcceleratorFrequency;

//    /**
//     * <pre>
//     *  东风-急减速时长 单位:0.1S
//     * </pre>
//     **/
//    private int dfSharpDownSpeedTime;
//
//    /**
//     * <pre>
//     *  东风-急减速距离 单位:M
//     * </pre>
//     **/
//    private int dfSharpDownSpeedMileage;

    /**
     * <pre>
     *  东风-急减速次数
     * </pre>
     **/
    private int dfSharpDownSpeedFrequency;

//    /**
//     * <pre>
//     *  东风-急减速油耗 单位:ML
//     * </pre>
//     **/
//    private int dfSharpDownSpeedFuelConsumption;

//    /**
//     * <pre>
//     *  东风-急加速时长 单位:0.1S
//     * </pre>
//     **/
//    private int dfSharpUpSpeedTime;
//
//    /**
//     * <pre>
//     *  东风-急加速距离 单位:M
//     * </pre>
//     **/
//    private int dfSharpUpSpeedMileage;

    /**
     * <pre>
     *  东风-急加速次数
     * </pre>
     **/
    private int dfSharpUpSpeedFrequency;

//    /**
//     * <pre>
//     *  东风-急加速油耗 单位:ML
//     * </pre>
//     **/
//    private int dfSharpUpSpeedFuelConsumption;
//
//    /**
//     * <pre>
//     * 东风-超速行驶油量 单位:ML
//     * </pre>
//     **/
//    private int dfOverSpeedFuelConsumption;
//
//    /**
//     * <pre>
//     * 东风-超速行驶距离 单位:M
//     * </pre>
//     **/
//    private int dfOverSpeedDistance;
//
//    /**
//     * <pre>
//     * 东风-超速行驶时长
//     * </pre>
//     **/
//    private int dfOverSpeedTime;

    /**
     * <pre>
     * 东风-超速行驶次数
     * </pre>
     **/
    private int dfOverSpeedFrequency;

    /**
     * <pre>
     * 东风-冷车运行次数
     * </pre>
     **/
    private int dfVehicleColdStartFrequency;

//    /**
//     * <pre>
//     * 东风-冷车运行驾驶距离 单位:M
//     * </pre>
//     **/
//    private int dfColdEngineSharpDrivingMileage;
//
//    /**
//     * <pre>
//     * 东风-冷车运行驾驶时长 单位:S
//     * </pre>
//     **/
//    private int dfColdEngineSharpDrivingTime;
//
//    /**
//     * <pre>
//     * 东风-冷车运行驾驶油量 单位:ML
//     * </pre>
//     **/
//    private int dfColdEngineSharpDrivingFuelConsumption;
//
//    /**
//     * 是否跨天(0:不跨，1:跨)
//     */
//    private int acrossFlag;
//
//    /**
//     * 前段行程里程(米)
//     */
//    private int acrossMileage;
//
//    /**
//     * 前段行程油耗（升）
//     */
//    private double acrossOil;
//
//    /**
//     * 超长累计油耗（ml）
//     */
//    private int longParkingIdleFuel;
//    /**
//     * 超长怠速时长 单位:s
//     */
//    private int longParkingIdleTimeMs;

    /**
     * 超长怠速次数
     */
    private int longParkingIdleNumber;

//    /**
//     * 增加载重AMT（t）
//     */
//    private double loadamt;
//    /**
//     * 载重VECU（t）
//     */
//    private double loadvecu;
//    /**
//     * 载荷状态
//     */
//    private int loadstatus;

}
