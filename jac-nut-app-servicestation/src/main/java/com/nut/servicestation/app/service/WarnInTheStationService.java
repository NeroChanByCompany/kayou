package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.QueryStationStayWarnCarsForm;

/*
 *  @author wuhaotian 2021/7/5
 */
public interface WarnInTheStationService {


    /**
     * 更新超时预警信息
     *
     * @param carId   车辆ID
     * @param staCode 服务站编码
     * @param status  状态（0：默认 1：已回访 2：标记解除预警
     *                3：建单解除预警 4：接单解除预警 5：接车解除预警
     *                6：拒单解除预警 7：超时解除预警）
     * @return String 提示语
     */
    String updWarningStatus(String carId, String staCode, int status);
    /**
     * 站内预警地图打点信息查询
     *
     * @param command 入参
     * @return result 查询结果
     * @throws Exception 异常信息
     */
    HttpCommandResultWithData queryStationStayWarnCars(QueryStationStayWarnCarsForm command) throws Exception;
}
