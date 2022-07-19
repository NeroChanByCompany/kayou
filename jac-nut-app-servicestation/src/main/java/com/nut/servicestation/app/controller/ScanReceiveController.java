package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.RescueCancelForm;
import com.nut.servicestation.app.form.ScanReceiveForm;
import com.nut.servicestation.app.service.ScanReceiveService;
import com.nut.servicestation.app.service.UptimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/7
 */
@Slf4j
@RestController
public class ScanReceiveController extends BaseController {

    @Autowired
    private ScanReceiveService scanReceiveService;
    @Autowired
    private UptimeService uptimeService;

    /**
     * 服务站取消救援
     */
    @PostMapping(value = "/rescueCancel")
    @LoginRequired
    public HttpCommandResultWithData rescueCancel(@RequestJson RescueCancelForm command) throws Exception {
        log.info("[rescueCancel]start");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = scanReceiveService.rescueCancel(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("提交失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("[rescueCancel]end");
        uptimeService.trigger(result, command.getWoCode(), ServiceStationVal.WEB_SERVICE_SERVICECANCELRESCUE,
                "", "");
        return result;
    }
    /**
     * @param command
     * @Description: 扫码接车
     * @method: businessHandle
     * @Date: 2018/5/7 18:56
     * @authur:
     */
    @PostMapping(value = "/scanReceive")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson ScanReceiveForm command) throws Exception {
        log.info("[businessHandle]start");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        Map<String, String> outParam = new HashMap<>(3);
        try {
            result = scanReceiveService.scanReceive(command, outParam);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("接车失败");
            log.error(e.getMessage(), e);
        }
        log.info("[businessHandle]end");
        uptimeService.trigger(result, command.getWoCode(), ServiceStationVal.WEB_SERVICE_MAINTENANCEINBOUNDTIME,
                "", "");
        uptimeService.trigger(result, command.getWoCode(), ServiceStationVal.WEB_SERVICE_VEHICLERECEIVESUPERVISE,
                "", outParam.get("mileage"));
        return result;
    }
}
