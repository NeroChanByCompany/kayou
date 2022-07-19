package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.*;
import com.nut.servicestation.app.service.AcceptRefuseOrderService;
import com.nut.servicestation.app.service.UptimeService;
import com.nut.servicestation.common.constants.UptimeVal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/*
 *  @author wuhaotian 2021/7/5
 */
@Slf4j
@RestController
public class AcceptRefuseOrderController extends BaseController {

    @Autowired
    private AcceptRefuseOrderService acceptRefuseOrderService;
    @Autowired
    private UptimeService uptimeService;

    /**
     * 外出救援立即出发
     */
    @PostMapping(value = "/rescueGo")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson RescueGoForm command) throws Exception {
        this.formValidate(command);
        log.info("========== rescueGo businessHandle start ==========");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = acceptRefuseOrderService.rescueGo(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("外出救援立即出发失败");
            log.error(e.getMessage(), e);
        }
        log.info("========== rescueGo businessHandle end ==========");
        uptimeService.trigger(result, command.getWoCode(), ServiceStationVal.WEB_SERVICE_GOINGOUTDEPARTURETIME,
                "", "");
        return result;
    }
    /**
     * 拒绝接单
     */
    @PostMapping(value = "/refuseOrder")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson RefuseOrderForm command) throws Exception {
        log.info("========== refuseOrder businessHandle start ==========");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = acceptRefuseOrderService.refuseOrder(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("拒绝接单失败");
            log.error(e.getMessage(), e);
        }
        log.info("========== refuseOrder businessHandle end ==========");
        uptimeService.trigger(result, command.getWoCode(), ServiceStationVal.WEB_SERVICE_ABNORMALWORKORDERAPPLY,
                UptimeVal.APPLY_TYPE_REFUSE, "");
        return result;
    }
    /**
     * 扫一扫/根据车辆查询工单
     */
    @PostMapping(value = "/scanWo")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson ScanWoForm command) throws Exception {
        log.info("========== scanWo businessHandle start ==========");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = acceptRefuseOrderService.scanWo(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("扫一扫/根据车辆查询工单失败");
            log.error(e.getMessage(), e);
        }
        log.info("========== scanWo businessHandle end ==========");
        return result;
    }
    /**
     * 确认接单
     */
    @PostMapping(value = "/acceptOrder")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson AcceptOrderForm command) throws Exception {
        log.info("========== acceptOrder businessHandle start ==========");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = acceptRefuseOrderService.acceptOrder(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("确认接单失败");
            log.error(e.getMessage(), e);
        }
        log.info("========== acceptOrder businessHandle end ==========");
        uptimeService.trigger(result, command.getWoCode(), ServiceStationVal.WEB_SERVICE_SERVICERECEIVINGORDER,
                "", "");
        return result;
    }

    /**
     * 扫码快捷查单
     */
    @PostMapping(value = "/scanOrder")
    @LoginRequired
    public HttpCommandResultWithData scanOrder(@ApiIgnore @RequestBody ScanOrderForm form){
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = acceptRefuseOrderService.scanOrder(form);
        }catch (Exception e){
            log.info("扫码查询失败");
            result.setMessage("扫码查询失败");
            result.setResultCode(ECode.CLIENT_ERROR.code());
        }
        return result;
    }
}
