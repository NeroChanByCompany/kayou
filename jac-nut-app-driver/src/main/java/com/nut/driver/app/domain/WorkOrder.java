package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class WorkOrder {
    private Long id;

    private String woCode;

    private Integer woStatus;

    private Integer woType;

    private Integer protocolMark;

    private String rejectionReason;

    private Date timeCreate;

    private Date timeAccept;

    private Date timeDepart;

    private Date timeArriveExpected;

    private Date timeReceive;

    private Date timeInspectBegin;

    private Date timeInspected;

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

    private String repairItem;

    private String maintainItem;

    private String registeredPhone;

    private String userComment;

    private String mileage;

    private Integer customerLevel;

    private String modifyReason;

    private String rescueStaffName;

    private String rescueStaffPhone;

    private Integer rescueStaffNum;

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

    private Date createTime;

    private Date updateTime;

    private Integer rescueType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWoCode() {
        return woCode;
    }

    public void setWoCode(String woCode) {
        this.woCode = woCode == null ? null : woCode.trim();
    }

    public Integer getWoStatus() {
        return woStatus;
    }

    public void setWoStatus(Integer woStatus) {
        this.woStatus = woStatus;
    }

    public Integer getWoType() {
        return woType;
    }

    public void setWoType(Integer woType) {
        this.woType = woType;
    }


    public Integer getProtocolMark() {
        return protocolMark;
    }

    public void setProtocolMark(Integer protocolMark) {
        this.protocolMark = protocolMark;
    }


    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Date getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }

    public Date getTimeAccept() {
        return timeAccept;
    }

    public void setTimeAccept(Date timeAccept) {
        this.timeAccept = timeAccept;
    }

    public Date getTimeDepart() {
        return timeDepart;
    }

    public void setTimeDepart(Date timeDepart) {
        this.timeDepart = timeDepart;
    }

    public Date getTimeArriveExpected() {
        return timeArriveExpected;
    }

    public void setTimeArriveExpected(Date timeArriveExpected) {
        this.timeArriveExpected = timeArriveExpected;
    }

    public Date getTimeReceive() {
        return timeReceive;
    }

    public void setTimeReceive(Date timeReceive) {
        this.timeReceive = timeReceive;
    }

    public Date getTimeInspectBegin() {
        return timeInspectBegin;
    }

    public void setTimeInspectBegin(Date timeInspectBegin) {
        this.timeInspectBegin = timeInspectBegin;
    }

    public Date getTimeInspected() {
        return timeInspected;
    }

    public void setTimeInspected(Date timeInspected) {
        this.timeInspected = timeInspected;
    }

    public Date getTimeClose() {
        return timeClose;
    }

    public void setTimeClose(Date timeClose) {
        this.timeClose = timeClose;
    }

    public Date getTimeApplyrefuse() {
        return timeApplyrefuse;
    }

    public void setTimeApplyrefuse(Date timeApplyrefuse) {
        this.timeApplyrefuse = timeApplyrefuse;
    }

    public Date getTimeApplymodify() {
        return timeApplymodify;
    }

    public void setTimeApplymodify(Date timeApplymodify) {
        this.timeApplymodify = timeApplymodify;
    }

    public Date getTimeApplyclose() {
        return timeApplyclose;
    }

    public void setTimeApplyclose(Date timeApplyclose) {
        this.timeApplyclose = timeApplyclose;
    }

    public String getAssignTo() {
        return assignTo;
    }

    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo == null ? null : assignTo.trim();
    }

    public Integer getRefuseType() {
        return refuseType;
    }

    public void setRefuseType(Integer refuseType) {
        this.refuseType = refuseType;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason == null ? null : refuseReason.trim();
    }

    public Integer getRefuseTimes() {
        return refuseTimes;
    }

    public void setRefuseTimes(Integer refuseTimes) {
        this.refuseTimes = refuseTimes;
    }

    public Integer getCloseType() {
        return closeType;
    }

    public void setCloseType(Integer closeType) {
        this.closeType = closeType;
    }

    public String getCloseReason() {
        return closeReason;
    }

    public void setCloseReason(String closeReason) {
        this.closeReason = closeReason == null ? null : closeReason.trim();
    }

    public String getChassisNum() {
        return chassisNum;
    }

    public void setChassisNum(String chassisNum) {
        this.chassisNum = chassisNum == null ? null : chassisNum.trim();
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId == null ? null : stationId.trim();
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode == null ? null : stationCode.trim();
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName == null ? null : stationName.trim();
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode == null ? null : areaCode.trim();
    }

    public String getAppoStationId() {
        return appoStationId;
    }

    public void setAppoStationId(String appoStationId) {
        this.appoStationId = appoStationId == null ? null : appoStationId.trim();
    }

    public Date getAppoArriveTime() {
        return appoArriveTime;
    }

    public void setAppoArriveTime(Date appoArriveTime) {
        this.appoArriveTime = appoArriveTime;
    }

    public Integer getAppoType() {
        return appoType;
    }

    public void setAppoType(Integer appoType) {
        this.appoType = appoType;
    }

    public Long getAppoUserId() {
        return appoUserId;
    }

    public void setAppoUserId(Long appoUserId) {
        this.appoUserId = appoUserId;
    }

    public String getAppoUserName() {
        return appoUserName;
    }

    public void setAppoUserName(String appoUserName) {
        this.appoUserName = appoUserName == null ? null : appoUserName.trim();
    }

    public String getAppoUserPhone() {
        return appoUserPhone;
    }

    public void setAppoUserPhone(String appoUserPhone) {
        this.appoUserPhone = appoUserPhone == null ? null : appoUserPhone.trim();
    }

    public String getSendToRepairName() {
        return sendToRepairName;
    }

    public void setSendToRepairName(String sendToRepairName) {
        this.sendToRepairName = sendToRepairName == null ? null : sendToRepairName.trim();
    }

    public String getSendToRepairPhone() {
        return sendToRepairPhone;
    }

    public void setSendToRepairPhone(String sendToRepairPhone) {
        this.sendToRepairPhone = sendToRepairPhone == null ? null : sendToRepairPhone.trim();
    }

    public String getCarLon() {
        return carLon;
    }

    public void setCarLon(String carLon) {
        this.carLon = carLon == null ? null : carLon.trim();
    }

    public String getCarLat() {
        return carLat;
    }

    public void setCarLat(String carLat) {
        this.carLat = carLat == null ? null : carLat.trim();
    }

    public String getCarLocation() {
        return carLocation;
    }

    public void setCarLocation(String carLocation) {
        this.carLocation = carLocation == null ? null : carLocation.trim();
    }

    public String getCarDistance() {
        return carDistance;
    }

    public void setCarDistance(String carDistance) {
        this.carDistance = carDistance == null ? null : carDistance.trim();
    }

    public String getRepairItem() {
        return repairItem;
    }

    public void setRepairItem(String repairItem) {
        this.repairItem = repairItem == null ? null : repairItem.trim();
    }

    public String getMaintainItem() {
        return maintainItem;
    }

    public void setMaintainItem(String maintainItem) {
        this.maintainItem = maintainItem == null ? null : maintainItem.trim();
    }

    public String getRegisteredPhone() {
        return registeredPhone;
    }

    public void setRegisteredPhone(String registeredPhone) {
        this.registeredPhone = registeredPhone == null ? null : registeredPhone.trim();
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment == null ? null : userComment.trim();
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage == null ? null : mileage.trim();
    }

    public Integer getCustomerLevel() {
        return customerLevel;
    }

    public void setCustomerLevel(Integer customerLevel) {
        this.customerLevel = customerLevel;
    }

    public String getModifyReason() {
        return modifyReason;
    }

    public void setModifyReason(String modifyReason) {
        this.modifyReason = modifyReason == null ? null : modifyReason.trim();
    }

    public String getRescueStaffName() {
        return rescueStaffName;
    }

    public void setRescueStaffName(String rescueStaffName) {
        this.rescueStaffName = rescueStaffName == null ? null : rescueStaffName.trim();
    }

    public String getRescueStaffPhone() {
        return rescueStaffPhone;
    }

    public void setRescueStaffPhone(String rescueStaffPhone) {
        this.rescueStaffPhone = rescueStaffPhone == null ? null : rescueStaffPhone.trim();
    }

    public Integer getRescueStaffNum() {
        return rescueStaffNum;
    }

    public void setRescueStaffNum(Integer rescueStaffNum) {
        this.rescueStaffNum = rescueStaffNum;
    }

    public Integer getIsAbnormalReceive() {
        return isAbnormalReceive;
    }

    public void setIsAbnormalReceive(Integer isAbnormalReceive) {
        this.isAbnormalReceive = isAbnormalReceive;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId == null ? null : deviceId.trim();
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId == null ? null : operatorId.trim();
    }

    public Integer getRemindTimes() {
        return remindTimes;
    }

    public void setRemindTimes(Integer remindTimes) {
        this.remindTimes = remindTimes;
    }

    public Date getLastRemindTime() {
        return lastRemindTime;
    }

    public void setLastRemindTime(Date lastRemindTime) {
        this.lastRemindTime = lastRemindTime;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason == null ? null : cancelReason.trim();
    }

    public Integer getIsEmergency() {
        return isEmergency;
    }

    public void setIsEmergency(Integer isEmergency) {
        this.isEmergency = isEmergency;
    }

    public Integer getBreakStatus() {
        return breakStatus;
    }

    public void setBreakStatus(Integer breakStatus) {
        this.breakStatus = breakStatus;
    }

    public String getLoadDescription() {
        return loadDescription;
    }

    public void setLoadDescription(String loadDescription) {
        this.loadDescription = loadDescription == null ? null : loadDescription.trim();
    }

    public Integer getTimesRescueNumber() {
        return timesRescueNumber;
    }

    public void setTimesRescueNumber(Integer timesRescueNumber) {
        this.timesRescueNumber = timesRescueNumber;
    }

    public Integer getRescueIsTransferring() {
        return rescueIsTransferring;
    }

    public void setRescueIsTransferring(Integer rescueIsTransferring) {
        this.rescueIsTransferring = rescueIsTransferring;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getRescueType() {
        return rescueType;
    }

    public void setRescueType(Integer rescueType) {
        this.rescueType = rescueType;
    }
}