package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.ApplyCloseWoForm;
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
public class ApplyCloseWoController extends BaseController {
    @Autowired
    private ApplyModifyOrCloseWoService applyModifyOrCloseWoService;
    @Autowired
    private UptimeService uptimeService;

    @PostMapping(value = "/applyCloseWo")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson ApplyCloseWoForm command) throws Exception {
        log.info("[ApplyCloseWoController] start -------");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();

        try {
            result = applyModifyOrCloseWoService.closeWo(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("申请关闭工单失败!");
            log.error(e.getMessage(), e);
        }
        log.info("[ApplyCloseWoController] end -------");
        uptimeService.trigger(result, command.getWoCode(), ServiceStationVal.WEB_SERVICE_ABNORMALWORKORDERAPPLY,
                UptimeVal.APPLY_TYPE_CLOSE, "");
        return result;
    }
}
