package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.domain.WorkOrderOutDetail;
import org.apache.ibatis.annotations.Mapper;

/*
 *  @author wuhaotian 2021/7/5
 */
@Mapper
public interface WorkOrderOutDetailDao {

    WorkOrderOutDetail selectByWoCode(String woCode);

    int updateByPrimaryKey(WorkOrderOutDetail record);

    int insert(WorkOrderOutDetail record);
}
