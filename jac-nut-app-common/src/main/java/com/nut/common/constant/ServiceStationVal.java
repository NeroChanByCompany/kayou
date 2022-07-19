package com.nut.common.constant;

/**
 * @Description: 服务站相关功能常量
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.constant
 * @Author: yzl
 * @CreateTime: 2021-06-21 19:55
 * @Version: 1.0
 */

public class ServiceStationVal {
    /* ######## ######## ######## ########
     *  工单状态(A070)
     * ######## ######## ######## ######## */
    /**
     * 待接受<br/><p>来自400客服或司机App的分派的，服务站还未确认接受的工单。</p>
     */
    @Deprecated
    public static final int TO_BE_ACCEPTED = 100;

    /**
     * 拒单申请审核中<br/><p>来自400客服或司机App的分派的，服务站提交拒单申请，等待客服审核的工单。</p>
     */
    @Deprecated
    public static final int REFUSE_APPLYING = 110;

    /**
     * 工单关闭-拒单<br/><p>服务站提交拒单申请，客服审核同意工单结束。</p>
     */
    @Deprecated
    public static final int CLOSE_REFUSED = 120;

    /**
     * 取消预约<br/><p>司机App预约服务站后，用户取消预约。</p>
     */
    @Deprecated
    public static final int CANCEL_ORDER = 125;

    /**
     * 待出发<br/><p>外出申请已通过，等待服务站维修人员出发的工单。</p>
     */
    @Deprecated
    public static final int TO_TAKE_OFF = 130;

    /**
     * 待接车<br/><p>等待接服务人员面对面扫码接车的工单。</p>
     */
    @Deprecated
    public static final int TO_RECEIVE = 140;

    /**
     * 修改申请审核中-待接受<br/><p>来自待接受状态申请的工单修改，修改申请等待客服处理。</p>
     */
    @Deprecated
    public static final int MODIFY_APPLYING_ACCEPT = 150;

    /**
     * 修改申请审核中-待出发<br/><p>来自待出发状态申请的工单修改，修改申请等待客服处理。</p>
     */
    @Deprecated
    public static final int MODIFY_APPLYING_TAKEOFF = 160;

    /**
     * 修改申请审核中-待接车<br/><p>来自待接车状态申请的工单修改，修改申请等待客服处理。</p>
     */
    @Deprecated
    public static final int MODIFY_APPLYING_RECEIVE = 170;

    /**
     * 关闭申请审核中-检查<br/><p>服务站在检查中提交的关闭申请，等待客服审核的工单。</p>
     */
    @Deprecated
    public static final int CLOSE_APPLYING_INSPECT = 180;

    /**
     * 关闭申请审核中-维修<br/><p>服务站在维修中提交的关闭申请，等待客服审核的工单。</p>
     */
    @Deprecated
    public static final int CLOSE_APPLYING_REPAIR = 190;

    /**
     * 工单关闭-关闭申请-检查<br/><p>服务站在检查中提交工单关闭申请，客服审核同意工单结束。</p>
     */
    @Deprecated
    public static final int CLOSE_INSPECT = 200;

    /**
     * 工单关闭-关闭申请-维修<br/><p>服务站在维修中提交工单关闭申请，客服审核同意工单结束。</p>
     */
    @Deprecated
    public static final int CLOSE_REPAIR = 210;

    /**
     * 检查中<br/><p>已成功接车，开始检查。</p>
     */
    @Deprecated
    public static final int INSPECTING = 220;

    /**
     * 维修中<br/><p>检查结束，维修中的工单。</p>
     */
    @Deprecated
    public static final int REPAIRING = 230;

    /**
     * 已完成<br/><p>维修结束，工单流程结束。</p>
     */
    @Deprecated
    public static final int WORK_DONE = 240;

    /**
     * 已评价<br/><p>司机App对已完成的工单完成评价。</p>
     */
    @Deprecated
    public static final int EVALUATED = 250;

