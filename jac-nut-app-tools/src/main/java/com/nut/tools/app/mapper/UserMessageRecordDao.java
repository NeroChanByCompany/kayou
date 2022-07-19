package com.nut.tools.app.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.nut.tools.app.entity.MessageRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository()
public interface UserMessageRecordDao extends BaseMapper<MessageRecordEntity> {
    /* ----------------自定义sql由此向下---------------- */
    @Override
    int insert(MessageRecordEntity record);

    int userMessageRecordBatchInsert(List<MessageRecordEntity> list);

    int queryReadMesCount(String receiverId);

}
