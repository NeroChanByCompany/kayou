package com.jac.app.job.constant;

/**
 * @Author: jiangcm
 * @Description: 静态方法 变量类
 * @Date: Created in 2017/7/6 9:09
 * @Modified By:
 */
public class IxinPushStaticLocarVal {
    /**
     * 消息大分类：type（1：通知、2：公告、3：版本、4：故障、5：车辆动态、6：报警、7：保养、8：保险、：9：违章
     * 10 ：服务、11：短信
     */
    public static final String PUSH_TYPE_NOTICE = "1"; // 通知
    public static final String PUSH_TYPE_FAULT = "4"; // 故障
    public static final String PUSH_TYPE_ALARM = "6"; // 报警
    public static final String PUSH_TYPE_CAR_STATUS = "5"; // 车辆状态
    public static final String PUSH_TYPE_MAINTAIN = "7"; // 保养
    public static final String PUSH_TYPE_10 = "10"; // 服务
    public static final String PUSH_TYPE_SMS = "11"; // 短信

    /**
     * 消息小分类 stype
     * 【通知：1：车主资质通过、2：车牌号码变更、3：车队名称变更、4：管理员取消司机、5：邀请司机、6：邀请管理员、7：管理员分配线路、8：管理员取消线路】
     * 【公告、1：公告】
     * 【版本、1：版本】
     * 【故障、1：故障通知】
     * 【车辆动态、1：车况、2：关键点出入】
     * 【报警、1：油量液位报警、2：超速报警、3：胎压报警、4：不良驾驶行为推送、5：油量液位异常减少】
     * 【保养、1：推荐保养】
     * 【保险、1:保险到期】
     * 【违章、1：违章信息】
     * 【服务、1：待接受工单提醒 司机预约、2：待接受工单提醒 400客服分派、3：拒单审核提醒 审核通过、4：拒单审核提醒 审核不通过、5：关闭申请审核提醒 审核通过、
     * 6：关闭申请审核提醒 审核不通过、7：修改申请审核提醒 客服修改、8：修改申请审核提醒 驳回、9：司机评价通知、10：催单消息、11：接受工单、12：取消预约维保、
     * 13：到站提示、14：服务评价、15：等待服务车、16：救援出发、17：待接受工单提醒 400客服分派（推送给司机车队）】
     */

    /**
     * 【1：通知】
     */
    public static final String PUSH_TYPE_NOTICE_STYPE_CAR_ADD = "1"; // 1通知：车主资质通过
    public static final String PUSH_TYPE_NOTICE_STYPE_CAR_ADD_FAIL = "2"; // 1通知：车主资质不通过
    public static final String PUSH_TYPE_NOTICE_STYPE_CAR_NUM_UPDATE = "3"; // 1通知：车牌号码变更
    public static final String PUSH_TYPE_NOTICE_STYPE_TEAM_NAME_UPDATE = "4"; // 1通知：车队名称变更
    public static final String PUSH_TYPE_NOTICE_STYPE_REMOVED_FROM_FLEET = "5"; // 1通知：管理员或司机移除车队
    public static final String PUSH_TYPE_NOTICE_STYPE_DRIVER_ADD = "6"; // 1通知：邀请司机
    public static final String PUSH_TYPE_NOTICE_STYPE_ADMIN_ADD = "7"; // 1通知：邀请管理员
    public static final String PUSH_TYPE_NOTICE_STYPE_ROUTE_ADD = "8"; // 1通知：管理员分配线路
    public static final String PUSH_TYPE_NOTICE_STYPE_ROUTE_CANCEL = "9"; // 1通知：管理员取消线路
    public static final String PUSH_TYPE_NOTICE_STYPE_ONLINE_CONSULTATION = "10"; // 1通知：在线咨询回复消息提醒
    public static final String PUSH_TYPE_NOTICE_STYPE_CAR_DRIVER_BIND = "11"; // 1通知：车辆司机绑定
    public static final String PUSH_TYPE_NOTICE_STYPE_CAR_DRIVER_UNBIND = "12"; // 1通知：车辆司机解绑
    public static final String PUSH_TYPE_NOTICE_STYPE_QUIT_FLEET = "13"; // 1通知：退出车队
    public static final String PUSH_TYPE_NOTICE_STYPE_FLEET_DELETE = "14"; // 1通知：解散车队
    public static final String PUSH_TYPE_NOTICE_STYPE_FLEET_HANDOVER = "15"; // 1通知：车队权限转移
    public static final String PUSH_TYPE_NOTICE_STYPE_CREADIT_CONSUME = "16"; // 1通知：积分兑换成功
    public static final String PUSH_TYPE_NOTICE_STYPE_SCORE_FROZEN = "17"; // 1通知：冻结积分
    public static final String PUSH_TYPE_NOTICE_STYPE_CONFIRM_DELIVERY = "18"; // 1通知：确认交车单
    public static final String PUSH_TYPE_NOTICE_STYPE_DELIVERY_UPDATE = "19"; // 1通知：交车单修改
    public static final String PUSH_TYPE_NOTICE_STYPE_GIVE_COUPON = "20"; // 1通知：获得优惠券
    public static final String PUSH_TYPE_NOTICE_STYPE_IMPROVE_INFORMATION= "21"; // 1通知：完善信息通过
    public static final String PUSH_TYPE_NOTICE_STYPE_CHECK_REPORT= "30"; // 1通知：查看燃油报告
    public static final String PUSH_TYPE_NOTICE_STYPE_STATIS_EVAL= "22"; // 1通知：问卷调查
    public static final String PUSH_TYPE_NOTICE_STYPE_SHOP_LOGISTICS= "35"; // 1通知：问卷调查
    public static final String PUSH_TYPE_NOTICE_STYPE_MAINTENANCE= "36"; // 1通知：推荐保养

