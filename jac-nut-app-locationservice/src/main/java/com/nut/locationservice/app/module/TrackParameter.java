package com.nut.locationservice.app.module;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description: 大数据车辆轨迹参数
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.module
 * @Author: yzl
 * @CreateTime: 2021-06-17 10:04
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class TrackParameter implements Serializable {

    /**
     * 通信号
     */
    private Long terminalId;

    /**
     * 开始时间，UTC时间，单位为秒
     */
    private Long startTime;

    /**
     * 结束时间，UTC时间，单位为秒
     */
    private Long endTime;

    /**
     * 是否抽稀（选填，True抽稀，false不抽稀）
     */
    private Boolean thin;

    /**
     * 抽稀级别
     */
    private Integer thinLevel;

    /**
     * 返回数据类型 0 base64编码pb 1 JSON
     */
    private Integer type;


}
