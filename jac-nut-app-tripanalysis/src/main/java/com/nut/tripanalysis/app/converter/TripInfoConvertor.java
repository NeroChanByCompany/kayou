package com.nut.tripanalysis.app.converter;


import com.nut.tripanalysis.app.dto.TripInfoDto;
import com.nut.tripanalysis.app.pojo.TripDetailPojo;
import com.nut.tripanalysis.common.utils.DoubleFormatUtil;

import java.math.BigDecimal;


/**
 * 查询行程详细信息接口--转换类
 * <p>
 * wangyang
 */
public class TripInfoConvertor {

    private static final String HIGH_SPEED = "1";//"速度偏高";
    private static final String A_LITTLE_HIGH_SPEED = "2";//"速度略高";
    private static final String STABLE_SPEED = "3";//"速度稳定,路况通畅";
    private static final String A_LITTLE_LOW_SPEED = "4";//"速度略低,部分路段不通畅";
    private static final String LOW_SPEED = "5";//"速度偏低,部分路段不通畅";
    private static final String SMOOTHLY_DRIVE = "6";//"建议平稳驾驶";
    private static final String IMPROVEMENT_DRIVE = "7";//"驾驶习惯急待改进";
    private static final String BAD_DRIVE = "8";//"注意不良驾驶习惯";
    private static final String BETTER_DRIVER = "9";//"驾驶习惯良好";
    private static final String OVER_HIGH_OIL = "10";//"油耗较高,请注意养成良好驾驶习惯";
    private static final String HIGH_OIL = "11";//"油耗偏高,请注意养成良好驾驶习惯";
    private static final String A_LITTLE_HIGH_OIL = "12";//"油耗略高,请注意养成良好驾驶习惯";
    private static final String NORMAL_OIL = "13";//"油耗正常,请保持良好驾驶习惯";
    private static final String LOW_OIL = "14";//"油耗低,请保持良好驾驶习惯";