    /**
     * 【4：故障】
     */
    public static final String PUSH_TYPE_FAULT_STYPE_ONE = "1"; // 4通知：故障通知

    /**
     * 【5：车辆动态】
     */
    public static final String PUSH_TYPE_CAR_STATUS_STYPE_STATUS = "1"; // 5车辆动态：车况
    public static final String PUSH_TYPE_CAR_STATUS_STYPE_INOUT = "2"; // 5车辆动态：关键点出入

    /**
     * 【6：报警】
     */
    public static final String PUSH_TYPE_ALARM_STYPE_OIL = "1"; // 6报警：油量液位报警
    public static final String PUSH_TYPE_ALARM_STYPE_SPEED = "2"; // 6报警：超速报警
    public static final String PUSH_TYPE_TIRE_TEMPERATURE = "3"; // 6报警：胎压报警
    public static final String PUSH_TYPE_BAD_DRIVING = "4"; // 6报警：不良驾驶行为推送
    public static final String PUSH_TYPE_OIL = "5"; // 6报警：油量液位异常减少

    /**
     * 【7：保养】
     */
    public static final String PUSH_TYPE_MAINTAIN_STYPE_MAINTAIN = "1"; // 7保养：推荐保养
    public static final String PUSH_TYPE_MAINTAIN_STYPE_CUSTOM_MILEAGE_VOICE_MAINTAIN = "2"; // 7保养：语音里程保养
    public static final String PUSH_TYPE_MAINTAIN_STYPE_CUSTOM_TIME_VOICE_MAINTAIN = "3"; // 7保养：语音时间保养
    public static final String PUSH_TYPE_MAINTAIN_STYPE_CUSTOM_TIME_MAINTAIN = "4"; // 7保养：自定义时间保养
    public static final String PUSH_TYPE_MAINTAIN_STYPE_CUSTOM_MILEAGE_MAINTAIN = "5"; // 7保养：自定义里程保养
    @Deprecated
    public static final String PUSH_TYPE_MAINTAIN_STYPE_CUSTOM_MILEAGE_MAINTAIN_NEW = "6"; // 7保养：新推荐保养
    public static final String PUSH_TYPE_MAINTAIN_STYPE_CUSTOM_OIL_CHANGE = "7"; // 7保养：机油更换推荐保养
    public static final String PUSH_TYPE_MAINTAIN_STYPE_CUSTOM_AIR_TIGHTNESS = "8"; // 7保养：气密性检查
    public static final String PUSH_TYPE_MAINTAIN_STYPE_CUSTOM_UREA_LEAKAGE = "9"; // 7保养：后处理（泄露）
    public static final String PUSH_TYPE_MAINTAIN_STYPE_CUSTOM_UREA_BLOCK = "10"; // 7保养：后处理（阻塞）
    public static final String PUSH_TYPE_MAINTAIN_STYPE_CUSTOM_CLUTCH = "11"; // 7保养：离合器检查
    public static final String PUSH_TYPE_MAINTAIN_STYPE_CUSTOM_AMT_YELLOW = "12"; // 7保养：AMT检查黄色警报
    public static final String PUSH_TYPE_MAINTAIN_STYPE_CUSTOM_AMT_RED = "13"; // 7保养：AMT检查红色警报
    public static final String PUSH_TYPE_MAINTAIN_STYPE_MILEAGE_MAINTAIN = "14"; // 7保养：推荐保养-按里程
    public static final String PUSH_TYPE_MAINTAIN_STYPE_TIME_MAINTAIN = "15"; // 7保养：推荐保养-按时间

