package com.nut.driver.app.dao;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.domain.FltFleet;
import com.nut.driver.app.domain.FltFleetLine;
import com.nut.driver.app.domain.FltFleetLineRoute;
import com.nut.driver.app.domain.FltFleetLineRouteCar;
import com.nut.driver.app.entity.FltFleetEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FltFleetDao extends BaseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FltFleet record);

    int insertSelective(FltFleet record);

    FltFleet selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FltFleet record);

    int updateByPrimaryKey(FltFleet record);

    /* ----------------自定义sql由此向下---------------- */

    /**
     * 根据车队ID列表查询
     *
     * @param list 车队ID列表
     * @return 车队记录
     */
    List<FltFleet> selectByIdIn(List<Long> list);

    int insertFleetLine(FltFleetLine fltFleetLine);

    int insertFleetLineRoute(FltFleetLineRoute fltFleetLineRoute);

    int insertFleetLineRouteCar(FltFleetLineRouteCar fltFleetLineRouteCar);

    int deleteFleetLineById(long id);

    int deleteFleetLineRouteBylineId(long lineId);

    int deleteFleetLineRouteCarBylineId(long lineId);

    List<FltFleetLine> selectFltFleetLineList(Long id);

    List<FltFleetLineRoute>selectfltFleetLineRouteListByLineId(Long lineId);

    List<FltFleetLineRouteCar>selectfltFleetLineRouteCarListByLineId(Long lineId);

}