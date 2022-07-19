package com.nut.jac.kafka.vo;

import com.nut.jac.kafka.util.RegexpUtils;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuBing
 * @Classname UploadRescueRoutePointVO
 * @Description TODO
 * @Date 2021/8/31 13:28
 */
@Data
@Accessors(chain = true)
public class UploadRescueRoutePointVO {

    /**
     * 工单号
     */
    private String woCode;
    /**
     * 轨迹点序号
     */
    private String index;
    /**
     * 轨迹点纬度
     */
    private String latitude;
    /**
     * 轨迹点经度
     */

    private String longitude;
    /**
     * 轨迹点时间
     */

    private String time;
    /**
     * 退出登录标识（1:发生退出登录）
     */

    private String isLogout;
    /**
     * 定位精度
     */
    private String radius;

    private String userId;
}
