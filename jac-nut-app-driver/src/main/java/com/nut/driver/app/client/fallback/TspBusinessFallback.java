package com.nut.driver.app.client.fallback;

import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.client.TspBusinessClient;
import com.nut.driver.app.entity.CarBasicInfoEntity;
import com.nut.driver.app.form.CarBasicInfoForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author liuBing
 * @Classname TspBusinessFallback
 * @Description TODO
 * @Date 2021/8/4 15:43
 */
@Slf4j
@Component
public class TspBusinessFallback implements TspBusinessClient {

    @Override
    public HttpCommandResultWithData<CarBasicInfoEntity> getCarBasicInfo(Map<String,Object> param) {
        log.error("对接tsp getCarBasicInfo 客户端异常回调 异常状态码：{},异常消息体：{}",ECode.FALLBACK.code(), ECode.FALLBACK.message());
        return Result.result(ECode.FALLBACK.code(), ECode.FALLBACK.message());
    }
}
