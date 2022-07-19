package com.nut.servicestation.common.assembler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 轨迹纠偏API 调用参数
 */
public class RectifyTrackRequestAssembler {
    /**
     * 是否需要将轨迹点绑路并补充道路形状点 ，示例：
     * need_mapmatch:0 不绑路
     * need_mapmatch:1 绑路
     */
    public static final String NEED_MAPMATCH = "need_mapmatch";
    /**
     * 根据不同交通工具选择不同的纠偏策略和参数，目前支持：自动（即鹰眼自动识别的交通方式）、驾车、骑行和步行。
     */
    public static final String TRANSPORT_MODE = "transport_mode";
    /**
     * 取值范围[0,5]，数值越大去噪力度越大，代表越多的点会被当做噪点去除。若取值0，则代表不去噪。示例：
     * denoise_grade:0 （不去噪）
     * denoise_grade:1 （系统默认去噪）
     * denoise_grade:2（系统默认去噪，同时去除定位精度低于500的轨迹点，相当于保留GPS定位点、大部分Wi-Fi定位点和精度较高的基站定位点）
     * denoise_grade:3（系统默认去噪，同时去除定位精度低于100的轨迹点，相当于保留GPS定位点和大部分Wi-Fi定位点）
     * denoise_grade:4（系统默认去噪，同时去除定位精度低于50的轨迹点，相当于保留GPS定位点和精度较高的Wi-Fi定位点）
     * denoise_grade:5（系统默认去噪，同时去除定位精度低于20的轨迹点，相当于仅保留GPS定位点）
     */
    public static final String DENOISE_GRADE = "denoise_grade";
    /**
     * 取值范围[0,5]，数值越大抽稀度力度越大，代表轨迹会越稀疏。若取值0，则代表不抽稀。示例：
     * vacuate_grade:0（不抽稀）
     * vacuate_grade:2（抽稀力度为2）
     */
    public static final String VACUATE_GRADE = "vacuate_grade";

