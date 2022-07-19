package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @Description: 服务站
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-21 19:47
 * @Version: 1.0
 */
@Data
public class ServiceStationInfoPojo {
    /**
     * 预约人 长度 10
     */
    private String userId;
    /**
     * 服务站名称 必须11位数字，不能为非数字字符
     */
    private String stationName;
    /**
     * 服务经度
     */
    private Double longitude;
    /**
     * 服务纬度
     */
    private Double latitude;
    /**
     * 服务站省市编码 逗号分隔，保养服务和维修服务项至少要选择一项
     */
    private Integer povince;
    /**
     * 服务站编码 逗号分隔，保养服务和维修服务项至少要选择一项
     */
    private String serviceCode;

    /**
     * 服务范围（不能建单：0 - 进站维修：1 - 外出救援：2 - 外出救援和进站维修：3）
     */
    private Integer scopeService;
}
