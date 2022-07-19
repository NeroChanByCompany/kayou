package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.StartInspectForm;
import com.nut.servicestation.app.service.StartEndRepairService;
import com.nut.servicestation.app.service.UptimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@RestController
public class StartInspectController extends BaseController {
    @Autowired
    private StartEndRepairService startEndRepairService;
    @Autowired
    private UptimeService uptimeService;

    /**
     * 提交开始检查时间
     */
    @PostMapping(value = "/startInspect")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson StartInspectForm command) throws Exception {
        log.info("[businessHandle]start");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = startEndRepairService.startInspect(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("开始检查失败");
            log.error(e.getMessage(), e);
        }
        log.info("[businessHandle]end");
        return result;
    }
}
