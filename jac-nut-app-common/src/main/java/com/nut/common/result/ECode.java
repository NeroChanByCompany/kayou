package com.nut.common.result;

/**
 * @description: 系统响应码
 * @author: hcb
 * @createTime: 2021/01/20 10:37
 * @version:1.0
 */
public class ECode {

    public static final ECode SUCCESS = new ECode(200, "success");
    public static final ECode PHONE_EXIST = new ECode(202, "号码已注册");
    public static final ECode BUSINESS_FAILURE = new ECode(400, "业务失败");
    public static final ECode ACCESS_DENIED = new ECode(401, "没有访问权限");
    public static final ECode SERVER_ERROR = new ECode(500, "服务内部错误");
    public static final ECode PARAM_FAIL = new ECode(510, "请求参数异常");
    public static final ECode SERVICE_UNAVAILABLE = new ECode(503, "服务限流，暂不可用");
    public static final ECode DISTANCE_LIMIT = new ECode(505, "Distance Limit");
    public static final ECode CLIENT_ERROR = new ECode(507, "Client Error");
    public static final ECode USER_DNE = new ECode(508, "User Does Not Exist");
    public static final ECode TOKEN_INVALID = new ECode(509, "登录已过期，请重新登录！");
    public static final ECode PARAM_ERROR = new ECode(510, "请求参数异常");
    public static final ECode PASSWORD_NOT_EQUAL = new ECode(537, "两次密码不一致");
    public static final ECode OLDPASSWORD_NOT_RIGHT = new ECode(538, "原始密码不正确");
    public static final ECode NEWPASSWORD_EQUAL_OLDPASSWORD = new ECode(539, "新旧密码相同");
    public static final ECode SMSCODE_ERROR = new ECode(540, "验证码不正确");
    public static final ECode NO_USERID = new ECode(555, "无法获取到用户信息，请重试！");
    public static final ECode NO_AUTH = new ECode(599, "暂无当前订单号信息");
    public static final ECode ADD_VEHICLE_VIN_EMPTY = new ECode(1008, "底盘号不能为空");
    public static final ECode ADD_VEHICLE_NOT_FOUND = new ECode(1005, "系统不存在此车辆");
    public static final ECode ADD_VEHICLE_ADDED = new ECode(1006, "已经添加过此车辆");
    public static final ECode ADD_VEHICLE_OTHER_ADDED = new ECode(1007, "已经被其他人添加");
    public static final ECode FALLBACK = new ECode(615, "服务器暂忙，请稍后重试！");
    public static final ECode NEED_WAIT_FOR_PIC_UPLOAD = new ECode(701, "提交成功，请等待图片上传完毕");
    public static final ECode NO_DATA = new ECode(511, "NO DATA");
    public static final ECode NEED_WAIT_FOR_POINT_UPLOAD = new ECode(703, "提交成功，请等待轨迹点上传完毕");
    public static final ECode DATA_Exception = new ECode(605, "数据异常！");
    public static final ECode COUPON_INVALID = new ECode(706, "优惠券不可用");
    public static final ECode APP_CAR_STATION_LIMIT = new ECode(707, "超出距离限制");
    /**
     * 请添加有效积分
     **/
    public static final ECode INTEGRAL_INVALID_ERROR = new ECode(1100, "请添加有效积分");

    public static final ECode APP_UPDATE_ERROR = new ECode(1998, "当前版本过低，请升级到最新版本");



    public ECode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;

    private String message;

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
