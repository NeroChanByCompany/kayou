package com.nut.tripanalysis.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.tripanalysis.app.dto.TripInfoDto;
import com.nut.tripanalysis.app.form.QueryTripByDayForm;
import com.nut.tripanalysis.app.form.QueryTripByMonthForm;
import com.nut.tripanalysis.app.form.QueryTripInfoForm;
import com.nut.tripanalysis.app.pojo.CarTripPojo;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/9
 */
public interface TripService {

    /**
     * 查询指定日期的行程列表
     */
    HttpCommandResultWithData queryTripByDay(QueryTripByDayForm command) throws Exception;
    /**
     * 月行程数据查询
     */
    HttpCommandResultWithData queryTripByMonth(QueryTripByMonthForm command) throws Exception;
    /**
     * 查询行程详细信息
     */
    TripInfoDto queryTripInfo(QueryTripInfoForm command) throws Exception;

}
