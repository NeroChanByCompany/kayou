package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.dto.LatLngDto;
import com.nut.servicestation.app.dto.WoStatusDto;
import com.nut.servicestation.app.form.UploadRescueRoutePointForm;
import com.nut.servicestation.app.form.UploadRescueRoutePointListForm;
import com.nut.servicestation.app.service.RescueRoutePointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/6
 */
@Slf4j
@RestController
public class UploadRescuePointController {
    @Autowired
    private RescueRoutePointService rescueRoutePointService;

    /**
     * 上传轨迹点功能已经单独抽离出来，所有和kafka有关的功能都同步到的新服务当中
     * @param command
     * @return
     */
    @LoginRequired
    @PostMapping(value = "/uploadRescuePoint")
    @Deprecated
    public HttpCommandResultWithData uploadRescuePoint(@RequestJson UploadRescueRoutePointListForm command) {
        log.info("[uploadRescuePoint]start");
        HttpCommandResultWithData<List> result = new HttpCommandResultWithData<>();
        try {
            if (command.getPointList() != null) {
                Map<String, List<LatLngDto>> pointMap = new HashMap<>();
                // 循环校验每个上传点并整合成Map
                for (UploadRescueRoutePointForm p : command.getPointList()) {
                    p.setUserId(command.getUserId());

                    //String error = rescueRoutePointService.validateSaveCommand(p);
                    // 校验失败的点跳过
                  /*  if (StringUtil.isNotEmpty(error)) {
                       // log.info("[uploadRescuePoint]ERROR POINT: {} ERROR MESSAGE : {}", p.toString(), error);
                        continue;
                    }*/

                    // 整合成以工单号为key的Map
                    String key = p.getWoCode();
                    if (pointMap.containsKey(key)) {
                        // 包含该工单号
                        List<LatLngDto> jsonMapList = pointMap.get(key);
                        jsonMapList.add(convertComToDto(p));
                        pointMap.replace(key, jsonMapList);
                    } else {
                        // 不包含该工单号
                        List<LatLngDto> jsonMapList = new ArrayList<>();
                        jsonMapList.add(convertComToDto(p));
                        pointMap.put(key, jsonMapList);
                    }
                }
                // 循环整合好的Map更新各个工单
                List<String> list = new ArrayList<>();
                List<String> noWoList = new ArrayList<>();
                for (Map.Entry<String, List<LatLngDto>> e : pointMap.entrySet()) {
                    // 校验工单是否存在
                    //Integer checkWoOrder = rescueRoutePointService.checkWorkOrder(e.getKey());
          /*          if(checkWoOrder == 0) {
                        // 工单不存在
                        noWoList.add(e.getKey());
                        continue;
                    }
                    rescueRoutePointService.uploadPoint(
                            e.getKey(),
                            e.getValue(),
                            command.getUserId());
                    list.add(e.getKey());*/
                }
                // 返回各个工单的状态
                List<WoStatusDto> reData = new ArrayList<>();
                if (list.size() > 0) {
                    //reData.addAll(rescueRoutePointService.queryWoStatus(list));
                }
                // 工单不存在，只在此接口返回-1给前端用
                if (noWoList.size() > 0) {
                    List<WoStatusDto> noWoStatusDtoList = new ArrayList<>();
                    for(String wcode : noWoList){
                        WoStatusDto noWoStatus = new WoStatusDto();
                        noWoStatus.setWoCode(wcode);
                        noWoStatus.setWoStatus(-1);
                        noWoStatus.setMaximumTime(0);
                        noWoStatusDtoList.add(noWoStatus);
                    }
                    reData.addAll(noWoStatusDtoList);
                }
                result.setData(reData);
            }
            result.setResultCode(ECode.SUCCESS.code());
            result.setMessage(ECode.SUCCESS.message());
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("轨迹点上传失败!");
            log.error(e.getMessage(), e);
        }
        log.info("[uploadRescuePoint]end");
        return result;
    }
    /**
     * 保存json字符串用的DTO
     */
    private LatLngDto convertComToDto(UploadRescueRoutePointForm com) {
        LatLngDto dto = new LatLngDto();
        dto.setIndex(com.getIndex());
        dto.setLatitude(Double.parseDouble(com.getLatitude()));
        dto.setLongitude(Double.parseDouble(com.getLongitude()));
        dto.setTime(com.getTime());
        if (StringUtil.isNotEmpty(com.getRadius())) {
            dto.setRadius(Double.parseDouble(com.getRadius()));
        }
        return dto;
    }
}
