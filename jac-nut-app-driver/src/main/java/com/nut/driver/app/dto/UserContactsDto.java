package com.nut.driver.app.dto;

import lombok.Data;

/**
 * 用户联系人列表接口返回DTO
 */
@Data
public class UserContactsDto extends AbstractPersonnelDto {
    private Integer bound;

    @Override
    public String toString() {
        return "UserContactsDto{" +
                "bound=" + bound +
                "} " + super.toString();
    }

}