    public static TripInfoDto entityToDto(TripDetailPojo entity) throws Exception {
        TripInfoDto dto = new TripInfoDto();
        dto.setStartLon(entity.getStartLon());
        dto.setStartLat(entity.getStartLat());
        dto.setEndLon(entity.getEndLon());
        dto.setEndLat(entity.getEndLat());
        dto.setStartTime(Long.parseLong(String.valueOf(entity.getStartTime() * 1000)));
        dto.setEndTime(Long.parseLong(String.valueOf(entity.getEndTime() * 1000)));
        dto.setTripScore(entity.getScore());
        dto.setTripLen(BigDecimal.valueOf(entity.getMileage() / 1000D).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
        dto.setAvgOil(DoubleFormatUtil.formatDoubleValueTo1Bit(entity.getAvgOil()));
        dto.setAvgSpeed(DoubleFormatUtil.formatDoubleValueTo1Bit(entity.getAvgSpeed()));
        dto.setTripTime(entity.getWorkTime() * 1000);
        dto.setTripOil(BigDecimal.valueOf(entity.getOil()).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
        dto.setTripIdleOil(DoubleFormatUtil.formatDoubleValueTo1Bit(entity.getParkingIdleFuelConsumption() * 0.1));
        dto.setOilLevel(entity.getOilLevel());

        //同车型、历史平均油耗虽在service计算，但避免在填充建议语言时空指针，先设置0
        //同车型信息在service计算
        dto.setAvgOilCarModel(0);
        //历史平均油耗信息在service计算
        dto.setAvgOilHistory(0);
        dto.setDriveLevel(entity.getLevel());
        dto.setZeroUpTime(entity.getSpeedRange01Time() * 1000);
        dto.setSlowTime((entity.getSpeedRange02Time()) * 1000);
        dto.setLowTime((entity.getSpeedRange03Time()) * 1000);
        dto.setMidTime((entity.getSpeedRange04Time()) * 1000);
        dto.setHighTime((entity.getSpeedRange05Time()) * 1000);
        dto.setOverTime((entity.getSpeedRange06Time()) * 1000);
        if (entity.getWorkTime() > 0) {
            long l = entity.getWorkTime() * 1000;
            dto.setZeroUpPer(dto.getZeroUpTime() * 100 / l);
            dto.setSlowPer(dto.getSlowTime() * 100 / l);
            dto.setLowPer(dto.getLowTime() * 100 / l);
            dto.setMidPer(dto.getMidTime() * 100 / l);
            dto.setHighPer(dto.getHighTime() * 100 / l);
            dto.setOverPer(dto.getOverTime() * 100 / l);
        }
        dto.setFastupCount(entity.getDfSharpUpSpeedFrequency());
        dto.setFastlowCount(entity.getDfSharpDownSpeedFrequency());
        dto.setCrookCount(entity.getSharpTurnFrequency());
        dto.setSpeedingCount(entity.getDfOverSpeedFrequency());

        /* 超长怠速 */
        dto.setIdleTimeoutCount(entity.getLongParkingIdleNumber());
        /* 冷车运行 */
        dto.setColdRunCount(entity.getDfVehicleColdStartFrequency());
        /* 夜晚开车 */
        dto.setNightRunCount(entity.getInNightFrequency());
        /* 低挡高速 */
        dto.setLowGrHighSpdCount(entity.getEngineOverSpeedNumber());
        /* 全油门 */
        dto.setFullThrottleCount(entity.getFullAcceleratorFrequency());
        /* 大油门 */
        dto.setRoughAccCount(entity.getLargeAcceleratorFrequency());
        /* 空挡滑行 */
        dto.setNeutralGearCoastCount(entity.getIdlingFrequency());
        /* 熄火滑行 */
        dto.setStallCoastCount(entity.getStallingSlideFrequency());
        /* 制动里程 km */
        dto.setBreakLen(DoubleFormatUtil.formatDoubleValueTo1Bit(entity.getCumulativeMileage() / 1000D));

        //驾驶建议计算
        String driveSuggest = getDriveSuggest(entity.getAvgSpeed()) + "," + getSafetySuggest(dto) + "," + getSuggestEnding();

        // 驾驶建议
        dto.setDriveSuggest(driveSuggest);

        /* 0-800转速区间范围 半角-分隔上下限 */
        dto.setRpmLowMidRange("0-800");
        /* 低转速区间时间 毫秒 */
        dto.setRpmLowMidTime(entity.getEngineSpeedRange01Time() * 1000);

        /* 800-1100转速区间范围 半角-分隔上下限 */
        dto.setRpmMidRange("800-1100");
        /* 800-1100转速区间时间 毫秒 */
        dto.setRpmMidTime(entity.getEngineSpeedRange02Time() * 1000);

        /* 1100-1700转速区间范围 半角-分隔上下限 */
        dto.setRpmMidHighRange("1100-1700");
        /* 1100-1700转速区间时间 毫秒 */
        dto.setRpmMidHighTime(entity.getEngineSpeedRange03Time() * 1000);

        /* 1700-2000转速区间范围 半角-分隔上下限 */
        dto.setRpmHighRange("1700-2000");
        /* 1700-2000转速区间时间 毫秒 */
        dto.setRpmHighTime(entity.getEngineSpeedRange04Time() * 1000);

        /* 2000-2300转速区间范围 半角-分隔上下限 */
        dto.setRpmSuperHighRange("2000-2300");
        /* 2000-2300转速区间时间 毫秒 */
        dto.setRpmSuperHighTime(entity.getEngineSpeedRange05Time() * 1000);

        /* 2000-2300转速区间范围 半角-分隔上下限 */
        dto.setRpmSuperHighRange2(">2300");
        /* 2000-2300转速区间时间 毫秒 */
        dto.setRpmSuperHighTime2(entity.getExceedEngineSpeedRangeTime() * 1000);

        /* 转速区间百分比 */
        /* 0-800转速区间百分比 */
        dto.setRpmLowMidPer(entity.getEngineSpeedRange01UseRatio());
        /* 800-1100转速区间百分比 */
        dto.setRpmMidPer(entity.getEngineSpeedRange02UseRatio());
        /* 1100-1700转速区间百分比 */
        dto.setRpmMidHighPer(entity.getEngineSpeedRange03UseRatio());
        /* 1700-2000转速区间百分比 */
        dto.setRpmHighPer(entity.getEngineSpeedRange04UseRatio());
        /* 2000-2300转速区间百分比 */
        dto.setRpmSuperHighPer(entity.getEngineSpeedRange05UseRatio());
        /* >2300转速区间百分比 */
        dto.setRpmSuperHighPer2(entity.getExceedEngineSpeedRangeUseRatio());

        /*  转速百分比修正 */
        //correctRpm(dto);
        TripInfoDto correct = correct(dto);
        correct.setEngineOverSpeedNumber(dto.getEngineOverSpeedNumber());

        return correct;
    }

    /**
     * 1.3.1
     * 驾驶分析建议--速度部分
     * <p/>
     * 规则
     */
    public static String getDriveSuggest(int avgSpeed) {

        if (avgSpeed > 100) {
            return HIGH_SPEED;
        }
        if (avgSpeed > 80) {
            return A_LITTLE_HIGH_SPEED;
        }
        if (avgSpeed > 60) {
            return STABLE_SPEED;
        }
        if (avgSpeed > 30) {
            return A_LITTLE_LOW_SPEED;
        }
        return LOW_SPEED;
    }

    /**
     * 1.3.2
     * 驾驶分析建议--安全性部分
     * <p/>
     * 规则
     */
    public static String getSafetySuggest(TripInfoDto pojo) {
        int count = pojo.getFastupCount() + pojo.getFastlowCount() + pojo.getCrookCount() + pojo.getSpeedingCount();
        if (count >= 30) {
            return IMPROVEMENT_DRIVE;
        }
        if (count >= 10) {
            return BAD_DRIVE;
        }
        return BETTER_DRIVER;
    }

    /**
     * 1.3.2
     * 建议部分
     * <p/>
     * 规则
     */
    public static String getSuggestEnding() {
        return SMOOTHLY_DRIVE;
    }

    /**
     * 转速百分比修正
     *
     * @param dto
     * @return
     */
    private static TripInfoDto correctRpm(TripInfoDto dto) {
        long[] pers = {dto.getRpmLowMidPer(), dto.getRpmMidPer(), dto.getRpmMidHighPer(), dto.getRpmHighPer(), dto.getRpmSuperHighPer(), dto.getRpmSuperHighPer2()};
        long totalPer = dto.getRpmLowMidPer() + dto.getRpmMidPer() + dto.getRpmMidHighPer() + dto.getRpmHighPer() + dto.getRpmSuperHighPer() + dto.getRpmSuperHighPer2();
        if (totalPer == 0) {
            return dto;
        }
        if (totalPer != 100) {
            for (int i = 0; i < pers.length; i++) {
                pers[i] = pers[i] * 100 / totalPer;
            }
        }
        dto.setRpmLowMidPer(pers[0]);
        dto.setRpmMidPer(pers[1]);
        dto.setRpmMidHighPer(pers[2]);
        dto.setRpmHighPer(pers[3]);
        dto.setRpmSuperHighPer(pers[4]);
        dto.setRpmSuperHighPer(pers[5]);
        totalPer = dto.getRpmLowMidPer() + dto.getRpmMidPer() + dto.getRpmMidHighPer() + dto.getRpmHighPer() + dto.getRpmSuperHighPer() + dto.getRpmSuperHighPer2();
        if (totalPer < 100 && totalPer != 0) {
            long maxPer = pers[0];
            int maxPosition = 0;
            for (int i = 0; i < pers.length; i++) {
                if (pers[i] > maxPer) {
                    maxPer = pers[i];
                    maxPosition = i;
                }
            }
            pers[maxPosition] += (100 - totalPer);
            dto.setRpmLowMidPer(pers[0]);
            dto.setRpmMidPer(pers[1]);
            dto.setRpmMidHighPer(pers[2]);
            dto.setRpmHighPer(pers[3]);
            dto.setRpmSuperHighPer(pers[4]);
            dto.setRpmSuperHighPer2(pers[5]);
        }
        return dto;
    }

    /**
     * 百分比修正
     *
     * @param dto
     * @return
     */
    private static TripInfoDto correct(TripInfoDto dto) {
        long[] pers = {dto.getZeroUpPer(), dto.getSlowPer(), dto.getLowPer(), dto.getMidPer(), dto.getHighPer(), dto.getOverPer()};
        long[] times = {dto.getZeroUpTime(), dto.getSlowTime(), dto.getLowTime(), dto.getMidTime(), dto.getHighTime(), dto.getOverTime()};
        long tripTime = dto.getTripTime();
        long totalTime = dto.getZeroUpTime() + dto.getSlowTime() + dto.getLowTime() + dto.getMidTime() + dto.getHighTime() + dto.getOverTime();
        long totalPer = dto.getZeroUpPer() + dto.getSlowPer() + dto.getLowPer() + dto.getMidPer() + dto.getHighPer() + dto.getOverPer();
        if (totalPer != 100 && totalPer != 0) {
            for (int i = 0; i < pers.length; i++) {
                pers[i] = pers[i] * 100 / totalPer;
                times[i] = times[i] * tripTime / totalTime;
            }
        }

        dto.setZeroUpTime((times[0] / 1000) * 1000);
        dto.setSlowTime((times[1] / 1000) * 1000);
        dto.setLowTime((times[2] / 1000) * 1000);
        dto.setMidTime((times[3] / 1000) * 1000);
        dto.setHighTime((times[4] / 1000) * 1000);
        dto.setOverTime((times[5] / 1000) * 1000);
        dto.setZeroUpPer(pers[0]);
        dto.setSlowPer(pers[1]);
        dto.setLowPer(pers[2]);
        dto.setMidPer(pers[3]);
        dto.setHighPer(pers[4]);
        dto.setOverPer(pers[5]);

        totalPer = dto.getZeroUpPer() + dto.getSlowPer() + dto.getLowPer() + dto.getMidPer() + dto.getHighPer() + dto.getOverPer();
        if (totalPer < 100) {
            long maxPer = pers[0];
            int maxPosition = 0;
            for (int i = 0; i < pers.length; i++) {
                if (pers[i] > maxPer) {
                    maxPer = pers[i];
                    maxPosition = i;
                }
            }
            pers[maxPosition] += (100 - totalPer);
            dto.setZeroUpPer(pers[0]);
            dto.setSlowPer(pers[1]);
            dto.setLowPer(pers[2]);
            dto.setMidPer(pers[3]);
            dto.setHighPer(pers[4]);
            dto.setOverPer(pers[5]);
        }
        return dto;
    }
}
