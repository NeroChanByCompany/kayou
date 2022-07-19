package com.nut.driver.app.domain;


/**
 * description
 * param 兑吧调用积分消费参数
 * return
 * author vv
 * createTime 2020/12/1 20:34
 **/
public class IntegralConsumeConfirmInfo {
    private String appKey;
    private String timestamp;
    private String uid;
    private String success;
    private String errorMessage;
    private String orderNum;
    private String bizId;
    private String sign;

    @Override
    public String toString() {
        return "IntegralConsumeConfirmInfo{" +
                "appKey='" + appKey + '\'' +
                ", timestamp=" + timestamp +
                ", uid='" + uid + '\'' +
                ", success='" + success + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", orderNum='" + orderNum + '\'' +
                ", bizId='" + bizId + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}