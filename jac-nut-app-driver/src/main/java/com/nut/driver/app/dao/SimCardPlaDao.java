package com.nut.driver.app.dao;/**
 * Created by Administrator on 2021/11/25.
 */

import com.github.pagehelper.Page;
import com.nut.driver.app.dto.SimCardPlaDto;
import com.nut.driver.app.vo.SimOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @version v1.0.0
 * @desc
 * @auther wangshuai
 * @create 2021/11/25
 * @Company esv
 */
@Mapper
public interface SimCardPlaDao {

    /**
    *@Auther wangshuai
    *@Description 获取用户相关车辆
    *@param
    *@return
    */
    List<SimCardPlaDto> getUserCar(@Param("userId")String userId);

    /**
    *@Auther wangshuai
    *@Description 获取订单列表
    *@param
    *@return
    */
    Page<SimOrderVo> getOrderList(@Param("userId")String userId);
}
