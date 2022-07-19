package com.nut.servicestation.app.enums;

/**
 * @author liuBing
 * @Classname ImageEnum
 * @Description TODO
 * @Date 2021/8/17 9:12
 */
public enum ImageEnum {

    /**
     * 证件照+里程表照片
     */
    PATH_CERTIFICATE("1","证件照+里程表照片"),
    /**
     * 其它照片
     */
    PATH_OTHER("2","其它照片"),
    /**
     * 整车及车牌照片
     */
    PATH_WHOLE_CAR ("3","整车及车牌照片"),
    /**
     * 底盘号信息照片
     */
    PATH_VIN("4"," 底盘号信息照片"),
    /**
     * 故障现象
     */
    PATH_FAULT("5","故障现象"),
    /**
     * 故障件整体照片
     */
    PATH_FAULT_WHOLE("6","故障件整体照片"),
    /**
     * 故障件厂家标识照片
     */
    PATH_FAULT_IDENTIFICATION("7","故障件厂家标识照片"),
    /**
     * 委托书
     */
    PATH_ENTRUST("8","委托书"),
    /**
     *  新件整体
     */
    PATH_NEW("9"," 新件整体"),
    /**
     * 配件采购单
     */
    PATH_PARTS("10","配件采购单"),
    /**
     * 外出人员和故障车合影
     */
    PATH_HY("11","外出人员和故障车合影"),
    ;

    /**
     * 状态码
     */
    private String code;
    /**
     * value值
     */
    private String value;

    ImageEnum(String code, String value){
        this.code = code;
        this.value = value;
    }

    public String getValue(){
        return this.value;
    };

    public String getCode(){
        return this.code;
    }

    public static String getValueByCode(String code){
        for (ImageEnum value : ImageEnum.values()) {
            if (value.code.equals(code)){
                return value.value;
            }
        }
        return ImageEnum.PATH_OTHER.getValue();
    };
}
