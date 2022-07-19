package com.nut.servicestation.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.locationservice.app.dto.CarLocationOutputDto;
import com.nut.locationservice.app.dto.CarOnlineStatusDTO;
import com.nut.servicestation.app.form.AddWoStationForm;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/5
 */
public interface AddWoStationService {

    /**
     * 获取车辆上报位置信息
     */
    public CarLocationOutputDto getCarUpInfo(String chassisNum);
    /**
     * 服务站建单、自动接单
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData<Map<String, String>> newWoSta(AddWoStationForm command) throws Exception;
    /**
     * 调用位置云接口查询车辆状态
     */
    HttpCommandResultWithData getRealTimeInfo(List<String> carVins, Map<String, CarOnlineStatusDTO> outMap) throws JsonProcessingException;
}
