package com.nut.servicestation.common.handler;

import com.nut.common.base.BaseForm;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.HttpUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.servicestation.app.domain.IntegralCommandList;
import com.nut.servicestation.app.domain.IntegralEntity;
import com.nut.servicestation.app.domain.IntegralErrorEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/8
 */
@Component
@Slf4j
public class IntegralHandler {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${integral_rule_url:dummy}")
    private String integralUrl;


    @Async
    public <T extends BaseForm> void toIntegral(T command, HttpCommandResultWithData handleResult) {

        if (200 == (handleResult.getResultCode())) {
            log.info("======http result code 200======");

            if (IntegralCommandList.getCommandList().contains(command.getClass().getSimpleName())) {
                log.info("======埋点处理start======");
                IntegralEntity entity = new IntegralEntity();
                entity.setCommandName(command.getClass().getName());
                entity.setDistId("1");
                entity.setCommand(command);
                entity.setResult(handleResult);
                try {
                    String jsonStr = JsonUtil.toJson(entity);
                    log.info(jsonStr);
                    Map<String, String> paramMap = new HashMap<>();
                    Map<String, String> header = new HashMap<>();
                    paramMap.put("msg", jsonStr);
                    if (jsonStr.contains("flag")) {
                        log.info("======json string contains flag======");
                        Map<String, Object> jsonMap = JsonUtil.toMap(jsonStr);
                        Map<String, String> jsonCommand = (Map<String, String>) jsonMap.get("command");
                        if ("0".equals(jsonCommand.get("flag"))) {
                            log.info("======flag equals to 0======");
                            HttpUtil.postWithRequestIntegral(integralUrl, paramMap, header);
                        }
                    } else {
                        HttpUtil.postWithRequestIntegral(integralUrl, paramMap, header);
                    }
                    log.info("======埋点处理end======");
                } catch (Exception e) {
                    IntegralErrorEntity errorEntity = new IntegralErrorEntity();
                    errorEntity.setUserId(command.getUserId());
                    errorEntity.setInterfaceParam(command.getClass().getName());
                    errorEntity.setErrorInfo(e.toString());
                    errorEntity.setErrTime(new Timestamp(System.currentTimeMillis()));
                    try {
                        //发布埋点错误信息
                        redisTemplate.convertAndSend("integralErrChannel", JsonUtil.toJson(errorEntity));
                    } catch (Exception e1) {
                        log.info(e1.getMessage(), e1);
                    }
                    log.info(e.getMessage(), e);
                }
            }
        }
    }
}
