package com.nut.servicestation.common.assembler;

import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/5
 */
@Slf4j
@Component
public class NaviDistanceClient {

    /**
     * 导航距离服务url
     */
    @Value("${navi.url:dummy}")
    private String naviDistanceUrl;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取 2个坐标 之间的距离<br/>
     * <b>注意起点终点顺序颠倒，返回距离值可能不同</b>
     *
     * @param lon1 起点经度
     * @param lat1 起点纬度
     * @param lon2 终点经度
     * @param lat2 终点纬度
     * @return java.lang.Integer 返回单位  米
     */
    public Integer getDistance(String lon1, String lat1, String lon2, String lat2) {
        log.info("[getDistance]start||lon1:{}||lat1:{}||lon2:{}||lat2:{}", lon1, lat1, lon2, lat2);
        int distance = 0;
        Double dl1 = Double.parseDouble(lon1) * 100000;
        Double dl2 = Double.parseDouble(lat1) * 100000;
        Double dl3 = Double.parseDouble(lon2) * 100000;
        Double dl4 = Double.parseDouble(lat2) * 100000;
        Map<String, Object> param = new HashMap<>();
        param.put("st", getMdLocation(dl1.intValue()) + "," + getMdLocation(dl2.intValue()));
        param.put("et", getMdLocation(dl3.intValue()) + "," + getMdLocation(dl4.intValue()));
        try {
            log.info("[getDistance]param:{}", param.toString());
            log.info("[getDistance]url:{}", naviDistanceUrl);
            String responseStr = restTemplate.getForObject(naviDistanceUrl, String.class, param);
            if (StringUtil.isNotEmpty(responseStr)) {
                String result = JsonUtil.toJson(JsonUtil.toMap(responseStr).get("result"));
                if (StringUtil.isNotEmpty(result)) {
                    String route = JsonUtil.toJson(JsonUtil.toMap(JsonUtil.toJson(JsonUtil.toList(result).get(0))).get("route"));
                    // 距离越远日志越多,debug级
                    log.debug("[getDistance]result:{}", result);
                    if (StringUtil.isNotEmpty(route)) {
                        String dis = JsonUtil.toMap(route).get("dis") == null ? "" : JsonUtil.toMap(route).get("dis").toString();
                        if (StringUtil.isNotEmpty(dis)) {
                            Long disLong = Long.parseLong(dis);
                            distance = disLong.intValue();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("[getDistance]end||distance:{}", distance);
        return distance;
    }
    /**
     * 获取加密后的 经纬度坐标
     */
    private String getMdLocation(int ll) {
        Map<Integer, String> dataMap = new HashMap<>();
        //abefnmklgh 分别对应 0123456789
        dataMap.put(48, "a");
        dataMap.put(49, "b");
        dataMap.put(50, "e");
        dataMap.put(51, "f");
        dataMap.put(52, "n");
        dataMap.put(53, "m");
        dataMap.put(54, "k");
        dataMap.put(55, "l");
        dataMap.put(56, "g");
        dataMap.put(57, "h");
        String lstr = String.valueOf(ll);
        StringBuilder sb = new StringBuilder();
        for (byte b : lstr.getBytes()) {
            int index = (int) b;
            sb.append(dataMap.get(index));
        }
        return sb.toString();
    }
}
