package com.nut.common.constant;

/**
 * @Description: 车队内部角色枚举
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.common.constant
 * @Author: yzl
 * @CreateTime: 2021-06-28 10:36
 * @Version: 1.0
 */
public enum FleetRoleEnum {
    /**
     * 角色权重值越大，权限越高（比较用）
     */
    CREATOR(0, "创建者", 9),
    ADMIN(1, "管理员", 8),
    DRIVER(2, "司机", 7),
    /**
     * 车主与车队无直接关系，权重暂定0
     */
    OWNER(9, "车主", 0);

    private int code;
    private String message;
    private int weight;

    FleetRoleEnum(final int code, final String message, final int weight) {
        this.code = code;
        this.message = message;
        this.weight = weight;
    }

    public static int getWeight(int code) {
        for (FleetRoleEnum e : FleetRoleEnum.values()) {
            if (e.code == code) {
                return e.weight;
            }
        }
        return 0;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public int weight() {
        return weight;
    }
}
