package com.nut.servicestation.app.dao;


import com.nut.servicestation.app.domain.AccessoriesCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/5
 */
@Mapper
public interface AccessoriesCodeDao {

    int deleteByWoCodeAndOper(@Param("woCode") String woCode, @Param("operateId") String operateId);

    int insert(AccessoriesCode record);

    List<AccessoriesCode> selectListByWoCode(@Param("woCode") String woCode, @Param("operateId") String operateId);
}
