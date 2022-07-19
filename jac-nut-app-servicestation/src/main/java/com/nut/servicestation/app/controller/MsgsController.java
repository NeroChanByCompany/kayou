package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.MsgsForm;
import com.nut.servicestation.app.service.MsgsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@RestController
public class MsgsController extends BaseController {

    @Autowired
    private MsgsService msgsService;


    /**
     * @param command
     * @Description: 消息列表
     */
    @PostMapping(value = "/msgs")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson MsgsForm command) throws Exception {
        log.info("[businessHandle] 消息列表 start");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        try {
            result.setData(msgsService.msgs(command));
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[businessHandle] 消息列表 end");
        return result;
    }
}
