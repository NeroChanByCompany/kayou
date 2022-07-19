package com.jac.app.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jac.app.job.domain.UserMessageRecord;
import com.jac.app.job.entity.UserMessageRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author liuBing
 * @Classname UserMessageRecordMapper
 * @Description TODO 消息记录
 * @Date 2021/8/13 16:54
 */
@Mapper
@Repository
public interface UserMessageRecordMapper extends BaseMapper<UserMessageRecordEntity> {
    /**
     * 添加消息
     * @param userMessageRecord
     */
    void insertSelective(UserMessageRecord userMessageRecord);
}