    /**new 保养里程提醒*/
    public static final String PUSH_TYPE_MAINTAIN_STYPE_ADVANCE_MILEAGE_MAINTAIN = "20"; // 7保养：预提醒保养-按里程
    public static final String PUSH_TYPE_MAINTAIN_STYPE_ADVANCE_TIME_MAINTAIN = "21"; // 7保养：预提醒保养-按时间
    public static final String PUSH_TYPE_MAINTAIN_STYPE_OVERDUE_MILEAGE_MAINTAIN = "22"; // 7保养：提醒未保养-按里程
    public static final String PUSH_TYPE_MAINTAIN_STYPE_OVERDUE_TIME_MAINTAIN = "23"; // 7保养：提醒未保养-按时间
    /**
     * 【10 ：服务】
     */
    public static final String PUSH_TYPE_TEN_STYPE_ONE = "1"; // 10服务：待接受工单提醒 司机预约
    public static final String PUSH_TYPE_TEN_STYPE_TWO = "2"; // 10服务：待接受工单提醒 400客服分派（推送给服务站）
    public static final String PUSH_TYPE_TEN_STYPE_THREE = "3"; // 10服务：拒单审核提醒 审核通过
    public static final String PUSH_TYPE_TEN_STYPE_FOUR = "4"; // 10服务：拒单审核提醒 审核不通过
    public static final String PUSH_TYPE_TEN_STYPE_FIVE = "5"; // 10服务：关闭申请审核提醒 审核通过
    public static final String PUSH_TYPE_TEN_STYPE_SIX = "6"; // 10服务：关闭申请审核提醒 审核不通过
    public static final String PUSH_TYPE_TEN_STYPE_SEVEN = "7"; // 10服务：修改申请审核提醒 客服修改
    public static final String PUSH_TYPE_TEN_STYPE_EIGHT = "8"; // 10服务：修改申请审核提醒 驳回
    public static final String PUSH_TYPE_TEN_STYPE_NINE = "9"; // 10服务：司机评价通知 功能：所有司机APP的评价，通过消息提醒被评价工单的所属服务APP账号
    public static final String PUSH_TYPE_TEN_STYPE_TEN = "10"; // 10服务：催单消息 功能：司机+车队App用户点击催单
    public static final String PUSH_TYPE_TEN_STYPE_ACCEPT_IN = "11"; // 10服务：接受工单 功能：服务APP接受工单 进出站
    public static final String PUSH_TYPE_TEN_STYPE_CLOSE = "12"; // 10服务：取消预约维保 功能：预约被取消
    public static final String PUSH_TYPE_TEN_STYPE_RECEIVE_IN = "13"; // 10服务：到站提示 功能：扫码接车
    public static final String PUSH_TYPE_TEN_STYPE_EVALUATE_IN = "14"; // 10服务：服务评价 功能：结束维修
    public static final String PUSH_TYPE_TEN_STYPE_ACCEPT_OUT = "15"; // 10服务：等待服务车 功能：服务站确认接单 外出救援
    public static final String PUSH_TYPE_TEN_STYPE_RESCUE_TAKE_OFF = "16"; // 10服务：救援出发 功能：确认出发
    public static final String PUSH_TYPE_TEN_STYPE_SEVENTEEN = "17"; // 10服务：待接受工单提醒 400客服分派（推送给司机车队）
    public static final String PUSH_TYPE_TEN_STYPE_EIGHTEEN = "18"; // 10服务：车辆进站预警提醒
    public static final String PUSH_TYPE_TEN_STYPE_RECEIVE_OUT = "19"; // 10服务：救援到达 功能：扫码接车
    public static final String PUSH_TYPE_TEN_STYPE_START_REPAIR_IN = "20"; // 10服务：车辆维修中 功能：检查结束
    public static final String PUSH_TYPE_TEN_STYPE_START_REPAIR_OUT = "21"; // 10服务：救援维修中 功能：检查结束
    public static final String PUSH_TYPE_TEN_STYPE_EVALUATE_OUT = "22"; // 10服务：服务评价 功能：结束维修
    public static final String PUSH_TYPE_TEN_STYPE_CHECK_WAIT = "23"; // 10服务：工单待审批 功能：司机提交工单至车管审核
    public static final String PUSH_TYPE_TEN_STYPE_CHECK_AGREE = "24"; // 10服务：审核通过 功能：司机提交的工单审核通过
    public static final String PUSH_TYPE_TEN_STYPE_CHECK_REFUSE = "25"; // 10服务：审核不通过 功能：司机提交的工单审核不通过

