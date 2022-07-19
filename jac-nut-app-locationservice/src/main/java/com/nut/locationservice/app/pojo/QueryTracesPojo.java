package com.nut.locationservice.app.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-17 09:28
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class QueryTracesPojo {
    //地址
    private String address;
    //报警
    private String alarm;
    //故障
    private String fault;
    //GPS时间
    private String gpsdate;
    //经度
    private double lat;
    //纬度
    private double lng;
    //剩余油量
    private String oilwear;
    //状态
    private String statue;
    //整车油耗
    private String totalFuelConsumption;
    //整车里程
    private String totolmileage;
    //速度
    private String velocity;
    //接收时间
    private String receiveDate;
    //方向
    private String direction;
    //终端里程
    private String mileage;
    //导出明细增加字段
    private double height;//高程
    private String dailyMileage;//当日里程 小计里程
    private double rotation;//车辆当前转速
    private double cumulativeTurningNumber;//发动机累计转数（单位:1000rpm）
    private long cumulativeRunningTime;//累计运行时间 秒
    private String cumulativeRunningTimeStr;//累计运行时间 秒
    private double coolingWaterTem;//发动机冷却水温度
    private double totalOil;//总油耗
    private double fuelConsumptionRate;//发动机燃油消耗率（毫升/小时）
    private double averageFuelConsumption;//平均燃油消耗率（千米/升）
    private double realTimeOilConsumption;//瞬时油耗
    private String engineTorMode;//转矩控制模式 0 低怠速调节器/无请求（默认模式）1 加速踏板2 巡航控制3 PTO调节器4 车速调节器5 ASR控制6 变速器控制7 ABS控制 8 转矩限定9 高速调节器10 制动系统11 遥控加速器
    private double driverEnginePercentTor;//驾驶员需求发动机转矩百分比
    private double actualEnginePercentTor;//实际发动机转矩百分比
    private String accPedalLowIdleSwitch;//加速踏板低怠速开关
    private String accPedalKickdownSwitch;//加速踏板Kickdown开关：0 被动 1 主动
    private String accPedalPos;//加速踏板开度
    private double percentLoadAtCurrentSpd;//当前速度下，负载百分比
    private double nominalFrictionPercentTrq;//名义摩擦力矩百分比
    private String parkingBrakeSwitch;//驻车制动器开关：0 未操作  1 操作
    private double wheelBasedVehicleSpd;//车轮车速
    private String cruiseCtrlActive;//巡航控制开关状态：0 未激活 1 激活
    private String brakeSwitch;//制动开关：0 未操作 1 操作
    private String clutchSwitch;//离合器开关：0 未分离  1 分离
    private String cruiseCtrlSetSwitch;//巡航控制设置开关：0 无按钮按下 1 退出巡航
    private String cruiseCtrlCoastSwitch;//巡航控制减速开关：0 未按下 1 按下
    private String cruiseCtrlResumeSwitch;//巡航控制恢复开关：0 未按下 1 按下
    private String cruiseCtrlAccSwitch;//巡航控制加速开关：0 未按下  1 按下
    private double cruiseCtrlSetSpd;//巡航控制设置速度
    private String cruiseCtrlStates;//巡航控制状态： 0 关闭/禁止 1 保持 2 加速 3 减速/滑行  4 恢复 5 设置  6 加速踏板取代
    private int outPutRoateSpeed;//轴转速
    private long tcuLoad;// 载重AMT
    private long vecuLoad;// 载重vecu
}
