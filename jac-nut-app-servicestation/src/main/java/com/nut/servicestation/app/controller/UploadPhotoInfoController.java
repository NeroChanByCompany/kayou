package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.UploadPhotoInfoListForm;
import com.nut.servicestation.app.service.UploadPhotoInfoService;
import com.nut.servicestation.common.component.TokenComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@RestController
public class UploadPhotoInfoController{
    @Autowired
    private UploadPhotoInfoService uploadPhotoInfoService;
    @Autowired
    private TokenComponent tokenComponent;

    @PostMapping(value = "/uploadPhoto")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson UploadPhotoInfoListForm commandList) {
        log.info("[uploadPhoto]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        /*
         * 因通用方法@LoginRequired 对多参数校验时，某个参数为null时会报错，所以这里单独校验TOKEN
         */
        if (!tokenComponent.checkTokenServer(commandList.getToken(),commandList)){
            ExceptionUtil.result(ECode.TOKEN_INVALID.code(),ECode.TOKEN_INVALID.message());
        }

        try {
            result = uploadPhotoInfoService.uploadPhoto(commandList);
        } catch (Exception e) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("上传文件失败，请稍后再试！");
            log.error(e.getMessage(), e);
        }finally {
        }
        log.info("[uploadPhoto]end");
        return result;
    }
    @PostMapping(value = "/uploadPhotoSimple",produces = {"application/json; charset=utf-8"})
//    @LoginRequired
    public HttpCommandResultWithData uploadPhotoSimple(@RequestParam(value = "file", required = false) MultipartFile file) {
        log.info("[uploadPhotoSimple]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = uploadPhotoInfoService.uploadPhotoSimple(file);
        } catch (Exception e) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("上传文件失败，请稍后再试！");
            log.error(e.getMessage(), e);
        }finally {

        }
        log.info("[uploadPhotoSimple]end");
        return result;
    }

}
