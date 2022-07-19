package com.nut.common.assembler;

import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 第三方导航距离服务
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.assembler
 * @Author: yzl
 * @CreateTime: 2021-06-22 09:06
 * @Version: 1.0
 */
@Slf4j
@Service
public class DistanceClient {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${navi.url:dummy}")
    private String DistanceUrl;

/*    public DistanceClient(RestTemplate restTemplate, String DistanceUrl) {
        this.restTemplate = restTemplate;
        this.DistanceUrl = DistanceUrl;
    }*/
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
            log.info("[getDistance]url:{}", DistanceUrl);
            String responseStr = restTemplate.getForObject(DistanceUrl, String.class, param);
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
