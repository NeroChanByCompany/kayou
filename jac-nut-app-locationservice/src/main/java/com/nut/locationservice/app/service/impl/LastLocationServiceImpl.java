package com.nut.locationservice.app.service.impl;

import com.nut.locationservice.app.dto.LastLocationDto;
import com.nut.locationservice.app.form.GetCarLastLocationByAutForm;
import com.nut.locationservice.app.module.LastLocationResponse;
import com.nut.locationservice.common.util.NumberFormatUtil;
import com.nut.common.utils.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 针对行程整合redis拆分，末次位置信息取位置云
 * 不再取行程整合保存的末次位置
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-17 10:59
 * @Version: 1.0
 */
@Service
@Slf4j
public class LastLocationServiceImpl {

    @Autowired
    private CloudServiceImpl cloudService;

    /**
     * 末次位置信息
     *
     * @param form
     * @return
     * @throws Exception
     */
    @SneakyThrows
    public Map<Long, LastLocationDto> queryCarLastLocationByAutoTerminal(GetCarLastLocationByAutForm form) {
        Map<Long, LastLocationDto> map = new HashMap<>();
        try {
            // 获取瞬时数据
            List<LastLocationResponse> lastLocationList = cloudService.queryJsonLastLocation(form.getTerminalIdList());

            log.info("[CarLastLocationService][queryCarLastLocation]位置云返回数据是否为空：{}", lastLocationList == null);
            // 转换成bean
            if (CollectionUtils.isNotEmpty(lastLocationList)) {
                for (LastLocationResponse lastLocation : lastLocationList) {
                    //获取到位置数据
                    LastLocationDto dto = new LastLocationDto();
                    dto.setTerminalId(lastLocation.getTerminalId());
                    dto.setGpsTime(lastLocation.getGpsDate() * 1000);
                    dto.setLat(lastLocation.getLat() / 1000000D);
                    dto.setLon(lastLocation.getLon() / 1000000D);
                    dto.setOil(NumberFormatUtil.getDoubleValueData(lastLocation.getSf() / 1D, 2));
                    dto.setMileage(NumberFormatUtil.getDoubleValueData(lastLocation.getSm() / 1D, 2));
                    map.put(lastLocation.getTerminalId(), dto);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("[CarLastLocationService][queryCarLastLocation] data : " + JsonUtil.toJson(map));
        return map;
    }
}
