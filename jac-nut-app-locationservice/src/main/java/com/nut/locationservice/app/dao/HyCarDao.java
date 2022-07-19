package com.nut.locationservice.app.dao;

import com.nut.locationservice.app.dto.CarLocationInputDto;
import com.nut.locationservice.app.entity.HyCarEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车辆基础信息
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-16 19:22:48
 */
@Mapper
public interface HyCarDao extends BaseMapper<HyCarEntity> {

    List<CarLocationInputDto> selectByTerminalId(@Param("DatabaseName") String DatabaseName, @Param("list") List<String> vins);

}
