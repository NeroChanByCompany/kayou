package com.nut.driver.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.domain.IntegralConsumeConfirmInfo;
import com.nut.driver.app.domain.IntegralConsumeInfo;
import com.nut.driver.app.service.DuibaService;
import com.nut.driver.common.sdk.CreditTool;
import com.nut.driver.common.sdk.VirtualCardConsumeParams;
import com.nut.driver.common.sdk.VirtualResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 对接兑吧的controller
 *
 * @author Administrator
 * @date 2019/8/1
 */

@RestController
@Slf4j
@Api(value = "兑吧接口")
public class DuibaController extends BaseController {

    @Autowired
    private DuibaService duibaService;

    @Value("${duiba_appKey}")
    private String appKey;

    @Value("${duiba_appSecret}")
    private String appSecret;

    /**
     * 扣积分接口
     */
    @RequestMapping(value = "/integral/consume")
    public JSONObject integralConsume(HttpServletRequest request) throws Exception{
        log.info("[/integral/consume]start");
        JSONObject result = null;
        try {
            IntegralConsumeInfo integralConsumeInfo = requestToIntegralConsumeInfo(request);
            log.info("调用积分消费兑吧请求的参数：{}", integralConsumeInfo);
            result = duibaService.integralConsume(integralConsumeInfo);
        } catch (Exception e) {
            result = new JSONObject();
            result.put("status", "fail");
            result.put("errorMessage", "服务异常");
            result.put("credits", "0");
            log.error(e.getMessage(), e);
        }
        log.info("[/integral/consume]end,返回的参数：{}", result);
        return result;
    }

    /**
     * 积分消费确认
     */
    @RequestMapping(value = "/integral/consumeConfirm")
    public String consumeConfirm(HttpServletRequest request) {
        log.info("[consumeConfirm]start");
        String result = "ok";
        try {
            IntegralConsumeConfirmInfo integralConsumeConfirmInfo = requestToIntegralConsumeConfirmInfo(request);
            log.info("兑吧调用确认积分消费的参数：{}", integralConsumeConfirmInfo);
            result = duibaService.consumeConfirm(integralConsumeConfirmInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = "fail";
        }
        log.info("[consumeConfirm]end");
        return result;
    }

    /**
     * 加积分
     */
    @RequestMapping(value = "/integral/add")
    public HttpCommandResultWithData add(HttpServletRequest request) {
        log.info("[integral/add] start");
        // 需要时再对接
        return Result.ok();
    }

    /**
     * 虚拟商品消费
     */
    @RequestMapping(value = "/virtualConsume")
    public VirtualResult virtualConsume(HttpServletRequest request) throws Exception {
        log.info("[virtualConsume]start");
        try {
            CreditTool creditTool = new CreditTool(appKey, appSecret);
            VirtualCardConsumeParams virtualCardConsumeParams = creditTool.parseVirtualCardConsume(request);
            log.info("兑吧调用添加积分的参数：{}", virtualCardConsumeParams);
            duibaService.virtualConsume(virtualCardConsumeParams);
            log.info("[integral/add]end");
            return new VirtualResult("success");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.info("[integral/add]end");
            return new VirtualResult("fail");
        }
    }

    private IntegralConsumeInfo requestToIntegralConsumeInfo(HttpServletRequest request){
        IntegralConsumeInfo integralConsumeInfo = new IntegralConsumeInfo();
        integralConsumeInfo.setUid(request.getParameter("uid"));
        integralConsumeInfo.setCredits(request.getParameter("credits"));
        integralConsumeInfo.setItemCode(request.getParameter("itemCode"));
        integralConsumeInfo.setAppKey(request.getParameter("appKey"));
        integralConsumeInfo.setTimestamp(request.getParameter("timestamp"));
        integralConsumeInfo.setDescription(request.getParameter("description"));
        integralConsumeInfo.setOrderNum(request.getParameter("orderNum"));
        integralConsumeInfo.setType(request.getParameter("type"));
        integralConsumeInfo.setFacePrice(Long.parseLong(request.getParameter("facePrice")));
        integralConsumeInfo.setActualPrice(Long.parseLong(request.getParameter("actualPrice")));
        integralConsumeInfo.setIp(request.getParameter("ip"));
        integralConsumeInfo.setQq(request.getParameter("qq"));
        integralConsumeInfo.setPhone(request.getParameter("phone"));
        integralConsumeInfo.setAlipay(request.getParameter("alipay"));
        integralConsumeInfo.setWaitAudit(Boolean.valueOf(request.getParameter("waitAudit")));
        integralConsumeInfo.setParams(request.getParameter("params"));
        integralConsumeInfo.setSign(request.getParameter("sign"));
        return integralConsumeInfo;
    }

    private IntegralConsumeConfirmInfo requestToIntegralConsumeConfirmInfo(HttpServletRequest request){
        IntegralConsumeConfirmInfo integralConsumeConfirmInfo = new IntegralConsumeConfirmInfo();
        integralConsumeConfirmInfo.setAppKey(request.getParameter("appKey"));
        integralConsumeConfirmInfo.setTimestamp(request.getParameter("timestamp"));
        integralConsumeConfirmInfo.setUid(request.getParameter("uid"));
        integralConsumeConfirmInfo.setSuccess(request.getParameter("success"));
        integralConsumeConfirmInfo.setErrorMessage(request.getParameter("errorMessage"));
        integralConsumeConfirmInfo.setOrderNum(request.getParameter("orderNum"));
        integralConsumeConfirmInfo.setBizId(request.getParameter("bizId"));
        integralConsumeConfirmInfo.setSign(request.getParameter("sign"));
        return integralConsumeConfirmInfo;
    }

}
