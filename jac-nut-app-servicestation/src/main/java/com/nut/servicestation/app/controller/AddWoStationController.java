package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.AddWoStationForm;
import com.nut.servicestation.app.service.AddWoStationService;
import com.nut.servicestation.app.service.UptimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
 *  @author wuhaotian 2021/7/5
 */
@Slf4j
@RestController
public class AddWoStationController extends BaseController {
    @Autowired
    private AddWoStationService addWoStationService;
    @Autowired
    private UptimeService uptimeService;

    @PostMapping(value = "/newWoSta")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson AddWoStationForm command) throws Exception {
        log.info("[businessHandle]start");
        this.formValidate(command);
        HttpCommandResultWithData<Map<String, String>> result = new HttpCommandResultWithData<>();
        try {
            result = addWoStationService.newWoSta(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("提交失败");
            log.error(e.getMessage(), e);
        }
        log.info("[businessHandle]end");
/*        if (result.getData() != null) {
            uptimeService.trigger(result, result.getData().get("woCode"), ServiceStationVal.WEB_SERVICE_APPWORKORDERRETURN,
                    "", "");
            uptimeService.trigger(result, result.getData().get("woCode"), ServiceStationVal.WEB_SERVICE_SERVICERECEIVINGORDER,
                    "", "", 10000L);
            // 增加一次重试，解决小概率失败
            uptimeService.trigger(result, result.getData().get("woCode"), ServiceStationVal.WEB_SERVICE_SERVICERECEIVINGORDER,
                    "", "", 20000L);
        }*/
        return result;
    }
}
