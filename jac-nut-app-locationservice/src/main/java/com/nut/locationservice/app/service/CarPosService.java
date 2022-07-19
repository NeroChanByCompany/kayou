package com.nut.locationservice.app.service;

import com.nut.locationservice.app.dto.CarLocationOutputDto;
import com.nut.locationservice.app.form.GetCarLocationForm;
import com.nut.locationservice.app.pojo.LastLocationPojo;

import java.util.List;
import java.util.Map;

/**
 * @Description: 查看车辆位置service
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service
 * @Author: yzl
 * @CreateTime: 2021-06-16 19:03
 * @Version: 1.0
 */
public interface CarPosService {

    Map<String, CarLocationOutputDto> getLocationByVins(GetCarLocationForm form) throws Exception;

    List<LastLocationPojo> queryJsonLastLocation(List<Long> commIds);
}
