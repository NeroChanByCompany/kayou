package com.nut.driver.app.dto;

import com.nut.common.result.PagingInfo;
import lombok.Data;

/**
 * @Description: 查询报警通知列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-30 10:27
 * @Version: 1.0
 */
@Data
public class QueryAlarmNoticeListAttributeDto extends PagingInfo<QueryAlarmNoticeListDto> {
    /**
     * 所有车辆的未读消息总数
     */
    private Integer unreadMesCount;
}
