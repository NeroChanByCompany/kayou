package com.nut.driver.app.dao;

import com.nut.driver.app.entity.DataDictEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-23 15:38:33
 */
@Mapper
public interface DataDictDao extends BaseMapper<DataDictEntity> {

    DataDictEntity selectByCodeAndValue(DataDictEntity record);

}
