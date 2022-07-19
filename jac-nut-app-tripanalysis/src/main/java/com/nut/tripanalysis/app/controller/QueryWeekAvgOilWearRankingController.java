package com.nut.tripanalysis.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.exception.BusinessException;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.tripanalysis.app.dto.AvgOilwearRankingDto;
import com.nut.tripanalysis.app.form.QueryWeekAvgOilWearRankingForm;
import com.nut.tripanalysis.app.service.RankingService;
import com.nut.tripanalysis.common.result.ListResult;
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
public class QueryWeekAvgOilWearRankingController extends BaseController {

    @Autowired
    private RankingService rankingService;
    @LoginRequired
    @PostMapping("/queryWeekAvgOilWearRanking")
    public HttpCommandResultWithData businessHandle(@RequestJson QueryWeekAvgOilWearRankingForm command) {
        try {
            log.info("================queryWeekAvgOilWearRanking begin========================");
            this.formValidate(command);
            List<AvgOilwearRankingDto> result = rankingService.queryWeekAvgOilWearRanking(command,true);
            log.info("================queryWeekAvgOilWearRanking end========================");
            return new HttpCommandResultWithData(ECode.SUCCESS.code(), ECode.SUCCESS.message(), new ListResult(result));
        } catch (Exception e) {
            if (e.getClass() == BusinessException.class) {
                log.info("================queryWeekAvgOilWearRanking end========================");
                return new HttpCommandResultWithData(ECode.CLIENT_ERROR.code(), e.getMessage());
            } else {
                e.printStackTrace();
                log.error("================queryWeekAvgOilWearRanking error========================");
                log.error(e.getMessage());
                return new HttpCommandResultWithData(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message());
            }
        }
    }
}
