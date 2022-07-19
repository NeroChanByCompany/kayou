package com.nut.servicestation.app.domain;

import lombok.Data;

@Data
public class AddWoStationInfo {
    //服务站名称
    private String stationName;
    //服务站id
    private String stationId;
    //服务站code
    private String stationCode;
    //服务站精度
    private String stationLon;
    //服务站纬度
    private String stationLat;
    //服务站地址
    private String stationAdd;
    //服务站区域id
    private String povince;
    //服务电话
    private String stationTel;
    //距离
    private String distance;
    //发动机授权
    private String engineType;
    //在途工单数阈值
    private String onlineMaxCnt;
    //售后总监电话
    private String serviceTel;
    //资质（不是为空）
    private String aptitude;
    //专属（不是为空）
    private String exclusive;
    //服务站推荐指数
    private String level;

    private String picture;

}