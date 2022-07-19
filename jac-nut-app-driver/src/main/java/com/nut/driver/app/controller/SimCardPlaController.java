package com.nut.driver.app.controller;/**
 * Created by Administrator on 2021/11/24.
 */

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.BuySetMealForm;
import com.nut.driver.app.form.SimCardPlaForm;
import com.nut.driver.app.service.SimCardPlaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version v1.0.0
 * @desc
 * @auther wangshuai
 * @create 2021/11/24
 * @Company esv
 */
@RestController
@Slf4j
@Api(tags = "Sim卡流量查询")
public class SimCardPlaController {

    @Autowired
    SimCardPlaService simCardPlaService;


    @PostMapping(value = "/simpla/iotSetMealsPage")
    @ApiOperation("可用套餐列表")
    @LoginRequired
    public HttpCommandResultWithData userFleets(@RequestJson SimCardPlaForm form) {
        log.info("userFleets start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = simCardPlaService.query(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userFleets end result:{}",result.getData());
        return result;
    }

    @PostMapping(value = "/simpla/getUserCar")
    @ApiOperation("获取当前用户可用车辆表")
    @LoginRequired
    public HttpCommandResultWithData getUserCar(@RequestJson SimCardPlaForm form) {
        log.info("userFleets start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = simCardPlaService.getUserCar(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userFleets end result:{}",result.getData());
        return result;
    }

    @PostMapping(value = "/simpla/buySetMeal")
    @ApiOperation("购买套餐")
    @LoginRequired
    public HttpCommandResultWithData buySetMeal(@RequestJson BuySetMealForm form) {
        log.info("userFleets start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = simCardPlaService.buySetMeal(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userFleets end result:{}",result.getData());
        return result;
    }


    @PostMapping(value = "/simpla/getSimLeftMeals")
    @ApiOperation("获取当前用户车辆的剩余流量")
    @LoginRequired
    public HttpCommandResultWithData getSimLeftMeals(@RequestJson SimCardPlaForm form) {
        log.info("userFleets start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = simCardPlaService.getSimLeftMeals(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userFleets end result:{}",result.getData());
        return result;
    }



    @PostMapping(value = "/simpla/getOrderList")
    @ApiOperation("订单列表")
    @LoginRequired
    public HttpCommandResultWithData getOrderList(@RequestJson SimCardPlaForm form) {
        log.info("userFleets start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = simCardPlaService.getOrderList(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userFleets end result:{}",result.getData());
        return result;
    }

    @PostMapping(value = "/simpla/getLeftMealBySimnum")
    @ApiOperation("基于Sim卡号进行剩余流量查询")
    @LoginRequired
    public HttpCommandResultWithData getLeftMealBySimnum(@RequestJson SimCardPlaForm form) {
        log.info("userFleets start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = simCardPlaService.getLeftMealBySimnum(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userFleets end result:{}",result.getData());
        return result;
    }







}
