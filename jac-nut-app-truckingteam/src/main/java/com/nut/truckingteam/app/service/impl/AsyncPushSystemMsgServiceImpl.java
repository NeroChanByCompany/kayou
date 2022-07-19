package com.nut.truckingteam.app.service.impl;

import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.truckingteam.app.client.ToolsClient;
import com.nut.truckingteam.app.dao.CarDao;
import com.nut.truckingteam.app.dao.FltCarOwnerMappingDao;
import com.nut.truckingteam.app.dao.FltFleetDao;
import com.nut.truckingteam.app.entity.CarEntity;
import com.nut.truckingteam.app.entity.FltFleetEntity;
import com.nut.truckingteam.app.form.PushMesForm;
import com.nut.truckingteam.app.service.AsyncPushSystemMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.truckingteam.app.service.ipml
 * @Author: yzl
 * @CreateTime: 2021-06-28 11:15
 * @Version: 1.0
 */
@Slf4j
@Service("AsyncPushSystemMsgService")
public class AsyncPushSystemMsgServiceImpl implements AsyncPushSystemMsgService {

    private static final String YES = "1";
    @Autowired
    private ToolsClient toolsClient;

    @Autowired
    private FltFleetDao fltFleetDao;

    @Autowired
    private CarDao carDao;

    @Autowired
    private FltCarOwnerMappingDao fltCarOwnerMappingDao;


