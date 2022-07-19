package com.nut.driver.app.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.dao.OrderDao;
import com.nut.driver.app.dao.PayOrderDao;
import com.nut.driver.app.entity.OrderEntity;
import com.nut.driver.app.entity.PayOrderEntity;
import com.nut.driver.app.form.BizContentForm;
import com.nut.driver.app.form.PayForm;
import com.nut.driver.app.service.PayOrderService;
import com.nut.driver.app.vo.PayOrderVo;
import com.nut.driver.common.component.CMBComponent;
import com.nut.driver.common.config.CMBConfig;
import com.nut.driver.common.config.SimFlowConfig;
import com.nut.driver.common.em.DictEnum;
import com.nut.driver.common.utils.DateUtils;
import com.nut.driver.common.utils.GsonUtils;
import com.nut.driver.common.utils.HttpUtil;
import com.nut.driver.common.utils.UUIDUtils;
import com.nut.driver.common.utils.cmb.AmountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service("payOrderService")
public class PayOrderServiceImpl extends ServiceImpl<PayOrderDao, PayOrderEntity> implements PayOrderService {
    @Resource
    private CMBComponent cmbComponent;
    @Resource
    private CMBConfig cmbConfig;
    @Resource
    private OrderDao orderDao;
    @Resource
    private SimFlowConfig simFlowConfig;
    @Value("${spring.profiles.active}")
    private String environment;
    @Value("${simservicerootpath:http://jacsimflow-test-api.syuntu.com}")
    private String simservicerootpath;


    /**
     * 根据支付方式设置总商户号
     * @param payWay 支付方式
     * @param payOrderEntity 支付单总表
     * @return
     */
    private void setMerId(String payWay,PayOrderEntity payOrderEntity) {
        if (DictEnum.PAY_WAY_ALI.getCode().equals(payWay)){
            //支付宝
            payOrderEntity.setMerId(cmbConfig.MER_ID);
            payOrderEntity.setMerUserId(cmbConfig.USER_ID);
        }else if (DictEnum.PAY_WAY_WECHAT_MINI.getCode().equals(payWay)){
            //微信
            payOrderEntity.setMerId(cmbConfig.WECHAT_MER_ID);
            payOrderEntity.setMerUserId(cmbConfig.WECHAT_USER_ID);
        }
    }

    /**
     * 根据支付方式设置经营商户号
     * @param payWay 支付方式
     * @param orderEntity 订单表
     * @return
     */
    private void setDealerMerId(String payWay,OrderEntity orderEntity) {
        String subMerId ;//经营商户号
        String subStoreId ;//经营商户门店号
        if (DictEnum.PAY_WAY_ALI.getCode().equals(payWay)){
            subMerId = cmbConfig.SUB_MER_ID;
            subStoreId = cmbConfig.SUB_STORE_ID;
        }else{
            subMerId = cmbConfig.WECHAT_SUB_MER_ID;
            subStoreId = cmbConfig.WECHAT_SUB_STORE_ID;
        }
        orderEntity.setDealerMerId(subMerId);
        orderEntity.setDealerStoreId(subStoreId);
    }

