package com.nut.locationservice.common.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Constants {

    // 删除区分：0未删除
    public static final int IS_NOT_DEL = 0;

    // 删除区分：0已删除
    public static final int HAVE_DEL = 1;

    // token key
    public static final String TOKEN_KEY = "STOKENS_";

    // openapi token key
    public static final String OPENAPI_TOKEN_KEY = "OPENAPI_TOKEN_KEY_";

    // 验证码 key
    public static final String VERIFYCODE_KEY = "VERIFYCODE_";

    // 登陆失败key
    public static final String ERROR_LOGIN_KEY = "ERROR_LOGIN_KEY_";

    // _
    public static final String SYMBO_UNDERLINE = "_";

    // *
    public static final String SYMBO_NUMBERSIGN = "*";

    // 校验token开关key
    public static final String VALIDATE_TOKEN = "validate.token";

    // http 超时时间
    public static final String CONNECTION_REQUEST_TIMEOUT = "connection.request.timeout";

    // http 超时时间
    public static final String CONNECT_TIMEOUT = "connect.timeout";

    // http 超时时间
    public static final String READ_TIMEOUT = "read.timeout";

    // token校验接口
    // public static final String VALIDATE_TOKEN_URL = "validate.token.url";

    // 用户校验接口
    // public static final String VALIDATE_USER_URL = "validate.user.url";

    // 指令消息缓存key前缀
    public static final String MSG_KEY_PREFIX = "MSGPUSH_";

    /**
     * 锁定状态枚举
     */
    public enum LockStatusEnum {
        LOCK("1"), // 锁定
        UNLOCK("0");// 未锁定
        private String value;

        LockStatusEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * 定位状态枚举
     */
    public enum LocationStatusEnum {
        HAVE_LOCATION(1), // 已定位
        HAVE_NOT_LOCATION(0), // 未定位
        ALL(-1);// 所有
        private int value;

        LocationStatusEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

    }

    public static final int loginstatus_index = 0; // 登录缓存

    public static final int car_index = 1; // 车辆分组

    public static final int car_plate = 2;

    public static final int business_index = 3; // 车组

    public static final int business_car_index = 4;

    public static final int terminal_index = 5;// 终端

    public static final int terminal_car_index = 6;//

    public static final int basicdata_index = 7;

    public static final int gps_index = 8;

    public static final int car_terminal_index = 9;// 通过车id找终端

    public static final int car_employee_index = 10;// 车辆找司机缓存

    public static final int employee_index = 11;

    public static final int code_phone = 12;

    public static final int traffic_rule = 13;

    public static final int gps_app = 14;

    public static final int avg_oil_index = 15;

    /**
     * 通用应答结果集 ResponseResult
     *
     * @author wenya
     */
    public enum ResponseResult {

        // success("成功", 0),
        // failure("失败", 1),
        // 与sl版本一致,暂时只处理成功，失败情况
        failure("失败", 0), success("成功", 1), messageerror("消息有误", 2), notsupport("终端不支持", 3), alarmhandle("报警处理确认", 4), offline(
                "终端离线", 5), noRegister("终端未注册", 6),
        // ecuSuccess("ECU成功",7),
        // ecuFailure("ECU失败",8),
        // ecuNotSupport("ECU不支持",9),
        typeError("操作类型无效", 10), limitError("限制类型无效", 11), vehicleClose("车辆关机", 12), communicateError("总线通信异常", 13), waiting(
                "等待未响应", -3), executing("正在执行", -2), outdate("已失效", -1), unlimited("不限", -4);

        private ResponseResult(String name, int value) {
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

        ResponseResult(String name) {
            this.name = name;
        }

        ResponseResult(int value) {
            this.value = value;
        }

    }

    // ecu异常码
    public enum ECUErrorCode {
        normal("正常", 0), paramlost("MPU下发的命令参数结构缺失", 1), synchro("当前处于等待同步状态", 2), cachefull("命令缓存已满", 3), queryouttime(
                "查询超时", 4), handshakefailure("重新握手当前执行命令返回失败", 5), outmaxcount("底层重发次数超过最大次数", 6), waitlock("进入等待锁车结果状态", 7), waitcanlelock(
                "进入等待解锁结果状态", 8), handshakecheck("握手校验失败", 9), prelock("当前处于软锁车状态", 10), cancletamper("当前处于防拆关闭状态", 11), ecufailure(
                "ECU返回结果为失败", 12), gpsidzero("MPU下发参数中密钥和GPS ID全为0", 13), unknown("未知", 225);

        private ECUErrorCode(String name, int value) {
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

        ECUErrorCode(String name) {
            this.name = name;
        }

        ECUErrorCode(int value) {
            this.value = value;
        }
    }

    /**
     * 报警提醒类型
     *
     * @author wenya (1:出区域限速 2:滞留超时3:一体机拆出报警4:车辆锁车报警5:ID不匹配报警)
     */
    public enum AlarmRemindType {

        inOutAreaAlarm("出区域限速", 1), retainedTimeoutAlarm("滞留超时", 2), terminalRemoveAlarm("一体机拆出", 3), carLockAlarm(
                "车辆锁车", 4), dNotMatchAlarm("ID不匹配", 5);

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

    /**
     * 车辆行驶状态枚举 CarStatue 在线行驶111(7)，在线停车011(3)，在线不定位01(1)，不在线行驶110(6),不在线停车010(2)，不在线不定位00(0) 第一位表示在线状态（0：不在线，1：在线）
     * 第二位表示定位状态（0：不定位，1：定位） 第三位表示行驶状态（0：停车，1：行驶）
     *
     * @author wenya
     */
    public enum CarState {

        VehicleStatusOnline("在线行驶", 7), VehicleStatusOnlineStop("在线停车", 3), VehicleStatusOnlineInvalid("在线不定位", 1), VehicleStatusOffline(
                "不在线行驶", 6), VehicleStatusOfflineStop("不在线停车", 2), VehicleStatusOfflineInvalid("不在线不定位", 0);

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

    // 方向枚举
    public enum Direction {
        NORTH("北"), SOUTH("南"), EAST("东"), WEST("西"), NORTHEAST("东北"), NORTHWEST("西北"), SOUTHEAST("东南"), SOUTHWEST("西南");

        private String value;

        public String getValue() {
            return value;
        }

        Direction(String value) {
            this.value = value;
        }
    }

    // 系统日志操作类型
    public enum HandleType {
        add("增加", 1), update("修改", 2), delete("删除", 3), query("查询", 4);

        private HandleType(String name, int value) {
            this.name = name;
            this.value = value;
        }

        private final String name;

        private final int value;

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }

    // webservice应答结果
    public enum WebserviceResponseResult {
        cloudfalse("操作失败", 0), usererror("用户名密码错误", 1), softwareerror("程序异常", 2), parameerror("参数值错误", 3), sqlerror(
                "数据库访问失败", 4), outtime("访问超时", 5), notsupport("接口方法暂不支持", 6), dataexist("数据已经存在", 7), unknowerror("未知错误", 8), savefail(
                "保存失败", 9), nodata("数据不存在", 10), deletefail("删除失败", 11), carcphcolorexist("车牌颜色已经存在", 12), terminalsimexist(
                "终端id号已经存在", 13), carnoexist("车辆不存在", 14), terminalnoexist("终端不存在", 15), carteamhaschildrencannotdel(
                "车组下有子节点不允许删除", 16), rootcarteamcannotdel("不允许删除根节点", 17), rootcarteamcannotsaveorupdate("不允许新增或更改根节点", 18), terminaltcodefalse(
                "角色绑定了用户", 19), clientisbound("不允许删除和修改被绑定的客户", 20), terminalhasusers("终端已被绑定", 21);

        private WebserviceResponseResult(String name, int value) {
            this.name = name;
            this.value = value;
        }

        private final String name;

        private final int value;

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

    }

    // 页面返回结果
    public enum ResultJsp {
        NOSIM("没有绑定终端，发送失败"), NOTERMINAL("没有绑定终端，无数据"), SEND("已发送"), PASSWORDERROR("密码错误"), OFFLINE("车辆不在线，系统将缓存下发");

        private String value;

        public String getValue() {
            return value;
        }

        ResultJsp(String value) {
            this.value = value;
        }
    }

    // 报表
    public static final String POSITION = "定位";

    public static final String NPOSITION = "不定位";

    public static final String NORMAL = "正常";

    public static final String NONORMAL = "异常";

    // 服务，配件（以后要改）
    public static final String SERVICE = "服务1，服务2，服务3，服务4，服务5，服务6";

    public static final String PARTS = "驾驶室专区配件，保养专区配件，发动机配件，底盘专区配件";

    // 日志
    public static final String OUTREGINALARMRULE = "禁出";

    public static final String OUTREGINCONTENT = "设置监控区域限速操作";

    public static final String OUTREGINCONTENTDEL = "取消监控区域限速操作";

    public static final String passworderror = "旧密码与原密码不符";

    public static final String SUCCESS = "成功";

    public static final String FAILURE = "失败";

    public static final String TTOP = "终端-平台";

    public static final String PTT = "平台-终端";

    public static final String TAMPERON = "8F40（激活）车辆控制";

    public static final String TAMPEROFF = "8F40（关闭）车辆控制";

    public static final String TAMPERCONTENTON = "8F40（激活）车辆控制操作";

    public static final String TAMPERCONTENTOFF = "8F40（关闭）车辆控制操作";

    public static final String LOCKCARN = "8F40（解锁）车辆控制";

    public static final String LOCKCARON = "8F40（解锁）车辆控制操作";

    public static final String LOCKCARF = "8F40（锁车）车辆控制";

    public static final String LOCKCAROFF = "8F40（锁车）车辆控制操作";

    public static final String SPEECH = "语音监控";

    public static final String LISTENING = "语音监控单向监听操作";

    public static final String CALLING = "语音监控普通通话操作";

    public static final String MESSAGE = "8300文本信息下发";

    public static final String MESSAGECONTENT = "8300文本信息下发操作";

    public static final String LOGIN = "用户登录";

    public static final String LOGINCONTENT = "用户登录操作";

    public static final String LOGOUT = "用户注销";

    public static final String LOGOUTCONTENT = "用户注销操作";

    // 系统日志
    public static final String UPDATEPASSWORD = "修改密码";

    public static final String UPDATEPASSWORDCONTENT = "修改密码操作";

    public static final String ADDSTATION = "新建服务站";

    public static final String ADDSTATIONCONTENT = "新建服务站操作";

    public static final String UPDATESTATION = "修改服务站";

    public static final String UPDATESTATIONCONTENT = "修改服务站操作";

    public static final String DELSTATION = "删除服务站";

    public static final String DELSTATIONCONTENT = "删除服务站操作";

    public static final String ADDCLIENT = "新建客户";

    public static final String ADDCLIENTCONTENT = "新建客户操作";

    public static final String UPDATECLIENT = "修改客户";

    public static final String UPDATECLIENTCONTENT = "修改客户操作";

    public static final String DELCLIENT = "删除客户";

    public static final String DELCLIENTCONTENT = "删除客户操作";

    public static final String ADDDEALER = "新建经销商";

    public static final String ADDDEALERCONTENT = "新建经销商操作";

    public static final String UPDATEDEALER = "修改经销商";

    public static final String UPDATEDEALERCONTENT = "修改经销商操作";

    public static final String DELDEALER = "删除经销商";

    public static final String DELDEALERCONTENT = "删除经销商操作";

    public static final String ADDDISTRICT = "新建区域";

    public static final String ADDDISTRICTCONTENT = "新建区域操作";

    public static final String UPDATEDISTRICT = "修改区域";

    public static final String UPDATEDISTRICTCONTENT = "修改区域操作";

    public static final String DELDISTRICT = "删除区域";

    public static final String DELDISTRICTCONTENT = "删除区域操作";

    public static final String ADDROLE = "新建角色";

    public static final String ADDROLECONTENT = "新建角色操作";

    public static final String UPDATEROLE = "修改角色";

    public static final String UPDATEROLECONTENT = "修改角色操作";

    public static final String DELROLE = "删除角色";

    public static final String DELROLECONTENT = "删除角色操作";

    public static final String ADDTERMINAL = "新建终端";

    public static final String ADDTERMINALCONTENT = "新建终端操作";

    public static final String UPDATETERMINAL = "修改终端";

    public static final String UPDATETERMINALCONTENT = "修改终端操作";

    public static final String DELTERMINAL = "删除终端";

    public static final String DELTERMINALCONTENT = "删除终端操作";

    public static final String ADDUSER = "新建用户";

    public static final String ADDUSERCONTENT = "新建用户操作";

    public static final String UPDATEUSER = "修改用户";

    public static final String UPDATEUSERCONTENT = "修改用户操作";

    public static final String DELUSER = "删除用户";

    public static final String DELUSERCONTENT = "删除用户操作";

    public static final String ADDVEHICLE = "新建车辆";

    public static final String ADDVEHICLECONTENT = "新建车辆操作";

    public static final String UPDATEVEHICLE = "修改车辆";

    public static final String UPDATEVEHICLECONTENT = "修改车辆操作";

    public static final String DELVEHICLE = "删除车辆";

    public static final String DELVEHICLECONTENT = "删除车辆操作";

    // 状态位
    public static final String ACC_OPEN = "ACC开,";

    public static final String ACC_CLOSE = "ACC关,";

    public static final String QIANMEN_OPEN = "前门开,";

    public static final String QIANMEN_CLOSE = "前门关,";

    public static final String ZHONGMEN_OPEN = "中门开,";

    public static final String ZHONGMEN_CLOSE = "中门关,";

    public static final String HOUMEN_OPEN = "后门开,";

    public static final String HOUMEN_CLOSE = "后门关,";

    public static final String DRIVER_OPEN = "驾驶门开,";

    public static final String DRIVER_CLOSE = "驾驶门关,";

    public static final String OFFLINE = "离线";

    public static final String ONLINE = "在线";

    // 报警位
    public enum alarm {
        emergencyAlarm("紧急报警"), speedingAlarm("超速报警"), tiredAlarm("疲劳驾驶"), comingAlarm("危险预警"), faultGNSS("GNSS模块发送故障"), noConnectGNSS(
                "GNSS天线未接或被剪断"), shortCircuitGNSS("GNSS天线短路"), underPower("主电源欠压"), lossPower("主电源掉电"), faultLCD(
                "终端LCD或显示器故障"), faultTTS("TTS模块故障"), faultCamera("摄像头故障"), icCardFailure("道路运输证IC卡模块故障"), speedingWarningAlarm(
                "超速预警"), tiredWarningAlarm("疲劳驾驶预警"), drivingTimeout("当天累计驾驶超时"), parkingTimeout("超时停车"), inOutArea("进出区域"), inOutRoute(
                "进出路线"), routeLackOrOverTime("路段行驶时间不足/过长"), routeDeviates("路线偏离报警");
        private String value;

        public String getValue() {
            return value;
        }

        alarm(String value) {
            this.value = value;
        }
    }

    // 报警附加位6
    public enum additionalarm6 {
        slideNeutral("空挡滑行"), rapidAcceleration("急加速"), rapidDeceleration("急减速"), sharpTurning("急转弯"), lowOilState(
                "低油量行驶"), slamTheAccelerator("猛踩油门"), brakePressureLow("制动气压低");
        private String value;

        public String getValue() {
            return value;
        }

        additionalarm6(String value) {
            this.value = value;
        }
    }

    // 报警附加位7
    public enum additionalarm7 {
        longTimeBreaking("长时间刹车"), longTimeClutch("长时间离合"), fastlyFlameout("停车立即熄火"), engineColdStart("发动机冷启动"), badDrivingHabits(
                "绿区外驾驶"), idNotMatch("ID不匹配"), terminalRemove("北斗终端拆出");
        private String value;

        public String getValue() {
            return value;
        }

        additionalarm7(String value) {
            this.value = value;
        }
    }

    // 报警附加位8
    public enum additionalarm8 {
        airCondition("空调开"), handBrake("手刹开"), gpsActivation("GPS激活"), handshake("握手失败"), lockVehicle("锁车"), keyMatch(
                "KEY匹配正确"), gpsIdMatch("GPSID匹配"), ecuAnticipation("ECU预判锁车");
        private String value;

        public String getValue() {
            return value;
        }

        additionalarm8(String value) {
            this.value = value;
        }
    }

    public static final String OUTREGINREMIND = "系统判断您的行为存在一定安全风险，您的车辆因此将自动限制转速，请与您的经销商联系进行解锁！";

    public static Long Identify = 0L;

    public static final String WAITING = "等待未响应";

    public static final String NULL = "无";

    public static final String LOGIN_MONITOR = "8001";

    public static final String LOGOUT_MONITOR = "8002";

    public static final String LINK_HEART_MONITOR = "8004";

    public static final String LINK_HEART_GPS = "9005";

    public static final String LOGIN_GPS = "9001";

    public static final String LOGOUT_GPS = "9002";

    public static final String VEHICLE_LOCATIONG_SUBSCRIBE = "9009";

    public static final String VEHICLE_LOCATIONG_UNSUBSCRIBE = "9010";

    public static final String LASTEST_LOCATION_REQ = "9111";

    public static final String LASTEST_GPSDATA = "9113";

    public static final String CLIENT_COMMON_RES = "9900";

    public static final String UP_LOGIN_RES = "1103";

    public static final String UP_COMMON_RES = "1100";

    public static final String HEART_BEAT = "1099";

    public static final String DATASUBSCRIBE = "0201";

    public static final String BATCHLOCATIONQUERY = "0203";

    public static final String SERVER_LOGIN_RES_TOCLIENT = "9901";

    public static final String SERVER_COMMON_RES_TOCLIENT = "9000";

    public static final String LINK_HEARTBEAT_TOMONITOR = "9006";

    public static final String LASTEST_LOCATION_RES = "9112";

    public static final String LASTEST_GPS_DATA_RES = "9114";

    public static final String AEROCROSS_GPS_DATA_RES = "9116"; // 区域查车响应

    public static final String SERVER_COMMON_RES_TOMONITOR = "9019";

    // 位置云与GPS中心通信类型01
    public static final String CLOUD_MESSAGE_TYPE = "01";

    // GPS中心与终端通信类型02
    public static final String GPS_MESSAGE_TYPE = "02";

    // 数据中心与监控中心通信类型05
    public static final String MONITOR_MESSAGE_TYPE = "05";

    // 监控中心与客户端通信类型06
    public static final String CLIENT_MESSAGE_TYPE = "06";

    // 数据中心到809
    public static final String ACCESS809_MESSAGE_TYPE = "07";

    // 登录位置云
    public static final String UP_LOGIN = "0103";

    // 请求平台鉴权标识
    public static final String REQUEST_LOGIN_KEY = "1101";

    // 发送云平台的心跳
    public static final String LINK_HEART = "1099";

    // 多服务鉴权请求（发送至云端）
    public static final String MULTI_SERVER_AUTH_CLOUD = "0109";

    // 订阅请求
    public static final String SUBSCRIBE_REQUEST = "0200";

    // 重连
    public static final String RECONNECT = "0105";

    // 注销
    public static final String Cloud_LOGOUT = "0106";

    // 接收前端的心跳
    public static final String LINK_HEART_CLIENT = "8004";

    // 接收前端的心跳
    public static final String LINK_HEART_CLOUD = "1009";

    // 回复前端的通用应答
    public static final String COMMON_RESPONSE_CLIENT = "8000";

    // 回复GPS端的通用应答
    public static final String COMMON_RESPONSE_GPS = "9000";

    // 发送多服务鉴权标示（至数据处理层）
    public static final String MULTI_SERVER_AUTH_RES = "9007";

    // 发送订阅应答返回的RP信息（至数据处理层）
    public static final String SUBSCRIBE_REQUEST_RES = "9008";

    // app提醒超时时间
    public static long timeOutSendApp = 0l;

    /**
     * 是否重置redis失效时间枚举
     */
    public enum RedisLiveTimeReSetEnum {
        NO_RESET("0", "不重置"), // 不重置
        RESET("1", "重置"); // 重置
        private String code;

        private String value;

        RedisLiveTimeReSetEnum(String code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * 防控状态枚举
     */
    public enum PreventionStatusEnum {
        NOT_ACTIVE(0, "未激活"), ACTIVE(2, "已激活"), LOCKED(3, "已锁车"), BEING_ACTIVE(4, "正在激活"), BEING_CANCEL_ACTIVE(5,
                "正在取消激活"), BEING_LOCK(6, "正在锁车"), BEING_UNLOCK(7, "正在解锁"), UNKNOWN(10, "");

        private int code;

        private String msg;

        private static Map<Integer, PreventionStatusEnum> codes = new ConcurrentHashMap<>();

        static {
            for (PreventionStatusEnum statusEnum : PreventionStatusEnum.values()) {
                codes.put(statusEnum.getCode(), statusEnum);
            }
        }

        PreventionStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public static PreventionStatusEnum valueOf(int code) {
            if (codes.get(code) != null) {
                return codes.get(code);
            } else {
                return UNKNOWN;
            }
        }
    }

    /**
     * 确认是否登陆踢掉之前登陆者枚举
     */
    public enum ConfirmLoginFlgEnum {
        IS_LOGIN("0", "提示是否登陆"), // 提示是否登陆
        CONFIRM_LOGIN("1", "确认登陆"); // 确认登陆
        private String code;

        private String value;

        ConfirmLoginFlgEnum(String code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * 区域类型枚举
     */
    public enum AreaTypeEnum {
        STATION(11), // 一级服务站
        SUBSTATION(12), // 二级服务站
        TEAM(21), // 一级经销商
        SUB_TEAM(22);// 二级经销

        private Integer value;

        AreaTypeEnum(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    // 不替换单引号
    public static final String NO_REPLACE_SINGLE_QUOTATION = "NO_REPLACE_SINGLE_QUOTATION_";

    /**
     * 同步模式枚举
     */
    public enum SyncModelEnum {
        ADD(0),
        UPDATE(1),
        DELETE(2);
        private int value;

        SyncModelEnum(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    /**
     * 同步操作结果枚举
     */
    public enum SyncOperateEnum {
        SUCCESS(0),
        ADD(1),
        DELETE(2),
        SWITCH_OFF(3),
        SWITCH_ON(4),
        UPDATE(5),//只更新二级网点(新增或更新)
        UPDATE_AND_DELETE(6);//更新且删除二级网点
        private int value;

        SyncOperateEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }
}
