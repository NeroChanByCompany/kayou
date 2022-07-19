package com.nut.driver.app.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuBing
 * @Classname AppVersion
 * @Description TODO
 * @Date 2021/11/2 16:44
 */
@Data
@Accessors(chain = true)
public class AppVersion {

    /**
     * 当前服务app版本
     * kehu_version 客户版本
     * fleet_version 车队版本
     * service_version 服务版本
     */
    private String actionCode;

    /**
     * 当前app类型 1 android 2 ios
     */
    private String type;

    /**
     * 当前app版本号
     */
    private String version;

    public void setActionCode0(String str){
        if (str.equals("0")){
            this.actionCode = "kehu_version";
        }
        if (str.equals("1")){
            this.actionCode = "fleet_version";
        }
        if (str.equals("2")){
            this.actionCode = "service_version";
        }
    }
}
