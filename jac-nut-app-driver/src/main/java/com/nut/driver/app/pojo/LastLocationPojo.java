package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @author liuBing
 * @Classname LastLocationPojo
 * @Description TODO
 * @Date 2021/6/24 11:03
 */
@Data
public class LastLocationPojo {

    /**
     *点位上传时间，从1970-01-01 00:00:00开始所经过的S数。
     */
    private long gpsDate;

    /**
     *轨迹点经度，以度为单位的纬度值乘以 10 的 6 次方，精确到百万 分之一度
     */
    private int lon;

    /**
     *轨迹点纬度，以度为单位的纬度值乘以 10 的 6 次方，精确到百万 分之一度
     */
    private int lat;

    /**
     *国家坐标系下原始点位轨迹点经度，以度为单位的经度值乘以 10 的 6 次方，精确到百万 分之一度
     */
    private int orgLnt;

    /**
     *国家坐标系下原始点位轨迹点纬度，以度为单位的纬度值乘以 10 的 6 次方，精确到百万 分之一度
     */
    private int orglat;

    /**
     *接收时间
     */
    private long receiveDate;

    /**
     *方向,顺时针(0~359),正北为0
     */
    private int direction;

    /**
     *高程，海拔高度，单位为米
     */
    private int height;

    /**
     *行驶里程数，单位米
     */
    private long mileage;

    /**
     *速度
     */
    private double speed;

    /**
     *其他状态（acc、车门）标志位
     * 在线： ("在线行驶", "1,3"), ("在线定位", "1, 5"),  ("在线不定位", "1,6"),
     *不在线： ("不在线行驶", "2,3"),  ("不在线停车", "2,4"), ("不在线定位", "2, 5"), ("不在线不定位", "2, 6")
     * 怠速行驶：("在线停车", "1,4"),
     */
    private String orgStatus;

    /**
     * 车辆状态
     */
    private String status;

    /**
     *里程
     */
    private float sm;

    /**
     *报警标志位
     */
    private int alarm;

    /**
     *整车油耗，标准油耗，升，保留2位小数
     */
    private float sf;

    /**
     *终端id
     */
    private long terminalId;

    /**
     *剩余油量，车辆当前油量、燃油液位（百分比）
     */
    private long oilValue;

    /**
     *车辆当前转速
     */
    private long rotation;

    /**
     *GPS终端里程，千米，保留2位小数
     */
    private long addm;

    /**
     *发动机累计运行时间 秒
     */
    private long crt;

    /**
     *加速踏板开度
     */
    private long app;

    /**
     *仪表里程
     */
    private long mileageDD;

    /**
     *发动机燃油消耗率（毫升/小时）
     */
    private long ifc;

    /**
     *今日里程单位m
     */
    private float dayMil;

    /**
     *今日油耗单位升
     */
    private float dayOil;

    /**
     *省份编码
     */
    private int provinceCode;

    /**
     *城市编码
     */
    private int cityCode;


    /**
     * CAN数据有效 末次位置PB Base64加密
     */
    private String canPB;
    /**
     * 所有末次位置PB Base64加密
     */
    private String allPB;
    /**
     * 经纬度有效末次位置PB Base64加密
     */
    private String latLonPB;
    /**
     * 针对车况报表录入末次位置PB Base64加密
     */
    private String combination;
}
