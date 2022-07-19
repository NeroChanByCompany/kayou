package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.common.utils.RegexpUtils;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.form.QueryWoListByStatusForm;
import com.nut.servicestation.app.form.queryOrderListForm;
import com.nut.servicestation.app.service.QueryWoListByStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@RestController
public class QueryWoListByStatusController extends BaseController {
    @Autowired
    private QueryWoListByStatusService queryWoListByStatusService;

    /**
     * @param command
     * @return
     * @Description: 工单列表查询
     */
    @PostMapping(value = "/queryWoListByStatus")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson QueryWoListByStatusForm command) throws Exception {
        log.info("[businessHandle]start");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            // 时间验证
            String error = checkTime(command.getTimeCloseStart(), command.getTimeCloseEnd());
            if (StringUtil.isNotEmpty(error)) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage(error);
            } else {
                result = queryWoListByStatusService.queryWoListByStatus(command);
            }
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[businessHandle]end");
        return result;
    }
    /**
     * @param timeCloseStart
     * @param timeCloseEnd
     * @return java.lang.String
     * @Description: 时间验证
     * @method: checkTime
     */
    private String checkTime(String timeCloseStart, String timeCloseEnd) {
        String error = "";
        // 非必填参数校验日期格式
        if (StringUtil.isNotEmpty(timeCloseStart) && !RegexpUtils.validateInfo(timeCloseStart, RegexpUtils.DATE_YYYY_MM_DD_REGEXP)) {
            error = "开始时间格式不正确";
        }
        if (StringUtil.isNotEmpty(timeCloseEnd) && !RegexpUtils.validateInfo(timeCloseEnd, RegexpUtils.DATE_YYYY_MM_DD_REGEXP)) {
            error = "结束时间格式不正确";
        }
        if (StringUtil.isNotEmpty(timeCloseStart) && StringUtil.isNotEmpty(timeCloseEnd)) {
            // 开始时间与结束时间的比对
            if (timeCloseStart.compareTo(timeCloseEnd) > 0) {
                error = "开始时间不能大于结束时间";
            }
        }
        return error;
    }


    /**
     * @param command
     * @Description: 抢单工单列表查询
     */
    @PostMapping(value = "/queryOrderList")
    @LoginRequired
    public HttpCommandResultWithData queryOrderList(queryOrderListForm command) throws Exception {
        log.info("[businessHandle]start");
        return Result.ok(queryWoListByStatusService.queryOrderList(command));
    }
}
