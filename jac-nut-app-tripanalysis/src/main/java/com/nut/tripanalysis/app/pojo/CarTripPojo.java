package com.nut.tripanalysis.app.pojo;

import lombok.Data;

import java.util.List;

@Data
public class CarTripPojo {
    private List<CarTripListPojo> data;

    private int total;

}
