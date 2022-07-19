package com.nut.driver.common.component;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.nut.driver.app.vo.OrderRefundVo;
import com.nut.driver.app.vo.OrderVo;
import com.nut.driver.app.vo.PayOrderVo;
import com.nut.driver.common.config.CMBConfig;
import com.nut.driver.common.em.DictEnum;
import com.nut.driver.common.utils.DateUtils;
import com.nut.driver.common.utils.GsonUtils;
import com.nut.driver.common.utils.ReqUtils;
import com.nut.driver.common.utils.UrlDeal;
import com.nut.driver.common.utils.cmb.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @description: 招商银行CMB
 * @author: MengJinyue
 * @create: 2021-04-21 19:50
 **/
@Slf4j
@Component
public class CMBComponent {
    @Resource
    private CMBConfig cmbConfig;
    @Value("${spring.application.name}")
    private String applicationName;

    private final String signType = CMBConfig.signMethod_02;//加签方式


    /**
     * 支付
     * @param payWay 支付方式
     * @param payOrderVo 支付订单信息
     * @return
     */
    public Map<String, String> toPay(String payWay, PayOrderVo payOrderVo) {
        //填充biz_content，请求参数
        Map<String, Object> bizContent = this.genOrderBizContent(payWay,payOrderVo);
        //发送请求
        String url = "";
        if(DictEnum.PAY_WAY_ALI.getCode().equals(payWay)){
            //支付宝native支付
            url = cmbConfig.OPEN_API + "/aliPay/nativeOrder";
        }else if(DictEnum.PAY_WAY_WECHAT_MINI.getCode().equals(payWay)){
            //微信支付
            url = cmbConfig.OPEN_API + "/weixinPay/miniAppOrderApply";
        }else if(DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
            //一网通
            url = cmbConfig.OPEN_API + "/netPay/order";
        }

        //调用招商银行接口
        Map<String, String> respMap = this.postForEntity(payWay,url,signType,bizContent,payOrderVo.getPayOrderNumber());
        return respMap;
    }

