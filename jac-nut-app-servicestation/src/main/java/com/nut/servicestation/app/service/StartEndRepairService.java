package com.nut.servicestation.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.EndRepairForm;
import com.nut.servicestation.app.form.StartInspectForm;
import com.nut.servicestation.app.form.StartRepairForm;

import java.io.IOException;

/*
 *  @author wuhaotian 2021/7/2
 */
public interface StartEndRepairService {

    /**
     * 检查结束/提交维修项过程中redis加锁
     *
     * @param woCode    工单号
     * @param timestamp 操作ID
     */
    boolean lock(String woCode, String timestamp);
    /**
     * 开始维修
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData startRepair(StartRepairForm command) throws JsonProcessingException;
    /**
     * 检查结束/提交维修项后redis解锁
     *
     * @param woCode    工单号
     * @param timestamp 操作ID
     */
    boolean unlock(String woCode, String timestamp);
    /**
     * 结束维修
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData endRepair(EndRepairForm command) throws IOException;
    /**
     * 尝试触发报单流程，失败时发送邮件
     */
    void trySendKafka(String woCode, String crmType, String functionStr);
    /**
     * 开始检查
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData startInspect(StartInspectForm command);
}
