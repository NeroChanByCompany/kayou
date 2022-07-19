package com.nut.driver.app.dao;

import com.nut.driver.app.entity.AppPopupConfigurationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.pojo.PopConfigurationPojo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 09:40:50
 */
@Mapper
public interface AppPopupConfigurationDao extends BaseMapper<AppPopupConfigurationEntity> {
    List<PopConfigurationPojo> queryPopConfigurationByNew();
}
