package com.nut.driver.common.wrapper;

/**
 * @Description:
 * @MClassName: MaintainCategoryEnum
 * @Authur: liubing
 * @Date: 2020/5/12 14:30
 */
public enum MaintainCategoryEnum {
    unknownCategory("1000", "未知"),
    checkCategory("1001", "检查"),
    replaceCategory("1002", "更换"),
    lubricateCategory("1003", "润滑"),
    partsCategory("1004", "配件");

    /**
     * 根据code获取value
     * @param code
     * @return
     */
    public static String getValueByCode(String code) {
        MaintainCategoryEnum[] carTypeEnums = values();
        for (MaintainCategoryEnum maintainCategoryEnum : carTypeEnums) {
            if (maintainCategoryEnum.code().equals(code)) {
                return maintainCategoryEnum.value();
            }
        }
        return MaintainCategoryEnum.unknownCategory.value;
    }

    /**
     * 根据value获取code
     * @param value
     * @return
     */
    public static String getCodeByValue(String value) {
        MaintainCategoryEnum[] carTypeEnums = values();
        for (MaintainCategoryEnum maintainCategoryEnum : carTypeEnums) {
            if (maintainCategoryEnum.value().equals(value)) {
                return maintainCategoryEnum.code();
            }
        }
        return MaintainCategoryEnum.unknownCategory.code;
    }

    private String code;

    private String value;

    MaintainCategoryEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String code() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String value() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
