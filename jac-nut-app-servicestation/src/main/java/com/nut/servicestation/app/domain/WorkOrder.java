package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class WorkOrder {
    private Long id;

    private String woCode;

    private Integer woStatus;

    private Integer woType;

    private Integer protocolMark;

    private Date timeCreate;

    private Date timeAccept;

    private Date timeDepart;

    private Date timeArriveExpected;

    private Date timeReceive;

    private Date timeInspectBegin;

    private Date timeInspected;

    private Date timeRepairPhoto;

    private Date timeClose;

    private Date timeApplyrefuse;

    private Date timeApplymodify;

    private Date timeApplyclose;

    private String assignTo;

    private Integer refuseType;

    private String refuseReason;

    private Integer refuseTimes;

    private Integer closeType;

    private String closeReason;

    private Integer closeTimes;

    private String chassisNum;

    private String stationId;

    private String stationCode;

    private String stationName;

    private String areaCode;

    private String appoStationId;

    private Date appoArriveTime;

    private Integer appoType;

    private Long appoUserId;

    private String appoUserName;

    private String appoUserPhone;

    private String sendToRepairName;

    private String sendToRepairPhone;

    private String carLon;

    private String carLat;

    private String carLocation;

    private String carDistance;

    private String carStationDistance;

    private String manCarDistance;

    private String manStationDistance;

    private Integer appStationLimit;

    private Integer appCarLimit;

    private Integer tboxConnectStatus;

    private Integer tboxRepairRecord;

    private Integer tboxRepairAlert;

    private String repairItem;

    private String maintainItem;

    private String registeredPhone;

    private String userComment;

    private String mileage;

    private Integer customerLevel;

    private String modifyReason;

    private Integer modifyTimes;

    private String rescueStaffName;

    private String rescueStaffPhone;

    private Integer rescueStaffNum;

    private String rescueCarDevice;

    private Integer isAbnormalReceive;

    private String deviceId;

    private String operatorId;

    private Integer remindTimes;

    private Date lastRemindTime;

    private String cancelReason;

    private Integer isEmergency;

    private Integer breakStatus;

    private String loadDescription;

    private Integer timesRescueNumber;

    private Integer rescueIsTransferring;

    private Integer rescueType;

    private Integer pointCompleteness;

    private Integer pointCompletenessTwo;

    private Date estimateTime;

    private String estimateFee;

    private Integer billSubStatus;

    private String billReject;

    private Date createTime;

    private Date updateTime;
}