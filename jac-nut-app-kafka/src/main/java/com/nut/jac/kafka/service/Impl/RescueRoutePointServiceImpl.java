package com.nut.jac.kafka.service.Impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.util.StringUtil;
import com.nut.jac.kafka.domain.Point;
import com.nut.jac.kafka.domain.TrackPoint;
import com.nut.jac.kafka.domain.WorkOrder;
import com.nut.jac.kafka.dto.LatLngDTO;
import com.nut.jac.kafka.dto.WoStatusDTO;
import com.nut.jac.kafka.mapper.WorkOrderMapper;
import com.nut.jac.kafka.service.RescueRoutePointService;
import com.nut.jac.kafka.util.DateUtils;
import com.nut.jac.kafka.util.RegexpUtils;
import com.nut.jac.kafka.vo.UploadRescueRoutePointVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author liuBing
 * @Classname RescueRoutePointServiceImpl
 * @Description TODO
 * @Date 2021/8/31 14:00
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class,readOnly = true)
public class RescueRoutePointServiceImpl implements RescueRoutePointService {

    @Resource
    WorkOrderMapper workOrderMapper;
    @Resource
    KafkaTemplate kafkaTemplate;

    @Value("${rescueTruckTopic:rescue_track}")
    private String topic;
    /**
     * 预计最小时间
     */
    @Value("${expectedMinSec:7200}")
    private int expectedMinSec;
    /**
     * 预计最大时间
     */
    @Value("${expectedMaxSec:172800}")
    private int expectedMaxSec;

    @Override
    public Integer checkWorkOrder(String key) {
        Integer reInt = 0;
        WorkOrder workOrder = workOrderMapper.selectByWoCode(key);
        if (workOrder != null && key.equals(workOrder.getWoCode())) {
            reInt = 1;
        }
        return reInt;
    }

    @Override
    public void uploadPoint(String woCode, List<LatLngDTO> points, String userId) {
        log.info("[uploadPoint]start");

        List<Point> ps = new ArrayList<>(points.size());
        for (LatLngDTO dto : points) {
            Double lat = dto.getLatitude() * 1000000;
            Double lot = dto.getLongitude() * 1000000;
            ps.add(new Point()
                    .setIndex(dto.getIndex())
                    .setTime(dto.getTime())
                    .setLatitude(lat.longValue())
                    .setRadius(dto.getRadius())
                    .setLongitude(lot.longValue())
            );
        }
        // 发送轨迹点到kafak
        TrackPoint trackPoint = new TrackPoint();
        trackPoint.woCode = woCode;
        trackPoint.points = ps;
        trackPoint.userId = userId;
        String data = JSON.toJSONString(trackPoint);
        this.kafkaTemplate.send(topic,data).addCallback(
                (r) -> log.info("发送kafka成功！topic: {}, woCode: {}, data: {}", topic, woCode, data),
                (r) -> {
                    log.warn("发送kafka失败！topic: {}, woCode: {},  data: {}", topic, woCode, data);
                }
        );

        log.info("[uploadPoint]end");
    }

    @Override
    public List<WoStatusDTO> queryWoStatus(List<String> list) {
        List<WoStatusDTO> reList = workOrderMapper.queryWoStatusByWoCode(list);
        if (reList != null && !reList.isEmpty()) {
            for (WoStatusDTO woStatusDto : reList) {
                woStatusDto.setMaximumTime(judgeMaximumTime(woStatusDto.getWoCode()));
            }
        }
        return reList;
    }

    /**
     * 计算工单时间
     * @param woCode
     * @return
     */
    private Integer judgeMaximumTime(String woCode) {
        Integer maximumTime = 0;
        WorkOrder workOrder = workOrderMapper.selectByWoCode(woCode);
        if (workOrder != null) {
            Date timeDepart = workOrder.getTimeDepart();
            Date timeArriveExpected = workOrder.getTimeArriveExpected();
            if (timeDepart != null && timeArriveExpected != null) {
                long expectedSec = (timeArriveExpected.getTime() - timeDepart.getTime()) / 1000;
                long usedSecNow = (DateUtils.getNowDate(DateUtils.time_pattern).getTime()  - timeDepart.getTime()) / 1000;
                if (expectedSec > 0 && usedSecNow > 0) {
                    long expectedSec2 = expectedSec * 2;
                    if (expectedSec2 <= expectedMinSec) {
                        // 2倍预计时长小于2小时
                        if (usedSecNow >= expectedMinSec) {
                            maximumTime = 1;
                        }
                    } else if (expectedSec2 > expectedMinSec && expectedSec2 <= expectedMaxSec) {
                        // 2倍预计时长大于于2小时小于48小时
                        if (usedSecNow >= expectedSec2) {
                            maximumTime = 1;
                        }
                    } else if (expectedSec2 > expectedMaxSec) {
                        // 2倍预计时长大于48小时
                        if (usedSecNow >= expectedMaxSec) {
                            maximumTime = 1;
                        }
                    }
                }
            }
        }

        return maximumTime;
    }

    @Override
    public String validateSaveCommand(UploadRescueRoutePointVO p) {
        if (StringUtil.isEmpty(p.getWoCode())) {
            return "工单号不能为空";
        }
        if (StringUtil.isEmpty(p.getIndex())) {
            return "轨迹点序号不能为空";
        }
        if (!RegexpUtils.validateInfo(p.getIndex(), RegexpUtils.NUMBER_CHECK)) {
            return "轨迹点序号只能为数字";
        }
        if (StringUtil.isEmpty(p.getLatitude())) {
            return "轨迹点纬度不能为空";
        }
//        if (!RegexpUtils.validateInfo(command.getLatitude(), "^(\\d{1,3})(\\.\\d+)?$")) {
//            return "轨迹点纬度格式不正确";
//        }
        if (StringUtil.isEmpty(p.getLongitude())) {
            return "轨迹点经度不能为空";
        }
//        if (!RegexpUtils.validateInfo(command.getLongitude(), "^(\\d{1,3})(\\.\\d+)?$")) {
//            return "轨迹点经度格式不正确";
//        }
        if (StringUtil.isEmpty(p.getTime())) {
            return "轨迹点时间不能为空";
        }
        if (!RegexpUtils.validateInfo(p.getIndex(), RegexpUtils.NUMBER_CHECK)) {
            return "轨迹点时间只能为数字";
        }
        try {
            if (StringUtil.isNotEmpty(p.getRadius())) {
                double radius = Double.parseDouble(p.getRadius());
            }
        } catch (NumberFormatException nfe) {
            return "定位精度格式不正确";
        }
        try {
            double zero = 1E-6;
            if (Double.parseDouble(p.getLongitude()) < zero || Double.parseDouble(p.getLatitude()) < zero) {
                return "位置点不合法";
            }
        } catch (NumberFormatException nfe) {
            return "位置点不合法";
        }

        if (!p.getLongitude().matches(RegexpUtils.SIX_DECIMA)) {
            // 截取位数
            BigDecimal lon = new BigDecimal(p.getLongitude());
            lon = lon.setScale(6, RoundingMode.HALF_UP);
            p.setLongitude(lon.toString());
        }
        if (!p.getLatitude().matches(RegexpUtils.SIX_DECIMA)) {
            BigDecimal lat = new BigDecimal(p.getLatitude());
            lat = lat.setScale(6, RoundingMode.HALF_UP);
            p.setLatitude(lat.toString());
        }
        return "";
    }
}
