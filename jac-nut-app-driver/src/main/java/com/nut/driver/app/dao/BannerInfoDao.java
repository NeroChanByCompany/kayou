package com.nut.driver.app.dao;

import com.nut.driver.app.dto.BannerInfoDto;
import com.nut.driver.app.entity.BannerInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 15:29:42
 */
@Mapper
public interface BannerInfoDao extends BaseMapper<BannerInfoEntity> {
    List<BannerInfoDto> getBannerInfoForApp(String appType);
}
