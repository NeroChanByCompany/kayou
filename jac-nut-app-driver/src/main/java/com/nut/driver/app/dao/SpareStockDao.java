package com.nut.driver.app.dao;

import com.github.pagehelper.Page;
import com.nut.driver.app.entity.SpareStockEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.form.QuerySpareStockForm;
import com.nut.driver.app.pojo.SpareStockListPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 16:17:56
 */
@Mapper
public interface SpareStockDao extends BaseMapper<SpareStockEntity> {

    Page<SpareStockListPojo> getSpareStockList(@Param("DbName") String DbName, @Param("paramMap") QuerySpareStockForm form);

    Page<SpareStockListPojo> getSpareStockListByName(@Param("DbName") String DbName, @Param("paramMap") QuerySpareStockForm form);

}
