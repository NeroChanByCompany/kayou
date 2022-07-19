package com.nut.tripanalysis.app.dao;

import com.nut.tripanalysis.app.dto.AvgOilwearRankingDto;
import com.nut.tripanalysis.app.pojo.OilwearRankingPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/10
 */
@Mapper
public interface RankMonthDao {

    List<OilwearRankingPojo> getOilwearByCarId(@Param("carlist") List<OilwearRankingPojo> carId, @Param("timeStamp") long timeStamp);

    List<AvgOilwearRankingDto> avgOilwear4MonthRanking(@Param("modelId") String modelId, @Param("timeStamp") long timeStamp);


}
