package com.nut.tripanalysis.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.exception.BusinessException;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.tripanalysis.app.dto.AvgOilwearRankingDto;
import com.nut.tripanalysis.app.dto.RankingListDto;
import com.nut.tripanalysis.app.form.QueryMonthAvgOilWearForm;
import com.nut.tripanalysis.app.service.RankingService;
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
public class QueryMonthAvgOilWearController extends BaseController {

    @Autowired
    private RankingService rankingService;

    @LoginRequired
    @PostMapping("/queryMonthAvgOilWear")
    public HttpCommandResultWithData businessHandle(@RequestJson QueryMonthAvgOilWearForm command) {
        log.info("================queryMonthAvgOilWear begin========================");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        try {
            List<AvgOilwearRankingDto> list = rankingService.queryMonthOilWear(command);
            RankingListDto rankingListDto = new RankingListDto();
            rankingListDto.setList(list);
            result.setData(rankingListDto);
            log.info("================queryMonthAvgOilWear end========================");
        } catch (Exception e) {
            if (e.getClass() == BusinessException.class) {
                log.info("================queryMonthAvgOilWear end========================");
                return new HttpCommandResultWithData(ECode.CLIENT_ERROR.code(), e.getMessage());
            } else {
                e.printStackTrace();
                log.error("================queryMonthAvgOilWear error========================");
                log.error(e.getMessage());
                return new HttpCommandResultWithData(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message());
            }
        }
        return result;
    }
}