    /**
     * 查询支付是否成功
     * @param payWay 支付方式
     * @param payOrderNumber 支付单号
     * @param orderTime 订单时间
     */
    public Map<String, String> searchPaySuccessFlag(String payWay,String payOrderNumber,Date orderTime){
        //发送请求
        String url = "";
        if(DictEnum.PAY_WAY_ALI.getCode().equals(payWay)){
            //支付宝native支付
            url = cmbConfig.OPEN_API + "/aliPay/orderStatusQuery";
            /**
             * 成功
             *
             * 支付宝
             * {"returnCode":"SUCCESS",
             * "biz_content":"{\"cmbOrderId\":\"003221042720480281218628\",\"dscAmt\":\"0\"
             * ,\"payType\":\"WX\",\"orderId\":\"0210427204759100802\",\"endDate\":\"20210427\",
             * \"openId\":\"oWxBY4wol4xnGCjM1nZqklt0mEEc\",\"txnTime\":\"20210427204802\",
             * \"merId\":\"308999107420080\",\"endTime\":\"204838\",\"currencyCode\":\"156\",
             * \"txnAmt\":\"1\",\"tradeState\":\"S\"}",
             * "encoding":"UTF-8",
             * "version":"0.0.1",
             * "respCode":"SUCCESS",
             * "signMethod":"02",
             * "sign":"MEUCIFcKVhpz242WspQcgp570coLXZwdoy1YMv1b0NWkK3jzAiEA2JHl3Q0Y72pgRD7u6bRStBb7RP2Fr8CNz2KAuVpix5o\u003d"}
             *
             * 失败
             *
             * 支付宝/微信
             * {"returnCode":"FAIL","errCode":"ZA24_NOT_EXIST",
             * "sign":"MEUCIC1V6goolx5DGJ70oYrzXajt0ZTNZ36li2Q0by3NcVNEAiEA1fSScafRBLGG76ul2X0YHpdQ3pHWv52mI6rMEen818g\u003d",
             * "respMsg":"订单不存在","encoding":"UTF-8","version":"0.0.1","signMethod":"02"}
             *
             * 一网通
             * {"charset":"UTF-8","respMessage":"消费失败-SUC9999",
             * "sign":"MEUCIBd2clAmbObtwGIVv9DjAtSnTMhufe1JGwj8+MOtAUU1AiEA+idi82B5Wol/ixxUaODSUqHhzza2WI9mrl/G04RjQUg\u003d",
             * "signType":"01","version":"1.0","respCode":"PTN4FAL
             *
             */
        }else if(DictEnum.PAY_WAY_WECHAT_MINI.getCode().equals(payWay)){
            //微信支付
            url = cmbConfig.OPEN_API + "/weixinPay/orderStatusQuery";
            /**
             * {"returnCode":"SUCCESS",
             * "biz_content":"{
             * \"orderId\":\"374001737662464000\",
             * \"endDate\":\"20210430\",
             * \"openId\":\"oWxBY4wol4xnGCjM1nZqklt0mEEc\",
             * \"payBank\":\"OTHERS\",
             * \"tradeState\":\"S\",
             * \"cmbOrderId\":\"003221043017584641556916\",
             * \"dscAmt\":\"0\",
             * \"payType\":\"WX\",
             * \"txnTime\":\"20210430175846\",
             * \"thirdOrderId\":\"4200001023202104300083407642\",
             * \"merId\":\"308999107420080\",
             * \"endTime\":\"175916\",\"currencyCode\":\"156\",\"txnAmt\":\"1\"}",
             * "encoding":"UTF-8","version":"0.0.1","respCode":"SUCCESS","signMethod":"02","sign":"MEQCIHPoTbpUO+NfEii8x8PfvB3zy4F6Qs5D+myA3OpSQjJPAiASBOB+3iwfK/x9CFi0zzLTZ+WOHlSnd920/QWs4tk++A\u003d\u003d"}
             */
        }else if(DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
            //一网通
            url = cmbConfig.OPEN_API + "/netPay/orderStatusQuery";
            /**
             * {"charset":"UTF-8",
             * "respMessage":"",
             * "sign":"MEUCICxwtAvuhaAGOpRvJI/Noztr5EUUAKKy2Ra1Z6CPYuRYAiEAgPFe1TpHIm/YYJAYqKOBfnxRcJl0oxH2r5ilIzFTPuM\u003d",
             * "signType":"01",
             * "respData":
             * "{\"dateTime\":\"20210502191533\",
             * \"date\":\"20210430\",
             * \"amount\":\"59.25\",
             * \"bankDate\":\"20210430\",
             * \"orderNo\":\"373882093899350016\",
             * \"settleTime\":\"100318\",
             * \"fee\":\"0\",
             * \"settleAmount\":\"59.25\",
             * \"cardType\":\"02\",
             * \"discountAmount\":\"0\",
             * \"orderStatus\":\"020\",
             * \"settleDate\":\"20210430\",
             * \"subOrderList\":[{\"subMchReserved\":\"\",\"subOrderNo\":\"373882093907329024\",\"subBillAmount\":\"59.25\",\"subMerchantNo\":\"30899910742007A\",\"subDescription\":\"\",\"subStoreId\":\"30899910742007A0001\"}],
             * \"merchantPara\":\"\",
             * \"bankSerialNo\":\"2260E09B00002900000A\",
             * \"bankTime\":\"100318\",
             * \"currency\":\"10\",
             * \"merchantNo\":\"018103\"}",
             * "version":"1.0","respCode":"SUC0000"}
             */
        }
        HashMap<String, Object> bizContent = new HashMap<>();//报文参数
        if(DictEnum.PAY_WAY_ALI.getCode().equals(payWay)){
            bizContent.put("orderId", payOrderNumber);//支付单号
            bizContent.put("merId", cmbConfig.MER_ID);//商户号
            bizContent.put("userId", cmbConfig.USER_ID);//收银员信息
        }else if(DictEnum.PAY_WAY_WECHAT_MINI.getCode().equals(payWay)){
            bizContent.put("orderId", payOrderNumber);//支付单号
            bizContent.put("merId", cmbConfig.WECHAT_MER_ID);//商户号
            bizContent.put("userId", cmbConfig.WECHAT_USER_ID);//收银员信息
        }else if(DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
            bizContent.put("dateTime", DateUtils.getSystemTime(DateUtils.DF_YMDHMS));// 请求时间（格式：yyyyMMddHHmmss）
            bizContent.put("merchantNo", cmbConfig.MER_ID);// 招行商户号,合单支付时必须填平台商户
            bizContent.put("date", DateUtils.getSystemTime(DateUtils.DF_YMD,orderTime));// 支付请求中上送的订单的日期，格式：yyyyMMdd
            bizContent.put("orderNo", payOrderNumber);// 支付请求中上送的商户订单号
        }
        Map<String, String> respMap = this.postForEntity(payWay,url,signType,bizContent,payOrderNumber);
        return respMap;

    }

