package com.nut.driver.app.dao;

import com.github.pagehelper.Page;
import com.nut.driver.app.entity.ServiceStationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.form.QueryStationForm;
import com.nut.driver.app.pojo.ServiceStationDetailPojo;
import com.nut.driver.app.pojo.ServiceStationInfoPojo;
import com.nut.driver.app.pojo.StationListPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 服务站
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-21 19:50:45
 */
@Mapper
public interface ServiceStationDao extends BaseMapper<ServiceStationEntity> {

    ServiceStationDetailPojo getServiceStationDetail(@Param("DbName") String DbName, @Param("stationId") String stationId);

    ServiceStationInfoPojo queryServiceStationInfo(@Param("DbName") String DbName,
                                                   @Param("stationId") String stationId, @Param("jobType") int jobType);

    Page<StationListPojo> getStationList(@Param("DbName") String DbName, @Param("paramMap") QueryStationForm form);


}
