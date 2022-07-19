package com.nut.driver.app.service;

import com.nut.driver.app.form.StatisticsFleetLineReportForm;
import com.nut.driver.app.form.StatisticsFleetReportForm;
import com.nut.common.result.HttpCommandResultWithData;

public interface StatisticsFleetService {

    HttpCommandResultWithData fleetReport(StatisticsFleetReportForm command) throws Exception;

    HttpCommandResultWithData fleetMileageReport(StatisticsFleetReportForm command) throws Exception;

    HttpCommandResultWithData fleetOilReport(StatisticsFleetReportForm command) throws Exception;

    HttpCommandResultWithData fleetTimeReport(StatisticsFleetReportForm command) throws Exception;

    HttpCommandResultWithData fleetBadDrivingReport(StatisticsFleetReportForm command) throws Exception;

    /**
     * @return com.nut.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 车队线路报表
     * @Date 10:59 2021/8/3
     * @Param [form]
     **/
    HttpCommandResultWithData fleetLineMileageReport(StatisticsFleetLineReportForm form);
}
