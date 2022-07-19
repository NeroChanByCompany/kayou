package com.nut.servicestation.common.assembler;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.utils.HttpUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.dto.PointDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.nut.servicestation.common.assembler.DirectionLiteRequestAssembler.*;
import static com.nut.servicestation.common.assembler.RectifyTrackRequestAssembler.*;


/**
 * 第三方地图接口
 */
@Component
@Slf4j
public class ExternalMapClient {
    private static final int SPLIT_SIZE = 1500;
    private static final int RETRY_COUNT = 3;
    /**
     * 轨迹纠偏接口地址
     */
    @Value("${baidu.rectify_track_url:http://api.map.baidu.com/rectify/v1/track?}")
    private String rectifyTrackUrl;
    /**
     * 驾车路线规划接口地址
     */
    @Value("${baidu.direction_lite_url:http://api.map.baidu.com/directionlite/v1/driving?}")
    private String directionLiteUrl;
    /**
     * 用户的AK，授权使用
     */
    @Value("${baidu_webapi_key:dummy}")
    private String bdKey;
    /**
     * 去噪力度
     */
    @Value("${rpDenoiseGrade:4}")
    private int rpDenoiseGrade;
    @Autowired
    private BaiduMapComponent baiduMapComponent;

    public ExternalMapClient(String rectifyTrackUrl, String directionLiteUrl, String bdKey, int rpDenoiseGrade) {
        this.rectifyTrackUrl = rectifyTrackUrl;
        this.directionLiteUrl = directionLiteUrl;
        this.bdKey = bdKey;
        this.rpDenoiseGrade = rpDenoiseGrade;
    }

    public ExternalMapClient() {
    }

    /**
     * 转换参数为map
     */
    private static Map<String, String> buildParameterMap(RectifyTrackRequestParam param) throws JsonProcessingException {
        String keyAk = "ak";
        String keyPointList = "point_list";
        String keyRectifyOption = "rectify_option";
        String keySupplementMode = "supplement_mode";
        String keyCoordTypeOutput = "coord_type_output";
        Map<String, String> result = new HashMap<>(16);
//        result.put(keyAk, param.getAk());
        result.put(keyPointList, JsonUtil.toJson(param.getPoint_list()));
        result.put(keyRectifyOption, param.getRectify_option());
        result.put(keySupplementMode, param.getSupplement_mode());
        result.put(keyCoordTypeOutput, param.getCoord_type_output());
        return result;
    }

    /**
     * 类型转换
     */
    private static Integer nullableFloat(Double d) {
        if (d == null) {
            return null;
        }
        return d.intValue();
    }

