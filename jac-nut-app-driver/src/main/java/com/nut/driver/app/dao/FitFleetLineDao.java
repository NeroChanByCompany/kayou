package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.app.driver.entity.FitFleetLineEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FitFleetLineDao extends BaseMapper<FitFleetLineEntity> {
    /**
     * 去重查询线路数据
     * @return
     */
    @Select("<script>" +
            "select start_city_name, end_city_name, GROUP_CONCAT(fleet_id SEPARATOR ',') fleetId ,GROUP_CONCAT(carids SEPARATOR ',') carids from " +
            "fit_fleet_line<where> " +
            "<if test=\"ucId != null and ucId != '' \">" +
            "create_user_id = #{ucId}" +
            "</if>" +
            "</where>" +
            " group by start_city_name,end_city_name"+
            "</script>")
    List<com.nut.app.driver.entity.FitFleetLineEntity> selectGroupByName(@Param("ucId") String ucId);
}
