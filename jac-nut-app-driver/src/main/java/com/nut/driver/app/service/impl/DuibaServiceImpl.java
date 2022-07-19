package com.nut.driver.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nut.driver.app.dao.DuibaDao;
import com.nut.driver.app.dao.IntegralDao;
import com.nut.driver.app.dao.UserDao;
import com.nut.driver.app.domain.IntegralAlterRecord;
import com.nut.driver.app.domain.IntegralConsumeConfirmInfo;
import com.nut.driver.app.domain.IntegralConsumeInfo;
import com.nut.driver.app.dto.IntegralDetailDto;
import com.nut.driver.app.entity.Integral;
import com.nut.driver.app.entity.IntegralEntity;
import com.nut.driver.app.form.IntegralOperationForm;
import com.nut.driver.app.form.QueryIntegralForm;
import com.nut.driver.app.service.DuibaService;
import com.nut.driver.app.service.IntegralService;
import com.nut.driver.common.sdk.VirtualCardConsumeParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
@Service("duibaService")
public class DuibaServiceImpl implements DuibaService {

    /**
     * 积分发放段 0消费 1获取
     **/
    private static final String CONSUME_MARK = "0";
    private static final String INCREASE_MARK = "1";

    /**
     * 积分发放段 2运营 3服务
     **/
    private static final String INTEGRAL_RESOURCE_BUSINESS = "2";
    private static final String INTEGRAL_RESOURCE_APP = "3";

    private static final String[] integralConsumeInfoKeys = {"actualPrice", "alipay", "appKey", "appSecret", "credits", "description", "facePrice", "ip", "itemCode", "orderNum", "params", "phone", "qq", "sign", "timestamp", "type", "uid", "waitAudit"};
    private static final String[] IntegralConsumeConfirmInfoKeys = {"appKey", "appSecret", "bizId", "errorMessage", "orderNum", "sign", "success", "timestamp", "uid"};

    @Value("${duiba_appKey}")
    private String appKey;

    @Value("${duiba_appSecret}")
    private String appSecret;

    @Autowired
    private DuibaDao duibaMapper;

    @Autowired
    private IntegralService integralService;

    @Autowired
    private IntegralDao integralMapper;

    @Autowired
    private UserDao userMapper;

    @Value("${guoQingStart}")
    private String guoQingStart;

    @Value("${guoQingEnd}")
    private String guoQingEnd;

    /**
     * 积分消费
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject integralConsume(IntegralConsumeInfo integralConsumeInfo) throws Exception {
        log.info("[integralConsume]start");
        JSONObject result = new JSONObject();

        if (!validateSign(integralConsumeInfo)) {
            log.info("**************sign的内容不相等");
            result.put("status", "fail");
            result.put("errorMessage", "sign校验不通过");

            integralConsumeInfo.setIsSuccess("2");
            integralConsumeInfo.setErrorMessage("积分消费sign校验不通过");
            integralConsumeInfo.setCreateTime(new Date());
            duibaMapper.insertIntegralConsumeInfo(integralConsumeInfo);
            return result;
        }
        String ucId = integralConsumeInfo.getUid();
        String requireCredits = integralConsumeInfo.getCredits();

        //由原 command 调整为form
        QueryIntegralForm queryIntegralForm = new QueryIntegralForm();
        queryIntegralForm.setUserId(ucId);
        IntegralDetailDto integralDetailDto = integralService.queryIntegral(queryIntegralForm);
        if (ObjectUtils.isEmpty(integralDetailDto)) {
            result.put("status", "fail");
            result.put("errorMessage", "用户信息异常");
            result.put("credits", "0");
            integralConsumeInfo.setIsSuccess("2");
            integralConsumeInfo.setErrorMessage("根据ucId，查询用户信息异常");
        } else {
            if (Integer.parseInt(requireCredits) > integralDetailDto.getIntegralCounts()) {
                result.put("status", "fail");
                result.put("errorMessage", "用户积分不足");
                result.put("credits", "0");
                integralConsumeInfo.setIsSuccess("2");
                integralConsumeInfo.setErrorMessage("用户积分不足");
            } else {
                String clwOrderNum = UUID.randomUUID().toString().replaceAll("-", "");
                integralConsumeInfo.setClwOrderNum(clwOrderNum);

                IntegralOperationForm integralOperationForm = new IntegralOperationForm();
                integralOperationForm.setUcId(ucId);
                integralOperationForm.setOperationId(1);
                integralOperationForm.setActionId("8");
                integralOperationForm.setIntegralCounts(Integer.parseInt(requireCredits));
                integralOperationForm.setConsumeOrder(integralConsumeInfo.getOrderNum());
                integralService.subtractionIntegralCounts(integralOperationForm);
                log.info("操作减积分，参数：{}", integralOperationForm);


                /**增加历史记录**/
                /*IntegralAlterRecord integralAlterRecord = new IntegralAlterRecord();
                integralAlterRecord.setUid(ucId);
                integralAlterRecord.setCredits(requireCredits);
                integralAlterRecord.setType(CONSUME_MARK);
                integralAlterRecord.setIntegralItem("兑吧消费");
                integralAlterRecord.setIntegralResource(INTEGRAL_RESOURCE_APP);
                integralAlterRecord.setOrderNum(integralConsumeInfo.getOrderNum());
                integralAlterRecord.setCreateTime(new Date());
                insertIntegralAlterRecord(integralAlterRecord);*/

