package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.EndRepairForm;
import com.nut.servicestation.app.service.StartEndRepairService;
import com.nut.servicestation.app.service.UptimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*
 *  @author wuhaotian 2021/7/5
 */
@RestController
@Slf4j
public class EndRepairController extends BaseController {
    @Autowired
    private StartEndRepairService startEndRepairService;
    @PostMapping(value = "/endRepair")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson EndRepairForm command) throws Exception {
        log.info("[businessHandle]start");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = startEndRepairService.endRepair(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("提交失败");
            log.error(e.getMessage(), e);
        }
        log.info("[businessHandle]end");
/*
        不清楚当前业务有什么具体作用 增加结束工单延时
        uptimeService.trigger(result, command.getWoCode(), ServiceStationVal.WEB_SERVICE_VEHICLEREPAIREND,
                "", "", 20000L);
        uptimeService.trigger(result, command.getWoCode(), ServiceStationVal.WEB_SERVICE_WORKORDERENCLOSURE,
                "manual", "");
        uptimeService.trigger(result, command.getWoCode(), ServiceStationVal.WEB_SERVICE_WOACCESSORIESCODE,
                "", "");*/
        return result;
    }
}
