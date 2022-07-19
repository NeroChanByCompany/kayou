package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.ApplyModifyWoForm;
import com.nut.servicestation.app.service.ApplyModifyOrCloseWoService;
import com.nut.servicestation.app.service.UptimeService;
import com.nut.servicestation.common.constants.UptimeVal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/6
 */
@Slf4j
@RestController
public class ApplyModifyWoController extends BaseController {
    @Autowired
    private ApplyModifyOrCloseWoService applyModifyOrCloseWoService;
    @Autowired
    private UptimeService uptimeService;

    @PostMapping(value = "/applyModifyWo")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson ApplyModifyWoForm command) throws Exception {
        log.info("[ApplyModifyWoController] start -------");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();

        try {
            result = applyModifyOrCloseWoService.modifyWo(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("申请修改工单失败!");
            log.error(e.getMessage(), e);
        }
        log.info("[ApplyModifyWoController] end -------");
        uptimeService.trigger(result, command.getWoCode(), ServiceStationVal.WEB_SERVICE_ABNORMALWORKORDERAPPLY,
                UptimeVal.APPLY_TYPE_MODIFY, "");
        return result;
    }
}
