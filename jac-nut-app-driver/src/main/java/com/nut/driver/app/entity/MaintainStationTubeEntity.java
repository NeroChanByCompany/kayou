package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 车管账号与服务站的关系
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.entity
 * @Author: yzl
 * @CreateTime: 2021-06-21 20:19
 * @Version: 1.0
 */
@Data
@TableName("maintain_station_tube")
public class MaintainStationTubeEntity implements Serializable {
    /**
     * 主键
     */
    private Long id;
    /**
     * 总公司ID
     */
    private String companyId;
    /**
     * 总公司名称
     */
    private String company;
    /**
     *服务站鉴定章号
     */
    private String serverId;
    /**
     * 创建时间
     */
    private Date createTime;

}
