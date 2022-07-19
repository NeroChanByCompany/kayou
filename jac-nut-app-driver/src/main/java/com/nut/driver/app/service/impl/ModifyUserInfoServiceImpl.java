package com.nut.driver.app.service.impl;

import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.utils.Constants;
import com.nut.common.utils.RedisComponent;
import com.nut.driver.app.form.ModifyUserInfoForm;
import com.nut.driver.app.service.ModifyUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liuBing
 * @Classname ModifyUserInfoServiceImpl
 * @Description TODO
 * @Date 2021/6/17 9:33
 */
@Slf4j
@Service
public class ModifyUserInfoServiceImpl implements ModifyUserInfoService {
    @Autowired
    private RedisComponent redisComponent;

    @Override
    @Transactional(timeout = Constants.UPDPASSWORD_TIMEOUT, rollbackFor = Exception.class)
    public String checkSmsCode2(ModifyUserInfoForm form) {

        if (StringUtils.isBlank(form.getPhone())) {
            ExceptionUtil.result(ECode.PARAM_FAIL.code(), "手机号不能为空");
        }

        if (StringUtils.isBlank(form.getSmsCode())) {
            ExceptionUtil.result(ECode.PARAM_FAIL.code(), "验证码不能为空");
        }

        String redisSmsCode = String.valueOf(redisComponent.get("SMS_VERIFICATION_CODE" + form.getPhone()));
        if (!StringUtils.equals(form.getSmsCode(), redisSmsCode)) {

            ExceptionUtil.result(ECode.SMSCODE_ERROR.code(), "验证码不正确");
        }
        redisComponent.del("SMS_VERIFICATION_CODE" + form.getPhone());
        log.info("checkSmsCode end return:{}",redisSmsCode);
        return redisSmsCode;

    }
}