    /**
     * 【11 ：短信】
     */
    public static final String PUSH_TYPE_SMS_STYPE_ONE = "1"; // 11短信：车队名称变更
    public static final String PUSH_TYPE_SMS_STYPE_TWO = "2"; // 11短信：邀请管理员
    public static final String PUSH_TYPE_SMS_STYPE_ADD_DRIVER = "3"; // 11短信：邀请司机

    /**
     * 推送标题
     */
    public static final String PUSH_TITLE_OIL = "油量液位异常推送";
    public static final String PUSH_TITLE_SPEED = "超速报警";
    public static final String PUSH_TITLE_KEYPOINT = "关键点出入";
    public static final String PUSH_TITLE_CARSTATUS = "车况";
    public static final String PUSH_TITLE_TIRE = "胎压异常报警";
    public static final String PUSH_TITLE_BAD_DRIVING = "不良驾驶行为推送";

    /**
     * 爱心推 app - 前端跳转code
     */
    public static final String MESSAGE_CODE_DRIVER_CHANGE = "1"; //我的车辆页面:取消司机身份、邀请司机推送消息，跳转到“我的车辆列表”
    public static final String MESSAGE_CODE_CAR_LIST = "6"; //我的车辆页面:添加车辆推送消息，点击后跳转到“我的车辆列表”
    public static final String MESSAGE_CODE_CAR_OWNER = "21"; //车辆详情页面：车辆号牌变更、车队更改名称、分配线路、取消线路推送消息，跳转到“车辆详情”
    public static final String MESSAGE_CODE_CAR_DETAILS = "3";//车辆详情页面：分配线路、取消线路推送消息，跳转到“车辆详情”
    public static final String MESSAGE_CODE_ROAD_LINE = "4";//线路提醒：跳转到“服务站列表”
    public static final String MESSAGE_CODE_FAULT = "5";//一键体检详情：故障通知推送消息，跳转到“故障检测详情页”
    public static final String MESSAGE_CODE_YUN_YING = "7";//运营消息 不做跳转页面
    public static final String MESSAGE_CODE_MAINTAIN = "9";//自定义保养详情：自定义保养推送消息，跳转到“自定义保养详情”
    public static final String MESSAGE_CODE_WO_INFO = "15";// 服务app：工单详情页面
    public static final String MESSAGE_CODE_ORDER_INFO = "16";// 司机车队app：预约详情页
    public static final String MESSAGE_CODE_CAR_CREATED = "36";//行程详情页：不良驾驶行为推送消息，跳转到“行程详情”
    public static final String MESSAGE_CODE_APP_NOT = "37";//车况、超速报警、关键点出入，都是一类：车辆运行状况，messageCode 37，点击无跳转
    public static final String MESSAGE_CODE_APP_UPGRADE = "38";//推荐保养：车辆保养提醒推送消息，跳转
    public static final String MESSAGE_CODE_APP_TEMPERATURE = "39";// 胎压异常提醒 待确认
    public static final String MESSAGE_CODE_APP_UNUSUALLY_LITTLEOI = "40";//异常少油 messageCode 40，点击无跳转
    public static final String MESSAGE_CODE_STATION_WARN = "2";// 服务app：站内预警页面 注意：语音播报特定code，不需语音播报的推送不能用此code
    public static final String MESSAGE_CODE_APP_AIR_TIGHTNESS = "41";// 司机、车队APP：气密性检查异常推送，跳转到预见性维修项列表页
    public static final String MESSAGE_CODE_APP_ONLINE_CONSULTATION = "42";// 司机、车队APP：在线咨询回复后推送，跳转到咨询详情页
    public static final String MESSAGE_CODE_FLEET_DETAIL = "43"; // 跳转到新车队模型-车队详情页面
    public static final String MESSAGE_CODE_SCORE_CREDIT = "51"; // 跳转到 积分   订单详情
    public static final String MESSAGE_CODE_SCORE_FROZEN = "52"; // 跳转到 积分   积分详情
    public static final String MESSAGE_CODE_DELIVERY_INFO = "55";// 客户app：交车单详情页面
    public static final String MESSAGE_CODE_GIVE_COUPON_LIST = "56";// 客户app：优惠券列表
    public static final String MESSAGE_CODE_IMPROVE_INFORMATION_INFO = "57";// 客户app：完善信息详情:完善信息通过
    public static final String MESSAGE_CODE_STATISEVAL_MSG_INFO = "58";// 客户app：满意度评价问卷调查
    public static final String MESSAGE_CODE_CRM_MESSAGE_INFO = "60";// 客户app：消息详情页面
    public static final String MESSAGE_CODE_OIL_REPORT = "22";// 客户app：燃油报告评分列表页面


