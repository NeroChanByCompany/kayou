package com.nut.servicestation.app.dto;

import lombok.Data;

@Data
public class CountByStatusDto {
    /** 待接收工单数量 */
    private Long acceptCnt = 0L;
    /** 外出救援待出发工单数量 */
    private Long rescueLeaveCnt = 0L;
    /** 外出救援待接车工单数量 */
    private Long rescueReceiveCnt = 0L;
    /** 外出救援待检查工单数量 */
    private Long rescueInspectCnt = 0L;
    /** 外出救援待维修工单数量 */
    private Long rescueRepairCnt = 0L;
    /** 进出站待接车工单数量 */
    private Long stationReceiveCnt = 0L;
    /** 进出站待检查工单数量 */
    private Long stationInspectCnt = 0L;
    /** 进出站待维修工单数量 */
    private Long stationRepairCnt = 0L;
    /** 进站超时预警数量 */
    private Long stationStayWarnCnt = 0L;
    /**进站已评价数量**/
    private Long inStationEvaluateCnt = 0L;
    /** 外出救援已评价数量 */
    private Long outStationEvaluateCnt = 0L;
    /**
     * 抢单数量
     */
    private Long grabAnOrder =0L;

}
