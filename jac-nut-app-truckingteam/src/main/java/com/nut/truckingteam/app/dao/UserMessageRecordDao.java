package com.nut.truckingteam.app.dao;

import com.nut.truckingteam.app.domain.UserMessageRecord;
import com.nut.truckingteam.app.pojo.CarInfoPojo;
import com.nut.truckingteam.app.pojo.PushUserInfoPojo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMessageRecordDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserMessageRecord record);

    int insertSelective(UserMessageRecord record);

    UserMessageRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserMessageRecord record);

    int updateByPrimaryKey(UserMessageRecord record);

    /* ----------------自定义sql由此向下---------------- */

    /**
     * 查询车队的管理员
     */
    String queryPushAdmin(Map<String, Object> param);

    /**
     * 查询车辆的司机
     */
    String queryPushDriver(Map<String, Object> param);

    /**
     * 查询车队的创建者
     */
    String queryPushCreator(Map<String, Object> param);

    /**
     * 查询车辆的车主
     */
    String queryPushOwner(Map<String, Object> param);

    /**
     * 查询车队下的车辆
     */
    List<CarInfoPojo> queryCarInfoByTeamId(Map<String, Object> param);

    /**
     * 车牌号修改推送-管理员查询
     */
    List<PushUserInfoPojo> queryAdminByCarId(String carId);

    /**
     * 车牌号修改推送-司机查询
     */
    List<PushUserInfoPojo> queryDriverByCarId(String carId);

    /**
     * 查询车辆信息
     */
    List<CarInfoPojo> queryCarInfoByCarId(List<String> list);
}