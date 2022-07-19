package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.nut.driver.app.dto.QueryMessageDetailListDto;
import com.nut.driver.app.entity.UserMessageRecordEntity;
import com.nut.driver.app.pojo.QueryMessageCountPojo;
import com.nut.driver.app.pojo.QueryMessageListPojo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 个人消息存储表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-27 13:01:24
 */
@Mapper
public interface UserMessageRecordDao extends BaseMapper<UserMessageRecordEntity> {

    /**
     * 查询消息列表
     */
    List<QueryMessageListPojo> queryMessageList(Map<String, Object> param);

    /**
     * 查询所有消息分类的未读消息数量
     */
    List<QueryMessageCountPojo> queryMessageCount(Map<String, Object> param);

    /**
     * 查询消息详情列表
     */
    Page<QueryMessageDetailListDto> queryMessageDetailList(Map<String, Object> param);

}
