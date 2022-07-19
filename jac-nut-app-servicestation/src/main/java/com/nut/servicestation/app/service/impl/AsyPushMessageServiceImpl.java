package com.nut.servicestation.app.service.impl;

import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.client.ToolsClient;
import com.nut.servicestation.app.dao.CarDao;
import com.nut.servicestation.app.dao.UserDao;
import com.nut.servicestation.app.service.AsyPushMessageService;
import com.nut.tools.app.form.PushMesForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*
 *  @author wuhaotian 2021/7/2
 */
@Slf4j
@Service("AsyPushMessageService")
public class AsyPushMessageServiceImpl implements AsyPushMessageService {


    @Autowired
    private UserDao userServiceMapper;

    @Autowired
    private CarDao carMapper;

    @Autowired
    private ToolsClient toolsClient;



    @Override
    @Async
    public void pushToDriverAndOwner(String stype, String wildcard, String messageExtra, String senderId, String receiverPhone, String chassisNum) {
        log.info("[pushToDriverAndOwner]start");
        try {
            if (StringUtil.isEmpty(receiverPhone) && StringUtil.isEmpty(chassisNum)) {
                log.info("[pushToDriverAndOwner] receiverPhone & chassisNum isEmpty");
                return;
            }
            String receiverId;
            if (StringUtil.isNotEmpty(receiverPhone)) {
                receiverId = userServiceMapper.queryUserIdByPhone(Arrays.asList(receiverPhone.split(",")));
            } else {
                List<String> ucIds =  carMapper.selectUcIdByChassisNum(chassisNum);
                receiverId = ucIds.stream().filter(StringUtil::isNotEmpty).distinct().collect(Collectors.joining(","));
            }
            PushMesForm command = new PushMesForm();
            // 大type：服务
            command.setType(PushStaticLocarVal.PUSH_TYPE_10);
            // 小type
            command.setStype(stype);
            // 通配符替换
            command.setWildcardMap(wildcard);
            // 扩展信息
            command.setMessageExtra(messageExtra);
            // 推送客户端：车队版
            command.setAppType(PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER);
            // 推送类型：指定消息推送
            command.setPushType(PushStaticLocarVal.PUSH_TYPE_MSG_SPECIFIED);
            // 接收者用户id
            command.setReceiverId(receiverId);
            // 发送者用户id
            command.setSenderId(senderId);
            // 消息展示分类：个人消息（车队APP）
            command.setPushShowType(PushStaticLocarVal.PUSH_SHOW_TYPE_USER);
            // 页面跳转code：预约详情页面
            command.setMessageCode(PushStaticLocarVal.MESSAGE_CODE_ORDER_INFO);
            toolsClient.MessagePush(command);
        } catch (Exception e) {
            log.error("[pushToDriverAndOwner]Exception:", e);
        }
        log.info("[pushToDriverAndOwner]end");
    }
}
