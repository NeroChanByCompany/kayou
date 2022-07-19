package com.nut.locationservice.common.constants;

/**
 * 位置云接口返回数据常量
 */

public class CloudConstants {

    /**
     * 邮箱默认油量
     */
    public static final String DEFAULT_OILCAPACITY = "200";
    /**
     * 车门状态
     */
    public static final String QIANMEN_OPEN = "前门开,";
    public static final String ZHONGMEN_OPEN = "中门开,";
    public static final String HOUMEN_OPEN = "后门开,";
    public static final String DRIVER_OPEN = "驾驶门开,";
    /**
     * ACC状态
     */
    public static final String ACC_OPEN = "ACC开,";
    public static final String ACC_CLOSE = "ACC关,";
    /**
     * 定位状态
     */
    public static final String GPS_TRUE = "定位";
    public static final String GPS_FALSE = "未定位";
    //无故障时显示无，不能为空
    public static final String NOTHING = "无";
    public static final String UNKNOW = "未知;";
    // 经纬度默认值（位置：默认"0.0;0.0"）
    public static final String LOCATION_DEFAULT= "0.0;0.0";
    //位置云查询类型
    public static final int PROVINCE = 1;
    public static final int CITY =2;

    /* ######## ######## ######## ########
     *  车辆在线状态
     * ######## ######## ######## ######## */
    /** 在线行驶 */
    public static final String VEHICLE_STATUS_ONLINE_TRAVEL = "1";
    /** 在线停车 */
    public static final String VEHICLE_STATUS_ONLINE_STOP = "2";
    /** 车辆离线 */
    public static final String VEHICLE_STATUS_OFFLINE_STOP = "3";
    /** 车辆断连 */
    public static final String VEHICLE_STATUS_DISCONNECTION = "4";

    /**
     *方向枚举
     */
    public enum Direction {
        NORTH("北"),
        SOUTH("南"),
        EAST("东"),
        WEST("西"),
        NORTHEAST("东北"),
        NORTHWEST("西北"),
        SOUTHEAST("东南"),
        SOUTHWEST("西南");

        private String value;
        public String getValue() {
            return value;
        }
        Direction(String value) {
            this.value = value;
        }
    }
    /**
     * 报警位
     */
    public enum alarm {
        emergencyAlarm("紧急报警"),
        speedingAlarm("超速报警"),
        tiredAlarm("疲劳驾驶"),
        comingAlarm("危险预警"),
        faultGNSS("GNSS模块发送故障"),
        noConnectGNSS("GNSS天线未接或被剪断"),
        shortCircuitGNSS("GNSS天线短路"),
        underPower("主电源欠压"),
        lossPower("主电源掉电"),
        faultLCD("终端LCD或显示器故障"),
        faultTTS("TTS模块故障"),
        faultCamera("摄像头故障"),
        icCardFailure("道路运输证IC卡模块故障"),
        speedingWarningAlarm("超速预警"),
        tiredWarningAlarm("疲劳驾驶预警"),
        drivingTimeout("当天累计驾驶超时"),
        parkingTimeout("超时停车"),
        inOutArea("进出区域"),
        inOutRoute("进出路线"),
        routeLackOrOverTime("路段行驶时间不足/过长"),
        routeDeviates("路线偏离报警");
        private String value;

        public String getValue() {
            return value;
        }
        alarm(String value) {
            this.value = value;
        }
    }
    /**
     * 报警提醒类型
     * 1:出区域限速 2:滞留超时3:一体机拆出报警4:车辆锁车报警5:ID不匹配报警
     */
    public enum AlarmRemindType {

        inOutAreaAlarm("出区域限速", 1),
        retainedTimeoutAlarm("滞留超时", 2),
        terminalRemoveAlarm("一体机拆出", 3),
        carLockAlarm("车辆锁车", 4),
        dNotMatchAlarm("ID不匹配", 5);

        private AlarmRemindType(String name, int value) {
            this.name = name;
            this.value = value;
        }

        private String name;
        private int value;

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        AlarmRemindType(String name) {
            this.name = name;
        }

        AlarmRemindType(int value) {
            this.value = value;
        }

    }
    //报警附加位6
    public enum additionalarm6 {
        slideNeutral("空挡滑行"),
        rapidAcceleration("急加速"),
        rapidDeceleration("急减速"),
        sharpTurning("急转弯"),
        lowOilState("低油量行驶"),
        slamTheAccelerator("猛踩油门"),
        brakePressureLow("制动气压低");
        private String value;

        public String getValue() {
            return value;
        }

        additionalarm6(String value) {
            this.value = value;
        }
    }

    //报警附加位7
    public enum additionalarm7 {
        longTimeBreaking("长时间刹车"),
        longTimeClutch("长时间离合"),
        fastlyFlameout("停车立即熄火"),
        engineColdStart("发动机冷启动"),
        badDrivingHabits("绿区外驾驶"),
        idNotMatch("ID不匹配"),
        terminalRemove("北斗终端拆出");
        private String value;

        public String getValue() {
            return value;
        }

        additionalarm7(String value) {
            this.value = value;
        }
    }

    //报警附加位8
    public enum additionalarm8 {
        airCondition("空调开"),
        handBrake("手刹开"),
        gpsActivation("GPS激活"),
        handshake("握手失败"),
        lockVehicle("锁车"),
        keyMatch("KEY匹配正确"),
        gpsIdMatch("GPSID匹配"),
        ecuAnticipation("ECU预判锁车");
        private String value;

        public String getValue() {
            return value;
        }

        additionalarm8(String value) {
            this.value = value;
        }
    }

    /**
     * 车辆行驶状态枚举
     * CarStatue
     * 在线行驶111(7)，在线停车011(3)，在线不定位01(1)，不在线行驶110(6),不在线停车010(2)，不在线不定位00(0)
     * 第一位表示在线状态（0：不在线，1：在线）
     * 第二位表示定位状态（0：不定位，1：定位）
     * 第三位表示行驶状态（0：停车，1：行驶）
     * @author wenya
     *
     */
    public enum CarState {

        VehicleStatusOnline("在线行驶", 7),
        VehicleStatusOnlineStop("在线停车", 3),
        VehicleStatusOnlineInvalid("在线不定位", 1),
        VehicleStatusOffline("不在线行驶", 6),
        VehicleStatusOfflineStop("不在线停车", 2),
        VehicleStatusOfflineInvalid("不在线不定位", 0);


        private CarState(String name, int value) {
            this.name = name;
            this.value = value;
        }

        private String name;
        private int value;

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        CarState(String name) {
            this.name = name;
        }

        CarState(int value) {
            this.value = value;
        }

    }

    public enum MilesRange
    {
        NO_CHOOSE("-1",""),
        CHOOSE_0("1", "[0,0]"),
        CHOOSE_0_3000("2","(0,3000]"),
        CHOOSE_3000_10000("3","(3000,10000]"),
        CHOOSE_10000_100000("4","(10000,100000]"),
        CHOOSE_ABOVE_100000("5","(100000,$)");

        private String type;
        private String value;

        MilesRange(String type, String value ) {
            this.type = type;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }
    }
}
