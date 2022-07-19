package com.nut.driver.app.dao;

import com.github.pagehelper.Page;
import com.nut.driver.app.entity.CustomMaintainInfoEntity;
import com.nut.driver.app.form.QueryDriverMaintenanceListForm;
import com.nut.driver.app.pojo.QueryCustomMaintainItemCountPojo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author liuBing
 * @Classname CustomMaintainInfoDao
 * @Description TODO
 * @Date 2021/6/24 14:36
 */
@Mapper
@Repository
public interface CustomMaintainInfoDao {

    Page<QueryCustomMaintainItemCountPojo> selectAvailableListByUserId(QueryDriverMaintenanceListForm from);

    CustomMaintainInfoEntity queryMaintanceInfo(long parseLong);

    CustomMaintainInfoEntity selectByPrimaryKey(long parseLong);

    void updateByPrimaryKeySelective(CustomMaintainInfoEntity customMaintainInfo);

    Integer insertSelective(CustomMaintainInfoEntity customMaintainInfo);
}
