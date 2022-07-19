package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @Description: 签到统计
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.pojo
 * @Author: yzl
 * @CreateTime: 2021-08-09 16:04
 * @Version: 1.0
 */
@Data
public class SignPojo {

    private int day;

    private int month;

    private int year;

    public String toString(){
        String dayStr=null;
        String monthStr=null;
        String yearStr=null;
        if (day<10){
            dayStr = "0" + day;
        }else {
            dayStr = "" + day;
        }
        if (month<10){
            monthStr = "0" + month;
        }else {
            monthStr = "" + month;
        }
        yearStr = year+"";
        return yearStr+monthStr+dayStr;
    }
}
