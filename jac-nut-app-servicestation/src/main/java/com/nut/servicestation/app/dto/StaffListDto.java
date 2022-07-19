package com.nut.servicestation.app.dto;

import lombok.Data;

/**
 * 查询指派人员列表
 */
@Data
public class StaffListDto {

    /** 人员ID */
    private String userId;

    /** 人员名称 */
    private String userName;

    @Override
    public String toString() {
        return "StaffListDto{" +
                "userId='" + userId + '\'' +
                ",userName='" + userName + '\'' +
                "} " + super.toString();
    }
}