    @Transactional(rollbackFor=Exception.class)
    @Override
    public HttpCommandResultWithData toPay(PayForm payForm){
        String message = "远程调用招商银行接口失败";
        try {
            String payWay = payForm.getPayWay();//支付方式
            String payOrderNumber = payForm.getPayOrderNumber();//支付单号

            //校验参数
            if(!DictEnum.PAY_WAY_ALI.getCode().equals(payWay) && !DictEnum.PAY_WAY_WECHAT_MINI.getCode().equals(payWay) && !DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
                return Result.result(ECode.BUSINESS_FAILURE.code(),"不支持此支付方式");
            }

            /**1、查询支付单是否存在*/
            //查询支付订单表的支付方式是否为空，如果空说明并没有选择支付方式调预支付下招商单子
            LambdaQueryWrapper<PayOrderEntity> queryWrapperPay = new LambdaQueryWrapper<>();
            queryWrapperPay.eq(PayOrderEntity::getPayOrderNumber,payOrderNumber);
            PayOrderEntity payOrderEntity = this.baseMapper.selectOne(queryWrapperPay);
            if (Objects.isNull(payOrderEntity)){
                return Result.result(ECode.BUSINESS_FAILURE.code(),"此支付单不存在，请刷新重试");
            }
            //此单的旧的支付方式
            String oldPayWay = payOrderEntity.getPayWay();
            //此单的旧的查询支付结果次数
            int searachPayCount = payOrderEntity.getSearchPayCount();
            //服务费百分比
            Integer serviceFeePercentage = payOrderEntity.getServiceFeePercentage();
            //平台商户号
            String merId = payOrderEntity.getMerId();
            //平台收银员号
            String merUserId = payOrderEntity.getMerUserId();

            /**2、查询订单是否存在*/
            LambdaQueryWrapper<OrderEntity> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(OrderEntity::getPayOrderNumber,payOrderNumber);
            OrderEntity orderEntity = this.orderDao.selectOne(queryWrapper2);
            String orderNumber = orderEntity.getOrderNumber();////订单号
            if (Objects.isNull(orderEntity)){
                return Result.result(ECode.BUSINESS_FAILURE.code(),"订单不存在，请刷新列表重试");
            }
            if (DictEnum.ORDER_STATUS_CANCEL.getCode().equals(orderEntity.getOrderStatus())){
                return Result.result(ECode.BUSINESS_FAILURE.code(),"订单已取消，不可支付");
            }

            /**3、查询此支付单是否已支付*/
            if (!StringUtils.isEmpty(payOrderEntity.getBankTradeState())){
                //查询支付是否成功
                Map<String, String> respMap = this.cmbComponent.searchPaySuccessFlag(oldPayWay,payOrderNumber,payOrderEntity.getOrderTime());
                if(Objects.isNull(respMap)){
                    return Result.result(ECode.BUSINESS_FAILURE.code(),"查询失败");
                }
                //判断响应结果
                if(DictEnum.PAY_WAY_ALI.getCode().equals(payWay) || DictEnum.PAY_WAY_WECHAT_MINI.getCode().equals(payWay)){
                    //支付宝native支付 / 微信支付
                    if ("SUCCESS".equals(respMap.get("returnCode")) && "SUCCESS".equals(respMap.get("respCode"))){
                        //解析招商返回的结果
                        BizContentForm bizContentForm = GsonUtils.jsonToObject(respMap.get("biz_content"), BizContentForm.class);
                        //交易状态：C - 订单已关闭 D - 交易已撤销 P - 交易在进行 F - 交易失败 S - 交易成功 R - 转入退款
                        String tradeState = bizContentForm.getTradeState();
                        if (CMBConfig.ALIPAY_PAY_STATUS_SUCCESS.equals(tradeState)){
                            //支付成功
                            return Result.result(ECode.BUSINESS_FAILURE.code(),"此单已支付成功，请刷新重试。");
                        }else if ("R".equals(tradeState)){
                            return Result.result(ECode.BUSINESS_FAILURE.code(),"此单已转入退款不可支付，请刷新重试。");
                        }else if ("C".equals(tradeState)){
                            return Result.result(ECode.BUSINESS_FAILURE.code(),"订单已关闭，请刷新重试。");
                        }else if ("D".equals(tradeState)){
                            return Result.result(ECode.BUSINESS_FAILURE.code(),"交易已撤销，请刷新重试。");
                        }else if ("F".equals(tradeState)){
                            return Result.result(ECode.BUSINESS_FAILURE.code(),"交易失败，请重新下单。");
                        }
                    }
                }
            }

            /**4、若同单号切换支付方式*/
            //若旧数据已有支付方式并且新的请求更换了支付方式，则需要改变单号
            if (!StringUtils.isEmpty(oldPayWay) && !payForm.getPayWay().equals(oldPayWay)){
                //之前的单子立刻关闭
                Map<String, String> resultMap = this.cmbComponent.orderClose(oldPayWay,payOrderNumber);
                if(Objects.isNull(resultMap)){
                    return Result.result(ECode.BUSINESS_FAILURE.code(),"关闭失败");
                }
                if(!"SUCCESS".equals(resultMap.get("respCode")) && !"SUC0000".equals(resultMap.get("respCode"))){
                    if("ORDER_PAID".equals(resultMap.get("errCode"))){
                        return Result.result(ECode.BUSINESS_FAILURE.code(),"此单已支付，请刷新页面重新下单");
                    } else if ("CMBORDERID_NOT_EXIST".equals(resultMap.get("errCode"))){
                        //todo 此判断原因：下单失败，关闭则不存在此单数据，但却还能支付成功，银行并未答复此情况
                        return Result.result(ECode.BUSINESS_FAILURE.code(),"银行系统下单异常，请重新下单购买");
                    }
                }
                //订单重新生成一个支付单号（目的：支付订单和订单表是一对多的关系，当未支付时，每个待付款订单会作为独立的支付单，此时需要重新生成支付单号）
                String payOrderNumber_new = UUIDUtils.getUuid(UUIDUtils.UUID_FLAG_PAY_ORDER_ID);//新的支付单号

                //保存新的支付订单
                PayOrderEntity payOrderEntity_new = new PayOrderEntity();
                payOrderEntity_new.setPayWay(payWay);
                payOrderEntity_new.setPayOrderNumber(payOrderNumber_new);
                payOrderEntity_new.setUserId(payForm.getAutoIncreaseId());
                payOrderEntity_new.setUcId(payForm.getUserId());
                payOrderEntity_new.setPrimeCost(payOrderEntity.getPrimeCost());
                payOrderEntity_new.setDiscountCost(payOrderEntity.getDiscountCost());
                payOrderEntity_new.setRealCost(payOrderEntity.getRealCost());
                payOrderEntity_new.setOrderTime(new Date());
                payOrderEntity_new.setServiceFeePercentage(serviceFeePercentage);
                payOrderEntity_new.setMerUserId(merId);
                payOrderEntity_new.setMerUserId(merUserId);
                BigDecimal totalFee = orderEntity.getRealCost();//支付订单交易总金额
                BigDecimal serviceFee = serviceFeePercentage == null ? null : totalFee.multiply(new BigDecimal(serviceFeePercentage.doubleValue() / 100)).setScale(2, BigDecimal.ROUND_HALF_UP);//服务费=实际付款 * 服务费百分比 todo bug,如果多个子单，则母单和子单的服务费重新算下
                payOrderEntity_new.setServiceFee(serviceFee);
                baseMapper.insert(payOrderEntity_new);

                /**
                 * 要求换新的订单号和支付单号
                 * 原因：收款码接口不支持幂等，每次支付订单号要唯一的。
                 */
                String orderNumber_new = UUIDUtils.getUuid(UUIDUtils.UUID_FLAG_ORDER_ID);//新的订单号

                //保存新的订单
                OrderEntity orderEntity_new = BeanUtil.copyProperties(orderEntity,OrderEntity.class);
                orderEntity_new.setOrderNumber(orderNumber_new);
                orderEntity_new.setPayOrderNumber(payOrderNumber_new);
                orderEntity_new.setCreateTime(new Date());
                orderDao.insert(orderEntity_new);
                //删除原来的订单表
                this.orderDao.delete(queryWrapper2);
                //更新单号
                orderNumber = orderNumber_new;
                //删除原来的支付单表
                this.baseMapper.delete(new LambdaQueryWrapper<PayOrderEntity>().eq(PayOrderEntity::getPayOrderNumber,payOrderNumber));
                //新的支付单号
                payOrderNumber = payOrderNumber_new;
                //新的支付单查询支付结果次数
                searachPayCount = 0;
            }

            /**5、更新订单表的经营商户信息*/
            OrderEntity orderEntityUpdate = new OrderEntity();
            this.setDealerMerId(payWay,orderEntityUpdate);
            this.orderDao.update(orderEntityUpdate,new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderNumber,orderNumber));

            /**6、预支付*/
            //查询支付订单信息
            PayOrderVo payOrderVo = this.baseMapper.getPayOrder(payOrderNumber);
            payOrderVo.setPayWay(payWay);//设置新的支付方式

            //调用招商银行支付接口
            Map<String, String> respMap = this.cmbComponent.toPay(payWay,payOrderVo);
            if(Objects.isNull(respMap) ){
                throw new Exception();
            }
            if(!"SUCCESS".equals(respMap.get("respCode")) && !"SUC0000".equals(respMap.get("respCode"))){
                message = respMap.get("respMsg")+respMap.get("respMessage");
                throw new Exception();
            }

            //设置返回结果给前端
            Map<String,Object> resultMap = this.cmbComponent.setPayResult(payWay,respMap);
            resultMap.put("payOrderNumber",payOrderVo.getPayOrderNumber());
            resultMap.put("orderNumber",orderNumber);
            resultMap.put("realCost", payOrderEntity.getRealCost());

            /**7、更新平台商户信息*/
            //初始化交易状态
            String bankTradeState ;
            if (DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
                bankTradeState = CMBConfig.NET_PAY_STATUS_ONGING;
            }else{
                bankTradeState = CMBConfig.ALIPAY_PAY_STATUS_ONGING;
            }

            //获取招商的流水号和下单时间（注意：一网通没有此信息）
            String bankOrderNumber= null;//招商流水号
            Date orderTime = new Date() ;//下单时间
            if(!DictEnum.PAY_WAY_NET.getCode().equals(payWay)){
                BizContentForm bizContentForm = GsonUtils.jsonToObject(GsonUtils.objectToJson(resultMap), BizContentForm.class);
                bankOrderNumber = bizContentForm.getCmbOrderId();
                orderTime = DateUtils.formatString(bizContentForm.getTxnTime());
            }

            //判断查询次数：原因jobs中查询支付成功结果只查15次，关闭订单却是24小时之后，所以会出现查询15次后再次支付后，却没有更改支付状态的情况。所以当查询次数大于13时，次数为0,可再次查询
            if (searachPayCount >= 15){
                searachPayCount = 0;
            }

            //更新支付单表
            PayOrderEntity payOrderEntityUpdate = new PayOrderEntity();
            payOrderEntityUpdate.setPayWay(payWay);
            payOrderEntityUpdate.setBankOrderNumber(bankOrderNumber);
            payOrderEntityUpdate.setBankTradeState(bankTradeState);
            payOrderEntityUpdate.setOrderTime(orderTime);
            payOrderEntityUpdate.setSearchPayCount(searachPayCount);
            this.setMerId(payWay,payOrderEntityUpdate);
            LambdaQueryWrapper<PayOrderEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PayOrderEntity::getPayOrderNumber, payOrderNumber);
            this.baseMapper.update(payOrderEntityUpdate, queryWrapper);

