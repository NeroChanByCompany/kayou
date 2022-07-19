package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.WoInfoForm;
import com.nut.servicestation.app.service.WoInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@RestController
public class WoInfoController extends BaseController {

    @Autowired
    private WoInfoService woInfoService;

    /**
     * request from APP
     */
    @PostMapping(value = "/woInfo")
    @LoginRequired
    public @ResponseBody
    HttpCommandResultWithData businessHandle(@RequestJson WoInfoForm command) throws Exception {
        this.formValidate(command);
        return woInfoService.core(command, false);
    }
}
