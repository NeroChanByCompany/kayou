package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.domain.RescueRoutePoint;
import com.nut.servicestation.app.domain.WorkOrder;
import com.nut.servicestation.app.dto.PointDto;
import com.nut.servicestation.app.form.RescueCancelForm;
import com.nut.servicestation.app.form.ScanReceiveForm;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/5
 */
public interface ScanReceiveService {
    /**
     * 计算救援距离
     *
     * @param rescueRoutePoint 救援轨迹信息
     * @param stationLon       服务站经度
     * @param stationLat       服务站纬度
     * @param timeDepart       救援出发时间
     * @param endRepairFlg     结束维修标识
     * @param outParam         输出参数
     */
    void calculateDistance(String woCode,RescueRoutePoint rescueRoutePoint, String stationLon, String stationLat, Date timeDepart,
                                  boolean endRepairFlg, Map<String, Object> outParam) throws IOException;

    /**
     * 服务站建单，自动接车
     */
    void receive(WorkOrder workOrder) throws Exception;
    /**
     * 查询或保存轨迹点最大序号
     * 轨迹点最大序号只保存一次，因为只要工单状态不迁移，前端的序号在一直增加
     */
    RescueRoutePoint preSavePoint(String woCode, String phoneLon, String phoneLat, Integer maxIndex) throws IOException;
    /**
     * 校验轨迹点数量
     *
     * @param maxIndex       轨迹点最大序号
     * @param originalPoints 手机定位原始轨迹点
     * @return 需要上传的轨迹点序号
     */
    List<Integer> checkRoutePointNum(Integer maxIndex, List<PointDto> originalPoints);
    /**
     * 服务站取消救援
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData rescueCancel(RescueCancelForm command) throws IOException;
    /**
     * @param command
     * @Description: 扫码接车
     * @method: scanReceive
     */
    HttpCommandResultWithData scanReceive(ScanReceiveForm command, Map<String, String> mileageMap) throws Exception;
}
