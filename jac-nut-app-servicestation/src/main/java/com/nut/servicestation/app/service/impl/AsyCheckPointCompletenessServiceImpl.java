package com.nut.servicestation.app.service.impl;

import com.nut.common.constant.ServiceStationVal;
import com.nut.common.utils.JsonUtil;
import com.nut.servicestation.app.dao.RescueRoutePointDao;
import com.nut.servicestation.app.dao.RescueRoutePointHistoryDao;
import com.nut.servicestation.app.dao.WorkOrderDao;
import com.nut.servicestation.app.domain.RescueRoutePoint;
import com.nut.servicestation.app.domain.RescueRoutePointHistory;
import com.nut.servicestation.app.domain.WorkOrder;
import com.nut.servicestation.app.dto.PointDto;
import com.nut.servicestation.app.service.AsyCheckPointCompletenessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/5
 */
@Slf4j
@Service("AsyCheckPointCompletenessService")
public class AsyCheckPointCompletenessServiceImpl implements AsyCheckPointCompletenessService {


    @Autowired
    private WorkOrderDao workOrderMapper;
    @Autowired
    private RescueRoutePointDao rescueRoutePointMapper;
    @Autowired
    private RescueRoutePointHistoryDao rescueRoutePointHistoryMapper;
    /**
     * 预计时长分割 分钟
     */
    @Value("${checkPointCompletenessDuration:60}")
    private int checkPointCompletenessDuration;
    /**
     * 异常轨迹点时长 5分钟 毫秒
     */
    @Value("${abnormalPointDuration:300000}")
    private int abnormalPointDuration;
    /**
     * 异常轨迹点次数 3次
     */
    @Value("${abnormalPointTimes:3}")
    private int abnormalPointTimes;



    @Override
    @Async
    public void checkPointCompleteness(String woCode) {
        log.info("******************************checkPointCompleteness Start**************************************");
        sleep();
        log.info("checkPointCompleteness woCode:{}", woCode);
        Map<String, String> param = new HashMap<>(1);
        param.put("woCode", woCode);
        WorkOrder workOrder = workOrderMapper.selectByWoCode(param);
        if (workOrder != null) {
            if (workOrder.getWoType() != ServiceStationVal.OUTSIDE_RESCUE) {
                log.info("checkPointCompleteness not outside");
                return;
            }
            Integer timesRescueNumber = 1;
            if (null != workOrder.getTimesRescueNumber()) {
                timesRescueNumber = workOrder.getTimesRescueNumber();
            }

            // 系统时间
            Date date = new Date();
            WorkOrder updateWorkOrder = new WorkOrder();
            updateWorkOrder.setId(workOrder.getId());

            if (timesRescueNumber > 1) {
                // 一次外出
                RescueRoutePointHistory rrph = rescueRoutePointHistoryMapper.queryHistoryRescueRoutePoint(woCode, "1");
                log.info("checkPointCompleteness one,{}", rrph.getEstimateDuration());
                int checkResultOne = checkOriginalPoints(rrph.getOriginalPoints(), rrph.getEstimateDuration());
                updateWorkOrder.setPointCompleteness(checkResultOne);
                // 二次外出
                RescueRoutePoint rrp = rescueRoutePointMapper.selectByWoCode(woCode);
                log.info("checkPointCompleteness two,{}", rrp.getEstimateDuration());
                int checkResultTwo = checkOriginalPoints(rrp.getOriginalPoints(), rrp.getEstimateDuration());
                updateWorkOrder.setPointCompletenessTwo(checkResultTwo);
            } else {
                // 一次外出
                RescueRoutePoint rrp = rescueRoutePointMapper.selectByWoCode(woCode);
                log.info("checkPointCompleteness one,{}", rrp.getEstimateDuration());
                int checkResult = checkOriginalPoints(rrp.getOriginalPoints(), rrp.getEstimateDuration());
                updateWorkOrder.setPointCompleteness(checkResult);
            }
            // 更新工单表中轨迹完整性
            updateWorkOrder.setUpdateTime(date);
            workOrderMapper.updateByPrimaryKeySelective(updateWorkOrder);

        }
        log.info("******************************checkPointCompleteness End**************************************");
    }
    /**
     * 适当延时等待其他线程事务提交
     */
    public void sleep() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
    public int checkOriginalPoints(String originalPoints, Integer estimateDuration) {
        int reCheck = 1;
        try {
            // 累计异常轨迹点时长
            long cumulativeAbnormalTime = 0;
            //累计异常轨迹点次数
            int abnormalPointTimesCount = 0;
            // 异常轨迹点最大时间限制，不足一小时按预计时长的二分之一，超过一小时的按30分钟，单位秒
            int abnormalPointMaxTime = 0;
            List<PointDto> points = JsonUtil.toList(originalPoints, PointDto.class);
            if (points != null && !points.isEmpty() && estimateDuration > 0) {
                log.info("checkOriginalPoints points size:" + points.size());
                // 判断预计时长是否满足1小时
                if (estimateDuration < checkPointCompletenessDuration) {
                    // 不足1小时 预计时长2分之一 单位秒
                    abnormalPointMaxTime = estimateDuration * 30;
                } else {
                    // 超过1小时 半小时 单位秒
                    abnormalPointMaxTime = 1800;
                }
                log.info("checkOriginalPoints abnormalPointMaxTime:" + abnormalPointMaxTime);
                for (int i = 0; i < points.size() - 1; i++) {
                    if (abnormalPointTimesCount > abnormalPointTimes || cumulativeAbnormalTime > abnormalPointMaxTime * 1000) {
                        break;
                    }
                    PointDto pointA = points.get(i);
                    PointDto pointB = points.get(i + 1);
                    long pointTimeDifference = Long.parseLong(pointB.getTime()) - Long.parseLong(pointA.getTime());
                    if (pointTimeDifference > abnormalPointMaxTime * 1000) {
                        // 超过最大限制一次即为不完整
                        reCheck = 0;
                        log.info("checkOriginalPoints error point greater than abnormalPointMaxTime,{},{}", i, pointTimeDifference);
                        break;
                    }
                    // 判断是否为异常轨迹点
                    if (pointTimeDifference > abnormalPointDuration) {
                        cumulativeAbnormalTime += pointTimeDifference;
                        abnormalPointTimesCount += 1;
                    }

                }
                if (abnormalPointTimesCount > abnormalPointTimes || cumulativeAbnormalTime > abnormalPointMaxTime * 1000) {
                    log.info("checkOriginalPoints cumulative error point greater than,{},{}", abnormalPointTimesCount, cumulativeAbnormalTime);
                    reCheck = 0;
                }
            } else {
                reCheck = 0;
            }
        } catch (Exception e) {
            reCheck = 0;
            log.error("***checkPointCompleteness checkOriginalPoints error", e);
        }
        log.info("checkOriginalPoints result:" + reCheck);
        return reCheck;
    }
}
