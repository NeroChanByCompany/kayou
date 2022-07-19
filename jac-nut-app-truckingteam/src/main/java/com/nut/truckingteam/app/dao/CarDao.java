package com.nut.truckingteam.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.truckingteam.app.dto.CarChasisModelInfoDto;
import com.nut.truckingteam.app.entity.CarEntity;
import com.nut.truckingteam.app.pojo.CarInfoPojo;
import com.nut.truckingteam.app.pojo.CarRolePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 车辆表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-21 14:34:16
 */
@Mapper
@Repository
public interface CarDao extends BaseMapper<CarEntity> {

    /**
     * 根据用户ID查询，所有车队内，与此用户相关的车（创建人、管理员、绑定司机、车主）
     */
    List<CarRolePojo> selectUserRelatedCar(Long userId);

    /**
     * 根据车辆ID列表查询车辆信息
     */
    List<CarInfoPojo> selectByCarIdIn(List<String> list);

    CarEntity findById(String id);


    Long getTodayAddedCarCount(@Param("DbName")String DbName);

    /**
     * 根据车辆终端号查车辆信息
     */
    List<CarChasisModelInfoDto> selectByCarTerminalIn(List<Long> list);
}