    /* ######## ######## ######## ########
     *  付费类型(A071)
     * ######## ######## ######## ######## */
    /**
     * 车厂付费
     * @deprecated 请使用枚举PayTypeEnum
     */
    @Deprecated
    public static final int PAY_BY_FACTORY = 1;
    /**
     * 用户自费
     * @deprecated 请使用枚举PayTypeEnum
     */
    @Deprecated
    public static final int PAY_BY_USER = 2;

    /* ######## ######## ######## ########
     *  处理方式
     * ######## ######## ######## ######## */
    /**
     * 修复处理
     * @deprecated 请使用枚举DealTypeEnum
     */
    @Deprecated
    public static final int DEAL_DIRECT = 1;
    /**
     * 换件维修
     * @deprecated 请使用枚举DealTypeEnum
     */
    @Deprecated
    public static final int DEAL_CHANGE = 2;

    /* ######## ######## ######## ########
     *  工单类型
     * ######## ######## ######## ######## */
    /** 进出站 */
    public static final int STATION_SERVICE = 1;
    /** 外出救援 */
    public static final int OUTSIDE_RESCUE = 2;
    /** 外出救援-正常救援 */
    public static final int OUTSIDE_NORMALRESCUE = 1;
    /** 外出救援-随叫随到 */
    public static final int OUTSIDE_ONCALL = 2;

    /* ######## ######## ######## ########
     *  岗位区分
     * ######## ######## ######## ######## */
    /** 管理员 */
    public static final int JOB_TYPE_ADMIN = 1;
    /** 业务员 */
    public static final int JOB_TYPE_SALESMAN = 2;

    /* ######## ######## ######## ########
     *  审核结果
     * ######## ######## ######## ######## */
    /** 同意 */
    public static final String FINDINGS_OF_AUDIT_AGREE = "1";
    /** 驳回 */
    public static final String FINDINGS_OF_AUDIT_REJECT = "2";

    /* ######## ######## ######## ########
     *  回访处理方式
     * ######## ######## ######## ######## */
    /** 回访完毕 */
    public static final int INQUIRE_DEAL_OK = 1;
    /** 稍后再回访 */
    public static final int INQUIRE_DEAL_PUT_OFF = 2;

    /* ######## ######## ######## ########
     *  预警类型
     * ######## ######## ######## ######## */
    /** 外出救援-超时未接单预警 */
    public static final int WARN_TYPE_OUT_ACCEPT = 1;
    /** 外出救援-超时未出发预警 */
    public static final int WARN_TYPE_OUT_TAKEOFF = 2;
    /** 外出救援-超时未到达预警 */
    public static final int WARN_TYPE_OUT_ARRIVE = 3;
    /** 外出救援-维修超时预警 */
    public static final int WARN_TYPE_OUT_REPAIR = 4;
    /** 进出站-超时未接单预警 */
    public static final int WARN_TYPE_STA_ACCEPT = 5;
    /** 进出站-超时未派工预警 */
    public static final int WARN_TYPE_STA_INSPECT_BEGIN = 6;
    /** 进出站-维修超时预警 */
    public static final int WARN_TYPE_STA_REPAIR = 7;
    /** 进出站-检查超时预警 */
    public static final int WARN_TYPE_STA_INSPECT = 8;

    /* ######## ######## ######## ########
     *  预约方式
     * ######## ######## ######## ######## */
    /** 400客服 */
    public static final int APPO_TYPE_400 = 1;
    /** APP预约 */
    public static final int APPO_TYPE_APP = 2;
    /** 服务站建单 */
    public static final int APPO_TYPE_STA = 3;

    /* ######## ######## ######## ########
     *  服务类型
     * ######## ######## ######## ######## */
    /**
     * 保外维修
     * @deprecated 请使用枚举ServiceTypeEnum
     */
    @Deprecated
    public static final int SERVICE_TYPE_ZI_FEI = 1;
    /**
     * 走保
     * @deprecated 请使用枚举ServiceTypeEnum
     */
    @Deprecated
    public static final int SERVICE_TYPE_ZOU_BAO = 2;
    /**
     * 里保
     * @deprecated 请使用枚举ServiceTypeEnum
     */
    @Deprecated
    public static final int SERVICE_TYPE_LI_BAO = 3;
    /**
     * 保修
     * @deprecated 请使用枚举ServiceTypeEnum
     */
    @Deprecated
    public static final int SERVICE_TYPE_BAO_XIU = 4;