    /**
     * 爱心推 附加字段定义
     */
    public static final String MESSAGE_EXTRA_CAR_ID = "carId"; // 车辆id
    public static final String MESSAGE_EXTRA_WO_CODE = "woCode"; // 工单号
    public static final String MESSAGE_EXTRA_MESSAGE_CODE = "messageCode"; // 前端跳转code
    public static final String MESSAGE_EXTRA_ROUTE_ID = "routeId"; // 线路id
    public static final String MESSAGE_EXTRA_PUSH_SHOW_TYPE = "pushShowType"; // 消息展示分类
    public static final String MESSAGE_EXTRA_TEAM_ID = "teamId"; // 车队id
    public static final String MESSAGE_EXTRA_BEGIN_DATE = "beginDate";//起始时间
    public static final String MESSAGE_EXTRA_END_DATE = "endDate";//结束时间
    public static final String MESSAGE_EXTRA_TRIP_DATE = "tripDate";//行程日期（格式yyyyMMdd）
    public static final String MESSAGE_EXTRA_TRIP_ID = "tripId";//行程id
    public static final String MESSAGE_EXTRA_MAINTAIN_TYPE = "type";//保养类型1推荐保养,2 自定义保养
    public static final String MESSAGE_EXTRA_MAINTAIN_ID = "maintainId";// 保养记录id
    public static final String MESSAGE_EXTRA_CAR_NUMBER = "carNumber"; //车牌号
    public static final String MESSAGE_EXTRA_MAIN_DRIVER_NAME = "mainDriverName"; //主驾姓名
    public static final String MESSAGE_EXTRA_MAIN_DRIVER_PHONE = "mainDriverPhone"; //主驾电话
    public static final String MESSAGE_EXTRA_SUB_DRIVER_NAME = "subDriverName"; //副驾姓名
    public static final String MESSAGE_EXTRA_SUB_DRIVER_PHONE = "subDriverPhone"; //副驾电话
    public static final String MESSAGE_EXTRA_POSITION = "position"; //
    public static final String MESSAGE_EXTRA_CARCONDITION = "carCondition"; //
    public static final String MESSAGE_EXTRA_MESSAGE_ID = "messageId"; // 消息id
    public static final String MESSAGE_EXTRA_WO_STATUS = "woStatus"; // 工单状态
    public static final String MESSAGE_EXTRA_WO_TYPE = "woType"; // 工单类型
    public static final String MESSAGE_EXTRA_APPO_TYPE = "appoType"; // 预约方式
    public static final String MESSAGE_EXTRA_VIN = "vin"; // 底盘号
    public static final String MESSAGE_EXTRA_ID = "consultationId"; // ID值
    public static final String MESSAGE_EXTRA_DELIVERY_ID = "deliveryId"; // 营销交车单ID
    public static final String MESSAGE_EXTRA_CRM_URL = "url"; // CRM跳转链接地址
    public static final String MESSAGE_EXTRA_IMPROVE_INFORMATION_ID = "improveInfoId"; // 营销交车单ID
    public static final String MESSAGE_EXTRA_MESSAGE_LINK = "messageLink"; // 消息链接
    public static final String MESSAGE_EXTRA_NOTICE_ID = "noticeId"; // 消息表Id
    public static final String MESSAGE_EXTRA_STATIS_EVAL_URL = "statisEvalUrl"; // 满意度评价url

