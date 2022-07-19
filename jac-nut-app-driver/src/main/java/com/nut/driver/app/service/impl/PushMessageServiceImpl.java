package com.nut.driver.app.service.impl;

import com.nut.driver.app.client.ToolsClient;
import com.nut.driver.app.form.PushMesForm;
import com.nut.driver.app.service.PushMessageService;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-22 13:15
 * @Version: 1.0
 */
@Slf4j
@Service
public class PushMessageServiceImpl implements PushMessageService {

    @Autowired
    private ToolsClient toolsClient;

    @Async
    public void pushToStation(String stype, String wildcard, String messageExtra, String senderId, String receiverId, String pushShowType) {
        log.info("[pushToStation]start");
        try {
            if (StringUtil.isEmpty(receiverId)) {
                log.info("[pushToStation] receiverId isEmpty");
                return;
            }
            PushMesForm form = new PushMesForm();
            form.setType(PushStaticLocarVal.PUSH_TYPE_10);
            form.setStype(stype);
            form.setWildcardMap(wildcard);
            // 扩展信息
            form.setMessageExtra(messageExtra);
            // 推送客户端  服务站
            form.setAppType(PushStaticLocarVal.PUSH_RECEIVETYPE_SERVERSTATION);
            // 推送类型
            form.setPushType(PushStaticLocarVal.PUSH_TYPE_MSG_SPECIFIED);
            // 接收者用户id ,多个用户用逗号分隔
            form.setReceiverId(receiverId);
            // 发送者用户id
            form.setSenderId(senderId);
            // 消息展示分类
            form.setPushShowType(pushShowType);
            // 页面跳转code  工单详情页面
            form.setMessageCode(PushStaticLocarVal.MESSAGE_CODE_WO_INFO);
            toolsClient.MessagePush(form);
        } catch (Exception e) {
            log.error("[pushToStation]Exception:{}", e.getMessage());
        }
        log.info("[pushToStation]end");
    }

    @Override
    public void pushMessageToStation(String stype, String wildcard, String messageExtra, String senderId, String receiverId, String pushShowType) {
        log.info("[pushToStation]start");
        try {
            if (StringUtil.isEmpty(receiverId)) {
                log.info("[pushToStation] receiverId isEmpty");
                return;
            }
            PushMesForm form = new PushMesForm();
            form.setType(PushStaticLocarVal.PUSH_TYPE_10);
            form.setStype(stype);
            form.setWildcardMap(wildcard);
            // 扩展信息
            form.setMessageExtra(messageExtra);
            // 推送客户端  服务站
            form.setAppType(PushStaticLocarVal.PUSH_RECEIVETYPE_SERVERSTATION);
            // 推送类型
            form.setPushType(PushStaticLocarVal.PUSH_TYPE_MSG_SPECIFIED);
            // 接收者用户id ,多个用户用逗号分隔
            form.setReceiverId(receiverId);
            // 发送者用户id
            form.setSenderId(senderId);
            // 消息展示分类
            form.setPushShowType(pushShowType);
            // 页面跳转code  工单详情页面
            form.setMessageCode(PushStaticLocarVal.MESSAGE_CODE_WO_INFO);
            toolsClient.sendMessage4Station(form);
        } catch (Exception e) {
            log.error("[pushToStation]Exception:{}", e.getMessage());
        }
        log.info("[pushToStation]end");
    }

}
