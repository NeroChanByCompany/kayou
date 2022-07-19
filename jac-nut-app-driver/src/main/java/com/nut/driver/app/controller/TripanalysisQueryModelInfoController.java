package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.TripanalysisQueryModelInfoForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/10
 */
@Slf4j
@RestController
public class TripanalysisQueryModelInfoController {
    
    
    @PostMapping(value = "/tripanalysisQueryModelInfo")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson TripanalysisQueryModelInfoForm command) {
        log.info("========== tripanalysisQueryModelInfoController businessHandle start ==========");
        log.info("[TripanalysisQueryModelInfoController] TripanalysisQueryModelInfoCommand : {}", command.toString());
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        result.setData(command.getAutoIncreaseId());
        log.info("========== tripanalysisQueryModelInfoController businessHandle end ==========");
        return result;
    }
}
