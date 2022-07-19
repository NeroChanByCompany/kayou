package com.nut.servicestation.app.service.impl;

import com.nut.common.constant.ServiceStationVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.dao.WorkOrderOperateDao;
import com.nut.servicestation.app.dao.WorkOrderOperatePhotoDao;
import com.nut.servicestation.app.domain.WorkOrderOperatePhoto;
import com.nut.servicestation.app.service.AsyTriggerFaultyPartService;
import com.nut.servicestation.app.service.UptimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@Service("AsyTriggerFaultyPartService")
public class AsyTriggerFaultyPartServiceImpl implements AsyTriggerFaultyPartService {

    private String usedPhotoType = "P40A,P40B,P40C,P41A,P41B,P41C,P42A,P42B,P42C,P50A,P50B,P50C,P51A,P51B,P51C,P52A,P52B,P52C,P90A,P90B,P90C,P91A,P91B,P91C,P92A,P92B,P92C,P100A,P100B,P100C,P101A,P101B,P101C,P102A,P102B,P102C,P140A,P140B,P140C,P141A,P141B,P141C,P142A,P142B,P142C,P150A,P150B,P150C,P151A,P151B,P151C,P152A,P152B,P152C";

    @Autowired
    private UptimeService uptimeService;
    @Autowired
    private WorkOrderOperateDao workOrderOperateMapper;
    @Autowired
    private WorkOrderOperatePhotoDao workOrderOperatePhotoMapper;

    @Override
    @Async
    public void triggerFaultyPart(String type, String woCode, String operateId) {
        if (!StringUtil.containMark(usedPhotoType, type)) {
            log.info("[triggerFaultyPart]no type:" + type);
            return;
        }
        log.info("[triggerFaultyPart]trigger:{},{},{}:", type, woCode, operateId);
        Integer repairOperatePhotoNum = workOrderOperateMapper.selectRepairPhotoNumByWoCodeAndOperateId(woCode, operateId);
        Map<String, Object> photoParam = new HashMap<>(2);
        photoParam.put("woCode", woCode);
        photoParam.put("operateId", operateId);
        List<WorkOrderOperatePhoto> photos = workOrderOperatePhotoMapper.selectByWoCodeAndOperateId(photoParam);

        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        if (repairOperatePhotoNum != null && photos != null) {
            if (repairOperatePhotoNum == photos.size()) {
                // 检查照片都上传后，同步crm
                uptimeService.trigger(result, woCode, ServiceStationVal.WEB_SERVICE_WORKORDERENCLOSURE_1,
                        "manual", operateId);
            }
        } else {
            if (photos != null && photos.size() >= 3) {
                // 检查照片都上传后，同步crm
                uptimeService.trigger(result, woCode, ServiceStationVal.WEB_SERVICE_WORKORDERENCLOSURE_1,
                        "manual", operateId);
            }
        }
    }
}
