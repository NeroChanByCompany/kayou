package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.CountByStatusForm;
import com.nut.servicestation.app.service.CountByStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/6
 */
@Slf4j
@RestController
public class CountByStatusController {
    @Autowired
    private CountByStatusService countByStatusService;

    @PostMapping(value = "/countByStatus")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson CountByStatusForm command) {
        log.info("[businessHandle]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = countByStatusService.countByStatus(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询工单数量失败");
            log.error(e.getMessage(), e);
        }
        log.info("[businessHandle]end");
        return result;
    }
}
