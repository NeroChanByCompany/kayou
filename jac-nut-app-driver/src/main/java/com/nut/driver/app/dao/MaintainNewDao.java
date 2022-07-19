package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.nut.driver.app.entity.CarMaintainEntity;
import com.nut.driver.app.entity.CarMaintainRecordEntity;
import com.nut.driver.app.form.QueryCarNewMaintainDetailsForm;
import com.nut.driver.app.pojo.CarMaintainNewDetailPojo;
import com.nut.driver.app.pojo.CarMaintainRecordPojo;
import com.nut.driver.app.pojo.MaintainPojo;
import com.nut.driver.app.pojo.StatisticsCarMaintainPojo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liuBing
 * @Classname MaintainNewDao
 * @Description TODO
 * @Date 2021/6/23 17:40
 */
@Mapper
@Repository
public interface MaintainNewDao extends BaseMapper<CarMaintainEntity> {

    /**
     * 根据底盘号查询保养信息列表
     * @param map
     * @return
     */
    List<MaintainPojo> getMaintainsByVins(Map<String, Object> map);

    /**
     * 查询手机号
     * @param phone
     * @return
     */
    List<String> queryMaintainVehicleTubeByPhone(String phone);

    /**
     * 查询详情
     * @param from
     * @return
     */
    Page<CarMaintainNewDetailPojo> queryCarNewMaintainDetail(QueryCarNewMaintainDetailsForm from);

    /**
     * 查询数量
     * @param carVin
     * @return
     */
    List<StatisticsCarMaintainPojo> queryCarMaintainCountByVin(String carVin);

    /**
     * 查询详情
     * @param carVin
     * @return
     */
    Page<CarMaintainRecordPojo> queryNewMaintainRecord(String carVin);

    /**
     * 批量查询
     * @param newArrayList
     * @return
     */
    List<CarMaintainEntity> queryCarMaintainByIds(ArrayList<String> newArrayList);

    /**
     * 更新
     * @param carMaintainEntity
     */
    void updateCarMaintain(CarMaintainEntity carMaintainEntity);

    /**
     * 添加
     * @param record
     */
    void insertCarMaintainRecord(CarMaintainRecordEntity record);
}
