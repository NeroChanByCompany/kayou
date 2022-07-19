package com.nut.driver.app.service.impl;

import com.nut.common.push.PushStaticLocarVal;
import com.nut.driver.app.client.TruckingTeamClient;
import com.nut.driver.app.service.AsyncPushSystemMsgService;
import com.nut.truckingteam.app.form.PushDriverOwnerMsgForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nut.driver.app.client.ToolsClient;
import com.nut.driver.app.client.TruckingTeamClient;
import com.nut.driver.app.service.AsyncPushSystemMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import com.nut.common.push.PushStaticLocarVal;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-28 10:46
 * @Version: 1.0
 */
@Slf4j
@Service("AsyncPushSystemMsgService")
public class AsyncPushSystemMsgServiceImpl implements AsyncPushSystemMsgService {

    @Autowired
    private TruckingTeamClient truckingTeamClient;

    @Override
    public void pushAddDriver(String receiverId, Long teamId, String senderId) {
        log.info("[pushAddDriver]start");
        try {
            PushDriverOwnerMsgForm form = new PushDriverOwnerMsgForm();
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_DRIVER_ADD);
            form.setReceiverId(receiverId);
            form.setTeamId(teamId);
            form.setSenderId(senderId);
            truckingTeamClient.pushDriverOwnerMsg(form);
        } catch (Exception e) {
            log.error("[pushAddDriver]Exception:", e);
        }
        log.info("[pushAddDriver]end");
    }

    @Override
    public void pushRemovedFromFleet(String receiverId, Long teamId, String senderId) {
        log.info("[pushRemovedFromFleet]start");
        try {
            PushDriverOwnerMsgForm form = new PushDriverOwnerMsgForm();
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_REMOVED_FROM_FLEET);
            form.setReceiverId(receiverId);
            form.setTeamId(teamId);
            form.setSenderId(senderId);
            truckingTeamClient.pushDriverOwnerMsg(form);
        } catch (Exception e) {
            log.error("[pushRemovedFromFleet]Exception:", e);
        }
        log.info("[pushRemovedFromFleet]end");
    }

    @Override
    public void pushCarDriverBind(String receiverId, Long teamId, String carId, String senderId) {
        log.info("[pushCarDriverBind]start");
        try {
            PushDriverOwnerMsgForm form = new PushDriverOwnerMsgForm();
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_CAR_DRIVER_BIND);
            form.setReceiverId(receiverId);
            form.setTeamId(teamId);
            form.setCarId(carId);
            form.setSenderId(senderId);
            truckingTeamClient.pushDriverOwnerMsg(form);
        } catch (Exception e) {
            log.error("[pushCarDriverBind]Exception:", e);
        }
        log.info("[pushCarDriverBind]end");
    }

    @Override
    public void pushCarDriverUnbind(String receiverId, Long teamId, String carId, String senderId) {
        log.info("[pushCarDriverUnbind]start");
        try {
            PushDriverOwnerMsgForm form = new PushDriverOwnerMsgForm();
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_CAR_DRIVER_UNBIND);
            form.setReceiverId(receiverId);
            form.setTeamId(teamId);
            form.setCarId(carId);
            form.setSenderId(senderId);
            truckingTeamClient.pushDriverOwnerMsg(form);
        } catch (Exception e) {
            log.error("[pushCarDriverBind]Exception:", e);
        }
        log.info("[pushCarDriverUnbind]end");
    }

    @Override
    public void pushDeleteFleet(String receiverId, String teamName, String senderId) {
        log.info("[pushDeleteFleet]start");
        try {
            PushDriverOwnerMsgForm form = new PushDriverOwnerMsgForm();
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_FLEET_DELETE);
            form.setReceiverId(receiverId);
            form.setTeamName(teamName);
            form.setSenderId(senderId);
            log.info("pushDeleteFleet to truckingTeamCliententity:{}", form);
            truckingTeamClient.pushDriverOwnerMsg(form);
        } catch (Exception e) {
            log.error("[pushDeleteFleet]Exception:", e);
        }
        log.info("[pushDeleteFleet]end");
    }

    @Override
    public void pushEditCarTeam(Long teamId, String oldTeamName, String teamName, String senderId) {
        log.info("[pushEditCarTeam]start");
        try {
            PushDriverOwnerMsgForm form = new PushDriverOwnerMsgForm();
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_TEAM_NAME_UPDATE);
            form.setTeamId(teamId);
            form.setOldTeamName(oldTeamName);
            form.setTeamName(teamName);
            form.setSenderId(senderId);
            truckingTeamClient.pushDriverOwnerMsg(form);
        } catch (Exception e) {
            log.error("[pushEditCarTeam]Exception:", e);
        }
        log.info("[pushEditCarTeam]end");
    }
    /**
     * 邀请管理员 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param senderId   发送者ID
     */
    @Async
    public void pushAddAdmin(String receiverId, Long teamId, String senderId) {
        try {
            PushDriverOwnerMsgForm form = new PushDriverOwnerMsgForm();
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_ADMIN_ADD);
            form.setReceiverId(receiverId);
            form.setTeamId(teamId);
            form.setSenderId(senderId);
            truckingTeamClient.pushDriverOwnerMsg(form);
        } catch (Exception e) {
            log.error("[pushAddAdmin]Exception:", e);
        }
    }


    @Override
    public void pushQuitFleet(String receiverId, Long teamId, String role, String senderId) {
        log.info("[pushQuitFleet]start");
        try {
            PushDriverOwnerMsgForm form = new PushDriverOwnerMsgForm();
            form.setStype(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_QUIT_FLEET);
            form.setReceiverId(receiverId);
            form.setTeamId(teamId);
            form.setRole(role);
            form.setSenderId(senderId);
            truckingTeamClient.pushDriverOwnerMsg(form);
        } catch (Exception e) {
            log.error("[pushQuitFleet]Exception:", e);
        }
        log.info("[pushQuitFleet]end");
    }
}
