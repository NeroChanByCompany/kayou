package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.entity.CustomMaintainItemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuBing
 * @Classname CustomMaintainItemDao
 * @Description TODO
 * @Date 2021/6/24 15:24
 */
@Mapper
@Repository
public interface CustomMaintainItemDao extends BaseMapper<CustomMaintainItemEntity> {
    List<CustomMaintainItemEntity> selectItemListByMaintainId(Long id);

    void insertItemList(List<CustomMaintainItemEntity> customMaintainItemList);

}
