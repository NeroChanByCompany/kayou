package com.nut.driver.app.controller;

import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.BizContentForm;
import com.nut.driver.app.form.PayForm;
import com.nut.driver.app.form.PaySuccessForm;
import com.nut.driver.app.service.OrderService;
import com.nut.driver.app.service.PayOrderService;
import com.nut.driver.common.config.CMBConfig;
import com.nut.driver.common.em.DictEnum;
import com.nut.driver.common.utils.GsonUtils;
import com.nut.driver.common.utils.UrlDeal;
import com.nut.driver.common.utils.cmb.SM2Util;
import com.nut.driver.common.utils.cmb.SignatureUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支付
 *
 * @author MengJinyue
 *
 */
@Controller
@Slf4j
@RestController
@RequestMapping("/paySimCard")
@Api(tags = "支付（立即购买按钮）")
public class PayController extends BaseController {
    @Autowired
    private CMBConfig cmbConfig;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private OrderService orderService;

    /**
     * 支付（支付宝/微信/银联云闪付）
     */
    @ApiOperation(value = "预支付（弹出支付窗口）")
    @PostMapping("/toPay")
    public HttpCommandResultWithData toPaySimCard(@RequestBody PayForm payForm) throws Exception {
        this.formValidate(payForm);
        return payOrderService.toPay(payForm);

    }

    /**
     * 查询支付是否成功
     */
    @ApiOperation(value = "查询支付是否成功")
    @PostMapping("/searchSuccessFlag")
    public HttpCommandResultWithData searchSuccessFlag(@RequestBody PaySuccessForm paySuccessForm) throws Exception {
        this.formValidate(paySuccessForm);
        return payOrderService.searchPaySuccessFlag(paySuccessForm.getPayOrderNumber());
    }

    /**
     * 通知接收（支付宝）
     * biz_content=%7B%22orderId%22%3A%22428669897124347904A73172%22%2C%22endDate%22%3A%2220210928%22%2C%22openId%22%3A%222088312713978504%22%2C%22userId%22%3A%22N087284002%22%2C%22mchReserved%22%3A%222103%22%2C%22cmbOrderId%22%3A%22004121092814302931068232%22%2C%22dscAmt%22%3A%220%22%2C%22payType%22%3A%22ZF%22%2C%22txnTime%22%3A%2220210928143029%22%2C%22merId%22%3A%2230899915511007Z%22%2C%22endTime%22%3A%22145538%22%2C%22currencyCode%22%3A%22156%22%2C%22txnAmt%22%3A%2299900%22%7D&sign=MEYCIQCrnBb0U%2FkzImj34S39SkBWRVreU4q1GUozVAK%2Bw9LKKgIhALFo12E1JBsmCn%2B2ajoMM%2Bk7qbAUJFJC%2B6XEnW%2BNFH8p&encoding=UTF-8&version=0.0.1&signMethod=02
     * @param requestBodyString
     * @return
     */
    @RequestMapping(value = "/notify/alipay", method = RequestMethod.POST)
    public Map<String, String> payAliNotify(@RequestBody String requestBodyString) {
        log.info("========支付宝回调 start============");
        log.info("========支付宝回调请求参数：=====" + requestBodyString);
        //响应数据
        Map<String, String> respData = new HashMap<>();
        respData.put("version", CMBConfig.version_0_0_1);//版本号，固定为0.0.1(必传)
        respData.put("encoding", CMBConfig.encoding);//编码方式，固定为UTF-8(必传)
        respData.put("signMethod", CMBConfig.signMethod_02);//签名方法(必传)
        try {
            respData.put("returnCode", "SUCCESS"); //SUCCESS表示商户接收通知成功并校验成功

            //非空校验
            if (requestBodyString == null || "".equals(requestBodyString.trim())) {
                respData.put("returnCode", "FAIL");
                return respData;
            }

            //请求参数解析
            Map<String, String> requestBodyMap = GsonUtils.str2Map(requestBodyString);
            Map<String, String> resultMap = requestBodyMap.entrySet().stream().collect(Collectors.toMap(e -> SignatureUtil.decode(e.getKey()), e -> SignatureUtil.decode(e.getValue())));
            if (resultMap == null) {
                respData.put("returnCode", "FAIL");
                return respData;
            }

            //移除sign
            String sign = resultMap.remove("sign");
            //对待加签内容进行排序拼接
            String contentStr = SignatureUtil.getSignContent(resultMap);
            //验证签名-使用招行公钥进行验签
            boolean flag = SM2Util.sm2Check(contentStr,sign,cmbConfig.SM2_CMB_PUBLIC_KEY);
            if (!flag) {
                //验签失败
                log.error("验签失败");
                respData.put("returnCode", "FAIL");
                return respData;
            }
            log.info("验签成功");

            //......（处理自身业务逻辑）
            try {
                //获取参数
                BizContentForm bizContentForm = GsonUtils.jsonToObject(UrlDeal.decodeURIComponent(resultMap.get("biz_content")), BizContentForm.class);
                HttpCommandResultWithData response = this.payOrderService.payCallback(DictEnum.PAY_WAY_ALI.getCode(), bizContentForm);
                if (ECode.SUCCESS.code() == response.getResultCode()){
                    respData.put("respCode", "SUCCESS");//业务错误码，成功为SUCCESS，失败为FAIL
                }else {
                    respData.put("respCode","FAIL");
                    respData.put("respMsg","error_msg");
                }
            } catch (Exception e) {
                e.printStackTrace();
                respData.put("respCode","FAIL");
                respData.put("respMsg","error_msg");
            }

            //对待加签内容进行排序拼接
            String signContent = SignatureUtil.getSignContent(respData);
            //加签-使用商户私钥加签（国密加签）
            respData.put("sign", SM2Util.sm2Sign(signContent,cmbConfig.SM2_PRIVATE_KEY));
            log.info("加签成功");
            log.info("========支付宝回调 end============");
            return respData;
        } catch (Exception e) {
            e.printStackTrace();
            respData.put("returnCode", "FAIL");
            return respData;
        }
    }

