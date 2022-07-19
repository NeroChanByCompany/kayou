package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.domain.DataDict;
import org.apache.ibatis.annotations.Mapper;

/*
 *  @author wuhaotian 2021/7/6
 */
@Mapper
public interface DataDictDao {

    DataDict selectByCodeAndValue(DataDict record);
}
