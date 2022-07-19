package com.nut.driver.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @description: 请求第三方江淮流量经营平台
 * @author: MengJinyue
 * @createTime: 2021/12/29 11:21
 * @version:1.0
 */
@Configuration
public class SimFlowConfig {
    @Value("${simservicerootpath:http://jacsimflow-test-api.syuntu.com}")
    public String simservicerootpath;

    /**
     *@Auther wangshuai
     *@Description 进行请求头的处理
     *@param
     *@return
     */
    public Map<String,String> getMap(){
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

