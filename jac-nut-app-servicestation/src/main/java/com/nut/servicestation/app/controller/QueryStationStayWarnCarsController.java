package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.QueryStationStayWarnCarsForm;
import com.nut.servicestation.app.service.WarnInTheStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/7
 */
@Slf4j
@RestController
public class QueryStationStayWarnCarsController {

    @Autowired
    private WarnInTheStationService warnInTheStationService;


    @PostMapping(value = "/stationStayWarnCars")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson QueryStationStayWarnCarsForm command) {
        log.info("[QueryStationStayWarnCarsController] start ......");
        HttpCommandResultWithData result = new HttpCommandResultWithData();

        try {
            result = warnInTheStationService.queryStationStayWarnCars(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("站内预警地图信息查询失败!");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStationStayWarnCarsController] end ......");
        return result;
    }
}
