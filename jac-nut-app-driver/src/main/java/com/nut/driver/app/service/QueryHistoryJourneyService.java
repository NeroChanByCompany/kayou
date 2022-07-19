package com.nut.driver.app.service;

import com.nut.driver.app.form.QueryHistoryJourneyForm;

import java.util.List;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service
 * @Author: yzl
 * @CreateTime: 2021-06-29 11:12
 * @Version: 1.0
 */

public interface QueryHistoryJourneyService {

    List QueryHistoryJourney(QueryHistoryJourneyForm form);
}
