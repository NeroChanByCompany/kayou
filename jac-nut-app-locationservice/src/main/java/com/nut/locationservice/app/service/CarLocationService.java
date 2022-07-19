package com.nut.locationservice.app.service;

import com.nut.locationservice.app.dto.CarLocationInputDto;
import com.nut.locationservice.app.dto.CarLocationOutputDto;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service
 * @Author: yzl
 * @CreateTime: 2021-06-16 19:31
 * @Version: 1.0
 */
public interface CarLocationService {

    Map<String, CarLocationOutputDto> getCarLocation(List<CarLocationInputDto> carList);

}
