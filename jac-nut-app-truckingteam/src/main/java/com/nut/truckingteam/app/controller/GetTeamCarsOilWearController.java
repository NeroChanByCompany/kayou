package com.nut.truckingteam.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.RedisComponent;
import com.nut.truckingteam.app.dao.UserDao;
import com.nut.truckingteam.app.form.GetTeamCarsOilWearForm;
import com.nut.truckingteam.app.form.User;
import com.nut.truckingteam.app.service.TripAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
 *  @author wuhaotian 2021/7/10
 */
@Slf4j
@RestController
public class GetTeamCarsOilWearController extends BaseController {
    @Autowired
    private TripAnalysisService tripAnalysisService;

    @LoginRequired
    @PostMapping(value = "/getTeamCarsOilWear")
    public HttpCommandResultWithData businessHandle(@RequestBody GetTeamCarsOilWearForm command) throws Exception {
        log.info("--------GetTeamCarsOilWearController start--------");
        this.formValidate(command);

        HttpCommandResultWithData result = new HttpCommandResultWithData<>();
        try {
            result = tripAnalysisService.getTeamCarsOilWear(command);
            log.info("--------GetTeamCarsOilWearController end--------");
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("获取车队车辆失败");
            log.error(e.getMessage(), e);
        }
        return result;
    }

}