    /**
     * 退款
     * @param orderRefundVo 退款订单信息
     */
    public Map<String, String> refund(OrderRefundVo orderRefundVo) {
        if (Objects.isNull(orderRefundVo)){
            return null;
        }
        String payWay = orderRefundVo.getPayWay();
        //填充biz_content，请求参数
        Map<String, Object> bizContent = this.getRedundBizContent(payWay,orderRefundVo);
        //发送请求
        String url = "";
        if(DictEnum.PAY_WAY_ALI.getCode().equals(payWay)){
            //支付宝native支付
            url = cmbConfig.OPEN_API + "/aliPay/refund";
        }else if(DictEnum.PAY_WAY_WECHAT_MINI.getCode().equals(payWay)){
            //微信支付
            /**
             * {"returnCode":"SUCCESS",
             * "biz_content":"{\"refundDscAmt\":\"0\",
             * \"refundState\":\"P\",
             * \"cmbOrderId\":\"003221043012165571022098\",
             * \"orderId\":\"373898777456267264\",
             * \"txnTime\":\"20210430121655\",
             * \"thirdOrderId\":\"50101408142021043008388628688\",
             * \"merId\":\"308999107420080\",
             * \"refundAmt\":\"1\"}",
             * "encoding":"UTF-8","version":"0.0.1","respCode":"SUCCESS","signMethod":"02","sign":"MEYCIQDGZqV/41wtFivac0mh27ERYLY1x3g93YvKTHF67i9ZYgIhAJDrlynhfjJ2sgTmTwml0XpQ4WfVsTpV1nzvzM6p6oXw"}
             */
            url = cmbConfig.OPEN_API + "/weixinPay/refund";
        }else if(DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
            //一网通
            url = cmbConfig.OPEN_API + "/netPay/refund";

            /**
             * {"charset":"UTF-8","respMessage":"",
             * "sign":"MEQCIG86U1NGJoYLR0vim60egmp+FSAmjhK+SJtIdmJ7r2TXAiBPjLmfBVZLtZTy2c7dtkw2BEWtnEuMgeSqGoA4804tgw\u003d\u003d",
             * "signType":"01",
             * "respData":"
             * {\"dateTime\":\"20210430110131\",
             * \"bankDate\":\"20210430\",
             * \"settleMode\":\"A\",
             * \"bankSerialNo\":\"2260E09B0003F000000A\",
             * \"settleAmount\":\"59.25\",
             * \"refundRefNo\":\"\",
             * \"bankTime\":\"105728\",
             * \"discountAmount\":\"0.00\",
             * \"currency\":\"10\",
             * \"refundSerialNo\":\"373895019355688960\",
             * \"refundAmount\":\"59.25\"}",
             * "version":"1.0","respCode":"SUC0000"}
             */
        }

        //调用招商银行接口
        Map<String, String> respMap = this.postForEntity(payWay,url,signType,bizContent,orderRefundVo.getPayOrderNumber());
        return respMap;
    }

    /**
     * 查询退款是否成功
     * @param payWay 支付方式
     * @param payOrderNumber 支付单号
     * @param orderTime 支付订单时间
     * @param refundNumber 退款单号
     * @return
     */
    public Map<String, String> searchRefundSuccessFlag(String payWay,String payOrderNumber,Date orderTime,String refundNumber){
        //发送请求
        String url = "";
        if(DictEnum.PAY_WAY_ALI.getCode().equals(payWay)){
            //支付宝native支付
            url = cmbConfig.OPEN_API + "/aliPay/refundStatusQuery";
            /**
             * {"returnCode":"SUCCESS",
             * "biz_content":
             * "{\"refundDscAmt\":\"0\",
             * \"cmbOrderId\":\"004121042921041360663898\",
             * \"orderId\":\"5654645654222\",
             * \"endDate\":\"20210429\",
             * \"txnTime\":\"20210429210413\",
             * \"merId\":\"308999107420078\",
             * \"endTime\":\"210415\",
             * \"currencyCode\":\"156\",
             * \"tradeState\":\"S\",
             * \"refundAmt\":\"9\",
             * \"refundDetailItemList\":\"[{\\\"amount\\\":\\\"0.09\\\",\\\"fund_channel\\\":\\\"ALIPAYACCOUNT\\\"}]\"
             * }",
             * "encoding":"UTF-8",
             * "version":"0.0.1",
             * "respCode":"SUCCESS",
             * "signMethod":"02",
             * "sign":"MEUCICaxoBNQTx7dq/9QwrVV+GAOceen9uB7C1T2SmIzU181AiEAl55BGNeuTVIsRBgJQ9CmsBmR5HAIiAgRNWGjuRwz8eU\u003d"}
             */
        }else if(DictEnum.PAY_WAY_WECHAT_MINI.getCode().equals(payWay)){
            //微信支付
            url = cmbConfig.OPEN_API + "/weixinPay/refundStatusQuery";
            /**
             * 成功返回结果
             * {"returnCode":"SUCCESS",
             * "biz_content":"{\"refundDscAmt\":\"0\",
             * \"cmbOrderId\":\"003221043012165571022098\",
             * \"orderId\":\"373898777456267264\",
             * \"endDate\":\"20210430\",
             * \"thirdOrderId\":\"50101408142021043008388628688\",
             * \"txnTime\":\"20210430121655\",
             * \"merId\":\"308999107420080\",
             * \"endTime\":\"121700\",
             * \"currencyCode\":\"156\",
             * \"tradeState\":\"S\",\"refundAmt\":\"1\"}",
             * "encoding":"UTF-8",
             * "version":"0.0.1",
             * "respCode":"SUCCESS",
             * "signMethod":"02",
             * "sign":"MEQCIDS0NBpoxhycsdLiViBI/9l2K3vP4qipXTlEsIm+T9WHAiAEoeWDr3Gr6qZdxVw/+rRIggVpgWbgvG5kb3pqS3zt/A\u003d\u003d"}
             */
        }else if(DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
            //一网通
            url = cmbConfig.OPEN_API + "/netPay/refundStatusQuery";
            /**
             * 成功返回结果
             * {"charset":"UTF-8",
             * "respMessage":"",
             * "sign":"MEUCIDmlo3TiupkZqOARgUVOcEdPHenWm7t0moC7wZSdGhdQAiEA9+b7tJn9Rwh23i4bqRyGbtLPJznK4lyykuOjxvsTDNw\u003d",
             * "signType":"01",
             * "respData":"{\"dateTime\":\"20210430145221\",
             * \"date\":\"20210430\",
             * \"acceptDate\":\"20210430\",
             * \"orderNo\":\"373895019355688960\",
             * \"fee\":\"0\",\"settleAmount\":\"59.25\",
             * \"acceptTime\":\"105728\",
             * \"orderStatus\":\"210\",
             * \"description\":\"\",\"discountAmount\":\"0\",
             * \"subOrderList\":[{\"subRefundAmount\":\"59.25\",\"subOrderNo\":\"373882093907329024\",\"subMerchantNo\":\"30899910742007A\",\"subStoreId\":\"30899910742007A0001\",\"subRefundNo\":\"373895019355688960\"}],
             * \"bankSerialNo\":\"2260E09B0003F000000A\",
             * \"bankTime\":\"110131\",
             * \"currency\":\"10\",
             * \"merchantNo\":\"018103\",
             * \"refundAmount\":\"59.25\"}",
             * "version":"1.0","respCode":"SUC0000"}
             */
        }
        HashMap<String, Object> bizContent = new HashMap<>();//报文参数
        if(DictEnum.PAY_WAY_ALI.getCode().equals(payWay)){
            bizContent.put("orderId", refundNumber);//被查询交易的商户退款订单号
            bizContent.put("merId", cmbConfig.MER_ID);//招行商户号
            bizContent.put("userId", cmbConfig.USER_ID);//被查询交易的收银员信息
        }else if(DictEnum.PAY_WAY_WECHAT_MINI.getCode().equals(payWay)){
            bizContent.put("orderId", refundNumber);//被查询交易的商户退款订单号
            bizContent.put("merId", cmbConfig.WECHAT_MER_ID);//商户号
            bizContent.put("userId", cmbConfig.WECHAT_USER_ID);//被查询交易的收银员信息
        }else if(DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
            bizContent.put("dateTime", DateUtils.getSystemTime(DateUtils.DF_YMDHMS));// 请求时间（格式：yyyyMMddHHmmss）
            bizContent.put("merchantNo", cmbConfig.MER_ID);// 招行商户号,合单支付时必须填平台商户
            bizContent.put("date", DateUtils.getSystemTime(DateUtils.DF_YMD,orderTime));// 订单日期
            bizContent.put("orderNo", payOrderNumber);// 商户原单号
            bizContent.put("refundSerialNo", refundNumber);// 商户退款流水号

        }
        Map<String, String> respMap = this.postForEntity(payWay,url,signType,bizContent,payOrderNumber);
        return respMap;
    }

