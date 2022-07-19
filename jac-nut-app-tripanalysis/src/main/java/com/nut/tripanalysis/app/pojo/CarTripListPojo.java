package com.nut.tripanalysis.app.pojo;

import lombok.Data;

@Data
public class CarTripListPojo {

    private String tripId;

    private String terminalId;

    private long startTime;

    private long endTime;

    private int mileage;

    private double oil;

    private int score;

    private double avgOil;

    private int acrossFlag;

    private int acrossMileage;

    private double acrossOil;

}
