package com.nut.locationservice.app.dao;

import com.nut.locationservice.app.dto.CarLocationInputDto;
import com.nut.locationservice.app.entity.CarEolSystemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * EOL系统未下线车辆表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-16 19:09:02
 */
@Mapper
public interface CarEolSystemDao extends BaseMapper<CarEolSystemEntity> {

    List<CarLocationInputDto> selectByTerminalId(@Param("list") List<String> vins);

}