    /**
     * 通知接收（微信）
     *biz_content=%7B%22orderId%22%3A%22376512595026247680%22%2C%22endDate%22%3A%2220210507%22%2C%22openId%22%3A%22oWxBY4yEFjmz6g3OUqaDjulz4PwA%22%2C%22userId%22%3A%22N003282575%22%2C%22cmbOrderId%22%3A%22003221050716160120565995%22%2C%22dscAmt%22%3A%220%22%2C%22payType%22%3A%22WX%22%2C%22txnTime%22%3A%2220210507161601%22%2C%22merId%22%3A%22308999107420080%22%2C%22endTime%22%3A%22161612%22%2C%22currencyCode%22%3A%22156%22%2C%22txnAmt%22%3A%221%22%7D&sign=MEUCIAqyl3vPXhBvrpt%2B4gPMAbh4a3qvkKRNuU72AXycOz0tAiEAzlzU5Ec5yrV4KrYTM5SninWnJAB2DnSjRoA9Qtc4znE%3D&encoding=UTF-8&version=0.0.1&signMethod=02
     * @param requestBodyString
     * @return
     */
    @RequestMapping(value = "/notify/wechat", method = RequestMethod.POST)
    public Map<String, String> payWechatNotify(@RequestBody String requestBodyString) {
        log.info("========微信回调 start============");
        log.info("========微信回调请求参数：=====" + requestBodyString);
        //响应数据
        Map<String, String> respData = new HashMap<>();
        respData.put("version", CMBConfig.version_0_0_1);//版本号，固定为0.0.1(必传)
        respData.put("encoding", CMBConfig.encoding);//编码方式，固定为UTF-8(必传)
        respData.put("signMethod", CMBConfig.signMethod_02);//签名方法(必传)
        try {
            respData.put("returnCode", "SUCCESS"); //SUCCESS表示商户接收通知成功并校验成功

            //非空校验
            if (requestBodyString == null || "".equals(requestBodyString.trim())) {
                respData.put("returnCode", "FAIL");
                return respData;
            }

            //请求参数解析
            Map<String, String> requestBodyMap = GsonUtils.str2Map(requestBodyString);
            Map<String, String> resultMap = requestBodyMap.entrySet().stream().collect(Collectors.toMap(e -> SignatureUtil.decode(e.getKey()), e -> SignatureUtil.decode(e.getValue())));
            if (resultMap == null) {
                respData.put("returnCode", "FAIL");
                return respData;
            }

            //移除sign
            String sign = resultMap.remove("sign");
            //对待加签内容进行排序拼接
            String contentStr = SignatureUtil.getSignContent(resultMap);
            //验证签名-使用招行公钥进行验签
            boolean flag = SM2Util.sm2Check(contentStr,sign,cmbConfig.SM2_CMB_PUBLIC_KEY);
            if (!flag) {
                //验签失败
                log.error("验签失败");
                respData.put("returnCode", "FAIL");
                return respData;
            }
            log.info("验签成功");

            //......（处理自身业务逻辑）
            try {
                //获取参数
                BizContentForm bizContentForm = GsonUtils.jsonToObject(UrlDeal.decodeURIComponent(resultMap.get("biz_content")), BizContentForm.class);
                HttpCommandResultWithData response = this.payOrderService.payCallback(DictEnum.PAY_WAY_WECHAT_MINI.getCode(), bizContentForm);
                if (ECode.SUCCESS.code() == response.getResultCode()){
                    respData.put("respCode", "SUCCESS");//业务错误码，成功为SUCCESS，失败为FAIL
                }else {
                    respData.put("respCode","FAIL");
                    respData.put("respMsg","error_msg");
                }
            } catch (Exception e) {
                e.printStackTrace();
                respData.put("respCode","FAIL");
                respData.put("respMsg","error_msg");
            }

            //对待加签内容进行排序拼接
            String signContent = SignatureUtil.getSignContent(respData);
            //加签-使用商户私钥加签（国密加签）
            respData.put("sign", SM2Util.sm2Sign(signContent,cmbConfig.SM2_PRIVATE_KEY));
            log.info("加签成功");
            log.info("========微信回调 end============");
            return respData;
        } catch (Exception e) {
            e.printStackTrace();
            respData.put("returnCode", "FAIL");
            return respData;
        }
    }

