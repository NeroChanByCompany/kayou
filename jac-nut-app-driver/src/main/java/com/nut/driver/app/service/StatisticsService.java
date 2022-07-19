package com.nut.driver.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.dto.CarReportDTO;
import com.nut.driver.app.form.*;
import com.nut.driver.app.pojo.FltStatisticalPojo;
import com.nut.driver.app.pojo.MileageAndOilPojo;

import java.util.List;

/**
 * @author liuBing
 * @Classname Statistics
 * @Description TODO
 * @Date 2021/6/19 17:11
 */
public interface StatisticsService {
    /**
     * 查询车辆报表
     * @param form
     * @return
     */
    HttpCommandResultWithData carReport(StatisticsCarReportForm form);
    /**
     * 统计时校验统计的时间及范围
     * 最多统计60天数据
     * 最早365天内的数据
     * @param startTime
     *          开始时间
     * @param endTime
     *          结束时间
     * @return
     *      校验结果
     */
    String checkQueryTime(String startTime, String endTime);
    /**
     * 调用位置云当日里程油耗查询接口
     * @param autoTerminals
     * @return
     */
     List<MileageAndOilPojo> getCurrentDayEntityList(List<String> autoTerminals);
    /**
     * 当日的不良驾驶行为统计
     * @param autoTerminals
     * @return
     * @throws Exception
     */
     List<FltStatisticalPojo> querySameDayBadDriving(List<String> autoTerminals);
    /**
     * 车辆报表-里程车辆列表
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData carReportMileageCarList(StatisticsMileageCarListForm command) throws Exception;
    /**
     * 车辆报表-里程图表
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData carReportMileageChart(StatisticsCarReportChartForm command) throws Exception;
    /**
     * 车辆报表-车辆里程明细列表查询
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData carReportDetailedMileageList(StatisticsMileageCarDetailForm command) throws Exception;
    /**
     * 车辆报表-总油耗图表查询
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData carReportOilChart(StatisticsCarReportChartForm command) throws Exception;
    /**
     * 车辆报表-怠速油耗
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData carReportIdlOil(StatisticsCarReportIdlOilForm command)throws Exception;
    /**
     * 车辆报表-油耗车辆列表查询
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData carReportOilCarList(StatisticsOilCarListForm command)throws Exception;
    /**
     * 车辆报表-油耗车辆明细列表查询
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData carReportDetailedOilList(StatisticsMileageCarDetailForm command) throws Exception;
    /**
     * 车辆报表-行驶时长图表查询
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData carReportDrivingChart(StatisticsCarReportChartForm command) throws Exception;
    /**
     * 车辆报表-行驶时长-怠速时长查询
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData carReportIdlTime(StatisticsCarReportIdlOilForm command) throws Exception;
    /**
     * 车辆列表-行驶时长车辆列表查询
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData carReportCarList(StatisticsOilCarListForm command) throws Exception;
    /**
     * 车辆报表-车辆行驶时长明细查询
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData carReportDetailedDrivingList(StatisticsMileageCarDetailForm command) throws Exception;
    /**
     * 车辆报表-不良驾驶行为次数查询
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData badDrivingChartForPie(StatisticsCarReportChartForm command) throws Exception;
    /**
     * 车辆报表-不良驾驶行为-按行为统计
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData badDrivingCountByBehavior(StatisticsCarReportForm command) throws Exception;
    /**
     * 车辆报表-不良驾驶行为-按车辆统计
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData badDrivingCountByCar(BadDrivingCountByCarForm command)throws Exception;
    /**
     * 车辆报表-不良驾驶行为-单车不良驾驶行为查询
     * @param command
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData badDrivingCountByCarDetailChart(StatisticsMileageCarDetailForm command) throws Exception;

    /**
     * 车辆报表统计-按线路
     * @param form
     * @return
     */
    CarReportDTO carLineReport(StatisticsCarLinReportForm form);
    /**
     * @Author liuBing
     * @Description //TODO 车辆报表统计详情-按线路
     * @Date 10:23 2021/8/3
     * @Param [form]
     * @return com.nut.common.result.HttpCommandResultWithData
     **/
    HttpCommandResultWithData carReportLinMileageCarList(StatisticsLineMileageCarListForm form);
}