    /**
     * 调用百度轨迹纠偏接口整形轨迹
     */
    public RectifyTrackResult rectifyTrack(List<PointDto> pointDtoList, boolean endRepairFlg) {
        log.info("[rectifyTrack]start");
        RectifyTrackResult result = new RectifyTrackResult();
        if (pointDtoList != null && !pointDtoList.isEmpty()) {
            // 先按序号排序
            pointDtoList.sort(Comparator.comparing(PointDto::getIndex));
            // 每1500个点一组
            List<List<PointDto>> listGroup = CollectionUtil.splitList(pointDtoList, SPLIT_SIZE);
            List<RectifyTrackResult> resultGroup = new ArrayList<>();
            for (List<PointDto> subList : listGroup) {
                RectifyTrackRequestParam param = new RectifyTrackRequestParam();
//                param.setAk(bdKey);
                List<Point> points = subList.stream()
                        .map(e -> new Point(e.getLatitude(), e.getLongitude(), CoordTypeInput.GCJ02,
                                Long.valueOf(e.getTime()) / 1000, nullableFloat(e.getRadius())))
                        .collect(Collectors.toList());
                param.setPoint_list(points);
                log.info("[rectifyTrack]input point size:{}", points.size());

                Map<String, Object> optionMap = new LinkedHashMap<>();
                optionMap.put(NEED_MAPMATCH, 1);
                optionMap.put(TRANSPORT_MODE, TransportMode.DRIVING);
                optionMap.put(DENOISE_GRADE, rpDenoiseGrade);
                optionMap.put(VACUATE_GRADE, 1);
                String rectifyOption = getRectifyOption(optionMap);
                param.setRectify_option(rectifyOption);

                param.setSupplement_mode(SupplementMode.DRIVING);
                param.setCoord_type_output(CoordTypeOutput.GCJ02);
                Map<String, String> headers = new HashMap<>(3);
                headers.put("Accept", "application/json");

                // 重试次数
                int retryCount = 1;
                if (endRepairFlg) {
                    // 结束维修
                    retryCount = RETRY_COUNT;
                }
                for (int i = 0; i < retryCount; i++) {
                    try {
                        Map<String, String> map = buildParameterMap(param);
                        log.debug("[rectifyTrack]param:{}", map);
                       String responseStr= JSON.toJSONString(baiduMapComponent.rectifyTrack("APP", JSON.parseObject(JSON.toJSONString(map))));
//                        String responseStr = HttpUtil.postWithRequest(rectifyTrackUrl, map, headers);
                        log.debug("[rectifyTrack]responseStr:{}", responseStr);
                        RectifyTrackResult rtrResp = JsonUtil.fromJson(responseStr, RectifyTrackResult.class);
                        if (rtrResp.getStatus() != 0) {
                            if (i < (retryCount - 1)) {
                                continue;
                            }
                        }
                        resultGroup.add(rtrResp);
                        break;
                    } catch (Exception e) {
                        log.error("request count（" + (i + 1) + "):" + e.getMessage(), e);
                        if (i == (retryCount - 1)) {
                            result.setStatus(-1);
                            result.setMessage(e.getMessage());
                            return result;
                        }
                    }
                }
            }
            // 合并所有结果。中间有一次调用失败即为失败
            if (resultGroup.stream().allMatch(e -> e.getStatus() == 0)) {
                int total = 0;
                double distance = 0.0;
                double tollDistance = 0.0;
                List<Point> p = new ArrayList<>();
                for (RectifyTrackResult r : resultGroup) {
                    if (r.getTotal() == 0) {
                        continue;
                    }
                    total += r.getTotal();
                    distance += r.getDistance();
                    tollDistance += r.getToll_distance();
                    p.addAll(r.getPoints());
                }
                result.setStatus(0);
                result.setTotal(total);
                result.setDistance(distance);
                result.setToll_distance(tollDistance);
                result.setPoints(p);
                return result;
            } else {
                log.info("[rectifyTrack]not all ok||{}", resultGroup.stream()
                        .map(e -> e.getStatus() + ":" + e.getMessage()).collect(Collectors.joining("|")));
                resultGroup = resultGroup.stream().filter(e -> e.getStatus() != 0).collect(Collectors.toList());
                result.setStatus(resultGroup.get(0).getStatus());
                result.setMessage(resultGroup.get(0).getMessage());
                return result;
            }
        }
        log.info("[rectifyTrack]return pointDtoList null");
        result.setStatus(-1);
        result.setMessage("轨迹点为空");
        return result;
    }

    /**
     * 调用百度轻量级路线规划接口查询路线
     */
    public DirectionLiteResult directionLite(String stLat, String stLon, String etLat, String etLon, boolean endRepairFlg) {
        log.info("[directionLite]start");
        DirectionLiteRequestAssembler.DirectionLiteResult ret = new DirectionLiteRequestAssembler.DirectionLiteResult();
        if (isValidLatLon(stLat, stLon) && isValidLatLon(etLat, etLon)) {
            String param = getRequestParam(bdKey, stLat, stLon, etLat, etLon, null, CoordType.GCJ02, CoordType.GCJ02);
            Map<String, String> headers = new HashMap<>(3);
            headers.put("Accept", "application/json");

            // 重试次数
            int retryCount = 1;
            if (endRepairFlg) {
                // 结束维修
                retryCount = RETRY_COUNT;
            }
            for (int i = 0; i < retryCount; i++) {
                try {
                    log.debug("[directionLite]param:{}", param);
                    String responseStr = HttpUtil.get(directionLiteUrl + param, "", headers);
                    log.debug("[directionLite]responseStr:{}", responseStr);
                    log.info("[directionLite]end");
                    DirectionLiteResult dlrResp = parseResponse(responseStr);
                    if (dlrResp.getStatus() != 0) {
                        if (i < (retryCount - 1)) {
                            continue;
                        }
                    }
                    return dlrResp;
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    if (i == (retryCount - 1)) {
                        ret.setStatus(-1);
                        ret.setMessage(e.getMessage());
                        return ret;
                    }
                }
            }
        }
        log.info("[directionLite]return lat|lon null");
        ret.setStatus(-2);
        ret.setMessage("规划点为空");
        return ret;
    }

    /**
     * 判断是否是合法的经纬度
     */
    public boolean isValidLatLon(String lat, String lon) {
        if (StringUtil.isEmpty(lat) || StringUtil.isEmpty(lon)) {
            return false;
        }
        double dLat;
        double dLon;
        try {
            dLat = Double.parseDouble(lat);
            dLon = Double.parseDouble(lon);
        } catch (NumberFormatException nfe) {
            dLat = 0;
            dLon = 0;
        }
        return dLat > 1E-6 && dLat <= 90 && dLon > 1E-6 && dLon <= 180;
    }
}
