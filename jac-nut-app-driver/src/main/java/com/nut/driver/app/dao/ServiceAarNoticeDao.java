package com.nut.driver.app.dao;

import com.nut.driver.app.entity.ServiceAarNoticeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 服务活动及返修通知数据同步表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-22 10:41:14
 */
@Mapper
public interface ServiceAarNoticeDao extends BaseMapper<ServiceAarNoticeEntity> {
    long countProtocolVin(@Param(value = "queryNoticeTypeList") List<String> queryNoticeTypeList, @Param(value = "vin") String vin);

}
