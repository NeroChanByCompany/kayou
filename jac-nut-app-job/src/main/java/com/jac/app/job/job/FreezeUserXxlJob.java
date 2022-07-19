package com.jac.app.job.job;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.dozermapper.core.Mapper;
import com.jac.app.job.client.ToolsClient;
import com.jac.app.job.common.Result;
import com.jac.app.job.entity.*;
import com.jac.app.job.enums.FreezeEnum;
import com.jac.app.job.enums.StatusEnum;
import com.jac.app.job.enums.WorkOrderLogEnum;
import com.jac.app.job.mapper.IntegralMapper;
import com.jac.app.job.service.*;
import com.jac.app.job.util.RedisComponent;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author liuBing
 * @Classname XxlJobTask
 * @Description TODO 冻结用户定时任务
 * @Date 2021/8/9 14:37
 */

@Component
public class FreezeUserXxlJob {

    private static Logger logger = LoggerFactory.getLogger(FreezeUserXxlJob.class);

    @Resource
    UserService userService;

    @Autowired
    IntegralMapper integralMapper;

    @Resource
    UserFreezeRecordsService userFreezeRecordsService;

    @Resource
    UserFreezeConfigService userFreezeConfigService;

    @Resource
    RedisComponent redisComponent;

    @Value("${bbs-limit-record:jac_bbs:limit_record:}")
    private String bbsKey;


    /**
     * 定时解冻账户
     */
    @XxlJob("thawUserJob")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> thawUserJob(String param) throws Exception {
        logger.info("执行解冻账户功能");
        MDC.put("traceId", UUID.randomUUID().toString().replaceAll("-", ""));
        //条件，冻结用户 并且状态不是永久冻结
        List<UserFreezeRecordsEntity> userFreezeRecordsEntities = userFreezeRecordsService.list();
        for (UserFreezeRecordsEntity userFreezeRecordsEntity : userFreezeRecordsEntities) {
            switch (Objects.requireNonNull(FreezeEnum.getCodeByType(userFreezeRecordsEntity.getTimeFormat()))) {
                case HOURS:
                    logger.info("执行解冻用户小时");
                    this.updateUserStatus(userFreezeRecordsEntity, DateUnit.HOUR);
                    break;
                case DAYS:
                    logger.info("执行解冻用户天");
                    this.updateUserStatus(userFreezeRecordsEntity, DateUnit.DAY);
                    break;
                default:
            }
        }
        return ReturnT.SUCCESS;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(UserFreezeRecordsEntity userFreezeRecordsEntity, DateUnit dateUnit) {
        logger.info("当前解冻时间{}，解冻用户：{}，",userFreezeRecordsEntity.getThawTime(),userFreezeRecordsEntity.getUcId());
        UserFreezeConfigEntity configEntity = userFreezeConfigService.getOne(new LambdaQueryWrapper<UserFreezeConfigEntity>().eq(UserFreezeConfigEntity::getFreezeNum, userFreezeRecordsEntity.getFreezeNum()).last("limit 1"));
        long between = DateUtil.between(userFreezeRecordsEntity.getFreezeTime(), new Date(), dateUnit);
        if (between >= Long.parseLong(configEntity.getThawTime())) {
            UserEntity userEntity = userService.getOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUcId, userFreezeRecordsEntity.getUcId()));
            integralMapper.unfreezeStatus(userFreezeRecordsEntity.getUcId());
            if (Objects.nonNull(userEntity)) {
                userEntity.setStatus(StatusEnum.NORMAL.getCode()).setUpdateTime(new Date());
                userService.updateById(userEntity);
            }
            userFreezeRecordsService.removeById(userFreezeRecordsEntity.getId());

            List<String> redisKeys = new ArrayList<>();
            for (int i = 1; i < 4; i++) {
                logger.info("bbsKey:{}", bbsKey + userEntity.getId() + ":" + i);
                redisKeys.add(bbsKey + userEntity.getId() + ":" + i);
            }
            redisComponent.delList(redisKeys);
            logger.info("解冻成功");
        }
    }

}
