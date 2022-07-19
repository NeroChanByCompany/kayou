package com.nut.driver.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.form.SendSmsForm;
import com.nut.driver.app.service.SendSmsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 发送短信
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-16 09:23
 * @Version: 1.0
 */
@Slf4j
@Service("SendSmsService")
public class SendSmsServiceImpl implements SendSmsService {

    private static final String SHST_SUCCESS = "0";
    private static final String DEFAULT_SIGN = "江淮轻卡";
    private static final String NOT_SET = "NONE";
    private static final String RESULT_OK = "200";
    private static final String RESULT_NG_OTHER = "999";

    //注册或者登录发送验证码短信
    private static final String REGISTRY_LOGIN_VERIFICATIONCODE = "0";
    //忘记密码验证码短信
    private static final String FORGET_PASSWORD_VERIFICATIONCODE = "1";

    //通用验证码
    private static final String COMMON_VERIFICATIONCODE = "9";
    //通知类短信
    private static final String OTHER_MESSAGE = "2";

    @Value("${accessKeyId}")
    private String accessKeyId;
    @Value("${secret}")
    private String secret;
    @Value("${sysVersion}")
    private String sysVersion;
    @Value("${systemDomain}")
    private String systemDomain;
    @Value("${canSendSms}")
    private String canSendSms;
    @Value("${filter_switch}")
    private String filterSwitch;


    /**
     * 短信通道封装
     *
     * @param form
     * @return Http响应的封装
     */
    public Map<String, Object> sendSms(SendSmsForm form) {
        log.info("[sendSms]start||phone:{}||content:{}||sign:{}", form.getPhone(),
                form.getContent(), form.getSign());
        Map<String, Object> result = new HashMap<>();
        result.put("code", RESULT_OK);
        result.put("msg", "发送完成");
        try {
            // 校验配置
            if (!isValidConfigValue(accessKeyId) || !isValidConfigValue(secret)
                    || !isValidConfigValue(sysVersion) || !isValidConfigValue(systemDomain)) {
                log.error("[sendSms]发送短信配置不全，无法发送||accessKeyId:{}||secret:{}||sysVersion:{}||systemDomain:{}",
                        accessKeyId, secret, sysVersion, systemDomain);
                result.put("code", RESULT_NG_OTHER);
                result.put("msg", "发送短信配置错误");
                return result;
            }

            if (StringUtils.equals(filterSwitch, "1") && !canSendSms.contains(form.getPhone())) {
                log.error("过滤号码开关：{}，允许发送的号码：{}",
                        filterSwitch, canSendSms);
                result.put("code", RESULT_NG_OTHER);
                result.put("msg", "配置没有校验通过");
                return result;
            }
            // 校验参数
            if (StringUtil.isEmpty(form.getPhone()) || StringUtil.isEmpty(form.getContent())) {
                log.info("[sendSms]没有需要发送的内容，返回");
                result.put("code", RESULT_NG_OTHER);
                result.put("msg", "发送短信发送的内容不能为空");
                return result;
            }
            String templateCode = null;
            JSONObject templateParam = new JSONObject();
            if (StringUtils.equals(form.getSign(), REGISTRY_LOGIN_VERIFICATIONCODE)) {
                templateCode = "SMS_208626714";
                templateParam.put("code", form.getContent());
            } else if (StringUtils.equals(form.getSign(), FORGET_PASSWORD_VERIFICATIONCODE)) {
                templateCode = "SMS_208590195";
                templateParam.put("code", form.getContent());
            } else if (StringUtils.equals(form.getSign(), COMMON_VERIFICATIONCODE)) {
                templateCode = "SMS_213677812";
                templateParam.put("code", form.getContent());
            } else {
                templateCode = "SMS_208590195";
                result.put("code", RESULT_NG_OTHER);
                result.put("msg", "发送短信参数错误");
                return result;
            }

            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, secret);
            IAcsClient client = new DefaultAcsClient(profile);

            CommonRequest request = new CommonRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysDomain(systemDomain);
            request.setSysVersion(sysVersion);
            request.setSysAction("SendSms");
            request.putQueryParameter("RegionId", "cn-hangzhou");
            request.putQueryParameter("PhoneNumbers", form.getPhone());
            request.putQueryParameter("SignName", DEFAULT_SIGN);
            request.putQueryParameter("TemplateCode", templateCode);
            request.putQueryParameter("TemplateParam", templateParam.toJSONString());

            log.info("[sendSms]发送短信配置，accessKeyId:{}||secret:{}||sysVersion:{}||systemDomain:{}",
                    accessKeyId, secret, sysVersion, systemDomain);
            CommonResponse response = client.getCommonResponse(request);
            log.info("调用短信平台返回的内容data = {},status = {},response = {}", response.getData(), response.getHttpStatus(), response.getHttpResponse());
        } catch (Exception e) {
            log.error("发送短信Exception出错.", e);
            result.put("code", RESULT_NG_OTHER);
            result.put("msg", "发送短信错误");
        }
        log.info("[sendSms]end");
        return result;
    }

    /**
     * 校验是否是有效的
     */
    private boolean isValidConfigValue(String config) {
        return StringUtil.isNotEmpty(config) && StringUtil.isNotEq(NOT_SET, config);
    }


}
