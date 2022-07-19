package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class WorkOrderOutDetail {
    private Long id;

    private String woCode;

    private int outTimes;

    private Date sOutDate;

    public Date getsOutDate() {
        return sOutDate;
    }

    public void setsOutDate(Date sOutDate) {
        this.sOutDate = sOutDate;
    }

    public Date geteOutDate() {
        return eOutDate;
    }

    public void seteOutDate(Date eOutDate) {
        this.eOutDate = eOutDate;
    }

    private Date eOutDate;

    private String serviceLicense;

    private Float startMileage;

    private Float endMileage;

    private Float appOutMileage;

    private String startAddress;

    private String endAddress;

    private Date twiceSOutDate;

    private Date twiceEOutDate;

    private String twiceServiceLicense;

    private Float twiceStartMileage;

    private Float twiceEndMileage;

    private Float twiceAppOutMileage;

    private String twiceStartAddress;

    private String twiceEndAddress;

    private String twiceOutCause;

}