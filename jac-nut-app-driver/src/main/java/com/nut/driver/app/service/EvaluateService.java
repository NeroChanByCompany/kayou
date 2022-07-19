package com.nut.driver.app.service;

import com.nut.driver.app.form.ReviewOrderForm;
import com.nut.driver.app.form.StationEvaluatesForm;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.driver.app.dto.StationEvaluatesDto;


/**
 * @Description: 评价
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service
 * @Author: yzl
 * @CreateTime: 2021-06-23 14:49
 * @Version: 1.0
 */
public interface EvaluateService {
    PagingInfo<StationEvaluatesDto> stationEvaluates(StationEvaluatesForm form);

    HttpCommandResultWithData reviewOrder(ReviewOrderForm form);

}
