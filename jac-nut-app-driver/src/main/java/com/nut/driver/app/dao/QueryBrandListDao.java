package com.nut.driver.app.dao;

import com.nut.driver.app.dto.BrandInfoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface QueryBrandListDao {

    List<BrandInfoDto> queryBrandListSql(String param);
    BrandInfoDto selectBrandInfoById(Long brandId);
}
