package com.nut.servicestation.common.assembler;

import com.nut.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 轻量级路线规划 调用参数
 */
@Slf4j
public class DirectionLiteRequestAssembler {
    /**
     * 开发者密钥
     */
    private static final String AK = "ak";
    /**
     * 起点经纬度，格式为：纬度,经度；小数点后不超过6位
     */
    private static final String ORIGIN = "origin";
    /**
     * 终点经纬度，格式为：纬度,经度；小数点后不超过6位
     */
    private static final String DESTINATION = "destination";
    /**
     * 默认值：0。
     * 可选值：
     * 0：常规路线，即多数用户常走的一条经验路线，满足大多数场景需求，是较推荐的一个策略
     * 1：不走高速
     * 2：躲避拥堵
     * 3：距离较短
     */
    private static final String TACTICS = "tactics";
    /**
     * 默认bd09ll
     * 允许的值为：
     * bd09ll：百度经纬度坐标
     * bd09mc：百度墨卡托坐标
     * gcj02：国测局加密坐标
     * wgs84：gps设备获取的坐标
     */
    private static final String COORD_TYPE = "coord_type";
    /**
     * 返回值的坐标类型，默认为百度经纬度坐标：bd09ll
     * 可选值：
     * bd09ll：百度经纬度坐标
     * gcj02：国测局加密坐标
     */
    private static final String RET_COORDTYPE = "ret_coordtype";

    /**
     * 生成请求参数
     */
    public static String getRequestParam(String ak, String stLat, String stLon, String etLat, String etLon,
                                         Integer tactics, String coordType, String retCoordType) {
        Map<String, Object> paramMap = new LinkedHashMap<>();
        paramMap.put(AK, ak);
        paramMap.put(ORIGIN, stLat + "," + stLon);
        paramMap.put(DESTINATION, etLat + "," + etLon);
        if (tactics != null) {
            paramMap.put(TACTICS, tactics);
        }
        paramMap.put(COORD_TYPE, coordType);
        paramMap.put(RET_COORDTYPE, retCoordType);
        return paramMap.entrySet().stream().map(entry -> entry.getKey() + "=" + String.valueOf(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    /**
     * 解析接口的JSON响应
     */
    public static DirectionLiteResult parseResponse(String json) {
        log.debug("[parseResponse]json:{}", json);
        DirectionLiteResult ret = new DirectionLiteResult();
        try {
            DirectionLiteResponse root = JsonUtil.fromJson(json, DirectionLiteResponse.class);
            int status = root.getStatus();
            log.debug("[parseResponse]status:{}", status);
            ret.setStatus(status);

            String message = root.getMessage();
            log.debug("[parseResponse]message:{}", message);
            ret.setMessage(message);

            DirectionLiteResponse.Result result = root.getResult();
            List<DirectionLiteResponse.Result.Route> routesList = result.getRoutes();
            if (routesList.isEmpty()) {
                return ret;
            }
            DirectionLiteResponse.Result.Route route = routesList.get(0);

            Double distance = route.getDistance();
            log.debug("[parseResponse]distance:{}", distance);
            ret.setDistance(distance);

            Double duration = route.getDuration();
            log.debug("[parseResponse]duration:{}", duration);
            ret.setDuration(duration);

            List<DirectionLiteResponse.Result.Route.Step> stepsList = route.getSteps();
            List<String> lonLats = new ArrayList<>();
            for (DirectionLiteResponse.Result.Route.Step step : stepsList) {
                String path = step.getPath();
                lonLats.addAll(Arrays.asList(path.split(";")));
            }
            log.debug("[parseResponse]lonLats:{}", lonLats);
            ret.setLonLats(lonLats);
        } catch (NullPointerException e) {
            // 停止解析
            log.info("[parseResponse]exception when parsing response", e);
        }
        return ret;
    }

    /**
     * 请求返回结果类
     */
    public static class DirectionLiteResponse {
        private Integer status;
        private String message;
        private Result result;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }

        public static class Result {
            private List<Route> routes;

            public List<Route> getRoutes() {
                return routes;
            }

            public void setRoutes(List<Route> routes) {
                this.routes = routes;
            }

            public static class Route {
                private Double distance;
                private Double duration;
                private Integer traffic_condition;
                private List<Step> steps;

                public Double getDistance() {
                    return distance;
                }

                public void setDistance(Double distance) {
                    this.distance = distance;
                }

                public Double getDuration() {
                    return duration;
                }

                public void setDuration(Double duration) {
                    this.duration = duration;
                }

                public Integer getTraffic_condition() {
                    return traffic_condition;
                }

                public void setTraffic_condition(Integer traffic_condition) {
                    this.traffic_condition = traffic_condition;
                }

                public List<Step> getSteps() {
                    return steps;
                }

                public void setSteps(List<Step> steps) {
                    this.steps = steps;
                }

                public static class Step {
                    private Double distance;
                    private Double duration;
                    private String path;
                    private Position start_location;
                    private Position end_location;

                    public Double getDistance() {
                        return distance;
                    }

                    public void setDistance(Double distance) {
                        this.distance = distance;
                    }

                    public Double getDuration() {
                        return duration;
                    }

                    public void setDuration(Double duration) {
                        this.duration = duration;
                    }

                    public String getPath() {
                        return path;
                    }

                    public void setPath(String path) {
                        this.path = path;
                    }

                    public Position getStart_location() {
                        return start_location;
                    }

                    public void setStart_location(Position start_location) {
                        this.start_location = start_location;
                    }

                    public Position getEnd_location() {
                        return end_location;
                    }

                    public void setEnd_location(Position end_location) {
                        this.end_location = end_location;
                    }

                    public static class Position {
                        private Double lng;
                        private Double lat;

                        public Double getLng() {
                            return lng;
                        }

                        public void setLng(Double lng) {
                            this.lng = lng;
                        }

                        public Double getLat() {
                            return lat;
                        }

                        public void setLat(Double lat) {
                            this.lat = lat;
                        }
                    }
                }
            }
        }
    }

    /**
     * 请求返回结果类（合并后）
     */
    public static class DirectionLiteResult {
        /**
         * 状态码
         */
        private Integer status;
        /**
         * 响应信息
         */
        private String message;
        /**
         * 方案距离，单位：米
         */
        private Double distance;
        /**
         * 线路耗时，单位：秒
         */
        private Double duration;
        /**
         * 所有分段起点终点经纬度
         */
        private List<String> lonLats;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public Double getDuration() {
            return duration;
        }

        public void setDuration(Double duration) {
            this.duration = duration;
        }

        public List<String> getLonLats() {
            return lonLats;
        }

        public void setLonLats(List<String> lonLats) {
            this.lonLats = lonLats;
        }
    }

    /**
     * 坐标类型
     */
    public static class CoordType {
        /**
         * 百度经纬度坐标
         */
        public static final String BD09LL = "bd09ll";
        /**
         * 百度墨卡托坐标
         */
        public static final String BD09MC = "bd09mc";
        /**
         * 国测局加密坐标
         */
        public static final String GCJ02 = "gcj02";
        /**
         * gps设备获取的坐标
         */
        public static final String WGS84 = "wgs84";
    }
}
