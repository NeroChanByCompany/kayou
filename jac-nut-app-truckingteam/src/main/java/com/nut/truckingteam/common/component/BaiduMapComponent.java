package com.nut.truckingteam.common.component;

import com.alibaba.fastjson.JSONObject;
import com.nut.truckingteam.common.pojo.BaiduMapServicePojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @description: 百度地图服务组件
 * @author: hcb
 * @createTime: 2021/01/26 9:44
 * @version:1.0
 */
@Slf4j
@Component
public class BaiduMapComponent {

    private static final int NUT_SUCCESS_CODE = 200;
    private static final int BAIDU_MAP_SUCCESS_CODE = 0;

    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${baiduMapUrl:http://api.map.baidu.com}")
    private String baiduMapUrl;
    @Value("${baiduMapServiceUrl:http://api.map.esvtek.com:2001/common/baiduservice}")
    private String baiduMapServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 通过nut封装，请求百度地图服务
     *
     * @param baiduMapServicePojo
     * @return
     */
    public JSONObject nutBaiduMapService(BaiduMapServicePojo baiduMapServicePojo) {
        baiduMapServicePojo.setProject(applicationName);
        log.info("[百度地图服务]请求参数：{}", baiduMapServicePojo.toString());

        JSONObject result = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> formEntity = new HttpEntity<>(baiduMapServicePojo.toString(), headers);

        try {
            JSONObject resultJson = restTemplate.postForEntity(baiduMapServiceUrl, formEntity, JSONObject.class).getBody();
            log.info("[百度地图服务]返回：{}", resultJson.toString());
            if (NUT_SUCCESS_CODE == resultJson.getIntValue("code")) {
                JSONObject dataJson = resultJson.getJSONObject("data");
                if (BAIDU_MAP_SUCCESS_CODE == dataJson.getIntValue("status")) {
                    result = dataJson.getJSONObject("result");
                }
            }
        } catch (RestClientException e) {
            log.info("[百度地图服务]请求异常");
            log.error(e.getMessage(), e);
        }

        return result;
    }

    /**
     * 逆地理解析
     *
     * @param lat 纬度
     * @param lon 经度
     * @return
     */
    public JSONObject reverseGeocoding(String lat, String lon, String coordtype) {
        String latLon = lat + "," + lon;
        String url = baiduMapUrl + "/reverse_geocoding/v3/";
        url = url + "?output=json&coordtype="+coordtype+"&location=" + latLon;
        BaiduMapServicePojo baiduMapServicePojo = new BaiduMapServicePojo();
        baiduMapServicePojo.setMethod("get");
        baiduMapServicePojo.setUrl(url);

        return this.nutBaiduMapService(baiduMapServicePojo);
    }

    /**
     * 通过经纬度获取地址
     *
     * @param lat 纬度
     * @param lon 经度
     * @return
     */
    public String getAddressByLonLat(String lat, String lon) {
        return this.getAddressByLonLat(lat, lon, null);
    }

    /**
     * 通过经纬度获取地址
     * @param lat 纬度
     * @param lon 经度
     * @param coordtype 坐标的类型，目前支持的坐标类型包括：bd09ll（百度经纬度坐标）、bd09mc（百度米制坐标）、gcj02ll（国测局经纬度坐标，仅限中国）、wgs84ll（ GPS经纬度）
     * @return
     */
    public String getAddressByLonLat(String lat, String lon, String coordtype) {
        if (Objects.isNull(coordtype)) {
            coordtype = "wgs84ll";
        }
        String address = null;
        JSONObject result = this.reverseGeocoding(lat, lon, coordtype);
        if (Objects.nonNull(result)) {
            address = result.getString("formatted_address");
        }
        return address;
    }

    /**
     * 地址解析
     *
     * @param address
     * @param coordinateSystem gcj02ll（国测局坐标）、bd09mc（百度墨卡托坐标）
     * @return
     */
    public JSONObject geocoding(String address, String coordinateSystem) {
        String url = baiduMapUrl + "/geocoding/v3/";
        url = url + "?output=json&ret_coordtype=" + coordinateSystem + "&address=" + address;
        BaiduMapServicePojo baiduMapServicePojo = new BaiduMapServicePojo();
        baiduMapServicePojo.setMethod("get");
        baiduMapServicePojo.setUrl(url);

        return this.nutBaiduMapService(baiduMapServicePojo);
    }

    /**
     * 根据地址获取02坐标系经纬度
     *
     * @param address
     * @return Map lon:经度，lat：纬度
     */
    public Map<String, String> get02LonLatByAddress(String address) {
        JSONObject result = null;
        try {
            result = this.geocoding(java.net.URLEncoder.encode(address, "utf-8"), "gcj02ll");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }

        Map<String, String> lonLatMap = null;
        if (Objects.nonNull(result)) {
            JSONObject location = result.getJSONObject("location");
            if (Objects.nonNull(location)) {
                lonLatMap = new HashMap<>(4);
                lonLatMap.put("lon", location.getString("lng"));
                lonLatMap.put("lat", location.getString("lat"));
            }
        }
        return lonLatMap;
    }
}