    /**
     * 关闭订单
     * @param payWay 支付方式
     * @param payOrderNumber 支付单号
     * @return
     */
    public Map<String, String> orderClose(String payWay, String payOrderNumber) {
        //填充biz_content，请求参数
        HashMap<String, Object> bizContent = new HashMap<>();
        if(DictEnum.PAY_WAY_ALI.getCode().equals(payWay)){
            bizContent.put("merId", cmbConfig.MER_ID);//商户号
            bizContent.put("userId", cmbConfig.USER_ID);//招行提供的收银员信息，每个收银员均会对应到一个门店下。若上送空值，则商户登录商户服务系统按照收银员、门店查询交易时查询不到此笔交易
            bizContent.put("origOrderId", payOrderNumber);// 原交易的母单商户订单号
        }else if(DictEnum.PAY_WAY_WECHAT_MINI.getCode().equals(payWay)){
            bizContent.put("merId", cmbConfig.WECHAT_MER_ID);//商户号
            bizContent.put("userId", cmbConfig.WECHAT_USER_ID);//收银员信息
            bizContent.put("origOrderId", payOrderNumber);// 原交易的母单商户订单号
        }else if(DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
            /**
             * 一网通没有关闭订单接口
             */
            Map<String, String> map = Maps.newHashMap();
            map.put("merId", cmbConfig.MER_ID);
            return map;
        }else{
            return null;
        }
        log.info("=======payOrderNumber="+payOrderNumber);
        //发送请求
        String url = "";
        if(DictEnum.PAY_WAY_ALI.getCode().equals(payWay)){
            //支付宝native支付
            url = cmbConfig.OPEN_API + "/aliPay/orderClose";
            /**
             * 返回结果
             * {"returnCode":"SUCCESS",
             * "biz_content":"
             * {\"origOrderId\":\"373994791936983040\",
             * \"txnTime\":\"20210430173234\",
             * \"merId\":\"308999107420078\",
             * \"closeState\":\"C\"}",
             * "encoding":"UTF-8","version":"0.0.1","respCode":"SUCCESS","signMethod":"02","sign":"MEQCIFjlnjNl3ux20b6DHg83mjfweJKo2ZcK1UFuY1LO6IjdAiAeSCQjl4dh7lr2ee5QtTdU4mYbwEqjDrdzcR4pKqTIDQ\u003d\u003d"}
             */
        }else if(DictEnum.PAY_WAY_WECHAT_MINI.getCode().equals(payWay)){
            //微信支付
            url = cmbConfig.OPEN_API + "/weixinPay/orderClose";
            /**
             * 返回结果
             * {"returnCode":"SUCCESS",
             * "biz_content":
             * "{\"origOrderId\":\"373994875621736448\",
             * \"txnTime\":\"20210430173945\",
             * \"merId\":\"308999107420080\",
             * \"closeState\":\"C\"}",
             * "encoding":"UTF-8","version":"0.0.1","respCode":"SUCCESS","signMethod":"02","sign":"MEYCIQD9Js912N/OdvMBzR8+g2eRprFmx6wlwa2NRQuxbM3JVgIhAMoONGEIK7Y2gP7xiFX1kcxaXanaBFa88fI8Qkvxva6J"}
             */
        }else if(DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
            //一网通
            url = cmbConfig.OPEN_API + "/netPay/orderClose";
        }

        //调用招商银行接口
        Map<String, String> respMap = this.postForEntity(payWay,url,signType,bizContent,payOrderNumber);
        return respMap;
    }

