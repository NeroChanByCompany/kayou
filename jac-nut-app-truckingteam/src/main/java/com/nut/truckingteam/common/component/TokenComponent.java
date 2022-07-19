package com.nut.truckingteam.common.component;

import com.nut.common.base.BaseForm;
import com.nut.common.utils.RedisComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 根据实际情况进行改造
 *
 * @description: Token组件，
 * @author: hcb
 * @createTime: 2021/01/20 14:25
 * @version:1.0
 */
@Component
@Slf4j
public class TokenComponent {

    /**
     * Token有效期：30天
     */
    private static final Long TOKEN_EXPIRY_TIME = 30 * 24 * 60 * 60L;

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private RedisComponent redisComponent;

    @Value("${login.token.key:jac-nut-app-driver-dev}")
    private String key ;

    /**
     * 创建Token
     *
     * @param tokenMap
     * @return
     */
    public String generateToken(Map<String, Object> tokenMap) {
        // 删除用户上次登录缓存信息
        String userId = (String) tokenMap.get("userId");
        String cacheKeyUserId = applicationName + ":userToken:" + userId;
        String preToken = (String) redisComponent.get(cacheKeyUserId);
        if (StringUtils.isNotEmpty(preToken)) {
            return preToken;
        }

        // 生成token并缓存用户信息
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        String cacheKey = applicationName + ":userToken:" + token;
        redisComponent.hmset(cacheKey, tokenMap, TOKEN_EXPIRY_TIME);

        // 根据userId缓存token
        redisComponent.set(cacheKeyUserId, token, TOKEN_EXPIRY_TIME);

        return token;
    }

    /**
     * 销毁Token
     *
     * @param token
     */
    public void destroyToken(String token) {
        String cacheKey = applicationName + ":userToken:" + token;
        Map<Object, Object> tokenMap = this.getTokenInfo(token);
        if (Objects.nonNull(tokenMap) && !tokenMap.isEmpty()) {
            String userId = (String) tokenMap.get("userId");
            String cacheKeyUserId = applicationName + ":userToken:" + userId;
            redisComponent.del(cacheKeyUserId);
        }
        redisComponent.del(cacheKey);
    }

    /**
     * 获取token信息
     *
     * @param token
     * @return
     */
    public Map<Object, Object> getTokenInfo(String token) {
        String cacheKey = key + ":userToken:" + token;
        return redisComponent.hmget(cacheKey);
    }

    /**
     * 校验Token
     *
     * @param token
     * @param t
     * @param <T>
     * @return
     */
    public <T extends BaseForm> Boolean checkToken(String token, T t) {
        Boolean checkResult = false;
        Map<Object, Object> tokenMap = this.getTokenInfo(token);
        if (Objects.nonNull(tokenMap) && !tokenMap.isEmpty()) {
            checkResult = true;
            if (Objects.nonNull(t)) {
                t.setToken(token);
                t.setUserId(String.valueOf(tokenMap.get("userId")));
                t.setAutoIncreaseId(Long.parseLong(tokenMap.get("id").toString()));
                t.setAppType(String.valueOf(tokenMap.get("appType")));
            }
        }

        return checkResult;
    }


}
