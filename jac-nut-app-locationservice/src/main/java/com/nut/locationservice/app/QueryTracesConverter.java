package com.nut.locationservice.app;

import com.nut.locationservice.app.dto.QueryTracesDto;
import com.nut.locationservice.app.pojo.QueryTracesPojo;
import com.nut.common.utils.CopyPropUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * @Description: 时间段内总里程、时间段内总油耗
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService
 * @Author: yzl
 * @CreateTime: 2021-06-17 10:00
 * @Version: 1.0
 */

public class QueryTracesConverter {

    private static final String PATTERN = "#0";
    private static final String PATTERN_1 = "#0.0";
    private static final String PATTERN_2 = "#0.00";
    private static final String PATTERN_4 = "#0.0000";
    private static final String REGEX = "^[-\\+]?[\\d]*[\\.]?[\\d]*$";

    public static QueryTracesDto queryTracesConverter(QueryTracesPojo queryTracesPojo) {
        QueryTracesDto queryTracesDto = new QueryTracesDto();
        if(null != queryTracesPojo) {
            try {
                CopyPropUtil.copyProp(queryTracesPojo, queryTracesDto);
                //剩余油量
                queryTracesDto.setOilwear(retainByDigit(queryTracesPojo.getOilwear(),1));
                //整车油耗
                queryTracesDto.setTotalFuelConsumption(retainByDigit(queryTracesPojo.getOilwear(),1));
                //终端里程
                queryTracesDto.setMileage(retainByDigit(queryTracesPojo.getMileage(),2));
                //当日里程 小计里程
                queryTracesDto.setDailyMileage(retainByDigit(queryTracesPojo.getDailyMileage(),2));
                //总油耗
                queryTracesDto.setTotalOil(Double.valueOf(retainByDigit(queryTracesPojo.getTotalOil(),1)));
                //瞬时油耗
                queryTracesDto.setRealTimeOilConsumption(Double.valueOf(retainByDigit(queryTracesPojo.getRealTimeOilConsumption(),1)));
                //整车里程
                queryTracesDto.setTotolmileage(retainByDigit(queryTracesPojo.getTotolmileage(),2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return queryTracesDto;
    }

    public static String retainByDigit(Object value, int digit) {
        String pattern;
        switch (digit) {
            case 0:
                pattern = PATTERN;
                break;
            case 1:
                pattern = PATTERN_1;
                break;
            case 2:
                pattern = PATTERN_2;
                break;
            default:
                pattern = PATTERN_4;
        }
        return retainByPattern(value, pattern);
    }

    private static String retainByPattern(Object value, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        if (value == null || value == "") {
            return pattern.substring(1, pattern.length());
        } else if (value instanceof String) {
            String strValue = value.toString();
            Pattern p = Pattern.compile(REGEX);
            if (p.matcher(strValue).matches()) {
                value = Double.valueOf((value.toString()));
            } else {
                return pattern.substring(1, pattern.length());
            }
        }
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(value);
    }

}
