package com.nut.driver.app.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 短信黑名单与白名单
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dao
 * @Author: yzl
 * @CreateTime: 2021-10-25 14:51
 * @Email: yzl379131121@163.com
 * @Version: 1.0
 */
@Mapper
public interface SmsListDao {

    // 电话号码是否处于黑名单列表
    Integer isBlackList(@Param("phonePrefix") String phonePrefix);

    List<String> phoneList();

    // 电话号码是否处于白名单
    Integer isWhiteList(@Param("phone") String phone);

    Integer insertIllegalUser(Map<String ,Object> map);

}
