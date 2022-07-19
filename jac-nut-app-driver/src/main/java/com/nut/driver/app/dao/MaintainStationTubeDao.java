package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.entity.MaintainStationTubeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuBing
 * @Classname MaintainStationTubeDao
 * @Description TODO
 * @Date 2021/6/24 17:55
 */
@Mapper
@Repository
public interface MaintainStationTubeDao extends BaseMapper<MaintainStationTubeEntity> {

    List<String> queryCarVinListByVinsAndServiceCode(@Param("carVinList") List<String> carVinList, @Param("serviceCode") String serviceCode);
}
