package com.nut.tools.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.nut.tools.app.entity.MessageTemplateEntity;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.result.ECode;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.tools.app.form.PushMesForm;
import com.nut.tools.app.mapper.MessageDao;
import com.nut.tools.app.pojo.NotReceiveUserInfoPojo;
import com.nut.tools.app.service.PushMessageService;
import com.nut.tools.app.service.SaveAndPushMessageService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @Description: 爱心推
 * @Modified By:
 */
@Service
public class PushMessageServiceImpl implements PushMessageService {
    protected static final Logger logger = LoggerFactory.getLogger(PushMessageServiceImpl.class);
    private static final String YES = "1";

    @Autowired
    private SaveAndPushMessageService savePushMessageService;
    @Autowired
    private MessageDao messageDao;

    /**
     * @param from
     * @return
     * @Description: 获取模板推送
     * @method: pushMessageGetTemplate
     */
    @Override
    public void pushMessageGetTemplate(PushMesForm from) {

        String messageId = StringUtil.getUUID().replace("-", "");
        Map<String, Object> param = new HashMap<>(3);
        List<Integer> appTypeList = new ArrayList<>();
        if (PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER_AND_DRIVER.equals(from.getAppType())) {
            appTypeList.add(Integer.parseInt(PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER));
            appTypeList.add(Integer.parseInt(PushStaticLocarVal.PUSH_RECEIVETYPE_DRIVER));
        } else {
            appTypeList.add(Integer.parseInt(from.getAppType()));
        }
        param.put("list", appTypeList);
        // 推送用户
        if (PushStaticLocarVal.PUSH_SHOW_TYPE_USER.equals(from.getPushShowType()) && StringUtil.isNotEmpty(from.getReceiverId())) {
            param.put("receiverIdList", Arrays.asList(from.getReceiverId().split(",")));
        }
        // 不接收消息的用户
        List<NotReceiveUserInfoPojo> notReceiverIdList = new ArrayList<>();
        // 设置推送消息模板
        List<MessageTemplateEntity> pojoList = new ArrayList<>();
        if (StringUtil.isEmpty(from.getTitle()) && StringUtil.isEmpty(from.getContent())) {
            param.put("type", from.getType());
            param.put("stype", from.getStype());
            // 获取不接收消息的用户
            notReceiverIdList = messageDao.getNotReceiveUser(param);
            // 获取消息模板
            pojoList = messageDao.getMessageTemplate(param);
        } else {
            // 自定义标题、内容
            if (!PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER_AND_DRIVER.equals(from.getAppType())) {
                MessageTemplateEntity messageTemplateEntity = new MessageTemplateEntity();
                messageTemplateEntity.setTitle(from.getTitle());
                messageTemplateEntity.setContent(from.getContent());
                messageTemplateEntity.setReceiveType(Integer.parseInt(from.getAppType()));
                pojoList.add(messageTemplateEntity);
            } else {
                MessageTemplateEntity messageTemplateEntityOwner = new MessageTemplateEntity();
                messageTemplateEntityOwner.setTitle(from.getTitle());
                messageTemplateEntityOwner.setContent(from.getContent());
                messageTemplateEntityOwner.setReceiveType(Integer.parseInt(PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER));
                pojoList.add(messageTemplateEntityOwner);

                MessageTemplateEntity messageTemplateEntityDriver = new MessageTemplateEntity();
                messageTemplateEntityDriver.setTitle(from.getTitle());
                messageTemplateEntityDriver.setContent(from.getContent());
                messageTemplateEntityDriver.setReceiveType(Integer.parseInt(PushStaticLocarVal.PUSH_RECEIVETYPE_DRIVER));
                pojoList.add(messageTemplateEntityDriver);
            }
            // 不接收消息的用户
            if (StringUtil.isNotEmpty(from.getNotReceivePushType()) && StringUtil.isNotEmpty(from.getNotReceivePushStype())) {
                param.put("type", from.getNotReceivePushType());
                param.put("stype", from.getNotReceivePushStype());
                notReceiverIdList = messageDao.getNotReceiveUser(param);
            } else {
                if (StringUtil.isEq(from.getStype(), PushStaticLocarVal.MESSAGE_TYPE_SYSTEM_TBOSS_STYPE)) {
                    param.put("messageType", Integer.parseInt(PushStaticLocarVal.MESSAGE_TYPE_SYSTEM));
                    notReceiverIdList = messageDao.getNotReceiveUserByMessageType(param);
                } else if (StringUtil.isEq(from.getStype(), PushStaticLocarVal.MESSAGE_TYPE_SYSTEM_CRM_STYPE)) {
                    // V2.6.1.1服务协议需求新增一类CRM提醒的消息，没有模版，推送内容CRM给啥推啥。
                    param.put("messageType", Integer.parseInt(PushStaticLocarVal.MESSAGE_TYPE_SERVICE));
                    notReceiverIdList = messageDao.getNotReceiveUserByMessageType(param);
                }
            }
        }
        if (pojoList == null || pojoList.isEmpty()) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "消息模板为空");
        }
        if (CollectionUtils.isEmpty(notReceiverIdList)) {
            notReceiverIdList = new ArrayList<>();
        }
        // 推送
        try {
            logger.info("[pushMessageGetTemplate] notReceiverIdList ={}", JsonUtil.toJson(notReceiverIdList));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        for (MessageTemplateEntity messageTemplateEntity : pojoList) {
            // 标题
            String title = messageTemplateEntity.getTitle();
            String content = setContent(messageTemplateEntity.getContent(), from.getWildcardMap());
            String appType = messageTemplateEntity.getReceiveType().toString();
            // 设置消息分类和消息分类名称
            if (null != messageTemplateEntity.getMessageType() && StringUtil.isNotEmpty(messageTemplateEntity.getMessageTypeName())
                    && StringUtil.isEmpty(from.getMessageType()) && StringUtil.isEmpty(from.getMessageTypeName())) {
                from.setMessageType(String.valueOf(messageTemplateEntity.getMessageType()));
                from.setMessageTypeName(messageTemplateEntity.getMessageTypeName());
            }
            if (StringUtil.isEmpty(title) || StringUtil.isEmpty(content)) {
                logger.info("[pushMessageGetTemplate] 获取消息模板为空 is Empty appType={},type={},stype={}"
                        , messageTemplateEntity.getReceiveType(), messageTemplateEntity.getType(), messageTemplateEntity.getStype());
                continue;
            } else {
                ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("push-pool-%d").build();
                ExecutorService executorService = new ThreadPoolExecutor(2, 4, 30L, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>(1024), threadFactory);
                try {
                    // 推送
                    List<String> finalNotReceiverIdList = notReceiverIdList.stream()
                            .filter(p -> p.getNotReceiveAppType().equals(messageTemplateEntity.getReceiveType()))
                            .map(NotReceiveUserInfoPojo::getUserId)
                            .collect(Collectors.toList());
                    // 判断：是否不推送只保存
                    if (!YES.equals(from.getNoPushSaveOnly())) {
                        //Runnable runnablePush = () -> savePushMessageService.pushMessage(command, title, content, appType, messageId, finalNotReceiverIdList);
                        //executorService.execute(runnablePush);
                    }
                    if (PushStaticLocarVal.PUSH_SHOW_TYPE_CAR.equals(from.getPushShowType())) {
                        Runnable runnableCarSave = () -> savePushMessageService.saveCarMessageRecord(messageId, title, content, appType, from);
                        executorService.execute(runnableCarSave);
                    } else {
                        Runnable runnableSave = () -> savePushMessageService.savePushUserMessageRecord(messageId, title, content, appType, from, finalNotReceiverIdList);
                        executorService.execute(runnableSave);
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    logger.error("[pushMessageGetTemplate] Thread Exception:{}", e.getMessage());
                } finally {
                    executorService.shutdown();
                }
            }
        }


    }

    /**
     * 替换消息的通配符,并设置entity
     */
    private String setContent(String content, String wildcardMap) {
        if (StringUtil.isNotEmpty(wildcardMap)) {
            try {
                Map<String, Object> map = JsonUtil.toMap(wildcardMap);
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    // 通配符
                    String wildcard = entry.getKey().trim();
                    // 通配符对应的值
                    String value = entry.getValue() == null ? "" : entry.getValue().toString();
                    // 通配符替换为value
                    if (content.contains(wildcard)) {
                        content = content.replace(wildcard, value);
                    }
                }
            } catch (Exception e) {
                content = "";
                logger.error("[setContent] Exception:{}", e.getMessage());
            }
        }
        return content;
    }
}
