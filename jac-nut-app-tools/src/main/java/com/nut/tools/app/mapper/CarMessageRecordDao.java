package com.nut.tools.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.nut.tools.app.entity.MessageRecordEntity;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CarMessageRecordDao extends BaseMapper<MessageRecordEntity> {

    int carMessageRecordBatchInsert(List<MessageRecordEntity> list);

    int insert(MessageRecordEntity record);
}
