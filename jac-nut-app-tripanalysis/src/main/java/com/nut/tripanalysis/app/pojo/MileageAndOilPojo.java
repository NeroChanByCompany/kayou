package com.nut.tripanalysis.app.pojo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by shianwei on 2018/8/14.
 */
@Data
public class MileageAndOilPojo implements Serializable {
    private long terminalId;
    private String date;
    private Long timestamp;
    private long speedSum;
    private int speedNum;
    private double mMilage;
    private double mGps;
    private double fuel;
    private double runFuel;
    private double idlingFuel;
    private double workHours;
    private double idleHours;
    private int stboTimes;
    private int acceTimes;
    private int overspeedTimes;
    private int hsnsTimes;
    private int tdTimes;
    private double fuelRateDay;
    private double avgSpeedNoidle;
    private double avgSpeed;
    private double idleP;
    private double avgHsnsDay;
    private int hesTimes;
    private double ofeP;
    private int brakeTimes;
    private double overspeedP;
    private double oilValue;
    private double bMil;
    private double eMil;
    private int bLat;
    private int bLng;
    private int eLat;
    private int eLng;
    private int start;
    private int end;
    private int sDate;
    private String terminalNum;
    public double getmMilage() {
        return mMilage;
    }

    public void setmMilage(double mMilage) {
        this.mMilage = mMilage;
    }

    public DBObject toDBObject() {
        DBObject alarm = new BasicDBObject();
        alarm.put("terminalId",this.terminalId);
        alarm.put("date",this.date);
        alarm.put("timestamp",this.timestamp);
        alarm.put("speedSum",this.speedSum);
        alarm.put("speedNum",this.speedNum);
        alarm.put("mMilage",this.mMilage);
        alarm.put("mGps",this.mGps);
        alarm.put("fuel",this.fuel);
        alarm.put("runFuel",this.runFuel);
        alarm.put("idlingFuel",this.idlingFuel);
        alarm.put("workHours",this.workHours);
        alarm.put("idleHours",this.idleHours);
        alarm.put("stboTimes",this.stboTimes);
        alarm.put("acceTimes",this.acceTimes);
        alarm.put("overspeedTimes",this.overspeedTimes);
        alarm.put("hsnsTimes",this.hsnsTimes);
        alarm.put("tdTimes",this.tdTimes);
        alarm.put("fuelRateDay",this.fuelRateDay);
        alarm.put("avgSpeedNoidle",this.avgSpeedNoidle);
        alarm.put("avgSpeed",this.avgSpeed);
        alarm.put("idleP",this.idleP);
        alarm.put("avgHsnsDay",this.avgHsnsDay);
        alarm.put("hesTimes",this.hesTimes);
        alarm.put("ofeP",this.ofeP);
        alarm.put("brakeTimes",this.brakeTimes);
        alarm.put("overspeedP",this.overspeedP);
        alarm.put("oilValue",this.oilValue);
        alarm.put("bMil",this.bMil);
        alarm.put("eMil",this.eMil);
        alarm.put("bLat",this.bLat);
        alarm.put("bLng",this.bLng);
        alarm.put("eLat",this.eLat);
        alarm.put("eLng",this.eLng);
        alarm.put("start",this.start);
        alarm.put("end",this.end);
        alarm.put("sDate",this.sDate);
        return alarm;
    }
}
