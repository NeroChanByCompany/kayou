package com.nut.locationservice.app.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-17 13:39
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class QueryByDayPojo {

    /**
     * 终端号
     */
    private Long terminalId;
    /**
     * 日期
     */
    private String date;
    /**
     * 始统计时间戳
     */
    private Long timestamp;
    /**
     * 速度值的累加值
     */
    private long speedSum;
    /**
     * 速度个数
     */
    private int speedNum;
    /**
     * 当日标准里程（KM）
     */
    private double mMilage;
    /**
     * 当日GPS里程（KM）
     */
    private double mGps;
    /**
     * 当日油耗量(L)
     */
    private double fuel;
    /**
     * 当日工作时长(秒)
     */
    private double workHours;
    /**
     * 当日怠速时长(秒)
     */
    private double idleHours;
    /**
     * 急刹车次数
     */
    private int stboTimes;
    /**
     * 急加速次数
     */
    private int acceTimes;
    /**
     * 超速次数
     */
    private int overspeedTimes;
    /**
     * 空挡滑行次数
     */
    private int hsnsTimes;
    /**
     * 疲劳驾驶次数
     */
    private int tdTimes;
    /**
     * （日）百公里油耗
     */
    private double fuelRateDay;
    /**
     * 平均车速(不含怠速)
     */
    private double avgSpeedNoidle;
    /**
     * 平均速度 (km/h)
     */
    private double avgSpeed;
    /**
     * 怠速占比
     */
    private double idleP;
    /**
     * 日均空挡滑行
     */
    private double avgHsnsDay;
    /**
     * 超转速
     */
    private int hesTimes;
    /**
     * 经济区占比
     */
    private double ofeP;
    /**
     * 刹车次数
     */
    private int brakeTimes;
    /**
     * 超速区占比
     */
    private double overspeedP;
    /**
     * 油量消耗(L)
     */
    private double oilValue;
    /**
     * 开始里程（KM）
     */
    private double bMil;
    /**
     * 结束里程（KM）
     */
    private double eMil;
    /**
     * 开始经纬度, 乘以10的6次方
     */
    private int bLat;
    /**
     * 开始经纬度, 乘以10的6次方
     */
    private int bLng;
    /**
     * 结束经纬度, 乘以10的6次方
     */
    private int eLat;
    /**
     * 结束经纬度, 乘以10的6次方
     */
    private int eLng;
    /**
     * 开始时间 时间戳 秒级
     */
    private int start;
    /**
     * 结束时间 时间戳 秒级
     */
    private int end;
    /**
     * 统计运行时长(秒)
     */
    private int sDate;
    /**
     * 熄火滑行次数
     */
    private int flameoutGlidingCount;
    /**
     * 夜晚驾驶时长(秒)
     */
    private long nightDrivingTimes;
    /**
     * 怠速油耗(L)
     */
    private double idlingFuel;
    /**
     * 行驶油耗(L)
     */
    private double runFuel;

}
