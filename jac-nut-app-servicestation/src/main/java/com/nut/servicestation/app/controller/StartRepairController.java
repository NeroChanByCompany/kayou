package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.StartRepairForm;
import com.nut.servicestation.app.service.StartEndRepairService;
import com.nut.servicestation.app.service.UptimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/2
 */
@RestController
@Slf4j
public class StartRepairController extends BaseController {

    @Autowired
    private StartEndRepairService startEndRepairService;
    @Autowired
    private UptimeService uptimeService;

    @PostMapping(value = "/startRepair")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson StartRepairForm command) throws Exception {
        log.info("[businessHandle]start");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        boolean lockGet = false;
        try {
            // 修正：【ID1000535】
            //OK20702019081522310023 ，工单维修结束，无法提交（插入了两条检车记录）
            // 由原来的在service获取分布式锁提到在controller中获取分布式锁。
            if (!startEndRepairService.lock(command.getWoCode(), command.getOperateId())) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("处理中，请稍后");
                return result;
            }
            lockGet = true;
            result = startEndRepairService.startRepair(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("提交失败");
            log.error(e.getMessage(), e);
        }finally {
            if (lockGet) {
                // 释放分布式锁
                startEndRepairService.unlock(command.getWoCode(), command.getOperateId());
            }
        }
        log.info("[businessHandle]end");
        uptimeService.trigger(result, command.getWoCode(), ServiceStationVal.WEB_SERVICE_VEHICLEINSPECTEND,
                "", "");
        return result;
    }
}
