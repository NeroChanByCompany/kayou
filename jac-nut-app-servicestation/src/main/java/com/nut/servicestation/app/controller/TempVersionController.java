package com.nut.servicestation.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.GetSetPictureInfoForm;
import com.nut.servicestation.app.service.TempSetPictureInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 *  @author wuhaotian 2021/7/7
 */
@Slf4j
@RestController
public class TempVersionController extends BaseController {
    @Autowired
    private TempSetPictureInfoService tempSetPictureInfoService;

    @PostMapping(value = {"/getAppVersion"})
    public HttpCommandResultWithData getTempAppVersion(@RequestJson GetSetPictureInfoForm command) throws Exception {
        log.info("==== getAppVersionController start ====");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();

        try {
            result = tempSetPictureInfoService.getPictureInfoList(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("获取版本升级信息失败");
            log.error(" ===== getAppVersionController:", e);
        }
        log.info("==== getAppVersionController end ====");

        return result;
    }
}
