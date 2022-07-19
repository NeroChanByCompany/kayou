package com.nut.tools.app.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.tools.app.entity.MessageTemplateEntity;
import com.nut.tools.app.pojo.NotReceiveUserInfoPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Mapper
@Repository
public interface MessageDao extends BaseMapper<MessageTemplateEntity> {
    /**
     * 获取消息模板
     */
    List<MessageTemplateEntity> getMessageTemplate(Map<String, Object> param);

    /**
     * 获取不接收消息的用户
     */
    List<NotReceiveUserInfoPojo> getNotReceiveUser(Map<String, Object> param);

    /**
     * 获取不接收消息的用户
     */
    List<NotReceiveUserInfoPojo> getNotReceiveUserByMessageType(Map<String, Object> param);

    String selectAppTypeByUcId(String ucId);

    String selectRegistrationIdByUcId(String ucId);

    String selectTspRegistrationIdByAccountName(@Param(value = "accountName") String accountName, @Param(value = "tspDatabaseName") String tspDatabaseName);

    /**
     * 获取指定消息
     * @param id
     * @return
     */
    MessageTemplateEntity getMessageTemplateById(int id);
}
