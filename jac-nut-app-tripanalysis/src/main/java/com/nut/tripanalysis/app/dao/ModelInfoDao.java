package com.nut.tripanalysis.app.dao;

import com.nut.tripanalysis.app.entity.ModelInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/*
 *  @author wuhaotian 2021/7/9
 */
@Mapper
public interface ModelInfoDao {

    ModelInfoEntity selectByPrimaryKey(Long modelId);
}
