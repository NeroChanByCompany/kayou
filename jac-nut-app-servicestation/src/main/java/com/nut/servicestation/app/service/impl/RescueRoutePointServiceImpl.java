package com.nut.servicestation.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.RegexpUtils;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.dao.WorkOrderDao;
import com.nut.servicestation.app.domain.WorkOrder;
import com.nut.servicestation.app.dto.LatLngDto;
import com.nut.servicestation.app.dto.WoStatusDto;
import com.nut.servicestation.app.form.UploadRescueRoutePointForm;
import com.nut.servicestation.app.service.RescueRoutePointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/*
 *  @author wuhaotian 2021/7/6
 */
@Slf4j
@Service("RescueRoutePointService")
@Deprecated
public class RescueRoutePointServiceImpl implements RescueRoutePointService {


  /*  @Autowired
    private WorkOrderDao workOrderMapper;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${rescueTruckTopic:rescue_track_new}")
    private String topic;

    *//**
     * 预计最小时间
     *//*
    @Value("${expectedMinSec:7200}")
    private int expectedMinSec;
    *//**
     * 预计最大时间
     *//*
    @Value("${expectedMaxSec:172800}")
    private int expectedMaxSec;

    @Override
    public String validateSaveCommand(UploadRescueRoutePointForm command) {
        if (StringUtil.isEmpty(command.getWoCode())) {
            return "工单号不能为空";
        }
        if (StringUtil.isEmpty(command.getIndex())) {
            return "轨迹点序号不能为空";
        }
        if (!RegexpUtils.validateInfo(command.getIndex(), RegexpUtils.NUMBER_CHECK)) {
            return "轨迹点序号只能为数字";
        }
        if (StringUtil.isEmpty(command.getLatitude())) {
            return "轨迹点纬度不能为空";
        }
//        if (!RegexpUtils.validateInfo(command.getLatitude(), "^(\\d{1,3})(\\.\\d+)?$")) {
//            return "轨迹点纬度格式不正确";
//        }
        if (StringUtil.isEmpty(command.getLongitude())) {
            return "轨迹点经度不能为空";
        }
//        if (!RegexpUtils.validateInfo(command.getLongitude(), "^(\\d{1,3})(\\.\\d+)?$")) {
//            return "轨迹点经度格式不正确";
//        }
        if (StringUtil.isEmpty(command.getTime())) {
            return "轨迹点时间不能为空";
        }
        if (!RegexpUtils.validateInfo(command.getIndex(), RegexpUtils.NUMBER_CHECK)) {
            return "轨迹点时间只能为数字";
        }
        try {
            if (StringUtil.isNotEmpty(command.getRadius())) {
                double radius = Double.parseDouble(command.getRadius());
            }
        } catch (NumberFormatException nfe) {
            return "定位精度格式不正确";
        }
        try {
            double zero = 1E-6;
            if (Double.parseDouble(command.getLongitude()) < zero || Double.parseDouble(command.getLatitude()) < zero) {
                return "位置点不合法";
            }
        } catch (NumberFormatException nfe) {
            return "位置点不合法";
        }

        if (!command.getLongitude().matches(RegexpUtils.SIX_DECIMA)) {
            // 截取位数
            BigDecimal lon = new BigDecimal(command.getLongitude());
            lon = lon.setScale(6, RoundingMode.HALF_UP);
            command.setLongitude(lon.toString());
        }
        if (!command.getLatitude().matches(RegexpUtils.SIX_DECIMA)) {
            BigDecimal lat = new BigDecimal(command.getLatitude());
            lat = lat.setScale(6, RoundingMode.HALF_UP);
            command.setLatitude(lat.toString());
        }
        return "";
    }

    @Override
    public Integer checkWorkOrder(String woCode) {
        Integer reInt = 0;
        Map<String, String> param = new HashMap<>(1);
        param.put("woCode", woCode);
        WorkOrder workOrder = workOrderMapper.selectByWoCode(param);
        if (workOrder != null && woCode.equals(workOrder.getWoCode())) {
            reInt = 1;
        }
        return reInt;
    }

    @Override
    public void uploadPoint(String woCode, List<LatLngDto> points, String userId) throws IOException {
        log.info("[uploadPoint]start");

        List<Point> ps = new ArrayList<>(points.size());
        for (LatLngDto dto : points) {
            ps.add(new Point(dto));
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
    public List<WoStatusDto> queryWoStatus(List<String> list) {
        List<WoStatusDto> reList = workOrderMapper.queryWoStatusByWoCode(list);
        if (reList != null && !reList.isEmpty()) {
            for (WoStatusDto woStatusDto : reList) {
                woStatusDto.setMaximumTime(judgeMaximumTime(woStatusDto.getWoCode()));
            }
        }
        return reList;
    }

    class TrackPoint {
        public String woCode;
        public List<Point> points;
        public String userId;
    }

    class Point {
        public String index;
        public Long latitude;
        public Long longitude;
        public String time;
        public Double radius;

        public Point(LatLngDto dto) {
            index = dto.getIndex();
            latitude = (long) (dto.getLatitude() * 1000000);
            longitude = (long) (dto.getLongitude() * 1000000);
            time = dto.getTime();
            radius = dto.getRadius();
        }
    }
    public Integer judgeMaximumTime(String woCode) {
        Integer maximumTime = 0;
        Map<String, String> param = new HashMap<>(1);
        param.put("woCode", woCode);
        WorkOrder workOrder = workOrderMapper.selectByWoCode(param);
        if (workOrder != null) {
            Date timeDepart = workOrder.getTimeDepart();
            Date timeArriveExpected = workOrder.getTimeArriveExpected();
            if (timeDepart != null && timeArriveExpected != null) {
                long expectedSec = (timeArriveExpected.getTime() - timeDepart.getTime()) / 1000;
                long usedSecNow = (DateUtil.getNowDate(DateUtil.time_pattern).getTime()  - timeDepart.getTime()) / 1000;
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
    }*/
}
