package com.nut.servicestation.app.dao;


import com.nut.servicestation.app.dto.AreaListDto;
import com.nut.servicestation.app.dto.StationDto;
import com.nut.servicestation.app.form.QueryStationForm;
import com.nut.servicestation.app.pojo.AreaStationPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface AreaStationDao {
    /**
     * 查询服务站code
     * @param DbName
     * @param areaId
     * @return
     */
    List<AreaStationPojo> queryAreaStation(@Param("DbName") String DbName, @Param("areaId") Long areaId);

    List<StationDto> getStationList(@Param("DbName") String DbName, @Param("cityID") String cityID);

    List<StationDto> getStationListById(@Param("DbName") String DbName,@Param("infoId") Integer infoId);
}
