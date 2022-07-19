package com.nut.driver.app.dto;/**
 * Created by Administrator on 2021/11/25.
 */

import lombok.Data;

/**
 * @version v1.0.0
 * @desc
 * @auther wangshuai
 * @create 2021/11/25
 * @Company esv
 */
@Data
public class SimCardPlaDto {
    //底盘号
    private String chassisNumber;
    //车牌号
    private String carNumber;
    //终端ID
    private String terminalId;
    //sim卡号
    private String autoTerminal;
    //用户ID
    private String userId;
    //用户电话
    private String userPhone;
    //剩余流量
    private String simLeftMeals;
}
