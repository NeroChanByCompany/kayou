package com.nut.tripanalysis.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.tripanalysis.app.dto.TripInfoDto;
import com.nut.tripanalysis.app.form.QueryTripInfoForm;
import com.nut.tripanalysis.app.service.TripService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 *  @author wuhaotian 2021/7/9
 */
@Slf4j
@RestController
public class QueryTripInfoController extends BaseController {

    @Autowired
    private TripService tripService;
    @LoginRequired
    @PostMapping("/queryTripInfo")
    public HttpCommandResultWithData businessHandle(@RequestJson QueryTripInfoForm command) {
        try {
            log.info("================queryTripInfo begin========================");
            this.formValidate(command);
            TripInfoDto result = tripService.queryTripInfo(command);
            log.info("================queryTripInfo end========================");
            return new HttpCommandResultWithData(ECode.SUCCESS.code(), ECode.SUCCESS.message(), result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("================queryTripInfo error========================");
            log.error(e.getMessage(), e);
            return new HttpCommandResultWithData(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message());

        }
    }
}