    /**
     * 支付-填充业务字段,biz_content
     * @param payWay 支付方式
     * @param payOrderVo 支付订单信息
     * @return
     */
    private Map<String, Object> genOrderBizContent(String payWay, PayOrderVo payOrderVo) {
        HashMap<String, Object> bizContent = new HashMap<>();
        List<OrderVo> orderList = payOrderVo.getOrderList();//订单集合
        String body = "流量套餐购买";//商品描述
        BigDecimal totalFee = payOrderVo.getRealCost();//支付订单交易总金额
        Double serviceFeePercentage = payOrderVo.getServiceFeePercentage().doubleValue();//服务费百分数
        String payOrderNumber = payOrderVo.getPayOrderNumber();//支付单号
        log.info("母单号：" + payOrderNumber);
        BigDecimal serviceFee = serviceFeePercentage == null ? null : totalFee.multiply(new BigDecimal(serviceFeePercentage.doubleValue() / 100)).setScale(2, BigDecimal.ROUND_HALF_UP);//服务费=实际付款 * 服务费百分比 todo bug,如果多个子单，则母单和子单的服务费重新算下
        if(DictEnum.PAY_WAY_ALI.getCode().equals(payWay)){
            bizContent.put("orderId", payOrderNumber);//商户订单号
            bizContent.put("body", body);// 商品描述
            bizContent.put("notifyUrl", cmbConfig.NOTIFY_URL_ALIPAY);//交易通知地址
            bizContent.put("txnAmt", AmountUtils.changeY2F(totalFee));//交易金额,单位为分
            bizContent.put("merId", cmbConfig.MER_ID);//商户号
            bizContent.put("userId", cmbConfig.USER_ID);//收银员信息
            bizContent.put("tradeScene", "OFFLINE");// 交易场景
            bizContent.put("buyerLogonId", "");//买家支付宝账号，和buyerLogonId不能同时为空
            bizContent.put("mchReserved",body);//支付结果通知时原样返回
            bizContent.put("serviceFee", AmountUtils.changeY2F(serviceFee));//服务费：单位分
            //填充子单
            List<Map> subOrderInfos = new ArrayList<>();
            for (OrderVo orderEntity:orderList) {
                Map<String, Object> subOrderInfo1 = new HashMap<>();
                subOrderInfo1.put("subMerId", orderEntity.getDealerMerId());
                String subOrderId = orderEntity.getOrderNumber();
                log.info("子单号：" + subOrderId);
                subOrderInfo1.put("subOrderId", subOrderId);
                subOrderInfo1.put("subStoreId", orderEntity.getDealerStoreId());
                subOrderInfo1.put("txnAmt", AmountUtils.changeY2F(orderEntity.getRealCost()));// 子单交易金额
                subOrderInfo1.put("subServiceFee",AmountUtils.changeY2F(serviceFee));
                subOrderInfos.add(subOrderInfo1);
            }
            bizContent.put("subOrderList", GsonUtils.objectToJson(subOrderInfos));
        }else if(DictEnum.PAY_WAY_WECHAT_MINI.getCode().equals(payWay)){
            bizContent.put("merId", cmbConfig.WECHAT_MER_ID);//商户号
            bizContent.put("orderId", payOrderNumber);//商户订单号
            bizContent.put("tradeType", "JSAPI");// 交易类型
            bizContent.put("tradeScene", "ONLINE");// 交易场景
            bizContent.put("userId", cmbConfig.WECHAT_USER_ID);//收银员信息
            bizContent.put("body", body);// 商品描述
            bizContent.put("mchReserved",body);//用于传输商户自定义数据，在支付结果查询和交易结果通知时原样返回
            bizContent.put("notifyUrl", cmbConfig.NOTIFY_URL_WECHAT);//交易通知地址
            bizContent.put("txnAmt", AmountUtils.changeY2F(totalFee));//交易金额
            bizContent.put("spbillCreateIp", ReqUtils.getHttpClientIp());// 终端IP
            //填充子单
            List<Map> subOrderInfos = new ArrayList<>();
            for (OrderVo orderEntity:orderList) {
                Map<String, Object> subOrderInfo1 = new HashMap<>();
                subOrderInfo1.put("subMerId", orderEntity.getDealerMerId());//子商户号
                String subOrderId = orderEntity.getOrderNumber();
                log.info("子单号：" + subOrderId);
                subOrderInfo1.put("subOrderId", subOrderId);
                subOrderInfo1.put("subStoreId", orderEntity.getDealerStoreId());//子单门店号
                subOrderInfo1.put("txnAmt", AmountUtils.changeY2F(orderEntity.getRealCost()));// 子单交易金额
                subOrderInfo1.put("subServiceFee",AmountUtils.changeY2F(serviceFee));
                subOrderInfos.add(subOrderInfo1);
            }
            Map<String, Object> combineInfo = new HashMap<>();
            combineInfo.put("subOrderList", GsonUtils.objectToJson(subOrderInfos));
            combineInfo.put("serviceFee",AmountUtils.changeY2F(serviceFee));//服务费：单位分
            bizContent.put("combineInfo", GsonUtils.objectToJson(combineInfo));// 合单信息
        }else if(DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
            bizContent.put("dateTime", DateUtils.getSystemTime(DateUtils.DF_YMDHMS));// 请求时间（格式：yyyyMMddHHmmss）
            bizContent.put("merchantNo", cmbConfig.MER_ID);// 招行商户号,合单支付时必须填平台商户
            bizContent.put("date", DateUtils.getSystemTime(DateUtils.DF_YMD));// 订单日期（格式：yyyyMMdd）
            bizContent.put("orderNo", payOrderNumber);// 商户订单号
            bizContent.put("billAmount", totalFee);// 订单金额
            bizContent.put("payNoticeUrl", cmbConfig.NOTIFY_URL_NETPAY);//
            bizContent.put("description", body);// 商品描述
            bizContent.put("payNoticePara",payWay);//支付结果通知时原样返回
            bizContent.put("serviceFee", serviceFee);//服务费：单位元
            //填充子单
            List<Map> subOrderInfos = new ArrayList<>();
            for (OrderVo orderEntity:orderList) {
                Map<String, Object> subOrderInfo1 = new HashMap<>();
                subOrderInfo1.put("subMerchantNo", cmbConfig.SUB_MER_ID);//子商户号
                String subOrderId = orderEntity.getOrderNumber();
                log.info("子单号：" + subOrderId);
                subOrderInfo1.put("subOrderNo", subOrderId);
                subOrderInfo1.put("subStoreId", cmbConfig.SUB_STORE_ID);//子单门店号
                subOrderInfo1.put("subBillAmount", orderEntity.getRealCost());// 子单交易金额
                subOrderInfo1.put("subServiceFee",serviceFee);//子单服务费，单位元
                subOrderInfos.add(subOrderInfo1);
            }
            bizContent.put("subOrderList", subOrderInfos.toArray());
        }

        return bizContent;
    }

