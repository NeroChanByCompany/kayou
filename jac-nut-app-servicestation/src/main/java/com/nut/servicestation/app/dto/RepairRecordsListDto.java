package com.nut.servicestation.app.dto;

import com.nut.common.result.PagingInfo;
import lombok.Data;

/**
 * @author: jiangcm
 * @Description: 	维修项列表
 * @Date: Created in 2018/5/7 18:31
 * @Modified By:
 */
@Data
public class RepairRecordsListDto extends PagingInfo {

    private String woStatus;// 工单状态
    private String chassisNum;// 底盘号
    private String carNumber;// 车牌号
    private String woCode;// 工单号
    private String carLocation;// 车辆位置
    private String carLon;// 车辆经度
    private String carLat;// 车辆纬度
    private String workTime;// 工单时间
    private Integer woType;// 工单类型
    private Integer appointmentType;// 预约方式	1：司机App，2：400客服
    private Integer appointmentItem;// 预约项目	1：维修项目，2：保养项目，3：维修和保养项目
    /** 是否已传维修方案确定时间 */
    private Integer hasRepairPhotoTime;
    /** 是否提示断联维修（0：否；1：是） */
    private Integer showDisConnection;

}
