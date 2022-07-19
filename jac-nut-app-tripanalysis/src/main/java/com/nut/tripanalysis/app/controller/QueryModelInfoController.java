package com.nut.tripanalysis.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.tripanalysis.app.dto.ModelInfoDto;
import com.nut.tripanalysis.app.form.QueryModelInfoForm;
import com.nut.tripanalysis.app.service.ModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/10
 */
@Slf4j
@RestController
public class QueryModelInfoController extends BaseController {

    @Autowired
    private ModelService modelService;

    @LoginRequired
    @PostMapping("/queryModelInfo")
    public HttpCommandResultWithData businessHandle(@RequestJson QueryModelInfoForm command) {
        try {
            log.info("================queryModelInfo begin========================");
            List<ModelInfoDto> result = modelService.queryModelInfo(command);
            log.info("================queryModelInfo end========================");
            return new HttpCommandResultWithData(ECode.SUCCESS.code(), ECode.SUCCESS.message(), result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("================queryModelInfo error========================");
            log.error(e.getMessage());
            return new HttpCommandResultWithData(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message());
        }
    }
}
