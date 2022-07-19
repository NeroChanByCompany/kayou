package com.nut.driver.app.service;/**
 * Created by Administrator on 2021/11/24.
 */

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.BuySetMealForm;
import com.nut.driver.app.form.SimCardPlaForm;

import java.io.IOException;

/**
 * @version v1.0.0
 * @desc
 * @auther wangshuai
 * @create 2021/11/24
 * @Company esv
 */
public interface SimCardPlaService {
    /**
    *@Auther wangshuai
    *@Description 可用流量包查询
    *@param
    *@return
    */
    HttpCommandResultWithData query(SimCardPlaForm form) throws IOException;

    /**
    *@Auther wangshuai
    *@Description 获取当前用户车辆表
    *@param
    *@return
    */
    HttpCommandResultWithData getUserCar(SimCardPlaForm form);

    /**
    *@Auther wangshuai
    *@Description 购买相关套餐
    *@param
    *@return
    */
    HttpCommandResultWithData buySetMeal(BuySetMealForm form) throws IOException;

    /**
    *@Auther wangshuai
    *@Description 获取当前车辆的剩余流量
    *@param
    *@return
    */
    HttpCommandResultWithData getSimLeftMeals(SimCardPlaForm form);

    /**
    *@Auther wangshuai
    *@Description 获取订单列表
    *@param
    *@return
    */
    HttpCommandResultWithData getOrderList(SimCardPlaForm form);

    HttpCommandResultWithData getLeftMealBySimnum(SimCardPlaForm form);

}
