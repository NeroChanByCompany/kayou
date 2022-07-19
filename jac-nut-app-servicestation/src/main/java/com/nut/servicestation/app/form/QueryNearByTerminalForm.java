package com.nut.servicestation.app.form;


import lombok.Data;

@Data
public class QueryNearByTerminalForm {

    /**
     * 单位应该为KM或M 枚举值 单位 1 KM , 2 M
     */
    private String unit;

    /**
     * 服务站经度
     */
    private String longitude;
    /**
     * 服务站纬度
     */
    private String latitude;
    /**
     * 查询范围半径
     */
    private String radius;

}
