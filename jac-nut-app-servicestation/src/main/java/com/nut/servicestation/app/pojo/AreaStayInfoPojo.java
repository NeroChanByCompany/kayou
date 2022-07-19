package com.nut.servicestation.app.pojo;

import java.io.Serializable;

/**
 * @author liuBing
 * @Classname AreaStayInfoPojo
 * @Description TODO 进出服务半径电子围栏信息信息
 * @Date 2021/7/7 17:54
 */
public class AreaStayInfoPojo implements Serializable {
    private static final long serialVersionUID = 4566766868617997332L;
    /**
     * 通信号
     */
    private Long tId;
    /**
     *区域标识
     */
    private Long areaId;
    /**
     *入区域时间
     */
    private Long startTime;
    /**
     *出区域时间
     */
    private Long endTime;
    /**
     *滞留超时时间长度
     */
    private Long stayTime;

    public Long gettId() {
        return tId;
    }

    public void settId(Long tId) {
        this.tId = tId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getStayTime() {
        return stayTime;
    }

    public void setStayTime(Long stayTime) {
        this.stayTime = stayTime;
    }
}
