package com.nut.driver.app.service;

import com.github.pagehelper.Page;
import com.nut.driver.app.domain.MessagePage;
import com.nut.driver.app.dto.QueryMessageDetailListDto;
import com.nut.driver.app.dto.QueryMessageListDto;
import com.nut.driver.app.form.QueryMessageDetailListForm;
import com.nut.driver.app.form.QueryMessageForm;

import java.util.List;

/**
 * @author liuBing
 * @Classname SlaveMessageService
 * @Description TODO 查询启停故障等车辆消息
 * @Date 2021/8/5 16:04
 */
public interface SlaveMessageService {

    /**
     * @Author 查询故障启停消息记录
     * @Description //TODO
     * @Date 16:12 2021/8/5
     * @Param [form]
     * @return java.util.List<com.nut.driver.app.dto.QueryMessageDetailListDto>
     **/
    MessagePage<QueryMessageDetailListDto> queryMessageDetailList(QueryMessageDetailListForm form);
    /**
     * @Author liuBing
     * @Description //TODO 查询全部消息记录
     * @Date 13:48 2021/8/6
     * @Param [form]
     * @return java.util.List<com.nut.driver.app.dto.QueryMessageListDto>
     **/
    List<QueryMessageListDto> queryMessageList(QueryMessageForm form);

    /**
     * 修改消息为已读
     * @param form
     */
    void update(QueryMessageDetailListForm form);
}