    /**
     * 给招商银行发送请求
     * @param payWay 支付方式
     * @param signType 加签方式
     * @param bizContent 参数
     * @param payOrderNumber 支付母单号
     */
    private Map<String, String> postForEntity(String payWay,String url,String signType,Map<String, Object> bizContent,String payOrderNumber)  {
        log.info("请求地址："+url);
        HashMap<String, String> requestMap = new HashMap<>();
        //报文参数
        if(DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
            requestMap.put("version", cmbConfig.version_1);
            requestMap.put("charset", cmbConfig.encoding);
            requestMap.put("signType",signType );
            requestMap.put("reqData", GsonUtils.objectToJson(bizContent));
        }else {
            requestMap.put("version", cmbConfig.version_0_0_1);
            requestMap.put("encoding", cmbConfig.encoding);
            requestMap.put("signMethod", signType);
            requestMap.put("biz_content", GsonUtils.objectToJson(bizContent));
        }

        //对报文参数加签
        String signContent = SignatureUtil.getSignContent(requestMap);
        String sign = "";
        if (cmbConfig.signMethod_02.equals(signType)){
            //国密加签
            log.info("国密加签");
            sign = SM2Util.sm2Sign(signContent,cmbConfig.SM2_PRIVATE_KEY);
        }else{
            log.info("此商户不支持其他加签方式"+signType);
            return null;
        }
        requestMap.put("sign",sign);
        String reqestString = GsonUtils.objectToJson(requestMap);
        log.info("加签后的报文内容：" + reqestString);

        //对请求报文进行加密
        String timestamp = "" + System.currentTimeMillis()/1000;
        String appid = cmbConfig.appId;
        Map<String, String> apiEncMap = new HashMap<>();
        apiEncMap.put("appid", appid);
        apiEncMap.put("secret", cmbConfig.appSecret);
        apiEncMap.put("sign", sign);
        apiEncMap.put("timestamp", timestamp);
        String MD5Content = SignatureUtil.getSignContent(apiEncMap);//MD5加密
        String apiEncString = MD5Utils.getMD5Content(MD5Content).toLowerCase();

        //请求报文头
        Map<String, String> requstHeader = new HashMap<>();
        requstHeader.put("appid", appid);
        requstHeader.put("timestamp", timestamp);
        requstHeader.put("apisign", apiEncString);

        //发送请求
        Map<String, String> respMap = RestTemplateUtils.postForEntity(url, reqestString, requstHeader);

//        //添加log日志
//        OrderPayLogEntity orderPayLogEntity = new OrderPayLogEntity();
//        orderPayLogEntity.setPayOrderNumber(payOrderNumber);
//        orderPayLogEntity.setRequestUrl(url);
//        orderPayLogEntity.setRequestBody(reqestString);
//        orderPayLogEntity.setResponseCode(Objects.isNull(respMap) ? null : respMap.get("respCode"));
//        orderPayLogEntity.setResponseMessage(Objects.isNull(respMap) ? null : GsonUtils.objectToJson(respMap));
//        orderPayLogEntity.setApplicationName(applicationName);
//        this.orderPayLogService.save(orderPayLogEntity);

        return respMap;
    }

