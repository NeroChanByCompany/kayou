package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.servicestation.app.domain.WorkOrder;
import com.nut.servicestation.app.form.PartForm;
import com.nut.servicestation.app.form.WoInfoForm;
import com.nut.servicestation.app.form.WorkOrderForm;
import com.nut.servicestation.app.service.MutualDmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
 *  @author wuhaotian 2021/7/6
 */
@Slf4j
@RestController
public class MutualDMSController extends BaseController {

    @Autowired
    private MutualDmsService mutualDmsService;

    /**
     * 服务APP配件查询-车辆配置查询
     */
    @PostMapping(value = "/vehicleProductSearch")
    @LoginRequired
    public HttpCommandResultWithData vehicleProductSearch(@RequestJson PartForm command) throws Exception {
        this.formValidate(command);
        HttpCommandResultWithData<Map> result = null;
        try {
            result =  mutualDmsService.vehicleProductSearch(command);
            return result;
        }catch (Exception e){
            log.info("车辆配置查询出现问题.", e);
        }
        result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SERVER_ERROR.code());
        result.setMessage("车辆配置查询出现问题");
        return result;
    }
    /**
     * 工单上报给DMS
     */
    @PostMapping(value = "/workOrderReport")
    @LoginRequired
    public HttpCommandResultWithData workOrderReport(@RequestJson WoInfoForm command) throws Exception {
        this.formValidate(command);
        HttpCommandResultWithData<Map> result = null;
        try {
            result =  mutualDmsService.uploadWorkOrderReport(command);
            return result;
        }catch (Exception e){
            log.info("上传工单出现问题.", e);
        }
        result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SERVER_ERROR.code());
        result.setMessage("上传工单出现问题");
        return result;
    }
}
