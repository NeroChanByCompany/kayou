package com.nut.driver.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.dto.QueryAlarmNoticeListAttributeDto;
import com.nut.driver.app.form.*;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service
 * @Author: yzl
 * @CreateTime: 2021-06-30 10:16
 * @Version: 1.0
 */

public interface NoticeService {
    QueryAlarmNoticeListAttributeDto queryAlarmNoticeList(QueryNoticeListForm form);
    HttpCommandResultWithData queryAlarmNoticeDetailList(QueryAlarmNoticeDetailListForm form);

    HttpCommandResultWithData queryNewAlarmNoticeReceiveSet(QueryAlarmNoticeReceiveSetForm form);
    HttpCommandResultWithData queryNewAlarmNoticeReceiveSetIos(QueryAlarmNoticeReceiveSetForm form);
    HttpCommandResultWithData updateNewReceiveSet(UpdateReceiveSetForm form);

    HttpCommandResultWithData addUnreadMessage(AddUnreadMessageForm form);
}
