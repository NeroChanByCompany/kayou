package com.nut.servicestation.app.domain;

import com.nut.servicestation.app.dto.StationDto;
import lombok.Data;

import java.util.List;

/**
 * @Description: 发放优惠券详情
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.domain
 * @Author: yzl
 * @CreateTime: 2021-08-03 13:57
 * @Version: 1.0
 */
@Data
public class CouponGiveDetail extends CouponGiveInfo{
    private String couponMessage;
    private String couponValue;
    private String startDate;
    private String endDate;
    private String validTime;
    private List<StationDto> list;

    public String setValidTime(){
        if (startDate == null || endDate == null ){
            validTime = "无限制";
        }else {
            validTime = startDate + " 至 " + endDate;
        }
        return validTime;
    }

}
