package com.nut.driver.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.nut.common.result.ECode;
import com.nut.common.utils.RedisComponent;
import com.nut.driver.app.dto.AppLoginDTO;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.enums.StatusEnum;
import com.nut.driver.app.form.AppLoginForm;
import com.nut.driver.app.form.RegisterForm;
import com.nut.driver.app.service.UserCenterService;
import com.nut.driver.common.component.TokenComponent;
import com.nut.driver.common.constants.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author liuBing
 * @Classname UserCenterServiceImpl
 * @Description TODO
 * @Date 2021/6/16 17:47
 */
@Slf4j
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class UserCenterServiceImpl implements UserCenterService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private TokenComponent tokenComponent;

    @Autowired
    private RedisComponent redisComponent;

    /**
     * 注册用户中心
     */
    @Override
    public JSONObject register(RegisterForm form) {
        JSONObject resultObject = null;
        try {
            resultObject = new JSONObject();
            resultObject.put("code", 200);
            JSONObject userJson = new JSONObject();
            userJson.put("userId", UUID.randomUUID().toString().replaceAll("-", ""));
            userJson.put("mobile", form.getPhone());
            userJson.put("token", UUID.randomUUID().toString().replaceAll("-", ""));
            resultObject.put("data", userJson);
        } catch (Exception e) {
            resultObject.put("code", 500);
            log.error(e.getMessage(), e);
        }
        return resultObject;
    }

    /**
     * 登录
     */
    @Override
    public AppLoginDTO login(AppLoginForm form, UserEntity user) {
        if (user == null) {
            ExceptionUtil.result(1017, "该用户未注册");
        }
        if (Objects.nonNull(user.getStatus()) && user.getStatus().equals(StatusEnum.CANCELLATION.getCode())){
            ExceptionUtil.result(1017, "该用户未注册");
        }
        if (Objects.nonNull(user.getStatus()) && user.getStatus().equals(StatusEnum.FREEZE.getCode())){
            ExceptionUtil.result(1018, "当前用户已被冻结");
        }
//        if (StringUtils.isNotBlank(form.getSmsCode())) {
//            if (this.checkSmsCode(form.getPhone())){
//                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "当前账户验证已到最大次数，请稍后再试!");
//            }
//            String redisSmsCode = redisTemplate.opsForValue().get(RedisConstant.SMS_CODE_VALID_PERIOD + form.getPhone());
//            log.info("从redis获取到的smscode={}", redisSmsCode);
//            if (!StringUtils.equals(form.getSmsCode(), redisSmsCode)) {
//                ExceptionUtil.result(1018, "验证码不正确");
//            }
//            redisTemplate.delete(RedisConstant.SMS_CODE_VALID_PERIOD + form.getPhone());
//        } else if (!StringUtil.isEq(user.getPassword(), form.getPassword()) && !StringUtil.isEq(user.getUcPassword(), form.getPassword())) {
//            ExceptionUtil.result(1017, "密码错误");
//        }

        if (StringUtils.isNotBlank(form.getSmsCode())) {
            if (this.checkSmsCode(form.getPhone())){
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "当前账户验证已到最大次数，请稍后再试!");
            }
            String redisSmsCode = redisTemplate.opsForValue().get(RedisConstant.SMS_CODE_VALID_PERIOD + form.getPhone());
            log.info("从redis获取到的smscode={}", redisSmsCode);
            if (!StringUtils.equals(form.getSmsCode(), redisSmsCode)) {
                ExceptionUtil.result(1018, "验证码不正确");
            }
            redisTemplate.delete(RedisConstant.SMS_CODE_VALID_PERIOD + form.getPhone());
        } else {
            if (StringUtils.isEmpty(form.getPassword())) {
                ExceptionUtil.result(1017, "请输入密码");
            } else {
                if (!form.getPassword().equals(user.getUcPassword())) {
                    ExceptionUtil.result(1017, "密码错误");
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUcId());
        map.put("id", user.getId());
        map.put("appType", form.getAppType());
        map.put("phone", form.getPhone());
        return new AppLoginDTO()
                .setUserId(user.getUcId())
                .setToken(tokenComponent.generateToken(map));
    }
    /**
     * 校验当前验证码是否达到最大次数
     *
     * @return
     */
    protected boolean checkSmsCode(String phone) {
        // 判断redis
        Long count = redisComponent.incr(RedisConstant.SMS_CODE_CHECK + phone, 1L);
        // key为初始值，需要设置失效时间:10分钟
        if (count == 1L) {
            redisComponent.expire(RedisConstant.SMS_CODE_CHECK+ phone, 10L, TimeUnit.MINUTES);
        }
        return count > 5;
    }
}
