package com.nut.tripanalysis.app.dao;

import com.nut.tripanalysis.app.entity.CarEntity;
import com.nut.tripanalysis.app.pojo.RankCarPojo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/9
 */
@Mapper
public interface CarDao {

    CarEntity selectByPrimaryKey(String id);

    CarEntity selectByTerminalId(String autoTerminal);

    List<RankCarPojo> queryRankCarsByUserId(Long userId);

    int queryCountByCarModel(List<String> list);


}