    /**
     * 退款通知接收（支付宝/微信）
     *
     * biz_content=%7B%22orderId%22%3A%22376488852203491328%22%2C%22endDate%22%3A%2220210507%22%2C%22userId%22%3A%22N003282575%22%2C%22refundDscAmt%22%3A%220%22%2C%22cmbOrderId%22%3A%22003221050714421730957325%22%2C%22payType%22%3A%22WX%22%2C%22txnTime%22%3A%2220210507144217%22%2C%22merId%22%3A%22308999107420080%22%2C%22endTime%22%3A%22144222%22%2C%22currencyCode%22%3A%22156%22%2C%22refundAmt%22%3A%221%22%7D&sign=MEUCIQCrGNQo7%2B0yu4isVn%2F0Z7ULEh5HMxDgE02fqU%2BNvWO29AIgR9Ng5gMg3SZXPCQkHy8wgtcYvI4qPjKw5HqhXf1xfI0%3D&encoding=UTF-8&version=0.0.1&signMethod=02
     *
     * @param requestBodyString
     * @return
     */
    @RequestMapping(value = "/refund/notify", method = RequestMethod.POST)
    public Map<String, String> refundAliNotify(@RequestBody String requestBodyString) {
        log.info("========退款回调 start============");
        log.info("========退款回调请求参数：=====" + requestBodyString);
        //响应数据
        Map<String, String> respData = new HashMap<>();
        respData.put("version", CMBConfig.version_0_0_1);//版本号，固定为0.0.1(必传)
        respData.put("encoding", CMBConfig.encoding);//编码方式，固定为UTF-8(必传)
        respData.put("signMethod", CMBConfig.signMethod_02);//签名方法(必传)
        try {
            respData.put("returnCode", "SUCCESS"); //SUCCESS表示商户接收通知成功并校验成功

            //非空校验
            if (requestBodyString == null || "".equals(requestBodyString.trim())) {
                respData.put("returnCode", "FAIL");
                return respData;
            }

            //请求参数解析
            Map<String, String> requestBodyMap = GsonUtils.str2Map(requestBodyString);
            Map<String, String> resultMap = requestBodyMap.entrySet().stream().collect(Collectors.toMap(e -> SignatureUtil.decode(e.getKey()), e -> SignatureUtil.decode(e.getValue())));
            if (resultMap == null) {
                respData.put("returnCode", "FAIL");
                return respData;
            }

            //移除sign
            String sign = resultMap.remove("sign");
            //对待加签内容进行排序拼接
            String contentStr = SignatureUtil.getSignContent(resultMap);
            //验证签名-使用招行公钥进行验签
            boolean flag = SM2Util.sm2Check(contentStr,sign,cmbConfig.SM2_CMB_PUBLIC_KEY);
            if (!flag) {
                //验签失败
                log.error("验签失败");
                respData.put("returnCode", "FAIL");
                return respData;
            }
            log.info("验签成功");

            //......（处理自身业务逻辑）
            try {
                //获取参数
                String noticeData = UrlDeal.decodeURIComponent(resultMap.get("biz_content"));
                BizContentForm bizContentForm = GsonUtils.jsonToObject(noticeData, BizContentForm.class);
                HttpCommandResultWithData response = this.payOrderService.refundCallback(bizContentForm);
                if (ECode.SUCCESS.code() == response.getResultCode()){
                    respData.put("respCode", "SUCCESS");//业务错误码，成功为SUCCESS，失败为FAIL
                }else {
                    respData.put("respCode","FAIL");
                    respData.put("respMsg","error_msg");
                }
            } catch (Exception e) {
                e.printStackTrace();
                respData.put("respCode","FAIL");
                respData.put("respMsg","error_msg");
            }

            //对待加签内容进行排序拼接
            String signContent = SignatureUtil.getSignContent(respData);
            //加签-使用商户私钥加签（国密加签）
            respData.put("sign", SM2Util.sm2Sign(signContent,cmbConfig.SM2_PRIVATE_KEY));
            log.info("加签成功");
            log.info("========退款回调 end============");
            return respData;
        } catch (Exception e) {
            e.printStackTrace();
            respData.put("returnCode", "FAIL");
            return respData;
        }
    }
}