    /**
     * 车牌号变更推送
     *
     * @param carId     车辆ID
     * @param oldCarNum 旧车牌号
     * @param carNum    新车牌号
     * @param senderId  发送者ID
     */
    @Async
    @Override
    public void pushModifyCarNum(String carId, String oldCarNum, String carNum, String senderId) {
        log.info("[pushModifyCarNum]start");
        try {
            PushMesForm form = new PushMesForm();
            // 大type：通知
            form.setType(PushStaticLocarVal.PUSH_TYPE_NOTICE);
            // 小type：车牌号码变更
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_CAR_NUM_UPDATE);
            // 发送者用户id
            form.setSenderId(senderId);
            // 页面跳转code：用户车辆详情
            form.setMessageCode(PushStaticLocarVal.MESSAGE_CODE_CAR_OWNER);
            // 推送类型：指定消息推送
            form.setPushType(PushStaticLocarVal.PUSH_TYPE_MSG_SPECIFIED);
            // 消息展示分类：个人消息（车队APP）
            form.setPushShowType(PushStaticLocarVal.PUSH_SHOW_TYPE_USER);
            // 推送客户端：车队版
            form.setAppType(PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER);
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(4);
            wildcardMap.put("{优先车牌号}", oldCarNum);
            wildcardMap.put("{新车牌号}", carNum);
            String wildcard = JsonUtil.toJson(wildcardMap);
            form.setWildcardMap(wildcard);
            // 扩展信息
            Map<String, String> messageExtraMap = new HashMap<>(3);
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_CAR_ID, carId);
            String messageExtra = JsonUtil.toJson(messageExtraMap);
            form.setMessageExtra(messageExtra);
            // 接收者用户id
            String receiverId = getPushUserIds(carId, null, PushStaticLocarVal.PUSH_USER_ROLE_CREATOR
                    | PushStaticLocarVal.PUSH_USER_ROLE_MANAGER | PushStaticLocarVal.PUSH_USER_ROLE_DRIVER
                    | PushStaticLocarVal.PUSH_USER_ROLE_OWNER);
            form.setReceiverId(receiverId);
            toolsClient.MessagePush(form);
        } catch (Exception e) {
            log.error("[pushModifyCarNum]Exception:", e);
        }
        log.info("[pushModifyCarNum]end");
    }

    /**
     * 车队名称变更推送
     *
     * @param teamId      车队ID
     * @param oldTeamName 旧车队名称
     * @param teamName    新车队名称
     * @param senderId    发送者ID
     */
    @Async
    @Override
    public void pushEditCarTeam(Long teamId, String oldTeamName, String teamName, String senderId) {
        log.info("[pushEditCarTeam]start");
        try {
            PushMesForm form = new PushMesForm();
            // 大type：通知
            form.setType(PushStaticLocarVal.PUSH_TYPE_NOTICE);
            // 小type：车队名称变更
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_TEAM_NAME_UPDATE);
            // 发送者用户id
            form.setSenderId(senderId);
            // 页面跳转code：车队详情
            form.setMessageCode(PushStaticLocarVal.MESSAGE_CODE_FLEET_DETAIL);
            // 推送类型：指定消息推送
            form.setPushType(PushStaticLocarVal.PUSH_TYPE_MSG_SPECIFIED);
            // 消息展示分类：个人消息（车队APP）
            form.setPushShowType(PushStaticLocarVal.PUSH_SHOW_TYPE_USER);
            // 推送客户端：车队版
            form.setAppType(PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER);
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(4);
            wildcardMap.put("{车队名称}", oldTeamName);
            wildcardMap.put("{修改后的车队名称}", teamName);
            String wildcard = JsonUtil.toJson(wildcardMap);
            form.setWildcardMap(wildcard);
            // 扩展信息
            Map<String, Object> messageExtraMap = new HashMap<>(3);
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_TEAM_ID, teamId);
            String messageExtra = JsonUtil.toJson(messageExtraMap);
            form.setMessageExtra(messageExtra);
            // 接收者用户id
            String receiverId = getPushUserIds(null, teamId, PushStaticLocarVal.PUSH_USER_ROLE_CREATOR
                    | PushStaticLocarVal.PUSH_USER_ROLE_MANAGER | PushStaticLocarVal.PUSH_USER_ROLE_DRIVER);
            form.setReceiverId(receiverId);
            // 是否推送：不推送只保存
            form.setNoPushSaveOnly(YES);
            toolsClient.MessagePush(form);
        } catch (Exception e) {
            log.error("[pushEditCarTeam]Exception:", e);
        }
        log.info("[pushEditCarTeam]end");
    }

    /**
     * 管理员或司机被移出车队 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param senderId   发送者ID
     */
    @Async
    @Override
    public void pushRemovedFromFleet(String receiverId, Long teamId, String senderId) {
        log.info("[pushRemovedFromFleet]start");
        try {
            PushMesForm form = new PushMesForm();
            // 大type：通知
            form.setType(PushStaticLocarVal.PUSH_TYPE_NOTICE);
            // 小type：管理员或司机移除车队
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_REMOVED_FROM_FLEET);
            // 发送者用户id
            form.setSenderId(senderId);
            // 页面跳转code：无跳转
            form.setMessageCode(PushStaticLocarVal.MESSAGE_CODE_YUN_YING);
            // 推送类型：指定消息推送
            form.setPushType(PushStaticLocarVal.PUSH_TYPE_MSG_SPECIFIED);
            // 消息展示分类：个人消息（车队APP）
            form.setPushShowType(PushStaticLocarVal.PUSH_SHOW_TYPE_USER);
            // 推送客户端：车队版
            form.setAppType(PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER);
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(3);
            // 查询车队名称
            FltFleetEntity fltFleet = fltFleetDao.selectByPrimaryKey(teamId);
            wildcardMap.put("{车队名称}", fltFleet.getName());
            String wildcard = JsonUtil.toJson(wildcardMap);
            form.setWildcardMap(wildcard);
            // 接收者用户id
            form.setReceiverId(receiverId);
            // 是否推送：不推送只保存
            form.setNoPushSaveOnly(YES);
            toolsClient.MessagePush(form);
        } catch (Exception e) {
            log.error("[pushRemovedFromFleet]Exception:", e);
        }
        log.info("[pushRemovedFromFleet]end");
    }

    /**
     * 邀请司机 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param senderId   发送者ID
     */
    @Async
    @Override
    public void pushAddDriver(String receiverId, Long teamId, String senderId) {
        log.info("[pushAddDriver]start");
        try {
            PushMesForm form = new PushMesForm();
            // 大type：通知
            form.setType(PushStaticLocarVal.PUSH_TYPE_NOTICE);
            // 小type：邀请司机
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_DRIVER_ADD);
            // 发送者用户id
            form.setSenderId(senderId);
            // 页面跳转code：车队详情
            form.setMessageCode(PushStaticLocarVal.MESSAGE_CODE_FLEET_DETAIL);
            // 推送类型：指定消息推送
            form.setPushType(PushStaticLocarVal.PUSH_TYPE_MSG_SPECIFIED);
            // 消息展示分类：个人消息（车队APP）
            form.setPushShowType(PushStaticLocarVal.PUSH_SHOW_TYPE_USER);
            // 推送客户端：车队版
            form.setAppType(PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER);
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(3);
            // 查询车队名称
            FltFleetEntity fltFleet = fltFleetDao.selectByPrimaryKey(teamId);
            wildcardMap.put("{车队名称}", fltFleet.getName());
            String wildcard = JsonUtil.toJson(wildcardMap);
            form.setWildcardMap(wildcard);
            // 扩展信息
            Map<String, Object> messageExtraMap = new HashMap<>(3);
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_TEAM_ID, teamId);
            String messageExtra = JsonUtil.toJson(messageExtraMap);
            form.setMessageExtra(messageExtra);
            // 接收者用户id
            form.setReceiverId(receiverId);
            toolsClient.MessagePush(form);
        } catch (Exception e) {
            log.error("[pushAddDriver]Exception:", e);
        }
        log.info("[pushAddDriver]end");
    }

    /**
     * 邀请管理员 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param senderId   发送者ID
     */
    @Async
    @Override
    public void pushAddAdmin(String receiverId, Long teamId, String senderId) {
        log.info("[pushAddAdmin]start");
        try {
            PushMesForm form = new PushMesForm();
            // 大type：通知
            form.setType(PushStaticLocarVal.PUSH_TYPE_NOTICE);
            // 小type：邀请管理员
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_ADMIN_ADD);
            // 发送者用户id
            form.setSenderId(senderId);
            // 页面跳转code：车队详情
            form.setMessageCode(PushStaticLocarVal.MESSAGE_CODE_FLEET_DETAIL);
            // 推送类型：指定消息推送
            form.setPushType(PushStaticLocarVal.PUSH_TYPE_MSG_SPECIFIED);
            // 消息展示分类：个人消息（车队APP）
            form.setPushShowType(PushStaticLocarVal.PUSH_SHOW_TYPE_USER);
            // 推送客户端：车队版
            form.setAppType(PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER);
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(3);
            // 查询车队名称
            FltFleetEntity fltFleet = fltFleetDao.selectByPrimaryKey(teamId);
            wildcardMap.put("{车队名称}", fltFleet.getName());
            String wildcard = JsonUtil.toJson(wildcardMap);
            form.setWildcardMap(wildcard);
            // 扩展信息
            Map<String, Object> messageExtraMap = new HashMap<>(3);
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_TEAM_ID, teamId);
            String messageExtra = JsonUtil.toJson(messageExtraMap);
            form.setMessageExtra(messageExtra);
            // 接收者用户id
            form.setReceiverId(receiverId);
            toolsClient.MessagePush(form);
        } catch (Exception e) {
            log.error("[pushAddAdmin]Exception:", e);
        }
        log.info("[pushAddAdmin]end");
    }

    /**
     * 司机绑定车辆 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param carId      车辆ID
     * @param senderId   发送者ID
     */
    @Async
    @Override
    public void pushCarDriverBind(String receiverId, Long teamId, String carId, String senderId) {
        log.info("[pushCarDriverBind]start");
        try {
            PushMesForm form = new PushMesForm();
            // 大type：通知
            form.setType(PushStaticLocarVal.PUSH_TYPE_NOTICE);
            // 小type：车辆司机绑定
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_CAR_DRIVER_BIND);
            // 发送者用户id
            form.setSenderId(senderId);
            // 页面跳转code：用户车辆详情
            form.setMessageCode(PushStaticLocarVal.MESSAGE_CODE_CAR_OWNER);
            // 推送类型：指定消息推送
            form.setPushType(PushStaticLocarVal.PUSH_TYPE_MSG_SPECIFIED);
            // 消息展示分类：个人消息（车队APP）
            form.setPushShowType(PushStaticLocarVal.PUSH_SHOW_TYPE_USER);
            // 推送客户端：车队版
            form.setAppType(PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER);
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(4);
            // 查询车队名称
            FltFleetEntity fltFleet = fltFleetDao.selectByPrimaryKey(teamId);
            wildcardMap.put("{车队名称}", fltFleet.getName());
            // 查询车牌号
            CarEntity car = carDao.findById(carId);
            if (StringUtil.isNotEmpty(car.getCarNumber())) {
                wildcardMap.put("{优先车牌号}", car.getCarNumber());
            } else {
                wildcardMap.put("{优先车牌号}", car.getCarVin());
            }
            String wildcard = JsonUtil.toJson(wildcardMap);
            form.setWildcardMap(wildcard);
            // 扩展信息
            Map<String, Object> messageExtraMap = new HashMap<>(3);
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_CAR_ID, carId);
            String messageExtra = JsonUtil.toJson(messageExtraMap);
            form.setMessageExtra(messageExtra);
            // 接收者用户id
            form.setReceiverId(receiverId);
            // 是否推送：不推送只保存
            form.setNoPushSaveOnly(YES);
            toolsClient.MessagePush(form);
        } catch (Exception e) {
            log.error("[pushCarDriverBind]Exception:", e);
        }
        log.info("[pushCarDriverBind]end");
    }

    /**
     * 司机解绑车辆 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param carId      车辆ID
     * @param senderId   发送者ID
     */
    @Async
    @Override
    public void pushCarDriverUnbind(String receiverId, Long teamId, String carId, String senderId) {
        log.info("[pushCarDriverUnbind]start");
        try {
            PushMesForm form = new PushMesForm();
            // 大type：通知
            form.setType(PushStaticLocarVal.PUSH_TYPE_NOTICE);
            // 小type：车辆司机解绑
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_CAR_DRIVER_UNBIND);
            // 发送者用户id
            form.setSenderId(senderId);
            // 页面跳转code：无跳转
            form.setMessageCode(PushStaticLocarVal.MESSAGE_CODE_YUN_YING);
            // 推送类型：指定消息推送
            form.setPushType(PushStaticLocarVal.PUSH_TYPE_MSG_SPECIFIED);
            // 消息展示分类：个人消息（车队APP）
            form.setPushShowType(PushStaticLocarVal.PUSH_SHOW_TYPE_USER);
            // 推送客户端：车队版
            form.setAppType(PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER);
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(4);
            // 查询车队名称
            FltFleetEntity fltFleet = fltFleetDao.selectByPrimaryKey(teamId);
            wildcardMap.put("{车队名称}", fltFleet.getName());
            // 查询车牌号
            CarEntity car = carDao.findById(carId);
            if (StringUtil.isNotEmpty(car.getCarNumber())) {
                wildcardMap.put("{优先车牌号}", car.getCarNumber());
            } else {
                wildcardMap.put("{优先车牌号}", car.getCarVin());
            }
            String wildcard = JsonUtil.toJson(wildcardMap);
            form.setWildcardMap(wildcard);
            // 接收者用户id
            form.setReceiverId(receiverId);
            // 是否推送：不推送只保存
            form.setNoPushSaveOnly(YES);
            toolsClient.MessagePush(form);
        } catch (Exception e) {
            log.error("[pushCarDriverUnbind]Exception:", e);
        }
        log.info("[pushCarDriverUnbind]end");
    }

    /**
     * 退出车队 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param role       角色
     * @param senderId   发送者ID
     */
    @Async
    @Override
    public void pushQuitFleet(String receiverId, Long teamId, String role, String senderId) {
        log.info("[pushQuitFleet]start");
        try {
            PushMesForm form = new PushMesForm();
            // 大type：通知
            form.setType(PushStaticLocarVal.PUSH_TYPE_NOTICE);
            // 小type：退出车队
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_QUIT_FLEET);
            // 发送者用户id
            form.setSenderId(senderId);
            // 页面跳转code：无跳转
            form.setMessageCode(PushStaticLocarVal.MESSAGE_CODE_YUN_YING);
            // 推送类型：指定消息推送
            form.setPushType(PushStaticLocarVal.PUSH_TYPE_MSG_SPECIFIED);
            // 消息展示分类：个人消息（车队APP）
            form.setPushShowType(PushStaticLocarVal.PUSH_SHOW_TYPE_USER);
            // 推送客户端：车队版
            form.setAppType(PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER);
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(4);
            // 查询车队名称
            FltFleetEntity fltFleet = fltFleetDao.selectByPrimaryKey(teamId);
            wildcardMap.put("{车队名称}", fltFleet.getName());
            wildcardMap.put("{角色}", role);
            String wildcard = JsonUtil.toJson(wildcardMap);
            form.setWildcardMap(wildcard);
            // 接收者用户id
            form.setReceiverId(receiverId);
            // 是否推送：不推送只保存
            form.setNoPushSaveOnly(YES);
            toolsClient.MessagePush(form);
        } catch (Exception e) {
            log.error("[pushQuitFleet]Exception:", e);
        }
        log.info("[pushQuitFleet]end");
    }

    /**
     * 解散车队 推送
     *
     * @param receiverId 接收者ID
     * @param teamName   车队名称
     * @param senderId   发送者ID
     */
    @Async
    @Override
    public void pushDeleteFleet(String receiverId, String teamName, String senderId) {
        log.info("[pushDeleteFleet]start");
        try {
            PushMesForm form = new PushMesForm();
            // 大type：通知
            form.setType(PushStaticLocarVal.PUSH_TYPE_NOTICE);
            // 小type：解散车队
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_FLEET_DELETE);
            // 发送者用户id
            form.setSenderId(senderId);
            // 页面跳转code：无跳转
            form.setMessageCode(PushStaticLocarVal.MESSAGE_CODE_YUN_YING);
            // 推送类型：指定消息推送
            form.setPushType(PushStaticLocarVal.PUSH_TYPE_MSG_SPECIFIED);
            // 消息展示分类：个人消息（车队APP）
            form.setPushShowType(PushStaticLocarVal.PUSH_SHOW_TYPE_USER);
            // 推送客户端：车队版
            form.setAppType(PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER);
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(3);
            wildcardMap.put("{车队名称}", teamName);
            String wildcard = JsonUtil.toJson(wildcardMap);
            form.setWildcardMap(wildcard);
            // 接收者用户id
            form.setReceiverId(receiverId);
            // 是否推送：不推送只保存
            form.setNoPushSaveOnly(YES);
            toolsClient.MessagePush(form);
        } catch (Exception e) {
            log.error("[pushDeleteFleet]Exception:", e);
        }
        log.info("[pushDeleteFleet]end");
    }

    /**
     * 转让车队 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param senderId   发送者ID
     */
    @Async
    @Override
    public void pushHandoverFleet(String receiverId, Long teamId, String senderId) {
        log.info("[pushHandoverFleet]start");
        try {
            PushMesForm form = new PushMesForm();
            // 大type：通知
            form.setType(PushStaticLocarVal.PUSH_TYPE_NOTICE);
            // 小type：车队权限转移
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_FLEET_HANDOVER);
            // 发送者用户id
            form.setSenderId(senderId);
            // 页面跳转code：车队详情
            form.setMessageCode(PushStaticLocarVal.MESSAGE_CODE_FLEET_DETAIL);
            // 推送类型：指定消息推送
            form.setPushType(PushStaticLocarVal.PUSH_TYPE_MSG_SPECIFIED);
            // 消息展示分类：个人消息（车队APP）
            form.setPushShowType(PushStaticLocarVal.PUSH_SHOW_TYPE_USER);
            // 推送客户端：车队版
            form.setAppType(PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER);
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(3);
            // 查询车队名称
            FltFleetEntity fltFleet = fltFleetDao.selectByPrimaryKey(teamId);
            wildcardMap.put("{车队名称}", fltFleet.getName());
            String wildcard = JsonUtil.toJson(wildcardMap);
            form.setWildcardMap(wildcard);
            // 扩展信息
            Map<String, Object> messageExtraMap = new HashMap<>(3);
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_TEAM_ID, teamId);
            String messageExtra = JsonUtil.toJson(messageExtraMap);
            form.setMessageExtra(messageExtra);
            // 接收者用户id
            form.setReceiverId(receiverId);
            // 是否推送：不推送只保存
            form.setNoPushSaveOnly(YES);
            toolsClient.MessagePush(form);
        } catch (Exception e) {
            log.error("[pushHandoverFleet]Exception:", e);
        }
        log.info("[pushHandoverFleet]end");
    }

    /**
     * 获取推送用户
     *
     * @param carId        车辆ID
     * @param teamId       车队ID
     * @param userRoleType 推送角色
     * @return 用户ID
     */
    private String getPushUserIds(String carId, Long teamId, int userRoleType) {
        log.info("[getPushUserIds]start");
        StringBuilder pushUserIds = new StringBuilder();
        Map<String, Object> param = new HashMap<>(4);
        param.put("carList", StringUtil.isEmpty(carId) ? null : Arrays.asList(carId.split(",")));
        param.put("teamId", teamId);
        if ((PushStaticLocarVal.PUSH_USER_ROLE_CREATOR & userRoleType) != 0) {
            // 查询车队的创建者
            String userIds = fltCarOwnerMappingDao.queryPushCreator(param);
            if (isValid(userIds)) {
                pushUserIds.append(userIds).append(",");
            }
        }
        if ((PushStaticLocarVal.PUSH_USER_ROLE_MANAGER & userRoleType) != 0) {
            // 查询车辆的管理员
            String userIds = fltCarOwnerMappingDao.queryPushAdmin(param);
            if (isValid(userIds)) {
                pushUserIds.append(userIds).append(",");
            }
        }
        if ((PushStaticLocarVal.PUSH_USER_ROLE_DRIVER & userRoleType) != 0) {
            // 查询车辆的司机
            String userIds = fltCarOwnerMappingDao.queryPushDriver(param);
            if (isValid(userIds)) {
                pushUserIds.append(userIds).append(",");
            }
        }
        if ((PushStaticLocarVal.PUSH_USER_ROLE_OWNER & userRoleType) != 0) {
            // 查询车主
            String userIds = fltCarOwnerMappingDao.queryPushOwner(param);
            if (isValid(userIds)) {
                pushUserIds.append(userIds).append(",");
            }
        }
        log.info("[getPushUserIds]pushUserIds:{}", pushUserIds.toString());
        // 去空并去重
        String result = Stream.of(pushUserIds.toString().split(","))
                .filter(StringUtil::isNotEmpty).distinct().collect(Collectors.joining(","));
        log.info("[getPushUserIds]result:{}", result);
        return result;
    }

    /**
     * 有效字符串
     */
    private boolean isValid(String str) {
        return StringUtil.isNotEmpty(str) && !"null".equals(str);
    }
}
