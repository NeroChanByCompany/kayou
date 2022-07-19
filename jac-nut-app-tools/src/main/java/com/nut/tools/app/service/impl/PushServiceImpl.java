package com.nut.tools.app.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.alibaba.fastjson.JSONObject;

import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.utils.JsonUtil;
import com.nut.tools.app.entity.MessageTemplateEntity;
import com.nut.tools.app.form.PushMesForm;
import com.nut.tools.app.mapper.MessageDao;
import com.nut.tools.app.service.PushService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PushServiceImpl implements PushService {

    protected static final Logger logger = LoggerFactory.getLogger(PushServiceImpl.class);
    @Autowired
    private MessageDao messageDao;

    /**
     * 数据库名
     */
    @Value("${tsp_database_name}")

    private String tspDatabaseName;

    /*appKey 和 mastersecret*/
    @Value("${msg.jpush.carOwner.app-key}")
    private String carOwnerAppKey;

    @Value("${msg.jpush.carOwner.master-secret}")
    private String carOwnerMasterSecret;

    @Value("${msg.jpush.fleet.app-key}")
    private String fleetAppKey;

    @Value("${msg.jpush.fleet.master-secret}")
    private String fleetMasterSecret;

    @Value("${msg.jpush.service.app-key}")
    private String serviceAppKey;

    @Value("${msg.jpush.service.master-secret}")
    private String serviceMasterSecret;

    @Override
    public void push2Device(PushMesForm from) {
        try {
            // 设置推送消息模板
            List<MessageTemplateEntity> pojoList = new ArrayList<>();

            Map<String, Object> param = new HashMap<>(3);
            param.put("type", from.getType());
            param.put("stype", from.getStype());
            List<Integer> appTypeList = new ArrayList<>();
            if (StringUtils.isNotBlank(from.getAppType())) {
                appTypeList.add(Integer.parseInt(from.getAppType()));
            }
            param.put("list", appTypeList);
            // 获取消息模板
            pojoList = messageDao.getMessageTemplate(param);
            if (pojoList == null || CollectionUtil.isEmpty(pojoList)) {
                ExceptionUtil.result(ECode.SERVER_ERROR.code(), "数据库消息模板没有查到数据");
            }
            for (MessageTemplateEntity messageTemplateEntity : pojoList) {
                // 标题
                String title = messageTemplateEntity.getTitle();
                String content = setContent(messageTemplateEntity.getContent(), from.getWildcardMap());
                String userCateory = getUserCateory(from);

                logger.info("发送消息的主题：{}，内容：{}", title, content);
                Map<String, String> parm = new HashMap<>();
                parm.put("msg", content);
                parm.put("title", title);

                String registrationId = null;
                if (StringUtils.equals(userCateory, "0")) {
                    registrationId = messageDao.selectRegistrationIdByUcId(from.getReceiverId());
                    logger.info("发送客户端消息，根据receiverId = {},得到key={}", from.getReceiverId(), registrationId);
                    parm.put("masterSecret", carOwnerMasterSecret);
                    parm.put("appKey", carOwnerAppKey);
                } else if (StringUtils.equals(userCateory, "1")) {
                    registrationId = messageDao.selectRegistrationIdByUcId(from.getReceiverId());
                    logger.info("发送车队消息，根据receiverId = {},得到key={}", from.getReceiverId(), registrationId);
                    parm.put("masterSecret", fleetMasterSecret);
                    parm.put("appKey", fleetAppKey);
                } else {
                    if (from.getReceiverId().contains(",")) {
                        //多个接收者情况
                        String[] receiverids = from.getReceiverId().split(",");
                        for (String receiverId : receiverids) {
                            registrationId = messageDao.selectTspRegistrationIdByAccountName(receiverId, tspDatabaseName);
                            logger.info("发送群发服务端消息，根据receiverId = {},得到key={}", receiverId, registrationId);
                            if (StringUtils.isNotBlank(registrationId)) {
                                parm.put("masterSecret", serviceMasterSecret);
                                parm.put("appKey", serviceAppKey);
                                parm.put("id", registrationId);
                                jpushAll(parm);
                            } else {
                                userCateory = getUserCateory(from);
                                if (StringUtils.equals(userCateory, "0")) {
                                    registrationId = messageDao.selectRegistrationIdByUcId(from.getReceiverId());
                                    logger.info("发送客户端消息，根据receiverId = {},得到key={}", from.getReceiverId(), registrationId);
                                    parm.put("masterSecret", carOwnerMasterSecret);
                                    parm.put("appKey", carOwnerAppKey);
                                } else if (StringUtils.equals(userCateory, "1")) {
                                    registrationId = messageDao.selectRegistrationIdByUcId(from.getReceiverId());
                                    logger.info("发送车队消息，根据receiverId = {},得到key={}", from.getReceiverId(), registrationId);
                                    parm.put("masterSecret", fleetMasterSecret);
                                    parm.put("appKey", fleetAppKey);
                                }
                                jpushAll(parm);
                            }
                        }
                        return;
                    }

                    registrationId = messageDao.selectTspRegistrationIdByAccountName(from.getReceiverId(), tspDatabaseName);
                    logger.info("发送服务端消息，根据receiverId = {},得到key={}", from.getReceiverId(), registrationId);
                    parm.put("masterSecret", serviceMasterSecret);
                    parm.put("appKey", serviceAppKey);
                }
                if (registrationId == null || StringUtils.isBlank(registrationId)) {

                    ExceptionUtil.result(ECode.SERVER_ERROR.code(), "registrationId为空");
                }
                parm.put("id", registrationId);
                jpushAll(parm);
            }
        } catch (Exception e) {
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "发送消息出错");
        }

    }

    /**
     * 替换消息的通配符,并设置entity
     */
    private String setContent(String content, String wildcardMap) {
        if (StringUtils.isNotEmpty(wildcardMap)) {
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

    //极光推送>>All所有平台
    private void jpushAll(Map<String, String> parm) {
        //创建JPushClient
        JPushClient jpushClient = new JPushClient(parm.get("masterSecret"), parm.get("appKey"));
        //创建option
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.all())  //所有平台的用户
                .setAudience(Audience.registrationId(parm.get("id")))//registrationId指定用户
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder() //发送ios
                                .setAlert(parm.get("msg")) //消息体
                                .setBadge(+1)
                                .setSound("happy") //ios提示音
                                .addExtras(parm) //附加参数
                                .build())
                        .addPlatformNotification(AndroidNotification.newBuilder() //发送android
                                .addExtras(parm) //附加参数
                                .setTitle(parm.get("title"))
                                .setAlert(parm.get("msg")) //消息体
                                .build())
                        .build())
                .setOptions(Options.newBuilder().setApnsProduction(false).build())//指定开发环境 true为生产模式 false 为测试模式 (android不区分模式,ios区分模式)
                .setMessage(Message.newBuilder().setMsgContent(parm.get("msg")).addExtras(parm).build())//自定义信息
                .build();
        try {
            PushResult pu = jpushClient.sendPush(payload);
            logger.info("发送消息返回：{}", pu.toString());
        } catch (APIConnectionException e) {
            logger.error("APIConnectionException", e);
        } catch (APIRequestException e) {
            logger.error("APIRequestException", e);
        }
    }

    private String getUserCateory(PushMesForm from) {
        String receiverId = from.getReceiverId();
        String type = messageDao.selectAppTypeByUcId(receiverId);
        logger.info("根据uc_id={}，查询用户type={}", receiverId, type);
        if (type != null && StringUtils.isNotBlank(type)) {
            return type;
        } else {
            return "2";
        }
    }

    @Override
    public void JPushMgs(JSONObject prams) {
        logger.info("[push2Device] start,userId = {}", prams);
        String userId = prams.getString("userId");
        String registrationId = null;
        String type = messageDao.selectAppTypeByUcId(userId);
        Map<String, String> parm = new HashMap<>();
        parm.put("msg", prams.getString("content"));
        parm.put("title", prams.getString("title"));
        if (StringUtils.equals(type, "0")) {
            registrationId = messageDao.selectRegistrationIdByUcId(userId);
            logger.info("发送客户端消息，根据receiverId = {},得到key={}", userId, registrationId);
            parm.put("masterSecret", carOwnerMasterSecret);
            parm.put("appKey", carOwnerAppKey);
        } else if (StringUtils.equals(type, "1")) {
            registrationId = messageDao.selectRegistrationIdByUcId(userId);
            logger.info("发送车队消息，根据receiverId = {},得到key={}", userId, registrationId);
            parm.put("masterSecret", fleetMasterSecret);
            parm.put("appKey", fleetAppKey);
        }
        parm.put("id", registrationId);
        jpushAll(parm);
        logger.info("[push2Device] start");
    }

    @Override
    public void push2Station(PushMesForm from) {
        try {
            // 获取消息模板
            MessageTemplateEntity messageTemplate = messageDao.getMessageTemplateById(1111);
            // 标题
            String title = messageTemplate.getTitle();
            String content = setContent(messageTemplate.getContent(), from.getWildcardMap());
            String userCateory = getUserCateory(from);

            logger.info("发送消息的主题：{}，内容：{}", title, content);
            Map<String, String> parm = new HashMap<>();
            parm.put("msg", content);
            parm.put("title", title);

            String registrationId = null;
            if (StringUtils.equals(userCateory, "0")) {
                registrationId = messageDao.selectRegistrationIdByUcId(from.getReceiverId());
                logger.info("发送客户端消息，根据receiverId = {},得到key={}", from.getReceiverId(), registrationId);
                parm.put("masterSecret", carOwnerMasterSecret);
                parm.put("appKey", carOwnerAppKey);
            } else if (StringUtils.equals(userCateory, "1")) {
                registrationId = messageDao.selectRegistrationIdByUcId(from.getReceiverId());
                logger.info("发送车队消息，根据receiverId = {},得到key={}", from.getReceiverId(), registrationId);
                parm.put("masterSecret", fleetMasterSecret);
                parm.put("appKey", fleetAppKey);
            } else {
                if (from.getReceiverId().contains(",")) {
                    //多个接收者情况
                    String[] receiverids = from.getReceiverId().split(",");
                    for (String receiverId : receiverids) {
                        registrationId = messageDao.selectTspRegistrationIdByAccountName(receiverId, tspDatabaseName);
                        logger.info("发送群发服务端消息，根据receiverId = {},得到key={}", receiverId, registrationId);
                        if (StringUtils.isNotBlank(registrationId)) {
                            parm.put("masterSecret", serviceMasterSecret);
                            parm.put("appKey", serviceAppKey);
                            parm.put("id", registrationId);
                            jpushAll(parm);
                        } else {
                            userCateory = getUserCateory(from);
                            if (StringUtils.equals(userCateory, "0")) {
                                registrationId = messageDao.selectRegistrationIdByUcId(from.getReceiverId());
                                logger.info("发送客户端消息，根据receiverId = {},得到key={}", from.getReceiverId(), registrationId);
                                parm.put("masterSecret", carOwnerMasterSecret);
                                parm.put("appKey", carOwnerAppKey);
                            } else if (StringUtils.equals(userCateory, "1")) {
                                registrationId = messageDao.selectRegistrationIdByUcId(from.getReceiverId());
                                logger.info("发送车队消息，根据receiverId = {},得到key={}", from.getReceiverId(), registrationId);
                                parm.put("masterSecret", fleetMasterSecret);
                                parm.put("appKey", fleetAppKey);
                            }
                            jpushAll(parm);
                        }
                    }
                    registrationId = messageDao.selectTspRegistrationIdByAccountName(from.getReceiverId(), tspDatabaseName);
                    logger.info("发送服务端消息，根据receiverId = {},得到key={}", from.getReceiverId(), registrationId);
                    parm.put("masterSecret", serviceMasterSecret);
                    parm.put("appKey", serviceAppKey);
                }
                if (registrationId == null || StringUtils.isBlank(registrationId)) {

                    ExceptionUtil.result(ECode.SERVER_ERROR.code(), "registrationId为空");
                }
                parm.put("id", registrationId);
                jpushAll(parm);
            }
        } catch (Exception e) {
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "发送消息出错");
        }
    }
}
