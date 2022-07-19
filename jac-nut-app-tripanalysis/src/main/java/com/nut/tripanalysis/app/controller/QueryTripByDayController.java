package com.nut.tripanalysis.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.tripanalysis.app.form.QueryTripByDayForm;
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
public class QueryTripByDayController extends BaseController {

    @Autowired
    private TripService tripService;

    @LoginRequired
    @PostMapping("/queryTripByDay")
    public HttpCommandResultWithData businessHandle(@RequestJson QueryTripByDayForm command) throws Exception {
        log.info("[QueryTripByDayHandler] start ...");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        log.info(command.toString());
        try {
            result = tripService.queryTripByDay(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("日行程数据查询失败!");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryTripByDayHandler] end ...");
        return result;
    }
}
