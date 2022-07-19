package com.nut.driver.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.driver.app.domain.Car;
import com.nut.driver.app.dto.*;
import com.nut.driver.app.entity.CarBasicInfoEntity;
import com.nut.driver.app.form.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author liuBing
 * @Classname CarService
 * @Description TODO
 * @Date 2021/6/24 17:30
 */
public interface CarService {

    UserCarsDTO query(UserCarsForm form);

    PagingInfo<MaintainItemListoDTO> queryMaintainItemList(QueryMaintainItemListForm form);

    QueryMaintenanceItemListDTO queryMaintainItemInfo(QueryMaintenanceItemForm form);

    /**
     * 通过 底盘号加车
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData addByChassisNumber(UserCarAddForm command) throws Exception;
    /**
     * 新车队模型相关-删车
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    @Transactional(rollbackFor = Exception.class)
    HttpCommandResultWithData delete(UserCarDeleteForm command);
    /**
     * 调用位置云接口查询车辆状态
     */
    HttpCommandResultWithData getRealTimeInfo(List<String> chassisNumList, Map<String, CarOnlineStatusDto> outMap) throws JsonProcessingException;

    Car findByChassisNumber(String classisNumber);

    UserCarDetailDto detail(UserCarDetailForm form);

    CarParmDto getCarParmInfo(QueryCarParmForm form);
    /**
     * @Author liuBing
     * @Description //TODO 获取车辆基础信息
     * @Date 13:52 2021/8/3
     * @Param [form]
     * @return com.nut.driver.app.entity.CarBasicInfoEntity
     **/
    CarBasicInfoEntity getCarBasicInfo(CarBasicInfoForm form);
}