    /* ######## ######## ######## ########
     *  费用类型
     * ######## ######## ######## ######## */
    /**
     * 保内
     * @deprecated 请使用枚举ChargeTypeEnum
     */
    @Deprecated
    public static final int CHARGE_TYPE_WARRANTY = 1;
    /**
     * 保外
     * @deprecated 请使用枚举ChargeTypeEnum
     */
    @Deprecated
    public static final int CHARGE_TYPE_OVER_WARRANTY = 2;

    /* ######## ######## ######## ########
     *  WebService接口类型
     * ######## ######## ######## ######## */
    /** 需要调用所有 WebService 接口*/
    public static final String WEB_SERVICE_ALL_INTERFACE = "1";
    /** TSP案例上传接口 */
    public static final String WEB_SERVICE_UPLOADTSPSRINF = "2";
    /** TSP案例附件上传接口 */
    public static final String WEB_SERVICE_UPLOADTSPSRPHOTO = "3";
    /** TSP维修单上传接口 */
    public static final String WEB_SERVICE_UPLOADTSPSMINF = "4";
    /** TSP维修单附件上传接口 */
    public static final String WEB_SERVICE_UPLOADTSPSMPHOTO = "5";
    /** 创建维修单接口 */
    public static final String WEB_SERVICE_CREATESMORDER = "6";
    /** 创建维修单接口-接车 */
    public static final String WEB_SERVICE_CREATESMORDER_2 = "7";
    /** 开始维修上传时间节点接口 */
    public static final String WEB_SERVICE_ONGOINGSMAPPOINTMENT = "8";

    /** 客户端APP创建的工单回传 */
    public static final String WEB_SERVICE_APPWORKORDERRETURN = "st1";
    /** 服务站异常操作工单申请 */
    public static final String WEB_SERVICE_ABNORMALWORKORDERAPPLY = "st2";
    /** 服务站接单（救援、进站） */
    public static final String WEB_SERVICE_SERVICERECEIVINGORDER = "st3";
    /** 车辆维修进站时间 */
    public static final String WEB_SERVICE_MAINTENANCEINBOUNDTIME = "st4";
    /** 外出救援出发时间 */
    public static final String WEB_SERVICE_GOINGOUTDEPARTURETIME = "st5";
    public static final String WEB_SERVICE_GOINGOUTDEPARTURETIME_1 = "st5_1";
    public static final String WEB_SERVICE_GOINGOUTDEPARTURETIME_2 = "st5_2";
    /** 车辆接车管理（救援、进站） */
    public static final String WEB_SERVICE_VEHICLERECEIVESUPERVISE = "st6";
    public static final String WEB_SERVICE_VEHICLERECEIVESUPERVISE_1 = "st6_1";
    public static final String WEB_SERVICE_VEHICLERECEIVESUPERVISE_2 = "st6_2";
    /** 车辆检查结束（救援、进站） */
    public static final String WEB_SERVICE_VEHICLEINSPECTEND = "st7";
    public static final String WEB_SERVICE_VEHICLEINSPECTEND_1 = "st7_1";
    public static final String WEB_SERVICE_VEHICLEINSPECTEND_2 = "st7_2";
    /** 首次上传照片时间 */
    public static final String WEB_SERVICE_FIRSTUPLOADPHOTOTIME = "st8";
    /** 车辆维修结束（救援、进站） */
    public static final String WEB_SERVICE_VEHICLEREPAIREND = "st9";
    /** 车辆维修离站时间 */
    public static final String WEB_SERVICE_VEHICLEREPAIROFFTIME = "st10";
    /** 二次救援发起时间 */
    public static final String WEB_SERVICE_TWORESCUELAUNCHTIME = "st11";
    /** 服务站取消救援 */
    public static final String WEB_SERVICE_SERVICECANCELRESCUE = "st12";
    /** 服务请求评价结果 */
    public static final String WEB_SERVICE_REQUESTEVALUATERESULT = "st13";
    /** 服务工单附件信息回传 */
    public static final String WEB_SERVICE_WORKORDERENCLOSURE = "st14";
    /** 服务工单保修保内原因件附件信息回传 */
    public static final String WEB_SERVICE_WORKORDERENCLOSURE_1 = "st14_1";
    /** 结算单附件信息回传 */
    public static final String WEB_SERVICE_WORKORDERENCLOSURE_2 = "st14_2";
    /** 故障码基础信息 */
    public static final String WEB_SERVICE_FAULTCODEBASICINF = "st15";
    /** 车辆与APP绑定关系 */
    public static final String WEB_SERVICE_VEHICLEAPPBINDING = "st16";
    /** 取消预约 */
    public static final String WEB_SERVICE_SERVICECANCELBEFORERECEIPT = "st17";
    /** 重保配件码 */
    public static final String WEB_SERVICE_WOACCESSORIESCODE = "st18";
    /** 故障上报 */
    public static final String WEB_SERVICE_VEHICLEFAULTINFORMATION = "st19";

