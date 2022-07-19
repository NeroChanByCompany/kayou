package com.nut.driver.app.dto;

import lombok.Data;

@Data
public abstract class AbstractPersonnelDto {
    private String nickname;
    private String phone;
    private Long userId;

    @Override
    public String toString() {
        return "AbstractPersonnelDto{" +
                "nickname='" + nickname + '\'' +
                ", phone='" + phone + '\'' +
                ", userId=" + userId +
                '}';
    }

}
