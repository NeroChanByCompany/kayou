package com.nut.servicestation.common.constants;

public class UptimeVal {
    /**
     * 接口：服务站异常操作工单申请
     * 字段：申请类型
     */
    public static final String APPLY_TYPE_REFUSE = "1";
    public static final String APPLY_TYPE_MODIFY = "2";
    public static final String APPLY_TYPE_CLOSE = "3";
    public static final String APPLY_TYPE_APPCAREXC = "4";
    public static final String APPLY_TYPE_APPSTAEXC = "5";
    public static final String APPLY_TYPE_APPSTACAREXC = "6";
    /**
     * 接口：故障码基础信息
     * 字段：数据状态
     */
    public static final String OPERATION_INSERT = "0";
    public static final String OPERATION_UPDATE = "1";
    public static final String OPERATION_DELETE = "2";
    /**
     * 接口：车辆与APP绑定关系
     * 字段：是否绑定APP账号
     */
    public static final String BIND_FLAG_UNBIND = "0";
    public static final String BIND_FLAG_BIND = "1";

    private UptimeVal() {
    }
}