                integralConsumeInfo.setIsSuccess("0");

                result.put("status", "ok");
                result.put("errorMessage", "");
                result.put("bizId", clwOrderNum);
                result.put("credits", integralDetailDto.getIntegralCounts() - Integer.parseInt(requireCredits));
            }
            //信息插入数据库
            log.info("兑吧消费信息存入数据库，开发者订单号：{}", integralConsumeInfo.getClwOrderNum());
            integralConsumeInfo.setCreateTime(new Date());
            duibaMapper.insertIntegralConsumeInfo(integralConsumeInfo);
        }
        log.info("[integralConsume]end");
        return result;
    }

    /**
     * 确认消费
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String consumeConfirm(IntegralConsumeConfirmInfo integralConsumeConfirmInfo) throws Exception {
        log.info("[consumeConfirm]start");
        String result = "ok";

        if (!validateSign(integralConsumeConfirmInfo)) {
            log.info("**************sign校验不通过");
            result = "fail";
            return result;
        }
        Map<String, String> params = new HashMap<>();
        params.put("appKey", integralConsumeConfirmInfo.getAppKey());
        params.put("uid", integralConsumeConfirmInfo.getUid());
        params.put("orderNum", integralConsumeConfirmInfo.getOrderNum());

        IntegralConsumeInfo order = duibaMapper.selectOrderByConfirmMap(params);
        if (Objects.isNull(order)) {
            log.error("查询订单的条数：{}，请检查！！", order);
            result = "fail";
        } else {
            String ucId = integralConsumeConfirmInfo.getUid();
            String success = integralConsumeConfirmInfo.getSuccess();
            if (StringUtils.equals(success, "true")) {
                log.info("订单号：{}消费成功", integralConsumeConfirmInfo.getOrderNum());
                order.setIsSuccess("1");
            } else {
                log.info("订单号：{}消费失败", integralConsumeConfirmInfo.getOrderNum());
                QueryWrapper<IntegralEntity> wrapper = new QueryWrapper<IntegralEntity>().eq("uc_id", ucId);
                wrapper.last("limit 1");
                IntegralEntity integralEntity = integralMapper.selectOne(wrapper);
                Integral integral = new Integral();
                integral.setIntegralCounts(Integer.parseInt(order.getCredits()) + integralEntity.getIntegralCounts().intValue());
                integral.setUcId(ucId);
                integral.setLastIntegralCounts(integralEntity.getIntegralCounts().intValue());
                integral.setUpdateTime(new Date());
                integral.setLastOperateType(0);
                integral.setLastIntegralOperatedCounts(Integer.parseInt(order.getCredits()));
                integralMapper.updateIntegralCounts(integral);

                /**增加历史记录**/
                IntegralAlterRecord integralAlterRecord = new IntegralAlterRecord();
                integralAlterRecord.setUid(ucId);
                integralAlterRecord.setCredits(order.getCredits());
                integralAlterRecord.setType(INCREASE_MARK);
                integralAlterRecord.setIntegralItem("兑吧消费失败，积分回退");
                integralAlterRecord.setIntegralResource(INTEGRAL_RESOURCE_APP);
                integralAlterRecord.setOrderNum(order.getOrderNum());
                integralAlterRecord.setCreateTime(new Date());
                integralAlterRecord.setBalance(integralEntity.getIntegralCounts().intValue() + Integer.parseInt(order.getCredits()));
                insertIntegralAlterRecord(integralAlterRecord);

                log.info("用户：{}，积分回退:{}", ucId, order.getCredits());
                log.info("操作加积分，参数：{}", integral);

                order.setIsSuccess("2");
                result = "ok";
            }

            order.setErrorMessage(integralConsumeConfirmInfo.getErrorMessage());
            order.setUpdateTime(new Date());
            duibaMapper.updateIntegralConsumeInfoByConfirmInfo(order);
        }
        log.info("[consumeConfirm]end");
        return result;
    }

    /**
     * 添加积分
     */
    @Override
    public void add(IntegralOperationForm integralOperationForm) {

    }

    /**
     * 虚拟消费
     */
    @Override
    public void virtualConsume(VirtualCardConsumeParams virtualCardConsumeParams) throws Exception {
        String ucId = virtualCardConsumeParams.getUid();
        String actionId = virtualCardConsumeParams.getParams();

        IntegralOperationForm integralOperationForm = new IntegralOperationForm();
        integralOperationForm.setUcId(ucId);
        integralOperationForm.setOperationId(0);
        integralOperationForm.setActionId(actionId);
        integralOperationForm.setOrderNum(virtualCardConsumeParams.getOrderNum());
        integralService.addIntegralCounts(integralOperationForm);
        try {
            integralService.midAutumnByRuleId(ucId, 72);
            integralService.FestivalByRuleId(ucId, 82, guoQingStart, guoQingEnd);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }


    @Override
    public void insertIntegralAlterRecord(IntegralAlterRecord integralAlterRecord) {
        log.info("增加积分变更历史记录，信息:{}", integralAlterRecord);
        duibaMapper.insertIntegralAlterRecord(integralAlterRecord);
    }

    private boolean validateSign(Object object) throws Exception {
        Map<String, Object> calss2Map = beanConvertMap(object);
        StringBuilder signStr = new StringBuilder();
        String sign = null;
        if (object instanceof IntegralConsumeInfo) {
            for (String key : integralConsumeInfoKeys) {
                if (StringUtils.equals(key, "sign")) {
                    sign = (String) calss2Map.get(key);
                } else if (StringUtils.equals(key, "appSecret")) {
                    signStr.append(appSecret);
                } else {
                    if (calss2Map.get(key) != null) {
                        signStr.append(calss2Map.get(key));
                    }
                }
            }
        } else if (object instanceof IntegralConsumeConfirmInfo) {
            for (String key : IntegralConsumeConfirmInfoKeys) {
                if (StringUtils.equals(key, "sign")) {
                    sign = (String) calss2Map.get(key);
                } else if (StringUtils.equals(key, "appSecret")) {
                    signStr.append(appSecret);
                } else {
                    if (calss2Map.get(key) != null) {
                        signStr.append(calss2Map.get(key));
                    }
                }
            }
        } else {
            return false;
        }

        log.info("需要加密的字符串：{}", signStr.toString());
        byte[] secretBytes = MessageDigest.getInstance("md5").digest(signStr.toString().getBytes());
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        log.info("加密后字符串：{}", md5code);
        if (StringUtils.equals(md5code, sign)) {
            log.info("**************sign校验通过");
            return true;
        } else {
            log.info("**************sign校验不通过");
            return false;
        }
    }

    /**
     * 将java对象转成map集合
     *
     * @param bean
     * @return
     * @throws Exception
     */
    public static Map<String, Object> beanConvertMap(Object bean) throws Exception {

        Map<String, Object> dataMap = new HashMap<>();
        // 获取bean的字节码对象
        Class clazz = bean.getClass();
        // 获取对象的所有属性
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            // 获取对象的属性
            Field field = fields[i];
            // 打开私有访问
            field.setAccessible(true);
            // 获取对象的属性名称
            String name = field.getName();
            // 获取对象的属性值
            Object value = field.get(bean);
            // 将对象的属性和属性值封装到map集合中
            dataMap.put(name, value);
        }
        return dataMap;
    }
}
