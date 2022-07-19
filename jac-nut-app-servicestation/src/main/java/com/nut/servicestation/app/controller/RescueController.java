package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.StaffListForm;
import com.nut.servicestation.app.service.RescueInfoQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@RestController
public class RescueController extends BaseController {
    @Autowired
    private RescueInfoQueryService rescueInfoQueryService;

    /**
     * 查询指派人员列表
     */
    @PostMapping(value = "/staffList")
    @LoginRequired
    public HttpCommandResultWithData staffList(@RequestJson StaffListForm command) {
        log.info("[staffList]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = rescueInfoQueryService.staffList(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询指派人员列表失败");
            log.error(e.getMessage(), e);
        }
        log.info("[staffList]end");
        return result;
    }
}
