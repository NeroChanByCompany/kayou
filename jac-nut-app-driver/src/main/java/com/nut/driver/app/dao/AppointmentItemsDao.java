package com.nut.driver.app.dao;

import com.nut.driver.app.dto.AppointMatinTainDto;
import com.nut.driver.app.entity.AppointmentItemsEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-23 13:51:15
 */
@Mapper
public interface AppointmentItemsDao extends BaseMapper<AppointmentItemsEntity> {
    List<AppointMatinTainDto> queryRepairList(String type);
}
