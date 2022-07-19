package com.nut.driver.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.form.OperationManualListForm;
import com.nut.driver.app.service.VersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 帮助手册
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-28 14:13
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "帮助手册")
public class VersionController extends BaseController {

    @Autowired
    private VersionService versionService;

    @PostMapping(value = "/getUserGuide")
    @ApiOperation("用户指南")
    @ApiImplicitParam(name = "appCode" , value = "生效范围" , dataType = "String")
    @SneakyThrows
    public HttpCommandResultWithData getUserGuide(@ApiIgnore @RequestJson OperationManualListForm form){
        log.info("getUserGuide start param:{}",form);
        this.formValidate(form);
        return Result.ok(versionService.getUserGuide(form));
    }


}