    public Map<String, Object> setPayResult(String payWay, Map<String, String> respMap) {
        Map<String,Object> resultMap = new HashMap<>();
        if(DictEnum.PAY_WAY_ALI.getCode().equals(payWay)){
            //支付宝native支付
            if ("SUCCESS".equals(respMap.get("respCode"))){
                JSONObject respData = JSONObject.parseObject(respMap.get("biz_content"));
                resultMap.put("qrCode",respData.get("qrCode"));
                resultMap.put("cmbOrderId",respData.get("cmbOrderId"));
                resultMap.put("merId", cmbConfig.MER_ID);//商户号
                resultMap.put("txnTime",respData.get("txnTime"));
            }
        }else if(DictEnum.PAY_WAY_WECHAT_MINI.getCode().equals(payWay)){
            //微信支付
            if ("SUCCESS".equals(respMap.get("respCode"))){
                JSONObject respData = JSONObject.parseObject(respMap.get("biz_content"));
                resultMap.put("encryptedTradeInfo",respData.get("encryptedTradeInfo"));
                resultMap.put("cmbOrderId",respData.get("cmbOrderId"));
                resultMap.put("orderId",respData.get("orderId"));
                resultMap.put("cmbMiniAppId",respData.get("cmbMiniAppId"));
                resultMap.put("txnTime",respData.get("txnTime"));
                resultMap.put("encryptedCmbOrderId",respData.get("encryptedCmbOrderId"));
                resultMap.put("merId", cmbConfig.WECHAT_MER_ID);//商户号
            }
        }else if(DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
            //一网通
            if ("SUC0000".equals(respMap.get("respCode").toString())){
                JSONObject respData = JSONObject.parseObject(respMap.get("respData"));
                resultMap.put("sign", respMap.get("sign"));
                String billInfo = UrlDeal.decodeURIComponent(respData.get("billInfo").toString());
                resultMap.put("billInfo",respData.get("billInfo").toString());
                String jsonRequestData = GsonUtils.str2Map(billInfo).get("jsonRequestData");
                resultMap.put("jsonRequestData",jsonRequestData);
                resultMap.put("appid",respData.get("appid"));
                resultMap.put("merId", cmbConfig.MER_ID);//商户号
            }
        }
        return resultMap;
    }

