package com.nut.tripanalysis.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.exception.BusinessException;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.StringUtil;
import com.nut.tripanalysis.app.dto.AvgOilwearRankingDto;
import com.nut.tripanalysis.app.form.QueryWeekAvgOilWearForm;
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
public class QueryWeekAvgOilWearController extends BaseController {
    @Autowired
    private RankingService rankingService;
    @LoginRequired
    @PostMapping("/queryWeekAvgOilWear")
    public HttpCommandResultWithData businessHandle(@RequestJson QueryWeekAvgOilWearForm command) {
        try {
            log.info("================queryWeekAvgOilWear begin========================");
            this.formValidate(command);
            List<AvgOilwearRankingDto> result = rankingService.queryWeekAvgOilWear(command);
            log.info("================queryWeekAvgOilWear end========================");
            if(StringUtil.isNotEmpty(command.getModelId())){
                return new HttpCommandResultWithData(ECode.SUCCESS.code(), ECode.SUCCESS.message(), new ListResult(result));
            }else{
                if(result !=null && result.size() >0) {
                    return new HttpCommandResultWithData(ECode.SUCCESS.code(), ECode.SUCCESS.message(), result.get(0));
                }else{
                    return new HttpCommandResultWithData(ECode.SUCCESS.code(), ECode.SUCCESS.message(), null);
                }
            }

        } catch (Exception e) {
            if (e.getClass() == BusinessException.class) {
                log.info("================queryWeekAvgOilWear end========================");
                return new HttpCommandResultWithData(ECode.CLIENT_ERROR.code(), e.getMessage());
            } else {
                e.printStackTrace();
                log.error("================queryWeekAvgOilWear error========================");
                log.error(e.getMessage());
                return new HttpCommandResultWithData(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message());
            }
        }
    }
}
