package com.nut.driver.app.dao;

import com.github.pagehelper.Page;
import com.nut.driver.app.domain.UserMessageReceiveSet;
import com.nut.driver.app.domain.UserMessageRecord;
import com.nut.driver.app.dto.QueryAlarmNoticeDetailListDto;
import com.nut.driver.app.dto.QueryMessageAndAlarmNoticeReceiveSetDto;
import com.nut.driver.app.pojo.QueryAlarmNoticeCountPojo;
import com.nut.driver.app.pojo.QueryAlarmNoticeListPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dao
 * @Author: yzl
 * @CreateTime: 2021-06-30 10:41
 * @Version: 1.0
 */
@Mapper
public interface CarMessageRecordDao {
    /**
     * 查询报警通知列表
     */
    Page<QueryAlarmNoticeListPojo> queryAlarmNoticeList(Map<String, Object> param);

    /**
     * 查询报警通知总数和已读数量
     */
    List<QueryAlarmNoticeCountPojo> queryAlarmNoticeCount(Map<String, Object> param);

    /**
     * 查询报警通知详情列表
     */
    Page<QueryAlarmNoticeDetailListDto> queryAlarmNoticeDetailList(Map<String, Object> param);

    List<QueryMessageAndAlarmNoticeReceiveSetDto> queryMessageByUserIdAndAppType(@Param("userId") String userId, @Param("appType") String appType);

    /**
     * 删除设置
     * @param record
     * @return
     */
    int deleteByMessageType(UserMessageReceiveSet record);

    /**
     * 查询消息设置
     * @param record
     * @return
     */
    List<UserMessageReceiveSet> queryMessage(UserMessageReceiveSet record);

    int insert(UserMessageReceiveSet record);

    /**
     * @Author liuBing
     * @Description //TODO 添加新的卡友圈消息
     * @Date 15:13 2021/5/27
     * @Param [userMessageRecord]
     * @return void
     **/
    int addUnreadMessage(UserMessageRecord userMessageRecord);



}
