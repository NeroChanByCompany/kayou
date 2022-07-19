package com.nut.common.enums;

/**
 * @Description: 工单状态枚举
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.enums
 * @Author: yzl
 * @CreateTime: 2021-06-16 16:27
 * @Version: 1.0
 */

public enum ServiceStationEnum {
    /**
     * 待接受<br/>
     * <p>来自400客服或司机App的分派的，服务站还未确认接受的工单。</p>
     */
    TO_BE_ACCEPTED(100, "待接受", "预约中"),

    /**
     * 拒单申请审核中<br/>
     * <p>来自400客服或司机App的分派的，服务站提交拒单申请，等待客服审核的工单。</p>
     */
    REFUSE_APPLYING(110, "拒单申请审核中", "预约中"),

    /**
     * 工单关闭-拒单<br/>
     * <p>服务站提交拒单申请，客服审核同意工单结束。</p>
     */
    CLOSE_REFUSED(120, "工单关闭-拒单", "已取消"),

    /**
     * 取消预约<br/>
     * <p>司机App预约服务站后，用户取消预约。</p>
     */
    CANCEL_ORDER(125, "取消预约", "已取消"),

    /**
     * 待出发<br/>
     * <p>外出申请已通过，等待服务站维修人员出发的工单。</p>
     */
    TO_TAKE_OFF(130, "待出发", "待出发"),

    /**
     * 待接车<br/>
     * <p>等待接服务人员面对面扫码接车的工单。</p>
     */
    TO_RECEIVE(140, "待接车", "待接车"),

    /**
     * 工单关闭-取消救援<br/>
     * <p>服务站在外出救援-待接车阶段成功提交取消救援，无需客服审核工单结束。</p>
     */
    CLOSE_RESCUE(145, "工单关闭-取消救援", "已取消"),

    /**
     * 修改申请审核中-待接受<br/>
     * <p>来自待接受状态申请的工单修改，修改申请等待客服处理。</p>
     */
    MODIFY_APPLYING_ACCEPT(150, "修改申请审核中-待接受", "待接受"),

    /**
     * 修改申请审核中-待出发<br/>
     * <p>来自待出发状态申请的工单修改，修改申请等待客服处理。</p>
     */
    MODIFY_APPLYING_TAKEOFF(160, "修改申请审核中-待出发", "待出发"),

    /**
     * 修改申请审核中-待接车<br/>
     * <p>来自待接车状态申请的工单修改，修改申请等待客服处理。</p>
     */
    MODIFY_APPLYING_RECEIVE(170, "修改申请审核中-待接车", "待接车"),

    /**
     * 关闭申请审核中-检查<br/>
     * <p>服务站在检查中提交的关闭申请，等待客服审核的工单。</p>
     */
    CLOSE_APPLYING_INSPECT(180, "关闭申请审核中-检查", "维修中"),

    /**
     * 关闭申请审核中-维修<br/>
     * <p>服务站在维修中提交的关闭申请，等待客服审核的工单。</p>
     */
    CLOSE_APPLYING_REPAIR(190, "关闭申请审核中-维修", "维修中"),

    /**
     * 工单关闭-关闭申请-检查<br/>
     * <p>服务站在检查中提交工单关闭申请，客服审核同意工单结束。</p>
     */
    CLOSE_INSPECT(200, "工单关闭-关闭申请-检查", "已取消"),

    /**
     * 工单关闭-关闭申请-维修<br/>
     * <p>服务站在维修中提交工单关闭申请，客服审核同意工单结束。</p>
     */
    CLOSE_REPAIR(210, "工单关闭-关闭申请-维修", "已取消"),

    /**
     * 检查中<br/>
     * <p>已成功接车，开始检查。</p>
     */
    INSPECTING(220, "检查中", "维修中"),

    /**
     * 维修中<br/>
     * <p>检查结束，维修中的工单。</p>
     */
    REPAIRING(230, "维修中", "维修中"),

    /**
     * 已完成<br/>
     * <p>维修结束，工单流程结束。</p>
     */
    WORK_DONE(240, "待评价", "待评价"),

    /**
     * 已评价<br/>
     * <p>司机App对已完成的工单完成评价。</p>
     */
    EVALUATED(250, "已评价", "已评价"),

    /**
     * 关闭申请审核中-待出发<br/>
     * <p>服务站在外出救援待出发提交的关闭申请，等待客服审核的工单。</p>
     */
    CLOSE_APPLYING_TAKEOFF(260, "关闭申请审核中-待出发", "待出发"),

    /**
     * 关闭申请审核中-待接车<br/>
     * <p>服务站在待接车提交的关闭申请，等待客服审核的工单。</p>
     */
    CLOSE_APPLYING_RECEIVE(270, "关闭申请审核中-待接车", "待接车"),

    /**
     * 工单关闭-关闭申请-待出发<br/>
     * <p>服务站在待出发提交工单关闭申请，客服审核同意工单结束。</p>
     */
    CLOSE_TAKEOFF(280, "工单关闭-关闭申请-待出发", "已取消"),

    /**
     * 工单关闭-关闭申请-待接车<br/>
     * <p>服务站在待接车提交工单关闭申请，客服审核同意工单结束。</p>
     */
    CLOSE_RECEIVE(290, "工单关闭-关闭申请-待接车", "已取消"),

    /**
     * 工单审核-待审核<br/>
     * <p>协议工单增加审核功能，创建完工单，工单状态为待审核。</p>
     */
    TO_AUDIT(300, "工单审核-待审核", "待审核"),

    /**
     * 工单审核-已驳回<br/>
     * <p>协议工单增加审核功能，创建完工单，工单状态为待审核。</p>
     */
    HAS_BEEN_REJECTED(310, "工单审核-已驳回", "已驳回"),

    /**
     * 工单已报单<br/>
     * <p></p>
     */
    HAS_BEEN_REPORTED(400, "已报单", "已报单");

    private int code;
    private String message;
    private String appMessage;

    ServiceStationEnum(final int code, final String message, final String appMessage) {
        this.code = code;
        this.message = message;
        this.appMessage = appMessage;
    }

    public static String getMessage(int code) {
        for (ServiceStationEnum serviceStationEnum : ServiceStationEnum.values()) {
            if (serviceStationEnum.code() == code) {
                return serviceStationEnum.message;
            }
        }
        return null;
    }

    public static String getAppMessage(int code) {
        for (ServiceStationEnum serviceStationEnum : ServiceStationEnum.values()) {
            if (serviceStationEnum.code() == code) {
                return serviceStationEnum.appMessage;
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

    public String appMessage() {
        return appMessage;
    }
}
