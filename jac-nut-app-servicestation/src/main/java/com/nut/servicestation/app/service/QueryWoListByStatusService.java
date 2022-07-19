package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.servicestation.app.form.QueryWoListByStatusForm;
import com.nut.servicestation.app.form.queryOrderListForm;
import com.nut.servicestation.app.pojo.QueryWoListByStatusPojo;

/*
 *  @author wuhaotian 2021/7/8
 */
public interface QueryWoListByStatusService {

    /**
     * 工单列表查询
     * @param command 接口入参
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData queryWoListByStatus(QueryWoListByStatusForm command);

    /**
     * 抢单列表查询
     * @param command 接口入参
     * @return
     */
    PagingInfo<QueryWoListByStatusPojo> queryOrderList(queryOrderListForm command);
}
