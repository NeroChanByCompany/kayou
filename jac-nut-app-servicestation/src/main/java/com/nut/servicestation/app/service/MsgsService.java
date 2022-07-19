package com.nut.servicestation.app.service;

import com.nut.common.result.PagingInfo;
import com.nut.servicestation.app.dto.MsgsDto;
import com.nut.servicestation.app.form.MsgsForm;

/*
 *  @author wuhaotian 2021/7/8
 */
public interface MsgsService {

    /**
     * @param command command
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @Description: 消息列表
     * @method: msgs
     */
    PagingInfo<MsgsDto> msgs(MsgsForm command) throws Exception;
}
