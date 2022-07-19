package com.nut.tripanalysis.app.service;

import com.nut.tripanalysis.app.dto.AvgOilwearRankingDto;
import com.nut.tripanalysis.app.form.*;

import java.text.ParseException;
import java.util.List;

/*
 *  @author wuhaotian 2021/7/10
 */
public interface RankingService {

    /**
     * 查询车辆的平均油耗及排名信息(上月)
     *
     * @param command
     * @return
     * @throws ParseException
     */
    List<AvgOilwearRankingDto> queryMonthOilWear(QueryMonthAvgOilWearForm command);

    /**
     * 上月油耗排行榜(top10)
     *
     * @param command
     * @return
     * @throws ParseException
     */
    public List<AvgOilwearRankingDto> queryMonthOilWearRanking(QueryMonthAvgOilWearRankingForm command, boolean carNumberHideFlg);
    /**
     * 查询车辆的平均油耗及排名信息(上周)
     *
     * @param command
     * @return
     * @throws ParseException
     */
    List<AvgOilwearRankingDto> queryWeekAvgOilWear(QueryWeekAvgOilWearForm command);
    /**
     * 上周油耗排行榜(top10)
     *
     * @param command
     * @return
     * @throws ParseException
     */
    List<AvgOilwearRankingDto> queryWeekAvgOilWearRanking(QueryWeekAvgOilWearRankingForm command, boolean carNumberHideFlg);
    /**
     * 查询车辆的平均油耗及排名信息(昨日)
     *
     * @param command
     * @return
     * @throws ParseException
     */
    List<AvgOilwearRankingDto> queryYesterdayOilWear(QueryYesterdayAvgOilWearForm command);
    /**
     * 昨日油耗排行榜(top10)
     *
     * @param command
     * @return
     * @throws ParseException
     */
    List<AvgOilwearRankingDto> queryYesterdayOilWearRanking(QueryYesterdayAvgOilWearRankingForm command,boolean carNumberHideFlg);
}
