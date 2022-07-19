package com.nut.driver.app.entity;

import lombok.Data;

/**
 * @Description: 轨迹回放查询抽析
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.entity
 * @Author: yzl
 * @CreateTime: 2021-06-30 09:21
 * @Version: 1.0
 */
@Data
public class TrackInfoEntity {
    private double _instant_oil;  //  瞬时油耗 L/h
    private double _mileage;  //  总里程
    private long _time;  //  轨迹点时间
    private double _x;  //  轨迹点经度
    private double _y;  //  轨迹点维度
    private int _v;  //  轨迹点速度 公里/小时
    private String _car_code;  //  车牌
    private String _auto_terminal;  //  通信号
    private String _car_id;  //  汽车唯一id
    private int _direction;//方向
    private	double _total_oil;  //  燃油消耗总量(升)
    private String status;  //当前状态
    private String location;  //当前位置
    private double standardMileage; //标准里程
}
