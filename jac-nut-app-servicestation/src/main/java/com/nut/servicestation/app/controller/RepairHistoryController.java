package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.RepairHistoryForm;
import com.nut.servicestation.app.service.RepairHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/5
 */
@RestController
@Slf4j
public class RepairHistoryController extends BaseController {

    @Autowired
    private RepairHistoryService repairHistoryService;

    /**
     * request from APP
     */
    @PostMapping(value = "/repairHistory")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson RepairHistoryForm command) throws Exception {
        log.info("[RepairHistoryController] start -------");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();

        try {
            result = repairHistoryService.queryRepairHistory(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("维修历史查询失败!");
            log.error(e.getMessage(), e);
        }
        log.info("[RepairHistoryController] end -------");
        return result;
    }
}
