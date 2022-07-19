package com.nut.servicestation.app.dao;

import com.github.pagehelper.Page;
import com.nut.servicestation.app.domain.UserMessageRecord;
import com.nut.servicestation.app.dto.MsgsDto;
import org.apache.ibatis.annotations.Mapper;

/*
 *  @author wuhaotian 2021/7/8
 */
@Mapper
public interface UserMessageRecordDao {

    Page<MsgsDto> queryMsgsList(UserMessageRecord record);
}
