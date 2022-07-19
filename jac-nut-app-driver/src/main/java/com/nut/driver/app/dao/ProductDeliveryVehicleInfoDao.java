package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.entity.ProductDeliveryVehicleInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author liuBing
 * @Classname ProductDeliveryVehicleInfoDao
 * @Description TODO
 * @Date 2021/6/25 13:47
 */
@Mapper
@Repository
public interface ProductDeliveryVehicleInfoDao extends BaseMapper<ProductDeliveryVehicleInfoEntity> {
    Long queryDeliveryCarListCount(Long autoIncreaseId);

    /**
     * 通过车辆ID和用户ID查询交车单ID
     * @param param 查询条件
     * @return 交车单ID
     */
    Long queryDeliveryIdByCarIdAndUserId(Map<String, Object> param);
}
