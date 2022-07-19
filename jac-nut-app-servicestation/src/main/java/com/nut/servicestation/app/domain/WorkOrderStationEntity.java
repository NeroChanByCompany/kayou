package com.nut.servicestation.app.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author liuBing
 * @Classname WorkOrderStationEntity
 * @Description TODO 抢单信息
 * @Date 2021/8/11 13:03
 */
@Data
@TableName("work_order_station")
@Accessors(chain = true)
public class WorkOrderStationEntity {
    /**
     * 主键
     */
    private String id;
    /**
     * 工单号
     */
    private String woCode;
    /**
     * 服务站id
     */
    private String stationId;
    /**
     * 服务站code
     */
    private String stationCode;
    /**
     * 服务站名称
     */
    private String stationName;
    /**
     * 是否绑定服务站 0 未绑定 1已绑定 2已被其他服务站绑定
     */
    private String woBind;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;
}
