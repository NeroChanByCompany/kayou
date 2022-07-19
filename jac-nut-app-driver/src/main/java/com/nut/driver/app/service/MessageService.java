package com.nut.driver.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.driver.app.dto.QueryMessageDetailListDto;
import com.nut.driver.app.form.QueryMessageDetailListForm;
import com.nut.driver.app.form.QueryMessageForm;

import java.util.List;

/**
 * @Description: 消息服务
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service
 * @Author: yzl
 * @CreateTime: 2021-06-27 11:21
 * @Version: 1.0
 */

public interface MessageService {
    List queryMessageList(QueryMessageForm form);
    PagingInfo<QueryMessageDetailListDto> queryMessageDetailList(QueryMessageDetailListForm form);

    /**
     * @Author liuBing
     * @Description //TODO 修改未读消息为已读
     * @Date 10:46 2021/7/24
     * @Param [list]
     * @return void
     **/
    void updateUnread(QueryMessageDetailListForm form);
}
