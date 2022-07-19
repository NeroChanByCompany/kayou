package com.nut.driver.app.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.nut.driver.app.dto.QueryMessageDetailListDto;
import com.nut.driver.app.dto.QueryMessageListDto;
import com.nut.driver.common.constants.CommonConstants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author liuBing
 * @Classname SlaveMessageDao
 * @Description TODO
 * @Date 2021/8/5 16:02
 */
@Mapper
public interface SlaveMessageDao{
    /**
     * 查询出当前月份的消息数据
     * @param param
     * @return
     */
    List<QueryMessageDetailListDto> queryNowMessageDetailList(Map<String, Object> param);

    /**
     * 获取上月消息数据
     * @param param
     * @return
     */
    QueryMessageListDto queryAfterMessageList(Map<String, Object> param);

    /**
     * 获取当月消息数据
     * @param param
     * @return
     */
    QueryMessageListDto queryNowMessageList(Map<String, Object> param);

    /**
     * 获取上月消息详情数据
     * @param param
     * @return
     */
    List<QueryMessageDetailListDto> queryBeforeMessageDetailList(Map<String, Object> param);

    /**
     * 更新消息为已读
     * @param table
     * @param messageType
     */
    void update(@Param("table") String table,@Param("messageType") Integer messageType,@Param("userId") String userId);

    /**
     * 查询上月消息数量
     * @param param
     * @return
     */
    Integer queryNowCount(Map<String, Object> param);

    /**
     * 查询下个月消息数量
     * @param param
     * @return
     */
    Integer queryBeforeCount(Map<String, Object> param);
}
