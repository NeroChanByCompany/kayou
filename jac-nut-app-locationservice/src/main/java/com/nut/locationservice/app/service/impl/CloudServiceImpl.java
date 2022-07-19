package com.nut.locationservice.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.protobuf.InvalidProtocolBufferException;
import com.nut.location.protocol.common.LCLocationData;
import com.nut.locationservice.app.module.LastLocationResponse;
import com.nut.locationservice.app.module.TrackParameter;
import com.nut.locationservice.app.service.CloudService;
import com.nut.common.utils.HttpUtil;
import com.nut.common.utils.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-16 19:41
 * @Version: 1.0
 */
@Service
@Slf4j
public class CloudServiceImpl implements CloudService {

    @Value("${big_data.cloud_base_url}")
    private String baseUrl;
    @Value("${last_location_size_split:5000}")
    private Integer LASTLOCATIONSIZESPLIT;
    @Value("${last_location_parameter_fields:gpsDate,lon,lat,receiveDate,direction,height,mileage,speed,orgStatus,status,sm,alarm,sf,terminalId,oilValue,rotation,addm,crt,app,mileageDD,ifc,dayMil,dayOil,provinceCode,cityCode,latLonPB}")
    private String fields;

    @SneakyThrows
    public List<LCLocationData.LocationData> queryByteTrack(long terminalId, long startTime, long endTime, boolean isThin, int thinLevel) {
        List<LCLocationData.LocationData> locationDataList = new ArrayList<>();
        String url = baseUrl + "/location/track/query";
        TrackParameter parameter = assemblyParameter(terminalId, startTime, endTime, isThin, thinLevel);
        Object data = queryTrack(url, parameter);
        if (Objects.nonNull(data)) {
            List<String> list = (List<String>) data;
            list.stream().forEach(o -> {
                try {
                    locationDataList.add(LCLocationData.LocationData.parseFrom(Base64.getDecoder().decode(o)));
                } catch (InvalidProtocolBufferException e) {
                    log.error("轨迹转换异常：", ExceptionUtils.getFullStackTrace(e));
                }
            });
        }
        return locationDataList;
    }

    @SneakyThrows
    public List<LastLocationResponse> queryJsonLastLocation(List<Long> commIds) {
        String url = baseUrl + "/location/last/search";
        List<LastLocationResponse> lastLocationList = new ArrayList<>();
        try {

            JSONObject map = new JSONObject();
            map.put("fields", fields.split(","));
            for (int i = 0; i < commIds.size(); i += LASTLOCATIONSIZESPLIT) {
                if (i + LASTLOCATIONSIZESPLIT < commIds.size()) {
                    map.put("terminalIds", commIds.subList(i, i + LASTLOCATIONSIZESPLIT));
                } else {
                    map.put("terminalIds", commIds.subList(i, commIds.size()));
                }
                String result = HttpUtil.postHttps(url, map);
                log.info("请求位置云参数：{}，返回内容:{}", map, result);
                Map<String, Object> resultMap = JsonUtil.toMap(result);
                if (Objects.nonNull(resultMap.get("resultCode")) && resultMap.get("resultCode").toString().equals("200") && resultMap.get("data") != null) {
                    String JSONStr = JSON.toJSONString(resultMap.get("data"));
                    log.info("JSONStr ：{}", JSONStr);
                    lastLocationList.addAll(JSON.parseObject(JSONStr, new TypeReference<List<LastLocationResponse>>() {
                    }));
                }
            }
        } catch (Exception e) {
            log.error("[query  track  is error :{}", ExceptionUtils.getFullStackTrace(e));

        }
        return lastLocationList;
    }

    /**
     * 组装查询轨迹参数
     *
     * @param terminalId 终端通讯号
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param isThin     是否抽稀
     * @param thinlevel  抽稀级别
     * @return
     */
    private TrackParameter assemblyParameter(long terminalId, long startTime, long endTime, boolean isThin, int thinlevel) {
        TrackParameter parameter = new TrackParameter();
        parameter.setEndTime(endTime);
        parameter.setThin(isThin);
        parameter.setStartTime(startTime);
        parameter.setTerminalId(terminalId);
        parameter.setThinLevel(thinlevel);
        parameter.setType(0);
        return parameter;
    }

    private Object queryTrack(String url, TrackParameter parameter) {
        try {
            String result = HttpUtil.postHttps(url, (JSONObject) JSONObject.toJSON(parameter));
            log.info("请求位置云queryTrack参数：{}，返回内容:{}", JSONObject.toJSON(parameter), result);
            Map<String, Object> map = JsonUtil.toMap(result);
            if (Objects.nonNull(map.get("resultCode")) && map.get("resultCode").toString().equals("200")) {
                return map.get("data");
            }
        } catch (Exception e) {
            log.error("[query  track  is error :{}", ExceptionUtils.getFullStackTrace(e));

        }
        return null;
    }

}
