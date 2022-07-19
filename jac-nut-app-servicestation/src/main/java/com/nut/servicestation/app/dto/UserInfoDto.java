package com.nut.servicestation.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录 信息
 */
@Data
public class UserInfoDto  implements Serializable {
    /** 服务站名称 */
    private String serviceStationName;
    /** 服务站CODE */
    private String serviceCode;
    /** 服务站CODE */
    private String serviceStationPhone;
    /** 登录者ID */
    private String accountId;
    /** 登录者名称 */
    private String accountName;
    /** 登录者电话 */
    private String phone;
    /** 服务站地址 */
    private String address;
    /** 密码 */
    @JsonIgnore
    private String accountPwd;
    /** 角色 */
    private Integer roleCode;
    /** 用户标记 */
    private String token;
    /** 服务站启用状态 0：停用 1：启用 */
    private int stationEnable;
    /** 服务站ID */
    private String serviceStationId;
    /** 图片地址 */
    private String picture;
    /** 服务站经度 */
    private String serviceStationLon;
    /** 服务站纬度 */
    private String serviceStationLat;
    /** 账号锁定状态 0：正常 1：锁定 */
    private String lockAccount;
    /** 服务站默认电子围栏半径（服务半径）*/
    private String stationRadius;
    /** 服务站省市编码 */
    private String areaCode;
    /** 服务站是否有建单权限 */
    private Integer stationWoCreatable;
    /** 是否有离线工单权限*/
    private Integer offlineWoEnable;
    /** 二网服务站建单范围（0:不能建单、1：进站、2：外出救援、3：进站和外出救援）*/
    private Integer secondaryCreateWoRange;
    /**
     * 消息发送key
     */
    private String sendMessageKey;


}
