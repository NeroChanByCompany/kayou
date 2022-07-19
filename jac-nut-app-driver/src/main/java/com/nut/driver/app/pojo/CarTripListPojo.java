package com.nut.driver.app.pojo;

import lombok.Data;

@Data
public class CarTripListPojo {

    private String terminalId;

    private int mileage;

    private double oil;

    private int score;
    //急加速次数
    private int dfSharpUpSpeedFrequency;
    //急刹车次数
    private int dfSharpDownSpeedFrequency;
    //急转弯次数
    private int sharpTurnFrequency;
    //超速次数
    private int dfOverSpeedFrequency;
    //超长怠速次数
    private int longParkingIdleNumber;
    //冷车运行次数
    private int dfVehicleColdStartFrequency;
    //夜晚开车次数
    private int inNightFrequency;
    //引擎高转速次数
    private int engineOverSpeedNumber;
    //大油门次数
    private int largeAcceleratorFrequency;
    //全油门次数
    private int fullAcceleratorFrequency;
    //空挡滑行次数
    private int idlingFrequency;
    //熄火滑行次数
    private int stallingSlideFrequency;

}
