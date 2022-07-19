package com.nut.locationservice.app.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-17 10:31
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class FaultInfoPojo {
    //通信号
    private Long tId;

    //故障开始时间(s)
    private Long begin;

    //故障结束时间(s)
    private Long end;

    //里程有效时间(s)
    private Integer con;

    //开始经度 乘以10的6次幂
    private Integer bLng;

    //开始纬度 乘以10的6次幂
    private Integer bLat;

    //结束经度 乘以10的6次幂
    private Integer eLng;

    //结束纬度 乘以10的6次幂
    private Integer eLat;

    private Integer fmi;

    private Integer spn;

    //故障源地址
    private Integer addr;
}
