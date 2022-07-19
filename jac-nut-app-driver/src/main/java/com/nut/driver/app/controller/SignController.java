package com.nut.driver.app.controller;

import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.dao.SignDao;
import com.nut.driver.app.form.QuerySignNumberForm;
import com.nut.driver.app.form.SignInForm;
import com.nut.driver.app.pojo.SignPojo;
import com.nut.driver.app.service.IntegralService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @Description: 签到接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-08-09 15:56
 * @Version: 1.0
 */
@RestController
@Slf4j
public class SignController extends BaseController {

    @Autowired
    private SignDao signDao;

    @Autowired
    private IntegralService integralService;

    @Value("${guoQingStart}")
    private String guoQingStart;

    @Value("${guoQingEnd}")
    private String guoQingEnd;


    @PostMapping(value = "/querySignNumber")
    @Transactional
    public HttpCommandResultWithData querySignNumber(@RequestBody QuerySignNumberForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        Map<String, Object> map = new HashMap<>();
        List<SignPojo> list = new ArrayList<>();
        List list2 = new ArrayList();
        Integer continueTime = 0;
        try {
            Integer id = signDao.selectIdInNumber(form.getUcId());
            if (id == null) {
                map.put("continueTime", 0);
                map.put("list", null);
                result.setData(map);
                return result;
            }
            String str = signDao.selectLastTime(form.getUcId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDate lastTime = LocalDate.parse(str, fmt);
            LocalDate nowTime = LocalDate.now();
            Long daysDiff = ChronoUnit.DAYS.between(lastTime, nowTime);
            if (daysDiff <= 1) {
                continueTime = signDao.selectContinueTime(form.getUcId());
            }
            list = signDao.selectSignNumber(form.getYear(), form.getMonth(), form.getUcId());
            for (SignPojo signPojo : list) {
                list2.add(new HashMap<String, Object>() {{
                    put("time", signPojo.toString());
                }});
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询时出现问题");
        }
        map.put("continueTime", continueTime);
        map.put("list", list2);
        result.setData(map);
        return result;
    }

    @PostMapping(value = "/signIn")
    @Transactional
    public HttpCommandResultWithData signIn(@RequestBody SignInForm form) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        String ucId = form.getUcId();
        LocalDate nowTime = LocalDate.now();
        int score = 5;
        Integer count = signDao.selectToNow(ucId);
        if (count != null) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("今天已签到");
            return result;
        }
        /**
         * 活动期间添加积分 持续时间2021/09/20-2021/09/26
         * 2021/9/15 13:45 YZL
         */
        log.info("签到活动添加积分");
        integralService.midAutumnByRuleId(ucId,71);
        integralService.FestivalByRuleId(ucId,81,guoQingStart,guoQingEnd);

        Integer id = signDao.selectIdInNumber(ucId);
        Integer continueTime = signDao.selectContinueTime(form.getUcId());
        if (id != null) {
            String str = signDao.selectLastTime(form.getUcId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDate lastTime = LocalDate.parse(str, fmt);
            Long daysDiff = ChronoUnit.DAYS.between(lastTime, nowTime);
            if (daysDiff <= 1) {
                if (continueTime == null) {
                    continueTime = 1;
                } else {
                    continueTime += 1;
                }
                try {
                    switch (continueTime) {
                        case 1:
                            integralService.addAction(ucId, 31);
                            break;
                        case 2:
                            integralService.addAction(ucId, 32);
                            score = 7;
                            break;
                        case 3:
                            integralService.addAction(ucId, 33);
                            score = 9;
                            break;
                        case 4:
                            integralService.addAction(ucId, 34);
                            score = 11;
                            break;
                        case 5:
                            integralService.addAction(ucId, 35);
                            score = 13;
                            break;
                        case 6:
                            integralService.addAction(ucId, 36);
                            score = 15;
                            break;
                        case 7:
                            integralService.addAction(ucId, 37);
                            score = 17;
                            break;
                        default:
                            integralService.addAction(ucId, 38);
                            score = 19;
                    }
                } catch (Exception e) {
                    log.info("签到时出现异常：{}", e.getMessage());
                    result.setResultCode(ECode.CLIENT_ERROR.code());
                    result.setMessage("签到失败，请重试");
                }
                signDao.updateToNumber(ucId, continueTime, new Date());
            } else {
                continueTime = 1;
                integralService.addAction(ucId, 31);
                signDao.updateToNumber(ucId, continueTime, new Date());
            }
        } else {
            continueTime = 1;
            integralService.addAction(ucId, 31);
            signDao.insertToNumber(ucId, continueTime, new Date());
        }
        Integer year = nowTime.getYear();
        Integer month = nowTime.getMonthValue();
        Integer day = nowTime.getDayOfMonth();
        signDao.inserToRecord(year, month, day, ucId, new Date());
        Map map = new HashMap();
        map.put("continueTime", continueTime);
        map.put("score", score);
        result.setData(map);
        return result;
    }

}