    /* ######## ######## ######## ########
     *  回访状态
     * ######## ######## ######## ######## */
    /** 未回访 */
    public static final int INQ_UNDONE = 0;
    /** 已回访 */
    public static final int INQ_FINISH = 1;
    /** 标记解除预警 */
    public static final int INQ_MARKED = 2;
    /** 建单解除预警 */
    public static final int INQ_CREATE = 3;
    /** 接单解除预警 */
    public static final int INQ_ACCEPT = 4;
    /** 接车解除预警 */
    public static final int INQ_RECEIVE = 5;
    /** 拒单解除预警 */
    public static final int INQ_REFUSE = 6;
    /** 超时解除预警 */
    public static final int INQ_TIMEOUT = 7;

    /* ######## ######## ######## ########
     *  调件状态
     * ######## ######## ######## ######## */
    /** 调件中 */
    public static final int TRANS_WAIT = 0;
    /** 配件到货确认 */
    public static final int TRANS_FINISH = 1;
    /** 维修项已删除 */
    public static final int TRANS_DELETE = 2;

    /* ######## ######## ######## ########
     *  建单权限
     * ######## ######## ######## ######## */
    /** 建单权限-不能建单 */
    public static final int AUTHORITY_NO_AUTH = 0;
    /** 建单权限-进站维修 */
    public static final int AUTHORITY_STATION_SERVICE = 1;
    /** 建单权限-外出救援 */
    public static final int WAUTHORITY_OUTSIDE_RESCUE = 2;
    /** 建单权限-外出救援和进站维修 */
    public static final int AUTHORITY_ALL_AUTH = 3;



    /* ================================================================================ */
    /* ######## ######## ######## ########
     *  其他不成组常量
     * ######## ######## ######## ######## */
    /** 维修流程图片替换定义 */
    public static final String PHOTO_URL_PLACEHOLDER = "_PHOTO_URL_PLACEHOLDER_";

    /** 故障码基础信息同步 请求ID redis key前缀 */
    public static final String REDIS_PREFIX_FAULTCODEBASICINF = "fb";
    /** 车辆与APP绑定关系同步 请求ID redis key前缀 */
    public static final String REDIS_PREFIX_VEHICLEAPPBINDING = "vb";
    /** 故障上报-故障信息同步 请求ID redis key前缀 */
    public static final String REDIS_PREFIX_VEHICLEFAULTINFORMATION = "rb";

    /* ######## ######## ######## ########
     *  数据字典类型
     * ######## ######## ######## ######## */
    /** 取消预约 */
    public static final String DATA_DICT_CODE_CANCEL_REASON = "A076";
    /** 干预状态 */
    public static final String DATA_DICT_CODE_INTERVENE_STATUS = "A078";
    /** 不满意的流程 */
    public static final String DATA_DICT_CODE_DISCONTENT = "A080";
    /** 二次外出原因 */
    public static final String DATA_DICT_CODE_TIMES_RESCUE = "A083";



}