            return Result.ok(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return Result.result(ECode.BUSINESS_FAILURE.code(),message);
    }

    @Transactional(rollbackFor=Exception.class)
    @Override
    public HttpCommandResultWithData searchPaySuccessFlag(String payOrderNumber) {
        try {
            log.info("===============支付结果查询 start===========：");
            //查询支付单的下单时间
            PayOrderEntity payOrderEntity = this.baseMapper.selectOne(new LambdaQueryWrapper<PayOrderEntity>().eq(PayOrderEntity::getPayOrderNumber,payOrderNumber));
            if (Objects.isNull(payOrderEntity)){
                return Result.result(ECode.BUSINESS_FAILURE.code(),"此支付单已不存在");
            }
            //支付方式
            String payWay = payOrderEntity.getPayWay();
            //查询支付是否成功
            Map<String, String> respMap = this.cmbComponent.searchPaySuccessFlag(payWay,payOrderNumber,payOrderEntity.getOrderTime());
            if(Objects.isNull(respMap)){
                return Result.result(ECode.BUSINESS_FAILURE.code(),"查询失败");
            }
            if(!"SUCCESS".equals(respMap.get("respCode")) && !"SUC0000".equals(respMap.get("respCode"))){
                return Result.result(ECode.BUSINESS_FAILURE.code(),"查询失败："+respMap.get("respMsg")+respMap.get("respMessage"));
            }

            boolean successFlag = false;//支付是否成功
            String bankTradeState = payOrderEntity.getBankTradeState();//数据库存的交易状态
            PayOrderEntity payOrderEntityUpdate = new PayOrderEntity();//设置支付订单表

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
                        //根据支付单号修改支付订单表
                        LambdaQueryWrapper<PayOrderEntity> queryWrapperPayOrder = new LambdaQueryWrapper<>();
                        queryWrapperPayOrder.eq(PayOrderEntity::getPayOrderNumber,payOrderNumber);
                        this.baseMapper.update(payOrderEntityUpdate,queryWrapperPayOrder);
                    }
                }
            }

            //若交易成功,则更新订单状态为待发货,并远程调用PMS-销售订单新增
            if (!successFlag){
                return Result.result(ECode.BUSINESS_FAILURE.code(),"未支付成功");
            }

            //根据支付单号查询订单信息
            OrderEntity orderEntity = this.orderDao.selectOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getPayOrderNumber,payOrderNumber));
            if (ObjectUtil.isNull(orderEntity)){
                return Result.result(ECode.BUSINESS_FAILURE.code(),"订单不存在");
            }

            //若订单是“待付款”，则修改订单表中的订单状态为“运营商处理中”
            if (DictEnum.ORDER_STATUS_WAIT_PAY.getCode().equals(orderEntity.getOrderStatus())){
                //修改订单表中的订单状态为“运营商处理中
                OrderEntity orderEntityUpdate = new OrderEntity();
                orderEntityUpdate.setOrderStatus(DictEnum.ORDER_STATUS_WAIT_RECEIVE.getCode());
                LambdaQueryWrapper<OrderEntity> queryWrapper = new LambdaQueryWrapper();
                queryWrapper.eq(OrderEntity::getOrderNumber, orderEntity.getOrderNumber());
                this.orderDao.update(orderEntityUpdate,queryWrapper);
            }

            log.info("===============支付结果查询 end===========：");
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return Result.result(ECode.BUSINESS_FAILURE.code(),"未支付成功");
    }

    @Override
    public HttpCommandResultWithData payCallback(String payWay, BizContentForm bizContentForm) {

        /**
         * todo 为什么用定时任务去查是否支付成功来处理业务逻辑，而不是在回调中处理?原因是因为银行的测试环境支付回调总不好使，耽误开发进度！！！
         *
         * 修改查询次数为1 todo 原因是用户可能不在app界面支付，且查询支付次数已为15次，需要重新查询
         */
        LambdaQueryWrapper<PayOrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayOrderEntity::getPayOrderNumber,bizContentForm.getOrderId());
        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setSearchPayCount(1);
        this.baseMapper.update(payOrderEntity,queryWrapper);

        return Result.ok();
    }

    @Override
    public HttpCommandResultWithData refundCallback(BizContentForm bizContentForm) {
        return Result.ok();
    }
}