package com.nut.driver.app.dto;

import com.nut.common.result.PagingInfo;
import lombok.Data;

/**
 * 用户车队列表接口返回DTO
 */
@Data
public class UserFleetsDto extends PagingInfo<UserFleetsDto.FleetInfo> {
    private Integer carTotal;


    @Data
    public static class FleetInfo {
        private Long teamId;
        private String name;
        private String avatar;
        private Integer carTotal;

    }
}
