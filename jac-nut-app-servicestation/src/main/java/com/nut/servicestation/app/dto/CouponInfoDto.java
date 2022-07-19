package com.nut.servicestation.app.dto;

import com.nut.servicestation.app.domain.BranchInfo;
import lombok.Data;

import java.util.List;

/**
 * @Description: 优惠券信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.dto
 * @Author: yzl
 * @CreateTime: 2021-07-27 19:51
 * @Version: 1.0
 */
@Data
public class CouponInfoDto {

    private Integer id;

    private String couponName;

    private String couponMessage;

    private String couponValue;

    private String startTime;

    private String endTime;

    private String vin;

    private String status;

    private List<StationDto> list;
}
