package com.nut.servicestation.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.ApplyCloseWoForm;
import com.nut.servicestation.app.form.ApplyModifyWoForm;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/*
 *  @author wuhaotian 2021/7/6
 */
public interface ApplyModifyOrCloseWoService {

    /**
     * 申请关闭工单
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData closeWo(ApplyCloseWoForm command) throws IOException;
    /**
     * 申请修改工单
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData modifyWo(ApplyModifyWoForm command) throws JsonProcessingException;
}
