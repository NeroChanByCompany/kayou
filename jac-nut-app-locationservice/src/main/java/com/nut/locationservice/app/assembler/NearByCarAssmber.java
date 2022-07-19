package com.nut.locationservice.app.assembler;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.assembler
 * @Author: yzl
 * @CreateTime: 2021-06-17 11:19
 * @Version: 1.0
 */

public class NearByCarAssmber {
    // 计算经纬度之间的距离时候使用
    private static final double EARTH_RADIUS = 6378.137;
    /**
     * 计算用的2
     **/
    private static final int MAGIC_TATE = 2;
    /**
     * 计算用的 180°
     **/
    private static final int EARTH_ECHOS = 180;
    private static final int LONG_CONVERT = 1000000;

    /**
     * 判断 返回的在线车辆 是否在指定的经纬度范围内
     *
     * @param centerLon 中心点的 经度
     * @param centerLat 中心点的 纬度
     * @param carLon    车辆经度
     * @param carLat    车辆纬度
     * @param range     指定半径 单位 千米
     * @return 返回 是否在范围内
     */
    public static boolean isCarInArea(double centerLon, double centerLat,
                                      int range, double carLon, double carLat) {
        //先计算查询点的经纬度范围
        //地球半径千米
        double r = EARTH_RADIUS;
        //0.5千米距离
        //double dis = 0.5;
        double dlng = MAGIC_TATE * Math.asin(Math.sin(range / (MAGIC_TATE * r)) / Math.cos(centerLat * Math.PI / EARTH_ECHOS));
        //角度转为弧度
        dlng = dlng * EARTH_ECHOS / Math.PI;
        double dlat = range / r;
        dlat = dlat * EARTH_ECHOS / Math.PI;
        // 根据传递 过来的中心点 获取 指定半径的 4个点
        // Lat/Lon 纬度/经度
        double minlat = centerLat - dlat;
        double maxlat = centerLat + dlat;
        double minlng = centerLon - dlng;
        double maxlng = centerLon + dlng;

        double lon = carLon / LONG_CONVERT;
        double lat = carLat / LONG_CONVERT;
        return isInArea(lat, lon, minlat, maxlat, minlng, maxlng);
    }

    /**
     * @param latitue        待测点的纬度
     * @param longitude      待测点的经度
     * @param areaLatitude1  纬度范围限制1
     * @param areaLatitude2  纬度范围限制2
     * @param areaLongitude1 经度限制范围1
     * @param areaLongitude2 经度范围限制2
     * @return 若在范围内 返回 true 否则 false
     */
    private static boolean isInArea(double latitue, double longitude, double areaLatitude1, double areaLatitude2, double areaLongitude1, double areaLongitude2) {
        //如果在纬度的范围内
        if (isInRange(latitue, areaLatitude1, areaLatitude2)) {
            //如果都在东半球或者都在西半球
            if (areaLongitude1 * areaLongitude2 > 0) {
                if (isInRange(longitude, areaLongitude1, areaLongitude2)) {
                    return true;
                } else {
                    return false;
                }
            } else {//如果一个在东半球，一个在西半球
                //如果跨越0度经线在半圆的范围内
                if (Math.abs(areaLongitude1) + Math.abs(areaLongitude2) < EARTH_ECHOS) {
                    if (isInRange(longitude, areaLongitude1, areaLongitude2)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    //如果跨越180度经线在半圆范围内
                    //东半球的经度范围left-180
                    double left = Math.max(areaLongitude1, areaLongitude2);
                    //西半球的经度范围right-（-180）
                    double right = Math.min(areaLongitude1, areaLongitude2);
                    if (isInRange(longitude, left, EARTH_ECHOS) || isInRange(longitude, right, -EARTH_ECHOS)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
    }

    private static boolean isInRange(double point, double left, double right) {
        if (point >= Math.min(left, right) && point <= Math.max(left, right)) {
            return true;
        } else {
            return false;
        }

    }

}
