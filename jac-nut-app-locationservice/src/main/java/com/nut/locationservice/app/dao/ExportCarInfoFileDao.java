package com.nut.locationservice.app.dao;

import com.nut.locationservice.app.dto.BaseData;
import com.nut.locationservice.app.pojo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ExportCarInfoFileDao {

    List<QuerySystemTypePojo> getSystemType(@Param("DatabaseName") String dbName);

    List<QueryFaultSummaryPojo> queryFaultSummaryList(Map<String, Object> param);

    Map<String, Object> getSymbolCode(Map<String, Object> param);

    List<QueryCarFaultPojo> getCarFaultList(@Param("DatabaseName") String dbName);

    List<BaseData> queryBasicDataList(Map<String, Object> map);

    void insertExportCarFile(Map<String, Object> resultMap);

    List<CarInfoPojo> queryCarList();

    QueryCommonCarPojo queryCommonCar(@Param("DatabaseName") String dbName, @Param("vin") String vin);
}
