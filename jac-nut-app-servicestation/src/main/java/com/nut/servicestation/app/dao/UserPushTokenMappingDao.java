package com.nut.servicestation.app.dao;


import com.nut.servicestation.app.domain.UserPushTokenMapping;

public interface UserPushTokenMappingDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserPushTokenMapping record);

    int insertSelective(UserPushTokenMapping record);

    UserPushTokenMapping selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserPushTokenMapping record);

    int updateByPrimaryKey(UserPushTokenMapping record);

    /* ----------------自定义sql由此向下---------------- */

    UserPushTokenMapping selectByUserIdAndAppType(UserPushTokenMapping record);

    int deleteByUserIdAndType(UserPushTokenMapping record);

}