package com.nut.driver.app.dto;

import com.nut.common.base.BaseForm;
import com.nut.common.result.PagingInfo;
import lombok.Data;

/**
 * @author liuBing
 * @Classname UserCarsDTO
 * @Description TODO
 * @Date 2021/6/24 17:35
 */
@Data
public class UserCarsDTO extends PagingInfo<UserCarsDTO.CarInfo> {

    private Integer onlineNum;
    private String totalMileage;

    /**
     * 交车单-是否有待添加车辆
     */
    private Boolean deliveryStatus;

    /**
     * 24小时内创建过工单的提示文案
     */
    private String orderStatusMessage;
    @Data
    public static class CarInfo{
        private String carId;
        private String carNumber;
        private String chassisNum;
        private Integer status;
        private String mileage;
        private String totalMileage;
        private String speed;
        private String lon;
        private String lat;
        private String location;
        private Integer bound;
        private Integer role;
        /**
         * 是否有车联网功能, 0: 无， 1：有
         */
        private String hasNetwork;

        /**
         * 车辆信息是否已经补全，1：已经补全, 0: 没有补全
         */
        private String extInfoOk;

        /** 24小时内是否创建过工单（true：是；false：否） */
        private Boolean orderStatus;
        /**
         * 	是否为协议车辆，true：协议车辆，flase：非协议车辆
         */
        private Boolean agreementCar;
        /**
         * 	车辆是否可以在所选服务站建单（此字段为false时做弹窗处理后续可调用协议服务站列表接口）
         */
        private Boolean createOrderStatus;
    }
}
