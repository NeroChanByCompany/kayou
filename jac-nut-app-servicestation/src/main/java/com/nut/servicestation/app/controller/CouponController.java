package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.CouponApprovalListForm;
import com.nut.servicestation.app.form.CouponDetailForm;
import com.nut.servicestation.app.form.CouponExchangeForm;
import com.nut.servicestation.app.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/7
 */
@Slf4j
@RestController
@RequestMapping("/coupon")
public class CouponController extends BaseController {

    @Autowired
    private CouponService couponService;

    /**
     * 扫码获取优惠券信息
     * @param command
     * @return
     */
    @PostMapping(value = "/detail")
    @LoginRequired
    public HttpCommandResultWithData detail(@RequestJson CouponDetailForm command) throws Exception {
        log.info("[detail]start");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = couponService.detail(command);
        } catch (IllegalArgumentException e) {
            log.info("[detail]CLIENT_ERROR:{}", e.getMessage());
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
        }
        log.info("[detail]end");
        return result;
    }
    /**
     * 核销记录
     * @param command
     * @return
     */
    @PostMapping(value = "/approvalList")
    @LoginRequired
    public HttpCommandResultWithData approvalList(@RequestJson CouponApprovalListForm command) {
        log.info("[approvalList]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = couponService.approvalList(command);
        } catch (IllegalArgumentException e) {
            log.info("[approvalList]CLIENT_ERROR:{}", e.getMessage());
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
        }
        log.info("[approvalList]end");
        return result;
    }
    /**
     * 兑换
     * @param command
     * @return
     */
    @PostMapping(value = "/exchange")
    @LoginRequired
    public HttpCommandResultWithData exchange(@RequestJson CouponExchangeForm command) throws Exception {
        log.info("[exchange]start");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = couponService.exchange(command);
        } catch (IllegalArgumentException e) {
            log.info("[exchange]CLIENT_ERROR:{}", e.getMessage());
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("兑换失败，请稍后重试");
        }
        log.info("[exchange]end");
        return result;
    }
}
