package com.nut.truckingteam.app.controller;


import com.nut.common.base.BaseController;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.truckingteam.app.form.PushDriverOwnerMsgForm;
import com.nut.truckingteam.app.service.AsyncPushSystemMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 供司机app/车队app/车队web调用的司机车队相关的推送接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.truckingteam.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-28 10:57
 * @Version: 1.0
 */
@RestController
@Slf4j
public class PushDriverOwnerMsgController extends BaseController {

    @Autowired
    private AsyncPushSystemMsgService asyncPushSystemMsgService;

    @PostMapping(value = "/pushDriverOwnerMsg")
    public HttpCommandResultWithData pushDriverOwnerMsg(@ApiIgnore @RequestBody PushDriverOwnerMsgForm form){
        try {
            switch (form.getStype()) {
                case PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_CAR_NUM_UPDATE:
                    // 车牌号码变更
                    asyncPushSystemMsgService.pushModifyCarNum(form.getCarId(), form.getOldCarNum(), form.getCarNum(), form.getSenderId());
                    break;
                case PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_TEAM_NAME_UPDATE:
                    // 车队名称变更
                    asyncPushSystemMsgService.pushEditCarTeam(form.getTeamId(), form.getOldTeamName(), form.getTeamName(), form.getSenderId());
                    break;
                case PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_REMOVED_FROM_FLEET:
                    // 管理员或司机被移出车队
                    asyncPushSystemMsgService.pushRemovedFromFleet(form.getReceiverId(), form.getTeamId(), form.getSenderId());
                    break;
                case PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_DRIVER_ADD:
                    // 邀请司机
                    asyncPushSystemMsgService.pushAddDriver(form.getReceiverId(), form.getTeamId(), form.getSenderId());
                    break;
                case PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_ADMIN_ADD:
                    // 邀请管理员
                    asyncPushSystemMsgService.pushAddAdmin(form.getReceiverId(), form.getTeamId(), form.getSenderId());
                    break;
                case PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_CAR_DRIVER_BIND:
                    // 司机绑定车辆
                    asyncPushSystemMsgService.pushCarDriverBind(form.getReceiverId(), form.getTeamId(), form.getCarId(), form.getSenderId());
                    break;
                case PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_CAR_DRIVER_UNBIND:
                    // 司机解绑车辆
                    asyncPushSystemMsgService.pushCarDriverUnbind(form.getReceiverId(), form.getTeamId(), form.getCarId(), form.getSenderId());
                    break;
                case PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_QUIT_FLEET:
                    // 退出车队
                    asyncPushSystemMsgService.pushQuitFleet(form.getReceiverId(), form.getTeamId(), form.getRole(), form.getSenderId());
                    break;
                case PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_FLEET_DELETE:
                    // 解散车队
                    asyncPushSystemMsgService.pushDeleteFleet(form.getReceiverId(), form.getTeamName(), form.getSenderId());
                    break;
                case PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_FLEET_HANDOVER:
                    // 车队权限转移
                    asyncPushSystemMsgService.pushHandoverFleet(form.getReceiverId(), form.getTeamId(), form.getSenderId());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "推送失败");
            log.error(e.getMessage(), e);
        }
        return Result.ok();
    }

}
