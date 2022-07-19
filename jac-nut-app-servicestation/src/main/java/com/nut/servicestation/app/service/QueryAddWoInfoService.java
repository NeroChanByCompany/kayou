package com.nut.servicestation.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.OrderCarForm;
import com.nut.servicestation.app.form.OrderItemsForm;

/*
 *  @author wuhaotian 2021/7/5
 */
public interface QueryAddWoInfoService {


    /**
     * 服务站预约项目
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData orderItems(OrderItemsForm command);
    /**
     * 查询预约车辆
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData orderCar(OrderCarForm command) throws JsonProcessingException;
}
