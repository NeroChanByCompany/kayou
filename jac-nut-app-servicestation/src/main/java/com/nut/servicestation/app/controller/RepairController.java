package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.RepairRecordDetailForm;
import com.nut.servicestation.app.form.RepairRecordSaveForm;
import com.nut.servicestation.app.form.RepairRecordsForm;
import com.nut.servicestation.app.form.TimesRescueForm;
import com.nut.servicestation.app.service.RepairService;
import com.nut.servicestation.app.service.StartEndRepairService;
import com.nut.servicestation.app.service.UptimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/2
 */
@RestController
@Slf4j
public class RepairController extends BaseController {

    @Autowired
    private RepairService repairService;
    @Autowired
    private UptimeService uptimeService;

    @Autowired
    private StartEndRepairService startEndRepairService;


    /**
     * @param command
     * @Description: 维修项列表查询
     * @method: businessHandle
     * @Date: 2018/5/7 18:56
     * @authur: wuhaotian
     */
    @PostMapping(value = "/repairRecords")
    @LoginRequired
    public HttpCommandResultWithData repairRecords(@RequestJson RepairRecordsForm command) throws Exception {
        log.info("[businessHandle] 维修项列表查询 start");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = repairService.repairRecords(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[businessHandle] 维修项列表查询 end");
        return result;
    }
    /**
     * @param command
     * @Description: 维修项提交
     * @method: businessHandle
     * @Date: 2018/5/7 18:56
     * @authur: wuhaotian
     */
    @PostMapping(value = "/repairRecordSave")
    @LoginRequired
    public HttpCommandResultWithData repairRecordSave(@RequestJson RepairRecordSaveForm command) throws Exception {
        log.info("[businessHandle] 维修项提交 start");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        boolean lockGet = false;
        try {
            // 由原来的在service获取分布式锁提到在controller中获取分布式锁。解决维修项并发提交问题
            if (!startEndRepairService.lock(command.getWoCode(), command.getOperateId())) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("处理中，请稍后");
                return result;
            }
            lockGet = true;
            result = repairService.repairRecordSave(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("提交失败");
            log.error(e.getMessage(), e);
        }finally {
            if (lockGet) {
                // 释放分布式锁
                startEndRepairService.unlock(command.getWoCode(), command.getOperateId());
            }
        }
        log.info("[businessHandle] 维修项提交 end");
        return result;
    }
    /**
     * @param command
     * @Description: 维修项详情查询
     * @method: businessHandle
     * @Date: 2018/5/7 18:56
     * @authur: wuhaotian
     */
    @PostMapping(value = "/repairRecordDetail")
    @LoginRequired
    public HttpCommandResultWithData repairRecordDetail(@RequestJson RepairRecordDetailForm command) throws Exception {
        log.info("[businessHandle] 维修项详情查询 start");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = repairService.repairRecordDetail(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[businessHandle] 维修项详情查询 end");
        return result;
    }
    /**
     * @param command
     * @Description: 二次外出
     * @method: businessHandle
     * @Date: 2018/5/7 18:56
     * @authur: wuhaotian
     */
    @PostMapping(value = "/timesRescue")
    @LoginRequired
    public HttpCommandResultWithData timesRescue(@RequestJson TimesRescueForm command) throws Exception {
        log.info("[businessHandle] 二次外出 start");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = repairService.timesRescue(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("提交失败");
            log.error(e.getMessage(), e);
        }
        log.info("[businessHandle] 二次外出 end");
        uptimeService.trigger(result, command.getWoCode(), ServiceStationVal.WEB_SERVICE_TWORESCUELAUNCHTIME,
                "", "");
        return result;
    }
}
