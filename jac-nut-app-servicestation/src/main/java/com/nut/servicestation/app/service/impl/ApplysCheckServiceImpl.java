package com.nut.servicestation.app.service.impl;

import com.nut.common.constant.ServiceStationVal;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.client.ToolsClient;
import com.nut.servicestation.app.dao.CarDao;
import com.nut.servicestation.app.dao.UserDao;
import com.nut.servicestation.app.domain.User;
import com.nut.servicestation.app.domain.WorkOrder;
import com.nut.servicestation.app.service.ApplysCheckService;
import com.nut.servicestation.app.service.AsyPushMessageService;
import com.nut.tools.app.form.SendSmsForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/7
 */
@Slf4j
@Service("ApplysCheckService")
public class ApplysCheckServiceImpl implements ApplysCheckService {


    @Autowired
    private CarDao carMapper;
    @Value("${customerServicePhone:4009005555}")
    private String customerServicePhone;
    @Autowired
    private UserDao userMapper;
    @Autowired
    private AsyPushMessageService asyPushMessageService;
    /**
     * 短信模板
     */
    @Value("${sms.template.wo.closed:工单{工单号}，车辆{优先车牌号}提交的工单已被客服人员关闭，若有任何疑问，请致电联系{400电话}。}")
    private String smsTemplate;
    @Autowired
    private ToolsClient toolsClient;

    @Override
    public void pushWoClose(WorkOrder workOrder, String senderId) {
        log.info("[pushWoClose]start");
        try {
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(7);
            String chassisNum = workOrder.getChassisNum();
            String carNumber = carMapper.queryCarNumberByVin(chassisNum);
            wildcardMap.put("{优先车牌号}", StringUtil.isNotEmpty(carNumber) ? carNumber : chassisNum);
            wildcardMap.put("{工单号}", workOrder.getWoCode());
            wildcardMap.put("{400电话}", customerServicePhone);
            if (ServiceStationVal.STATION_SERVICE == workOrder.getWoType()) {
                // 进出站
                wildcardMap.put("{工单类型}", "进站");
            } else {
                // 外出救援
                wildcardMap.put("{工单类型}", "外出救援");
            }
            String wildcard = JsonUtil.toJson(wildcardMap);
            // 扩展信息
            Map<String, String> messageExtraMap = new HashMap<>(3);
            // 工单号
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_CODE, workOrder.getWoCode());
            String messageExtra = JsonUtil.toJson(messageExtraMap);
            asyPushMessageService.pushToDriverAndOwner(PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_CLOSE, wildcard,
                    messageExtra, senderId, null, chassisNum);

            /* 短信 */
            if (StringUtil.isNotEmpty(workOrder.getAppoUserPhone())) {
                User queryUser = userMapper.selectByPhone(workOrder.getAppoUserPhone());
                if (queryUser == null || StringUtil.isEmpty(queryUser.getUcId())) {
                    // 未注册时
                    SendSmsForm sendSmsCommand = new SendSmsForm();
                    String content = smsTemplate.replace("{工单号}", workOrder.getWoCode())
                            .replace("{优先车牌号}", StringUtil.isNotEmpty(carNumber) ? carNumber : chassisNum)
                            .replace("{400电话}", customerServicePhone);
                    log.info("[pushWoClose]sms content:{}", content);
                    sendSmsCommand.setContent(content);
                    sendSmsCommand.setPhone(workOrder.getAppoUserPhone());
                    toolsClient.sendSms(sendSmsCommand);
                }
            }
        } catch (Exception e) {
            log.info("[pushWoClose]Exception:", e);
        }
        log.info("[pushWoClose]end");
    }
}
