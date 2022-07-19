package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.*;
import com.nut.servicestation.app.service.AreaStationService;
import com.nut.servicestation.app.service.Coupon2Service;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 服务站优惠发放相关接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.controller
 * @Author: yzl
 * @CreateTime: 2021-07-26 10:06
 * @Version: 1.0
 */
@RestController
@Slf4j
public class Coupon2Controller extends BaseController {


    //******************//
    //******************//
    //推翻重写//
    //******************//
    //******************//

    @Autowired
    private AreaStationService areaStationService;

    @Autowired
    private Coupon2Service coupon2Service;

    @PostMapping("/coupon2/new/queryStationList")
    public HttpCommandResultWithData queryStationList(@RequestBody QueryStationForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = areaStationService.queryStationList(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败");
        }
        return result;
    }

    @PostMapping("/coupon2/new/add")
    @SneakyThrows
    public HttpCommandResultWithData add(@RequestBody Coupon2Form form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        this.formValidate(form);
        if (form.getCouponValid().equals("1") || form.getCouponValid().equals("3")) {
            result = coupon2Service.add(form);
        } else {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("优惠券类型传递不正确");
        }
        return result;
    }

    @PostMapping("/coupon2/new/list")
    @SneakyThrows
    public HttpCommandResultWithData couponList(@RequestBody QueryCouponListForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result = coupon2Service.list(form);
        return result;
    }

    @PostMapping("/coupon2/new/detail")
    @SneakyThrows
    public HttpCommandResultWithData couponDetail(@RequestBody QueryCouponDetailForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result = coupon2Service.detail(form);
        return result;
    }


}
