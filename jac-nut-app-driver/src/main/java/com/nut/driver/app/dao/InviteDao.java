package com.nut.driver.app.dao;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * @Description: 邀请注册双方信息记录留存
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dao
 * @Author: yzl
 * @CreateTime: 2021-08-20 16:21
 * @Email: yzl379131121@163.com
 * @Version: 1.0
 */
public interface InviteDao {

    void insertMessage(
            @Param("inviterId") String inviterId, @Param("registerId") String registerId,
            @Param("inviterPhone") String inviterPhone, @Param("registerPhone") String registerPhone,
            @Param("createTime") LocalDateTime dateTime,
            @Param("inviterIdSecond") String inviterIdSecond, @Param("inviterPhoneSecond") String inviterPhoneSecond
    );

    String invite2invite(@Param("registerId") String registerId);

}
