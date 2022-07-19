package com.nut.driver.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.driver.app.form.CouponBranchListForm;
import com.nut.driver.app.form.CouponDetailForm;
import com.nut.driver.app.form.CouponListForm;
import com.nut.driver.app.service.CouponService;
import com.nut.common.annotation.LoginRequired;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 优惠券接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.controller
 * @Author: yzl
 * @CreateTime: 2021-06-18 15:07
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "优惠券服务")
public class CouponController extends BaseController {

    @Autowired
    private CouponService couponService;

    /**
     * 客户优惠券列表
     * @param form
     * @return
     */
    @PostMapping(value = "/coupon/list")
    @ApiOperation(value = "获取优惠券列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cumStatus", value = "优惠券状态码", dataType = "String")
    })
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData list(@ApiIgnore @RequestJson CouponListForm form) {
        log.info("couponList start param:{}",form);
        this.formValidate(form);
        return Result.ok(couponService.list(form));
    }

    @PostMapping(value = "/coupon/detail")
    @ApiOperation(value = "获取优惠券详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cumId", value = "优惠券ID", dataType = "String")
    })
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData detail(@ApiIgnore @RequestJson CouponDetailForm form){
        log.info("couponDetail start param:{}", form);
        this.formValidate(form);
        return Result.ok(couponService.detail(form));
    }

    /**
    * @Description：使用网点列表
    * @author YZL
    * @data 2021/6/28 20:15
    */
    @PostMapping(value = "/coupon/branch/list")
    @ApiOperation(value = "使用网点列表")
    @LoginRequired
    @SneakyThrows
    @ApiImplicitParams({
            @ApiImplicitParam(name = "infoId",value = "优惠券ID",dataType = "String"),
            @ApiImplicitParam(name = "areaCode",value = "地区ID",dataType = "String")
    })
    public HttpCommandResultWithData branchList(@ApiIgnore @RequestJson CouponBranchListForm form){
        log.info("branchList start param:{}", form);
        this.formValidate(form);
        return Result.ok(couponService.branchList(form));
    }

}
