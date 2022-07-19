package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.OrderCarForm;
import com.nut.servicestation.app.form.OrderItemsForm;
import com.nut.servicestation.app.service.QueryAddWoInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/5
 */
@Slf4j
@RestController
public class QueryAddWoInfoController extends BaseController {


    @Autowired
    private QueryAddWoInfoService queryAddWoInfoService;

    /**
     * 服务站预约项目接口
     */
    @PostMapping(value = "/orderItems")
    @LoginRequired
    public HttpCommandResultWithData orderItems(@RequestJson OrderItemsForm command) {
        log.info("[orderItems]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = queryAddWoInfoService.orderItems(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("[orderItems]end");
        return result;
    }

    /**
     * 查询预约车辆接口
     */
    @PostMapping(value = "/orderCar")
    @LoginRequired
    public HttpCommandResultWithData orderCar(@RequestJson OrderCarForm command) throws Exception {
        log.info("[orderCar]start");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = queryAddWoInfoService.orderCar(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("[orderCar]end");
        return result;
    }
}
