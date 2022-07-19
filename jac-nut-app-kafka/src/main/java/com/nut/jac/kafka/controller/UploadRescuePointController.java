package com.nut.jac.kafka.controller;

import com.github.pagehelper.util.StringUtil;
import com.nut.jac.kafka.common.Result;
import com.nut.jac.kafka.dto.LatLngDTO;
import com.nut.jac.kafka.dto.WoStatusDTO;
import com.nut.jac.kafka.hanlder.ExceptionUtil;
import com.nut.jac.kafka.service.RescueRoutePointService;
import com.nut.jac.kafka.vo.UploadRescueRoutePointListVO;
import com.nut.jac.kafka.vo.UploadRescueRoutePointVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuBing
 * @Classname UploadRescuePointController
 * @Description TODO 轨迹点上传
 * @Date 2021/8/31 13:23
 */
@Slf4j
@RestController
@RequestMapping("/")
public class UploadRescuePointController {

    @Resource
    private RescueRoutePointService rescueRoutePointService;


    @PostMapping(value = "/uploadRescuePoint")
    public Result uploadRescuePoint(@RequestBody UploadRescueRoutePointListVO vo) {
        log.info("[uploadRescuePoint]start");
        try {
            if (vo.getPointList() != null) {
                Map<String, List<LatLngDTO>> pointMap = new HashMap<>();
                // 循环校验每个上传点并整合成Map
                for (UploadRescueRoutePointVO p : vo.getPointList()) {
                    String error = rescueRoutePointService.validateSaveCommand(p);
                    // 校验失败的点跳过
                    if (StringUtil.isNotEmpty(error)) {
                        log.info("[uploadRescuePoint]ERROR POINT: {} ERROR MESSAGE : {}", p.toString(), error);
                        continue;
                    }
                    p.setUserId(vo.getUserId());
                    // 整合成以工单号为key的Map
                    String key = p.getWoCode();
                    if (pointMap.containsKey(key)) {
                        // 包含该工单号
                        List<LatLngDTO> jsonMapList = pointMap.get(key);
                        jsonMapList.add(convertComToDto(p));
                        pointMap.replace(key, jsonMapList);
                    } else {
                        // 不包含该工单号
                        List<LatLngDTO> jsonMapList = new ArrayList<>();
                        jsonMapList.add(convertComToDto(p));
                        pointMap.put(key, jsonMapList);
                    }
                }
                // 循环整合好的Map更新各个工单
                List<String> list = new ArrayList<>();
                List<String> noWoList = new ArrayList<>();
                for (Map.Entry<String, List<LatLngDTO>> e : pointMap.entrySet()) {
                    // 校验工单是否存在
                    Integer checkWoOrder = rescueRoutePointService.checkWorkOrder(e.getKey());
                    if(checkWoOrder == 0) {
                        // 工单不存在
                        noWoList.add(e.getKey());
                        continue;
                    }
                    rescueRoutePointService.uploadPoint(
                            e.getKey(),
                            e.getValue(),
                            vo.getUserId());
                    list.add(e.getKey());
                }
                // 返回各个工单的状态
                List<WoStatusDTO> reData = new ArrayList<>();
                if (list.size() > 0) {
                    reData.addAll(rescueRoutePointService.queryWoStatus(list));
                }
                // 工单不存在，只在此接口返回-1给前端用
                if (noWoList.size() > 0) {
                    List<WoStatusDTO> noWoStatusDtoList = new ArrayList<>();
                    for(String wcode : noWoList){
                        WoStatusDTO noWoStatus = new WoStatusDTO();
                        noWoStatus.setWoCode(wcode);
                        noWoStatus.setWoStatus(-1);
                        noWoStatus.setMaximumTime(0);
                        noWoStatusDtoList.add(noWoStatus);
                    }
                    reData.addAll(noWoStatusDtoList);
                }
                return Result.ok(reData);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ExceptionUtil.result(Result.NETWORK_FAIL_CODE,"轨迹点上传失败!");
        }
        log.info("[uploadRescuePoint]end");
        return Result.SUCCESS;
    }
    /**
     * 保存json字符串用的DTO
     */
    private LatLngDTO convertComToDto(UploadRescueRoutePointVO com) {
        LatLngDTO dto = new LatLngDTO();
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
