package com.nut.driver.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.form.AppFreezeForm;
import com.nut.driver.app.form.AppFreezeTokenForm;
import com.nut.driver.app.form.AppLoginForm;
import com.nut.driver.app.service.UserFreezeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

/**
 * @author liuBing
 * @Classname UserFreezeController
 * @Description TODO 用户冻结
 * @Date 2021/9/14 16:46
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户冻结")
public class UserFreezeController extends BaseController {

    @Resource
    UserFreezeService userFreezeService;

    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 冻结用户
     * @Date 16:48 2021/9/14
     * @Param [form]
     **/
    @ApiOperation(value = "冻结用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String",required = true),
            @ApiImplicitParam(name = "appType", value = "登录APP类型", dataType = "String",required = true),
            @ApiImplicitParam(name = "key", value = "key", dataType = "String",required = true),
    })
    @PostMapping(value = {"/freeze"})
    public HttpCommandResultWithData freeze(@ApiIgnore @RequestBody AppFreezeForm form) throws Exception {
        this.formValidate(form);
        userFreezeService.freeze(form);
        return Result.ok();
    }


    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 激活用户
     * @Date 16:48 2021/9/14
     * @Param [form]
     **/
    @ApiOperation(value = "激活用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String",required = true),
            @ApiImplicitParam(name = "appType", value = "登录APP类型", dataType = "String",required = true),
            @ApiImplicitParam(name = "key", value = "key", dataType = "String",required = true),
    })
    @PostMapping(value = {"/activate"})
    public HttpCommandResultWithData activate(@ApiIgnore @RequestBody AppFreezeForm form) throws Exception {
        this.formValidate(form);
        userFreezeService.activate(form);
        return Result.ok();
    }


    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 冻结用户
     * @Date 16:48 2021/9/14
     * @Param [form]
     **/
    @ApiOperation(value = "冻结用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", dataType = "String",required = true),
            @ApiImplicitParam(name = "key", value = "key", dataType = "String",required = true),
            @ApiImplicitParam(name = "remark", value = "备注", dataType = "String",required = false),
    })
    @PostMapping(value = {"/freezeByUcId"})
    public HttpCommandResultWithData freezeByUcId(@ApiIgnore @RequestBody AppFreezeTokenForm form) throws Exception {
        this.formValidate(form);
        userFreezeService.freezeByUcId(form);
        return Result.ok();
    }

}
