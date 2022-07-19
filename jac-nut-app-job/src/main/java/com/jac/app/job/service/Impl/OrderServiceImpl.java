package com.jac.app.job.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jac.app.job.client.ToolsClient;
import com.jac.app.job.common.Result;
import com.jac.app.job.common.component.CMBComponent;
import com.jac.app.job.common.config.CMBConfig;
import com.jac.app.job.common.config.SimFlowConfig;
import com.jac.app.job.common.em.DictEnum;
import com.jac.app.job.constant.IxinPushStaticLocarVal;
import com.jac.app.job.domain.Car;
import com.jac.app.job.entity.MaintainSendEntity;
import com.jac.app.job.entity.OrderEntity;
import com.jac.app.job.entity.PayOrderEntity;
import com.jac.app.job.form.BizContentForm;
import com.jac.app.job.mapper.CarMapper;
import com.jac.app.job.mapper.OrderMapper;
import com.jac.app.job.mapper.PayOrderMapper;
import com.jac.app.job.service.CustomMaintainService;
import com.jac.app.job.service.OrderService;
import com.jac.app.job.util.DateUtils;
import com.jac.app.job.util.GsonUtils;
import com.jac.app.job.util.HttpUtil;
import com.jac.app.job.util.cmb.AmountUtils;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {
    @Resource
    private PayOrderMapper payOrderDao;
    @Resource
    private CMBComponent cmbComponent;
    @Resource
    private SimFlowConfig simFlowConfig;
    @Autowired
    private ToolsClient toolsClient;
    @Autowired
    private CarMapper carMapper;
    @Autowired
    CustomMaintainService customMaintainService;

    @Transactional(rollbackFor=Exception.class)
    @Override
    public Result searchPaySuccessFlag() {
        try {
            //查询银行交易状态为待支付的订单
            LambdaQueryWrapper<PayOrderEntity> queryWrapperPay = new LambdaQueryWrapper<>();
            queryWrapperPay.in(PayOrderEntity::getBankTradeState,CMBConfig.NET_PAY_STATUS_ONGING,CMBConfig.ALIPAY_PAY_STATUS_ONGING);
            queryWrapperPay.le(PayOrderEntity::getSearchPayCount,14);
            List<PayOrderEntity> list = this.payOrderDao.selectList(queryWrapperPay);
            String payOrderNumber;//支付单号
            String payWay;//支付方式
            for (PayOrderEntity payOrderEntity:list) {
                payOrderNumber = payOrderEntity.getPayOrderNumber();
                payWay = payOrderEntity.getPayWay();
                //查询支付是否成功
                Map<String, String> respMap = this.cmbComponent.searchPaySuccessFlag(payWay,payOrderNumber,payOrderEntity.getOrderTime());
                XxlJobLogger.log("[调用招商银行查询支付结果,end,respMap={}]",respMap);
                if(Objects.isNull(respMap)){
                    continue;
                }
                PayOrderEntity payOrderEntityUpdate = new PayOrderEntity();//设置支付订单表
                payOrderEntityUpdate.setSearchPayCount(payOrderEntity.getSearchPayCount() + 1);//设置支付订单表：查询次数
                if(!"SUCCESS".equals(respMap.get("respCode")) && !"SUC0000".equals(respMap.get("respCode"))){
//                    payOrderEntityUpdate.setBankTradeState("C");//设置支付订单表：银行交易状态
                }

                boolean successFlag = false;//支付是否成功
                String bankTradeState = payOrderEntity.getBankTradeState();//数据库存的交易状态

                //判断响应结果
                //支付宝native支付 / 微信支付
                if ("SUCCESS".equals(respMap.get("returnCode")) && "SUCCESS".equals(respMap.get("respCode"))){
                    //解析招商返回的结果
                    BizContentForm bizContentForm = GsonUtils.jsonToObject(respMap.get("biz_content"), BizContentForm.class);
                    //交易状态：C - 订单已关闭 D - 交易已撤销 P - 交易在进行 F - 交易失败 S - 交易成功 R - 转入退款
                    String tradeState = bizContentForm.getTradeState();
                    if (CMBConfig.ALIPAY_PAY_STATUS_SUCCESS.equals(tradeState)){
                        //支付成功
                        successFlag = true;
                        //若数据库存的交易状态是P/null,且银行返回的查询结果为S,则支付交易成功。
                        if((CMBConfig.ALIPAY_PAY_STATUS_ONGING.equals(bankTradeState) || StringUtils.isEmpty(bankTradeState))){
                            //交易金额
                            BigDecimal txnAmt = AmountUtils.changeF2Y(Integer.parseInt(bizContentForm.getTxnAmt()));
                            //设置支付订单表：银行交易状态
                            payOrderEntityUpdate.setBankTradeState(tradeState);
                            //设置支付订单表：银行流水单号
                            payOrderEntityUpdate.setBankOrderNumber(bizContentForm.getCmbOrderId());
                            //设置支付订单表：下单时间
                            payOrderEntityUpdate.setOrderTime(DateUtils.formatString(bizContentForm.getTxnTime()));
                            //设置支付订单表：支付时间
                            payOrderEntityUpdate.setPayTime(DateUtils.formatString(bizContentForm.getEndDate()+bizContentForm.getEndTime()));
                        }
                    }
                }

                //根据支付单号修改支付订单表
                LambdaQueryWrapper<PayOrderEntity> queryWrapperPayOrder = new LambdaQueryWrapper<>();
                queryWrapperPayOrder.eq(PayOrderEntity::getPayOrderNumber,payOrderNumber);
                this.payOrderDao.update(payOrderEntityUpdate,queryWrapperPayOrder);

                //若交易成功,则更新订单状态为待发货,并远程调用PMS-销售订单新增
                if (!successFlag){
                    continue;
                }

                //根据支付单号查询订单信息
                OrderEntity orderEntity = this.baseMapper.selectOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getPayOrderNumber,payOrderNumber));
                if (ObjectUtil.isNull(orderEntity)){
                    return Result.result(500,"订单不存在");
                }

                //若订单是“待付款”，则修改订单表中的订单状态为“运营商处理中”,且提交套餐订单
                if (DictEnum.ORDER_STATUS_WAIT_PAY.getCode().equals(orderEntity.getOrderStatus())){
                    //提交套餐订单
                    Map<String,Object> mappa =new HashMap<String,Object>();
                    mappa.put("iotCardNumber",orderEntity.getSimNum());
                    mappa.put("iotSetMealId",orderEntity.getPacId());
                    String pa = JSON.toJSONString(mappa);
                    //获取返回值
                    Map<String, Object> jsonObject = HttpUtil.postForEntity(simFlowConfig.simservicerootpath+ "/api/nsc/simflow/iotDataUsage/orderPlaceByCardNumber" , pa, simFlowConfig.getMap());
                    String code = Objects.isNull(jsonObject.get("code")) ? "" : jsonObject.get("code").toString();
                    if(null == jsonObject.get("data") || !"10000".equals(code)){
                        continue;
                    }

                    //修改订单表中的订单状态为“运营商处理中
                    OrderEntity orderEntityUpdate = new OrderEntity();
                    orderEntityUpdate.setOrderStatus(DictEnum.ORDER_STATUS_WAIT_RECEIVE.getCode());
                    LambdaQueryWrapper<OrderEntity> queryWrapper = new LambdaQueryWrapper();
                    queryWrapper.eq(OrderEntity::getOrderNumber, orderEntity.getOrderNumber());
                    this.baseMapper.update(orderEntityUpdate,queryWrapper);
                }

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return Result.ok(null);
    }

    @Transactional(rollbackFor=Exception.class)
    @Override
    public Result searchSimOrderFlag() {
        try {
            //查询运营商处理中且未远程下套餐单的订单
            LambdaQueryWrapper<OrderEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderEntity::getOrderStatus,DictEnum.ORDER_STATUS_WAIT_RECEIVE.getCode());
            List<OrderEntity> list = this.baseMapper.selectList(queryWrapper);
            for (OrderEntity orderEntity:list) {
                //1、提交套餐订单
                Map<String,Object> mappa =new HashMap<String,Object>();
                mappa.put("iotCardNumber",orderEntity.getSimNum());
                mappa.put("iotSetMealId",orderEntity.getPacId());
                String pa = JSON.toJSONString(mappa);
                //获取返回值
                Map<String, Object> result = HttpUtil.postForEntity(simFlowConfig.simservicerootpath+ "/api/nsc/simflow/iotDataUsage/orderPlaceByCardNumber" , pa, simFlowConfig.getMap());
                String code = Objects.isNull(result.get("code")) ? "" : result.get("code").toString();
                if(null == result.get("data") || !"10000".equals(code)){
                    continue;
                }

                //2、推送消息
                //查询车牌号
                Car car = carMapper.selectCarBySimNum(orderEntity.getSimNum());
                String carNumber = Objects.isNull(car) ? "": car.getCarNumber();
                //消息内容
                String content = "您好，您已成功订购"+orderEntity.getPaName()+"业务。";
                //消息标题
                String title = "【"+carNumber+"】流量订购提醒";
                //用户ucId
                String userId = orderEntity.getUcId();
                //是否已推送消息
                Integer pushFlag = null;

                //推送消息
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", userId);
                jsonObject.put("content", content);
                jsonObject.put("title", title);
                Result jsonObjectResult = toolsClient.JPushMes(jsonObject);
                log.info("推送消息回调:{}",jsonObjectResult);
                log.info("消息推送开始,推送内容{}", content);
                if (jsonObjectResult.getResultCode() == 200){
                    pushFlag = 1;
                    //将消息存储到usermessage中
                    MaintainSendEntity maintain = new MaintainSendEntity();
                    maintain.setContent(content);
                    maintain.setTitle(title);
                    maintain.setUserId(userId);
                    maintain.setMessageType(Integer.parseInt(IxinPushStaticLocarVal.MESSAGE_TYPE_SIMFLOW));
                    maintain.setMessageTypeName(IxinPushStaticLocarVal.MESSAGE_TYPE_NAME_SIMFLOW);
                    customMaintainService.addMessage(maintain);
                }

                //3、修改订单表中的订单状态为“已完成、是否已推送消息
                OrderEntity orderEntityUpdate = new OrderEntity();
                orderEntityUpdate.setOrderStatus(DictEnum.ORDER_STATUS_WAIT_COMPLETE.getCode());
                orderEntityUpdate.setPushFlag(pushFlag);
                LambdaQueryWrapper<OrderEntity> queryWrapperUpdate = new LambdaQueryWrapper();
                queryWrapperUpdate.eq(OrderEntity::getOrderNumber, orderEntity.getOrderNumber());
                this.baseMapper.update(orderEntityUpdate,queryWrapperUpdate);

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return Result.ok(null);
    }
}
