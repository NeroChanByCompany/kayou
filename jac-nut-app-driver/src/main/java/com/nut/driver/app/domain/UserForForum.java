package com.nut.driver.app.domain;

import com.nut.driver.app.entity.UserEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuBing
 * @Classname UserForForum
 * @Description TODO
 * @Date 2021/7/23 15:27
 */
@Data
@Accessors(chain = true)
public class UserForForum extends UserEntity {
    private String deviceType;
}