    /**
     * 推送App类型
     */
    public static final String PUSH_RECEIVETYPE_OWNER = "1"; // 接收者类型 1:车主版
    public static final String PUSH_RECEIVETYPE_ZERO = "0"; // 接收者类型 1:车主版
    @Deprecated
    public static final String PUSH_RECEIVETYPE_DRIVER = "2"; // 接收者类型 2:司机版
    public static final String PUSH_RECEIVETYPE_SERVERSTATION = "3"; // 接收者类型 3:服务站
    @Deprecated
    public static final String PUSH_RECEIVETYPE_OWNER_AND_DRIVER = "4"; // 接收者类型 车主版和司机版
    public static final String PUSH_RECEIVETYPE_MANAGER = "5"; // 接收者类型 5:管理员

    /**
     * 推送类型 1：广播，2：指定消息推送
     */
    public static final String PUSH_TYPE_MSG_BROADCAST = "1"; // 广播
    public static final String PUSH_TYPE_MSG_SPECIFIED = "2"; // 指定消息推送

    /**
     * 推送用户角色类型 1：管理员，2：司机，3：司机和管理员
     */
    public static final int PUSH_USER_ROLE_MANAGER = 0x01; // 管理员
    public static final int PUSH_USER_ROLE_DRIVER = 0x02; // 司机
    public static final int PUSH_USER_ROLE_CREATOR = 0x04; // 创建者
    public static final int PUSH_USER_ROLE_OWNER = 0x08; // 车主
    public static final int PUSH_USER_VEHICLE_TUBE = 0x10; // 车管
    @Deprecated
    public static final String PUSH_USER_ROLE_ALL = "3"; // 司机和管理员

    /**
     * 爱心推 消息展示分类 1：车辆，2：个人，3：工单消息,4:通知消息
     */
    public static final String PUSH_SHOW_TYPE_CAR = "1"; //1：车辆消息（车队APP）
    public static final String PUSH_SHOW_TYPE_USER = "2"; //2：个人消息（车队APP）
    public static final String PUSH_SHOW_TYPE_WO = "3"; //3：工单消息（服务APP）
    public static final String PUSH_SHOW_TYPE_NOTICE = "4"; //4：通知消息（服务APP）
    public static final String PUSH_SHOW_TYPE_WARNING = "5"; //5：預警消息（服务APP）

    /**
     * 用户是否可见 1：不可见，不展示到消息里；2：可见，在消息模块里显示
     */
    public static final String IS_USER_VISIBLE_NOT = "1";//不可见
    public static final String IS_USER_VISIBLE = "2";//可见

    public static final String PUSH_PRODUCT_NI = "DF";

    /**
     * 接收状态,0:不接收，1：接收
     */
    public static final String RECEIVE_STATE_NOT = "0";//不接收
    public static final String RECEIVE_STATE = "1";//接收

    /**
     * 司机车队融合版消息分类
     */
    public static final String MESSAGE_TYPE_FLEET = "1";// 车队消息
    public static final String MESSAGE_TYPE_SERVICE = "2";// 服务站通知
    public static final String MESSAGE_TYPE_MAINTAIN = "3";// 保养提醒
    public static final String MESSAGE_TYPE_SYSTEM = "4";// 系统消息
    public static final String MESSAGE_TYPE_BAD_DRIVING = "5";// 不良驾驶行为
    public static final String MESSAGE_TYPE_CAR_FAULT = "6";// 车辆故障
    public static final String MESSAGE_TYPE_OIL_EXCEPTION = "7";// 油量液位异常
    public static final String MESSAGE_TYPE_SIMFLOW = "11";// 流量提醒

    /**
     * V2.6.1.1服务协议需求新增一类CRM提醒的消息（属于服务站通知）
     */
    public static final String MESSAGE_TYPE_NAME_SERVICE = "服务站通知";
    /**
     * 司机车队融合版TBOSS自定义消息所属消息分类名称
     */
    public static final String MESSAGE_TYPE_NAME_SYSTEM = "系统消息";

    public static final String MESSAGE_TYPE_NAME_MAINTENANCE = "保养消息";

    public static final String MESSAGE_TYPE_NAME_SIMFLOW = "流量提醒";

    /**
     * V2.6.1.1服务协议需求新增一类CRM提醒的消息，消息默认stype值（用于APP端展示图片）。
     */
    public static final String MESSAGE_TYPE_SYSTEM_CRM_STYPE = "98";
    /**
     * 司机车队融合版TBOSS自定义消息默认stype值（用于APP端展示图片）
     */
    public static final String MESSAGE_TYPE_SYSTEM_TBOSS_STYPE = "99";

    /**
     * 未读
     */
    public static final Integer UNREAD=1;
    /**
     * 已读
     */
    public static final Integer HAVE_READ=2;
}