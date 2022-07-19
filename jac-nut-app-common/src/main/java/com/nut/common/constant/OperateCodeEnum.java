package com.nut.common.constant;

/**
 * @Description: 工单操作类型枚举
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.common.constant
 * @Author: yzl
 * @CreateTime: 2021-06-23 16:57
 * @Version: 1.0
 */
public enum OperateCodeEnum {
    OP_ACCEPT (901, "确认接单（服务站）"),
    OP_REFUSE_APPLY (902, "拒单申请（服务站）"),
    OP_REFUSE_APPLY_CONFIRM (903, "拒单申请审核（客服）"),
    OP_RECORD (904, "客服追访记录（客服）"),
    OP_MODIFY_APPLY (905, "工单修改申请（服务站）"),
    OP_MODIFY_APPLY_CONFIRM (906, "工单修改申请处理（客服）"),
    OP_TAKE_OFF (907, "确认出发（服务站）"),
    OP_SCAN_RECEIVE (908, "确认接车（服务站）"),
    OP_CLOSE_APPLY (909, "申请关闭（服务站）"),
    OP_CLOSE_APPLY_CONFIRM(910, "关闭申请审核（客服）"),
    OP_INSPECT (911, "检查过程（服务站）"),
    OP_REPAIR (912, "维修过程（服务站）"),
    OP_TIMEOUT_NO_ACCEPT (913, "超时未接单回访（客服）"),
    OP_TIMEOUT_NO_TAKEOFF (914, "超时未出发回访（客服）"),
    OP_TIMEOUT_NO_ARRIVE (915, "超时未到达回访（客服）"),
    OP_TIMEOUT_NO_REPAIR (916, "维修超时回访（客服）"),
    OP_TIMEOUT_NO_STAFF (917, "超时未开始派工回访（客服）"),
    OP_COMMENT (919, "司机评价（用户）"),
    OP_DISTANCE_ANOMALY (920, "外出距离异常处理（客服）"),
    OP_TIMEOUT_NO_INSPECT(921, "检查超时回访（客服）"),
    OP_TIMES_RESCUE(922, "二次外出（服务站）"),
    OP_TIMEOUT_STATION(923, "进站超时回访（客服）"),
    OP_TRANSFER_PARTS(924, "调件信息（服务站）"),
    OP_TRANSFER_PARTS_DONE(925, "到货确认（服务站）"),
    OP_TIMEOUT_TRANSFER(926, "调件超时回访（客服）"),
    OP_ABORT_RESCUE(927, "取消救援（服务站）");

    private int code;
    private String message;

    OperateCodeEnum(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code) {
        for (OperateCodeEnum operateCodeEnum : OperateCodeEnum.values()) {
            if (operateCodeEnum.code() == code) {
                return operateCodeEnum.message;
            }
        }
        return null;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
