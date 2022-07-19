package com.nut.common.assembler;

import com.alibaba.fastjson.JSONObject;
import com.nut.common.utils.HttpUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.dto.GeographicalDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: 第三方逆地理
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.assembler
 * @Author: yzl
 * @CreateTime: 2021-06-22 10:26
 * @Version: 1.0
 */
@Slf4j
@Service
public class ReverseGeoCodingClient {
    /**
     * 逆地理编码服务URL
     */
    @Value("${geographical_url:dummy}")
    private String reverseGeoCodingUrl;
    /**
     * 调用公共逆地理服务
     */
    @Value("${geographical_local:dummy}")
    private String ownReverseGeoCodingUrl;
    /**
     * 逆地理编码服务ak
     */
    @Value("${geographical_ak:dummy}")
    private String ak;


    /**
     * 查询所在地理位置的描述信息
     *
     * @param lat 纬度
     * @param lon 经度
     * @return GeographicalDto 所在地理位置的描述信息
     */
    public GeographicalDto queryGeographicalOld(String lat, String lon) {
        log.info("[queryGeographical]start");
        GeographicalDto geographicalDto = new GeographicalDto();
        try {
            String latLon = "lat=" + lat + "&" + "lon=" + lon;
            log.info("[queryGeographical]request url:{}", this.reverseGeoCodingUrl + latLon);
            String validateStr = HttpUtil.get(this.reverseGeoCodingUrl + latLon, "", "");
            // 获取返回值
            Map<String, Object> validateMap = JsonUtil.toMap(validateStr);
            if (!validateMap.isEmpty()) {
                // DTO转换
                geographicalDto = mapToDto(validateMap);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("[queryGeographical]end");
        return geographicalDto;
    }

    public GeographicalDto queryGeographical(String lat, String lon) {
        log.info("[queryGeographical]start");
        GeographicalDto geographicalDto = new GeographicalDto();
        try {
            String latLon = lat + "," + lon;
            log.info("[queryGeographical]request url:{}", this.reverseGeoCodingUrl + latLon);
            //不添加AK请求
            //String reverseGeocodeParam = "ak=" + ak + "&output=json&coordtype=wgs84ll&location=";//23.39643,113.198965";
            String reverseGeocodeParam = "output=json&coordtype=wgs84ll&location=";//23.39643,113.198965";
            StringBuffer reverseGeocodeParamStr = new StringBuffer(reverseGeocodeParam).append(latLon);

            Map<String, String> paramMap = new HashMap<>();
            Map<String, String> headers = new HashMap<>(3);
            headers.put("Content-Type", "application/json");

            paramMap.put("project", "江淮app");
            paramMap.put("method", "get");
            paramMap.put("url", this.reverseGeoCodingUrl +"reverse_geocoding/v3/?"+ reverseGeocodeParamStr.toString());
            String urls=this.reverseGeoCodingUrl +"?"+ reverseGeocodeParamStr.toString();
            String sendContent=JsonUtil.toJson(paramMap);
            String requestResult=HttpUtil.postJson(this.ownReverseGeoCodingUrl, sendContent, "");
            log.info("[queryGeographical]result:{}", requestResult);
            if (!requestResult.isEmpty()) {
                JSONObject resultJson = JSONObject.parseObject(requestResult);
                if (resultJson.getString("code").equals("200")) {
                    geographicalDto = json2Dto(resultJson);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("[queryGeographical]end");
        return geographicalDto;
    }

/*    public static void main(String[] args) {
        GeographicalDto geographicalDto = new ReverseGeoCodingClient("http://api.map.baidu.com/reverse_geocoding/v3/","","http://192.168.31.249:2001/common/baiduservice").queryGeographical("31.225696563611", "121.49884033194");
        System.out.println();

        String re= new ReverseGeoCodingClient("http://api.map.baidu.com/","","http://192.168.31.249:2001/common/baiduservice").geocode("辽宁省沈阳市和平区青年大街开宇大厦");


    }*/
    private GeographicalDto json2Dto(JSONObject resultJson) throws Exception {
        GeographicalDto dto = new GeographicalDto();
        JSONObject data = resultJson.containsKey("data") ? resultJson.getJSONObject("data") : null;
        JSONObject result = (null != data && data.containsKey("result")) ? data.getJSONObject("result") : null;
        JSONObject addressComponent = (null != result && result.containsKey("addressComponent")) ? result.getJSONObject("addressComponent") : null;

        if(null == addressComponent || null == result){
            return dto;
        }
        // 所在省
        dto.setProvince(addressComponent.getString("province"));
        // 所在城市
        dto.setCity(addressComponent.getString("city"));
        // 所在区县
        dto.setDist(addressComponent.getString("district"));
        // 所在区域
        dto.setArea(addressComponent.getString("street") + addressComponent.getString("street_number"));
        // 所在乡镇
        dto.setTown(addressComponent.getString("town"));
        // 方向
        dto.setDirection(addressComponent.getString("direction"));
        // 距离
        dto.setDistance(addressComponent.getString("distance"));
        return dto;
    }

    /**
     * 地理解析
     *
     * @param address 地点名称
     * @return 经纬度
     */
    public String geocode(String address) {
        log.info("[geocode]start");
        String resultLngLat = null;
        try {
            log.info("[geocode]request url:{},address:{}", this.reverseGeoCodingUrl + address);
            String requestReocodeStr = "address=" + address + "&output=json";

            Map<String, String> paramMap = new HashMap<>();
            Map<String, String> headers = new HashMap<>(3);

            paramMap.put("project", "江淮app");
            paramMap.put("method", "get");
            paramMap.put("url", this.reverseGeoCodingUrl +"geocoding/v3/?"+ requestReocodeStr);

            String urls=this.reverseGeoCodingUrl +"?"+ requestReocodeStr;
            String sendContent=JsonUtil.toJson(paramMap);
            String requestResult=HttpUtil.postJson(this.ownReverseGeoCodingUrl, sendContent, "");
            log.info("[geocode]result:{}", requestResult);
            String resultJson = requestResult;
            if (!resultJson.isEmpty()) {
                JSONObject resultJson1 = JSONObject.parseObject(resultJson);
                if (resultJson1.containsKey("code") && resultJson1.getString("code").equals("200")) {
                    if(resultJson1.containsKey("data")){
                        JSONObject data =  resultJson1.getJSONObject("data");
                        JSONObject result = data.getJSONObject("result");
                        JSONObject location = result.getJSONObject("location");
                        String lng = location.getString("lng");
                        String lat = location.getString("lat");
                        resultLngLat = lng + "&" + lat;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("[geocode]end");
        return resultLngLat;
    }
    /**
     * 逆地理
     *
     * @param lat 纬度
     * @param lon 经度
     * @return 地点名称
     */
    public String getPosition(String lat, String lon) {
        // 获取所在地理位置
        GeographicalDto geographicalDto = queryGeographical(lat, lon);
        return getPosition(geographicalDto);
    }

    /**
     * 组合逆地理后字符串
     *
     * @param geographicalDto 所在地理位置的描述信息
     * @return 地点名称
     */
    public String getPosition(GeographicalDto geographicalDto) {
        String position = "";
        String province = geographicalDto.getProvince();
        if (province != null) {
            position += province;
        }
        String city = geographicalDto.getCity();
        if (city != null) {
            position += city;
        }
        String dist = geographicalDto.getDist();
        if (dist != null) {
            position += dist;
        }
        String area = geographicalDto.getArea();
        if (area != null) {
            position += area;
        }
        String town = geographicalDto.getTown();
        if (town != null) {
            position += town;
        }
        String village = geographicalDto.getVillage();
        if (village != null) {
            position += village;
        }
        String address = geographicalDto.getAddress();
        if (address != null) {
            position += address;
        }
        String direction = geographicalDto.getDirection();
        if (direction != null) {
            position += direction;
        }
        String distance = geographicalDto.getDistance();
        if (distance != null && !"0米".equals(distance)) {
            position += distance;
        }
        return position;
    }

    /**
     * 单条数据装换
     *
     * @param validateMap 所在地理位置的描述信息
     * @return 所在地理位置的描述信息数据DTO
     */
    private GeographicalDto mapToDto(Map<String, Object> validateMap) {
        GeographicalDto dto = new GeographicalDto();
        // 所在省
        LinkedHashMap simInfo = (LinkedHashMap) validateMap.get("province");
        if (simInfo.get("value") != null) {
            dto.setProvince(simInfo.get("value").toString());
        }
        // 所在城市
        simInfo = (LinkedHashMap) validateMap.get("city");
        if (simInfo.get("value") != null) {
            dto.setCity(simInfo.get("value").toString());
        }
        // 所在区县
        simInfo = (LinkedHashMap) validateMap.get("dist");
        if (simInfo.get("value") != null) {
            dto.setDist(simInfo.get("value").toString());
        }
        // 所在区域
        simInfo = (LinkedHashMap) validateMap.get("area");
        if (simInfo.get("value") != null) {
            dto.setArea(simInfo.get("value").toString());
        }
        // 所在乡镇
        simInfo = (LinkedHashMap) validateMap.get("town");
        if (simInfo.get("value") != null) {
            dto.setTown(simInfo.get("value").toString());
        }
        // 所在村
        simInfo = (LinkedHashMap) validateMap.get("village");
        if (simInfo.get("value") != null) {
            dto.setVillage(simInfo.get("value").toString());
        }
        // 地址
        if (validateMap.get("address") != null) {
            dto.setAddress(validateMap.get("address").toString());
        }
        // 方向
        if (validateMap.get("direction") != null) {
            dto.setDirection(validateMap.get("direction").toString());
        }
        // 距离
        if (validateMap.get("distance") != null) {
            dto.setDistance(validateMap.get("distance").toString());
        }
        return dto;
    }
}
