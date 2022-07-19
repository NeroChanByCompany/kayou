package com.nut.driver.app.service;

import com.nut.driver.app.dto.CarPhyExaDetailDto;
import com.nut.driver.app.dto.CarPhyExaDto;
import com.nut.driver.app.form.QueryCarPhysicalExaminationDetailForm;
import com.nut.driver.app.form.QueryCarPhysicalExaminationForm;

/**
 * @Description: 车辆体检
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service
 * @Author: yzl
 * @CreateTime: 2021-06-29 15:39
 * @Version: 1.0
 */

public interface CarPhysicalExaminationService {
    CarPhyExaDto carPhysicalExamination(QueryCarPhysicalExaminationForm form);
    CarPhyExaDetailDto carPhysicalExaminationDetail(QueryCarPhysicalExaminationDetailForm form);
}
