package com.nut.driver.app.service.impl;

import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.dao.*;
import com.nut.driver.app.entity.ActivityEntity;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.form.GuoQingForm;
import com.nut.driver.app.form.MidAutumnForm;
import com.nut.driver.app.form.Shuang11Form;
import com.nut.driver.app.service.ActiveService;
import com.nut.driver.app.service.IntegralService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-29 10:41
 * @Version: 1.0
 */
@Service
@Slf4j
public class ActiveServiceImpl implements ActiveService {


    @Autowired
    private ActivityLuckyUserDao activityLuckyUserDao;

    @Autowired
    private UserDao userDao;


    @Autowired
    ActivityDao activityDao;

    @Autowired
    IntegralService integralService;

    @Value("${midAutumnStart}")
    private String midAutumnStart;

    @Value("${midAutumnEnd}")
    private String midAutumnEnd;

    @Value("${guoQingStart}")
    private String guoQingStart;

    @Value("${guoQingEnd}")
    private String guoQingEnd;

    @Value("${shuang11Start}")
    private String shuang11Start;

    @Value("${shuang11End}")
    private String shuang11End;


    /**
     * @Description：判断活动条件
     * @author YZL
     * @data 2021/6/29 10:47
     */
    @SneakyThrows
    @Override
    public boolean active1Check(long userId) {
        UserEntity user = userDao.selectByPrimaryKey(userId);
        if (user == null || user.getPhone() == null || user.getCreateTime() == null || user.getAppType() == null) {
            return false;
        }
        log.info("allowCheck end param:{}", activityLuckyUserDao.countByPhoneAndAppType(user.getPhone(), user.getAppType()) == 0);
        return activityLuckyUserDao.countByPhoneAndAppType(user.getPhone(), user.getAppType()) != 0;
    }


    /**
     * @Author liuBing
     * @Description //TODO 查询所有活动列表-entity版
     * @Date 18:20 2021/5/27
     * @Param []
     **/
    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<ActivityEntity> checkList() {
        List<ActivityEntity> activities = activityDao.checkList();
        return activities;
    }

    @Override
    public HttpCommandResultWithData midAutumn(MidAutumnForm form) {
        if (form.getMidAutumnType() == 0) {
            log.info("用户浏览车辆网");
            return integralService.midAutumnByRuleId(form.getUserId(), 74);
        } else if (form.getMidAutumnType() == 1) {
            log.info("用户推荐注册");
            return Result.ok();
        }
        return Result.result(ECode.CLIENT_ERROR.code(), "积分添加条件不满足");
    }

    @Override
    public HttpCommandResultWithData timeIsEnd() {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(midAutumnStart, sdf);
        LocalDateTime endDate = LocalDateTime.parse(midAutumnEnd, sdf);
        LocalDateTime now = LocalDateTime.now();
        // 判断时间是否处于两者之间
        if (startDate.isBefore(now) && endDate.isAfter(now)) {
            result.setResultCode(ECode.SUCCESS.code());
            return result;
        } else {
            if (startDate.isAfter(now)) {
                result.setResultCode(508);
                result.setMessage("活动未开始");
            } else {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("活动已结束");
            }
            return result;
        }
    }

    @Override
    public HttpCommandResultWithData guoqing(GuoQingForm form) {
        if (form.getGuoQingType() == 0) {
            log.info("用户浏览直销商城");
            return integralService.FestivalByRuleId(form.getUserId(), 84, guoQingStart, guoQingEnd);
        }
        return Result.result(ECode.CLIENT_ERROR.code(), "积分添加条件不满足");
    }

    @Override
    public HttpCommandResultWithData shuang11(Shuang11Form form) {
        if (form.getShuang11Type() == 0) {
            log.info("用户浏国六");
            return integralService.FestivalByRuleId(form.getUserId(), 91, shuang11Start, shuang11End);
        }else if (form.getShuang11Type() == 1){
            log.info("用户浏配件");
            return integralService.FestivalByRuleId(form.getUserId(), 92, shuang11Start, shuang11End);
        }else if (form.getShuang11Type() == 2){
            log.info("用户浏整车");
            return integralService.FestivalByRuleId(form.getUserId(), 93, shuang11Start, shuang11End);
        }
        return Result.result(ECode.CLIENT_ERROR.code(), "积分添加条件不满足");
    }

    @Override
    public HttpCommandResultWithData timeIsEndGuoQing() {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(guoQingStart, sdf);
        LocalDateTime endDate = LocalDateTime.parse(guoQingEnd, sdf);
        LocalDateTime now = LocalDateTime.now();
        // 判断时间是否处于两者之间
        if (startDate.isBefore(now) && endDate.isAfter(now)) {
            result.setResultCode(ECode.SUCCESS.code());
            return result;
        } else {
            if (startDate.isAfter(now)) {
                result.setResultCode(508);
                result.setMessage("活动未开始");
            } else {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("活动已结束");
            }
            return result;
        }
    }

    @Override
    public HttpCommandResultWithData timeIsEndBy11() {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(shuang11Start, sdf);
        LocalDateTime endDate = LocalDateTime.parse(shuang11End, sdf);
        LocalDateTime now = LocalDateTime.now();
        // 判断时间是否处于两者之间
        if (startDate.isBefore(now) && endDate.isAfter(now)) {
            result.setResultCode(ECode.SUCCESS.code());
            return result;
        } else {
            if (startDate.isAfter(now)) {
                result.setResultCode(508);
                result.setMessage("活动未开始");
            } else {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("活动已结束");
            }
            return result;
        }
    }
}
