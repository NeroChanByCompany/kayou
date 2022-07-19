package com.nut.tools.app.service.impl;

import com.nut.tools.app.entity.MessageRecordEntity;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.tools.app.form.PushMesForm;
import com.nut.tools.app.mapper.CarMessageRecordDao;
import com.nut.tools.app.mapper.UserMessageRecordDao;
import com.nut.tools.app.service.SaveAndPushMessageService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

/**
 * @Description: 存储推送消息
 */
@Service
public class SaveAndPushMessageServiceImpl implements SaveAndPushMessageService {
    protected static final Logger logger = LoggerFactory.getLogger(SaveAndPushMessageServiceImpl.class);
    @Resource
    private UserMessageRecordDao userMessageRecordDao;
    @Autowired
    private CarMessageRecordDao carMessageRecordDao;


    /**
     * 每次批量添加消息数量
     */
    @Value("${batchInsertMessageSize:2000}")
    private int batchInsertMessageSize;

    /**
     * @param from
     * @return void
     * @Description: 存储个人消息
     * @method: savePushUserMessageRecord
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePushUserMessageRecord(String messageId, String title, String content, String appType,
                                          PushMesForm from, List<String> notReceiverIdList) {
        logger.info("[savePushUserMessageRecord] start");
        logger.info("[save start appType={}]", from.getAppType());
        from.setTitle(title);
        from.setContent(content);
        from.setAppType(appType);
        List<MessageRecordEntity> insertList = new ArrayList<>();
        String[] receiverIds = from.getReceiverId().split(",");
        // 系统消息
        Set<String> receiverIdSet = new HashSet<>();
        for (String receiverId : receiverIds) {
            // 接收者用户id
            if (!receiverIdSet.contains(receiverId)) {
                // 存储数据
                MessageRecordEntity entity = setMessageRecord(messageId, from);
                if (entity != null) {
                    // 消息展示分类 1、车队消息，2、个人消息，3、工单消息，4、通知类消息
                    entity.setPushShowType(Integer.parseInt(from.getPushShowType()));
                    // 发送者用户id 2、系统为system,3、tboss
                    entity.setSenderId(from.getSenderId());
                    // 阅读标记	默认1，1：未读，2：已读',
                    entity.setReadFlag(1);
                    entity.setReceiverId(receiverId);
                    // 接收状态,0:不接收，1：接收
                    String receiveState = PushStaticLocarVal.RECEIVE_STATE;
                    if (notReceiverIdList != null && !notReceiverIdList.isEmpty()) {
                        receiveState = notReceiverIdList.indexOf(receiverId) == -1 ? PushStaticLocarVal.RECEIVE_STATE : PushStaticLocarVal.RECEIVE_STATE_NOT;
                    }
                    entity.setReceiveState(Integer.parseInt(receiveState));
                    receiverIdSet.add(receiverId);
                    insertList.add(entity);
                }
            }
        }
        logger.info("[savePushUserMessageRecord] insertList.size={}", insertList.size());
        if (CollectionUtils.isNotEmpty(insertList)) {
            int size = batchInsertMessageSize;
            List<MessageRecordEntity> subList;
            long starttime = System.currentTimeMillis();
            for (int i = 0; i < insertList.size(); i += size) {
                if (i + size > insertList.size()) {
                    subList = insertList.subList(i, insertList.size());
                } else {
                    subList = insertList.subList(i, i + size);
                }
                userMessageRecordDao.userMessageRecordBatchInsert(subList);
            }
            long endtime = System.currentTimeMillis();
            logger.info("[savePushUserMessageRecord] batchInsert cost time = {}", endtime - starttime);
        }
        logger.info("[savePushUserMessageRecord]end");
    }

    /**
     * @param from
     * @return void
     * @Description: 存储车辆消息
     * @method: savePushUserMessageRecord
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveCarMessageRecord(String messageId, String title, String content, String appType, PushMesForm from) {
        logger.info("[saveCarMessageRecord] start");
        logger.info("[save start appType={}]", from.getAppType());
        from.setTitle(title);
        from.setContent(content);
        from.setAppType(appType);
        List<MessageRecordEntity> insertList = new ArrayList<>();
        Set<String> carIdSet = new HashSet<>();
        String[] carIds = from.getCarId().split(",");
        for (String carId : carIds) {
            // 接收者用户id
            if (!carIdSet.contains(carId)) {
                MessageRecordEntity entity = setMessageRecord(messageId, from);
                if (entity != null) {
                    // 接收者角色
                    entity.setReceiverRole(from.getReceiverRole());
                    // 创建时间
                    entity.setCreateTimestamp(DateUtil.getNowDate(DateUtil.time_pattern).getTime());
                    entity.setCarId(carId);
                    carIdSet.add(carId);
                    insertList.add(entity);
                }
            }
        }
        logger.info("[saveCarMessageRecord] insertList.size={}", insertList.size());
        if (!insertList.isEmpty()) {
            carMessageRecordDao.carMessageRecordBatchInsert(insertList);
        }
        logger.info("[saveCarMessageRecord]end");
    }

    /**
     * @param messageId
     * @param from
     * @Description: 设置推送消息实体
     * @method: setMessageRecord
     */
    public MessageRecordEntity setMessageRecord(String messageId, PushMesForm from) {
        logger.info("[setMessageRecord]start");
        MessageRecordEntity entity = new MessageRecordEntity();
        try {
            // 消息id
            entity.setMessageId(messageId);
            // 设置消息标题
            entity.setTitle(from.getTitle());
            // 设置消息内容
            entity.setContent(from.getContent());
            // 页面跳转code
            entity.setMessageCode(StringUtil.isEmpty(from.getMessageCode()) ? 0 : Integer.parseInt(from.getMessageCode()));
            //  用户是否可见 1：不可见，不展示到消息里；2：可见，在消息模块里显示
            if (StringUtil.isNotEmpty(from.getIsUserVisible())) {
                entity.setUserVisible(Integer.parseInt(from.getIsUserVisible()));
            } else {
                entity.setUserVisible(Integer.parseInt(PushStaticLocarVal.IS_USER_VISIBLE));
            }
            // 接收的客户端,1、司机，2、车主，3、服务app
            logger.info("[save ing appType={}]", from.getAppType());
            entity.setReceiveAppType(StringUtil.isEmpty(from.getAppType()) ? null : Integer.parseInt(from.getAppType()));
            // 定时消息推送
            if (StringUtil.isNotEmpty(from.getPushTime())) {
                entity.setSendTime(DateUtil.strTimeChangeLong(from.getPushTime() + ":00"));
            } else {
                entity.setSendTime(System.currentTimeMillis());
            }
            // 推送的消息
            entity.setMessageExtra(from.getMessageExtra());
            // 设置大分类
            entity.setType(StringUtil.isEmpty(from.getType()) ? null : Integer.parseInt(from.getType()));
            // 设置小分类
            entity.setStype(StringUtil.isEmpty(from.getStype()) ? null : Integer.parseInt(from.getStype()));
            // 设置消息分类
            entity.setMessageType(StringUtil.isEmpty(from.getMessageType()) ? null : Integer.parseInt(from.getMessageType()));
            // 设置消息分类名称
            entity.setMessageTypeName(from.getMessageTypeName());
            entity.setCreateTime(new Timestamp(System.currentTimeMillis()));
            entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        } catch (Exception e) {
            logger.error("[setMessageRecord] Exception:", e);
            entity = null;
        }
        logger.info("[setMessageRecord]end");
        return entity;
    }

    /**
     * @param messageExtra
     * @return void
     * @Description: ios 前端取值 扩展信息设置
     * @method: setMessageExtra
     */
    public String setMessageExtraMap(String messageExtra) {
        logger.info("[setMessageExtraMap] start");
        String extraStr = "";
        try {
            Map<String, Object> map = JsonUtil.toMap(messageExtra);
            if (map != null) {
                StringBuilder extra = new StringBuilder("{");
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    // 通配符
                    String wildKey = entry.getKey().trim();
                    // 通配符对应的值
                    String value = StringUtil.valueOf(entry.getValue());
                    extra.append("'").append(wildKey).append("'").append(":").append("'").append(value).append("'")
                            .append(",");
                }
                extraStr = extra.substring(0, extra.length() - 1);
                extraStr = extraStr.concat("}");
            }
        } catch (Exception e) {
            logger.error("[setMessageExtraMap] messageExtra Exception:", e);
        }
        logger.info("[setMessageExtraMap] end");
        return extraStr;
    }
}
