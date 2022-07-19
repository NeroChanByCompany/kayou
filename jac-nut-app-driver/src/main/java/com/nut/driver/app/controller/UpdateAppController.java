package com.nut.driver.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.driver.app.form.GetSetPictureInfoForm;
import com.nut.driver.app.service.ActionPictureSetService;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 检测APP版本
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.controller
 * @Author: yzl
 * @CreateTime: 2021-06-18 11:03
 * @Version: 1.0
 */
@Slf4j
@RestController
@Api(tags = "APP更新")
public class UpdateAppController extends BaseController {

    @Autowired
    private ActionPictureSetService actionPictureSetService;

    @PostMapping(value = "/getAppVersion")
    @ApiOperation(value = "获取软件版本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "actionCode", value = "动作编码", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "APP类别", dataType = "String"),
    })
    @SneakyThrows
    public HttpCommandResultWithData getAppVersion(@ApiIgnore @RequestJson GetSetPictureInfoForm form) {
        log.info("getAppVersion start param:{}",form);
        this.formValidate(form);
        return Result.ok(actionPictureSetService.getVersionMessage(form));
    }

}
