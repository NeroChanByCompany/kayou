package com.nut.driver.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nut.common.base.BaseController;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.entity.AuthLogEntity;
import com.nut.driver.app.form.AuthLogForm;
import com.nut.driver.app.form.AuthStateForm;
import com.nut.driver.app.service.AuthLogService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author liuBing
 * @Classname AuthLogController
 * @Description TODO 实名认证
 * @Date 2021/8/10 13:33
 */
@Slf4j
@RestController
@Api(tags = "用户实名认证")
@RequestMapping("/userAuth")
public class AuthLogController extends BaseController {
    @Autowired
    AuthLogService authLogService;

    /**
     * 用户认证回调
     * @param form
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单号", dataType = "String"),

    })
    @PostMapping("/callback")
    public HttpCommandResultWithData<Integer> callback(@ApiIgnore @RequestBody AuthLogForm form){
        log.info("auth callback param:{}",form);
        if (StringUtils.isBlank(form.getOrderNo())){
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "请输入订单号");
        }
        return Result.ok(authLogService.callback(form));
    }

    /**
     * 查询某个用户是否认证成功
     * @param form
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "carVin", value = "底盘号", dataType = "String"),
    })
    @PostMapping("/getOne")
    public HttpCommandResultWithData<AuthLogEntity> getOne(@ApiIgnore @RequestBody AuthLogForm form){
        log.info("auth getOne param:{}",form);
        if (StringUtils.isBlank(form.getCarVin())){
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "请输入底盘号");
        }
        QueryWrapper<AuthLogEntity> wrapper = new QueryWrapper<AuthLogEntity>().eq("car_vin", form.getCarVin());
        wrapper.last("limit 1");
        return Result.ok(authLogService.getOne(wrapper));
    }


    /**
     * 用户实名认证状态变更
     * @param form
     * @return
     */
    @ApiOperation(value = "用户认证状态变更", notes = "根据车辆simId变更是否认证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authStatePojos", value = "参数集合", allowMultiple  =true ,dataType = "AuthStatePojo",required = true),

    })
    @PostMapping("/state")
    public HttpCommandResultWithData<Integer> state(@ApiIgnore @RequestBody AuthStateForm form) throws Exception {
        log.info("auth state param:{}",form);
        authLogService.state(form);
        return Result.ok();
    }



}
