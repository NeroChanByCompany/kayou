package com.nut.tripanalysis.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.exception.BusinessException;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.StringUtil;
import com.nut.tripanalysis.app.dto.AvgOilwearRankingDto;
import com.nut.tripanalysis.app.form.QueryYesterdayAvgOilWearForm;
import com.nut.tripanalysis.app.service.RankingService;
import com.nut.tripanalysis.common.component.TokenComponent;
import com.nut.tripanalysis.common.result.ListResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/10
 */
@Slf4j
@RestController
public class QueryYesterdayAvgOilWearController extends BaseController {

    @Autowired
    private RankingService rankingService;

    @LoginRequired
    @PostMapping("/queryYesterdayAvgOilWear")
    public HttpCommandResultWithData businessHandle(@RequestJson QueryYesterdayAvgOilWearForm command) {
        try {
            log.info("================queryYesterdayAvgOilWear begin========================");
            this.formValidate(command);
            List<AvgOilwearRankingDto> result = rankingService.queryYesterdayOilWear(command);
            log.info("================queryYesterdayAvgOilWear end========================");
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
                log.info("================queryYesterdayAvgOilWear end========================");
                return new HttpCommandResultWithData(ECode.CLIENT_ERROR.code(), e.getMessage());
            } else {
                e.printStackTrace();
                log.error("================queryYesterdayAvgOilWear error========================");
                log.error(e.getMessage());
                return new HttpCommandResultWithData(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message());
            }
        }
    }
}