    /**
     * 退款-填充业务字段,biz_content
     * @param payWay 支付方式
     * @param orderRefundVo 退款订单信息
     * @return
     */
    private Map<String, Object> getRedundBizContent(String payWay, OrderRefundVo orderRefundVo) {
        HashMap<String, Object> bizContent = new HashMap<>();
        String subMerId = orderRefundVo.getDealerMerId();//子商户号
        String subStoreId = orderRefundVo.getDealerStoreId();//子单门店号
        BigDecimal realCost = orderRefundVo.getTotalRealCost();//支付订单交易总金额
        String payOrderNumber = orderRefundVo.getPayOrderNumber();//支付单号
        Date orderTime = orderRefundVo.getOrderTime();//母单下单时间
        BigDecimal refundServiceFee = orderRefundVo.getServiceFeePercentage() == null ? null : realCost.multiply(new BigDecimal(orderRefundVo.getServiceFeePercentage().doubleValue() / 100)).setScale(2, BigDecimal.ROUND_HALF_UP);//服务费=实际付款 * 服务费百分比
        String orderNumber = orderRefundVo.getOrderNumber();//子单号
        String refundNumber = orderRefundVo.getRefundNumber();//退款单号
        BigDecimal refundCost = orderRefundVo.getRefundCost();//退款金额
        log.info("母单号：" + payOrderNumber);
        log.info("子单号：" + orderNumber);
        log.info("退款订单号：" + refundNumber);
        if(DictEnum.PAY_WAY_ALI.getCode().equals(payWay)){
            bizContent.put("merId", cmbConfig.MER_ID);//商户号
            bizContent.put("orderId", refundNumber);//商户端生成退款订单号，要求此订单号母单商户下唯一
            bizContent.put("userId", cmbConfig.USER_ID);//招行提供的收银员信息，每个收银员均会对应到一个门店下。若上送空值，则商户登录商户服务系统按照收银员、门店查询交易时查询不到此笔交易
            bizContent.put("origOrderId", payOrderNumber);// 原交易的母单商户订单号
            bizContent.put("notifyUrl", cmbConfig.REFUND_NOTIFY_URL);//交易通知地址
            bizContent.put("txnAmt", AmountUtils.changeY2F(realCost));//交易金额
            bizContent.put("refundAmt", AmountUtils.changeY2F(refundCost));// 退款金额，单位为分
            bizContent.put("subMerId", subMerId);// 子单商户号
            bizContent.put("subStoreId", subStoreId);// 子单门店号
            bizContent.put("subOrigOrderId", orderNumber);// 子单原商户订单号
            bizContent.put("subOrderId", refundNumber);//子单商户退款订单号
            bizContent.put("refundServiceFee", AmountUtils.changeY2F(refundServiceFee));//服务费退款金额
            bizContent.put("refundReason","流量套餐退款");
            bizContent.put("mchReserved", "流量套餐");// 用于传输商户自定义数据，在退款结果查询和退款结果通知时原样返回
        }else if(DictEnum.PAY_WAY_WECHAT_MINI.getCode().equals(payWay)){
            bizContent.put("merId", cmbConfig.WECHAT_MER_ID);//商户号
            bizContent.put("orderId", refundNumber);//商户端生成退款订单号，要求此订单号母单商户下唯一
            bizContent.put("userId", cmbConfig.WECHAT_USER_ID);//收银员信息
            bizContent.put("origOrderId", payOrderNumber);// 原交易的母单商户订单号
            bizContent.put("notifyUrl", cmbConfig.REFUND_NOTIFY_URL);//若为空则通知到原交易的通知地址
            bizContent.put("txnAmt", AmountUtils.changeY2F(realCost));//原交易金额，单位为分(如果是合单交易填写合单交易的交易金额)
            bizContent.put("refundAmt", AmountUtils.changeY2F(refundCost));// 退款金额，单位为分
            bizContent.put("subMerId", subMerId);// 子单商户号
            bizContent.put("subStoreId", subStoreId);// 子单门店号
            bizContent.put("subOrigOrderId", orderNumber);// 子单原商户订单号
            bizContent.put("subOrderId", refundNumber);//子单商户退款订单号
            bizContent.put("refundServiceFee", AmountUtils.changeY2F(refundServiceFee));//服务费退款金额
            bizContent.put("refundReason","流量套餐退款");
            bizContent.put("mchReserved", "流量套餐");// 用于传输商户自定义数据，在退款结果查询和退款结果通知时原样返回
        }else if(DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
            bizContent.put("dateTime", DateUtils.getSystemTime(DateUtils.DF_YMDHMS));// 请求时间（格式：yyyyMMddHHmmss）
            bizContent.put("merchantNo", cmbConfig.MER_ID);// 招行商户号,合单支付时必须填平台商户
            bizContent.put("date", DateUtils.getSystemTime(DateUtils.DF_YMD,orderTime));// 原支付交易订单日期（格式：yyyyMMdd）
            bizContent.put("orderNo", payOrderNumber);// 原支付交易订单号
            bizContent.put("refundSerialNo", refundNumber);// 退款流水号,商户生成，保证该流水在退款与支付时都是唯一。
            bizContent.put("refundAmount", refundCost);// 退款金额
            bizContent.put("refundServiceFee", refundServiceFee);//服务费退款金额
            //填充子单
            List<Map> subOrderInfos = new ArrayList<>();//当原订单为合单支付时才允许填以下信息，暂时仅支持一笔子单
            Map<String, Object> subOrderInfo = new HashMap<>();
            subOrderInfo.put("subMerchantNo", cmbConfig.SUB_MER_ID);//子商户号
            subOrderInfo.put("subOrderNo", orderNumber);//商户订单号，在该子单商户下唯一
            subOrderInfo.put("subStoreId", cmbConfig.SUB_STORE_ID);//子单门店号
            subOrderInfo.put("subRefundAmount", realCost);// 该子单交易金额，单位是元
            subOrderInfos.add(subOrderInfo);
            bizContent.put("subOrderList", subOrderInfos.toArray());
        }
        return bizContent;
    }
}
