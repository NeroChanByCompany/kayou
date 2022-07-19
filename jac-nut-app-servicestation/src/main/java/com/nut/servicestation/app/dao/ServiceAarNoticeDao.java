package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.dto.ServiceAarNoticeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/5
 */
@Mapper
public interface ServiceAarNoticeDao {

    long countProtocolVin(@Param(value = "queryNoticeTypeList") List<String> queryNoticeTypeList, @Param(value = "vin") String vin);


    List<ServiceAarNoticeDto> queryServiceAarNotices(@Param(value = "stationCode") String stationCode, @Param(value = "vin") String vin, @Param(value = "queryNoticeTypeList") List<String> queryNoticeTypeList);

}
