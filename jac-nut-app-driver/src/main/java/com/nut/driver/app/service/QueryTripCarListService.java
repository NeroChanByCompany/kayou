package com.nut.driver.app.service;

import com.nut.driver.app.dto.TripCarListDto;
import com.nut.driver.app.form.QueryTripCarListForm;

import java.util.List;

/**
 * @Description: 行程分析
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service
 * @Author: yzl
 * @CreateTime: 2021-06-30 09:48
 * @Version: 1.0
 */

public interface QueryTripCarListService {
    List<TripCarListDto> queryTripCarList(QueryTripCarListForm form);
}
