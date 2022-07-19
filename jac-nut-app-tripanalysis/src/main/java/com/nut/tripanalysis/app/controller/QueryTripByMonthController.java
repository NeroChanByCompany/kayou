package com.nut.tripanalysis.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.tripanalysis.app.form.QueryTripByMonthForm;
import com.nut.tripanalysis.app.service.TripService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/*
 *  @author wuhaotian 2021/7/9
 */

@Slf4j
@RestController
public class QueryTripByMonthController extends BaseController {

    @Autowired
    private TripService tripService;
    @LoginRequired
    @PostMapping("/queryTripByMonth")
    public HttpCommandResultWithData businessHandle(@RequestJson QueryTripByMonthForm command) throws Exception {
        log.info("================queryTripByMonth begin========================");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = tripService.queryTripByMonth(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("月行程数据查询失败!");
            log.error(e.getMessage(), e);
        }
        log.info("================queryTripByMonth end========================");
        return result;
    }
}