    public static String getRectifyOption(Map<String, Object> options) {
        if (options == null || options.isEmpty()) {
            return null;
        }
        return options.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining("|"));
    }

    /**
     * 请求入参类（转换为JSON字符串）
     */
    public static class RectifyTrackRequestParam {
        private String ak;
        /**
         * 最多不能超过2000个轨迹点，且轨迹里程不超过500公里（注：若轨迹里程超长，可能会出现响应时间过长或超时）。
         * point_list格式为json， 其中每个point必须包含：latitude,longitude,coord_type_input,loc_time这4个字段，
         * 可选speed,direction,height,radius这4个字段，其他字段会被舍弃。
         */
        private List<Point> point_list;
        /**
         * 支持以下配置项，多个项之间用”|”竖线相隔。
         * 默认值为：need_mapmatch:1|transport_mode:auto|denoise_grade:1|vacuate_grade:1
         */
        private String rectify_option;
        /**
         * 在里程计算时，两个轨迹点定位时间间隔5分钟以上，被认为是中断。中断轨迹提供以下5种里程估算方式。
         * no_supplement：不补充，中断两点间距离不记入里程
         * straight：使用直线距离补充
         * driving：使用最短驾车路线距离补充
         * riding：使用最短骑行路线距离补充
         * walking：使用最短步行路线距离补充
         */
        private String supplement_mode;
        /**
         * 该字段用于控制返回结果中的坐标类型。可选值为：
         * gcj02：国测局加密坐标
         * bd09ll：百度经纬度坐标
         * 该参数仅对国内（包含港、澳、台）轨迹有效，海外区域轨迹均返回 wgs84坐标系
         */
        private String coord_type_output;

        public String getAk() {
            return ak;
        }

        public void setAk(String ak) {
            this.ak = ak;
        }

        public List<Point> getPoint_list() {
            return point_list;
        }

        public void setPoint_list(List<Point> point_list) {
            this.point_list = point_list;
        }

        public String getRectify_option() {
            return rectify_option;
        }

        public void setRectify_option(String rectify_option) {
            this.rectify_option = rectify_option;
        }

        public String getSupplement_mode() {
            return supplement_mode;
        }

        public void setSupplement_mode(String supplement_mode) {
            this.supplement_mode = supplement_mode;
        }

        public String getCoord_type_output() {
            return coord_type_output;
        }

        public void setCoord_type_output(String coord_type_output) {
            this.coord_type_output = coord_type_output;
        }
    }

    /**
     * 轨迹点类
     */
    public static class Point {
        /**
         * 纬度，支持小数点后6位
         */
        private Double latitude;
        /**
         * 经度，支持小数点后6位
         */
        private Double longitude;
        /**
         * 轨迹点的坐标系，支持以下值：bd09ll（百度经纬度坐标）、gcj02（国测局加密坐标）、wgs84（GPS所采用的坐标系）
         */
        private String coord_type_input;
        /**
         * 轨迹点的定位时间，使用UNIX时间戳
         */
        private Long loc_time;
        /**
         * 定位时返回的定位精度，单位：米
         */
        private Integer radius;

        public Point() {
        }

        public Point(Double latitude, Double longitude, String coord_type_input, Long loc_time, Integer radius) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.coord_type_input = coord_type_input;
            this.loc_time = loc_time;
            this.radius = radius;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public String getCoord_type_input() {
            return coord_type_input;
        }

        public void setCoord_type_input(String coord_type_input) {
            this.coord_type_input = coord_type_input;
        }

        public Long getLoc_time() {
            return loc_time;
        }

        public void setLoc_time(Long loc_time) {
            this.loc_time = loc_time;
        }

        public Integer getRadius() {
            return radius;
        }

        public void setRadius(Integer radius) {
            this.radius = radius;
        }
    }

    /**
     * 请求返回结果类
     */
    public static class RectifyTrackResult {
        /**
         * 状态码
         */
        private Integer status;
        /**
         * 响应信息
         */
        private String message;
        /**
         * 忽略掉page_index，page_size后的轨迹点数量
         */
        private Integer total;
        /**
         * 此段轨迹的里程数，单位：米
         */
        private Double distance;
        /**
         * 此段轨迹的收费里程数，单位：米
         */
        private Double toll_distance;
        /**
         * 历史轨迹点列表
         */
        private List<Point> points;

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

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public Double getToll_distance() {
            return toll_distance;
        }

        public void setToll_distance(Double toll_distance) {
            this.toll_distance = toll_distance;
        }

        public List<Point> getPoints() {
            return points;
        }

        public void setPoints(List<Point> points) {
            this.points = points;
        }
    }

    /**
     * 交通方式
     */
    public static class TransportMode {
        /**
         * 自动（即鹰眼自动识别的交通方式）
         */
        public static final String AUTO = "auto";
        /**
         * 驾车
         */
        public static final String DRIVING = "driving";
        /**
         * 骑行
         */
        public static final String RIDING = "riding";
        /**
         * 步行
         */
        public static final String WALKING = "walking";

        private TransportMode() {
        }
    }

    /**
     * 里程补偿设置
     */
    public static class SupplementMode {
        /**
         * 不补充，中断两点间距离不记入里程
         */
        public static final String NO_SUPPLEMENT = "no_supplement";
        /**
         * 使用直线距离补充
         */
        public static final String STRAIGHT = "straight";
        /**
         * 使用最短驾车路线距离补充
         */
        public static final String DRIVING = "driving";
        /**
         * 使用最短骑行路线距离补充
         */
        public static final String RIDING = "riding";
        /**
         * 使用最短步行路线距离补充
         */
        public static final String WALKING = "walking";

        private SupplementMode() {
        }
    }

    /**
     * 返回的坐标类型
     */
    public static class CoordTypeOutput {
        /**
         * 国测局加密坐标
         */
        public static final String GCJ02 = "gcj02";
        /**
         * 百度经纬度坐标
         */
        public static final String BD09LL = "bd09ll";

        private CoordTypeOutput() {
        }
    }

    /**
     * 轨迹点的坐标系
     */
    public static class CoordTypeInput {
        /**
         * 国测局加密坐标
         */
        public static final String GCJ02 = "gcj02";
        /**
         * 百度经纬度坐标
         */
        public static final String BD09LL = "bd09ll";
        /**
         * GPS所采用的坐标系
         */
        public static final String WGS84 = "wgs84";

        private CoordTypeInput() {
        }
    }
}
