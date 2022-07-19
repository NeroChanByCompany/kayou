package com.nut.jac.kafka.service;

import com.nut.jac.kafka.dto.LatLngDTO;
import com.nut.jac.kafka.dto.WoStatusDTO;
import com.nut.jac.kafka.vo.UploadRescueRoutePointVO;

import java.util.Collection;
import java.util.List;

/**
 * @author liuBing
 * @Classname RescueRoutePointService
 * @Description TODO
 * @Date 2021/8/31 13:48
 */
public interface RescueRoutePointService {
    /**
     * 校验工单
     * @param key
     * @return
     */
    Integer checkWorkOrder(String key);

    /**
     * 上报轨迹点
     * @param woCode 工单号
     * @param points 轨迹点列表
     * @param userId 用户id
     */
    void uploadPoint(String woCode, List<LatLngDTO> points, String userId);

    /**
     * 查询状态
     * @param list
     * @return
     */
    List<WoStatusDTO> queryWoStatus(List<String> list);

    /**
     * 校验入参
     * @param p
     * @return
     */
    String validateSaveCommand(UploadRescueRoutePointVO p);
}
