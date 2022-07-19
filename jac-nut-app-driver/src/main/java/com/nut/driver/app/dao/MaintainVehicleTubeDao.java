package com.nut.driver.app.dao;

import com.nut.driver.app.entity.MaintainVehicleTubeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车辆与车管对应关系
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-21 20:15:25
 */
@Mapper
public interface MaintainVehicleTubeDao extends BaseMapper<MaintainVehicleTubeEntity> {

    MaintainVehicleTubeEntity getInfoByClassisNo(@Param("chassisNo") String chassisNo);

    List<String> queryCarVinListByVins(List<String> chassisNumList);

    List<MaintainVehicleTubeEntity> queryMaintainVehicleTubeListByMobile(@Param("mobile") String mobile);
}
