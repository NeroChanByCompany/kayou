package com.nut.driver.app.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.dao.OrderDao;
import com.nut.driver.app.dao.PayOrderDao;
import com.nut.driver.app.entity.OrderEntity;
import com.nut.driver.app.entity.PayOrderEntity;
import com.nut.driver.app.form.InsertOrderForm;
import com.nut.driver.app.service.OrderService;
import com.nut.driver.app.vo.InsertOrderVo;
import com.nut.driver.common.config.CMBConfig;
import com.nut.driver.common.em.DictEnum;
import com.nut.driver.common.utils.HttpUtil;
import com.nut.driver.common.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Resource
    private PayOrderDao payOrderDao;
    @Resource
    private CMBConfig cmbConfig;
    @Value("${simservicerootpath:http://jacsimflow-test-api.syuntu.com}")
    private String simservicerootpath;

    public static final String MEALS_STATUS_ON = "100202";
    public static final String MEALS_PAGE_URL = "/api/nsc/simflow/iotSetMeal/iotSetMeals/page";

    /**
     *@Auther wangshuai
     *@Description 进行请求头的处理
     *@param
     *@return
     */
    private Map<String,String> getMap(){
        // 设置请求头
        Map<String,String> map = new HashMap<>(8);
        map.put("Ql-Client-Id","aff904cc-3111-4f62-96f3-524c8f34c88a");
        // 不重复随机数
        String strRandom = UUID.randomUUID().toString().replace("-", "");
        map.put("Ql-Auth-Nonce", strRandom);
        // 当前时间戳 秒为单位
        long currentTimeMillis = System.currentTimeMillis();
        String strTime = String.valueOf(currentTimeMillis).substring(0,10);
        map.put("Ql-Auth-Timestamp",strTime);
        // 双重md5加密
        String s = strRandom + strTime;
        String str = DigestUtils.md5DigestAsHex(s.getBytes());
        String sign = str + "a8e5b674-7c2e-4854-9430-f29ca933418b";
        map.put("Ql-Auth-Sign",DigestUtils.md5DigestAsHex(sign.getBytes()));
        return map;
    }
}
