package com.nut.servicestation.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.RepairRecordDetailForm;
import com.nut.servicestation.app.form.RepairRecordSaveForm;
import com.nut.servicestation.app.form.RepairRecordsForm;
import com.nut.servicestation.app.form.TimesRescueForm;
import org.springframework.transaction.annotation.Transactional;

/*
 *  @author wuhaotian 2021/7/2
 */
public interface RepairService {


    /**
     * @param command command
     * @Description: 维修项列表查询
     * @method: repairRecords
     * @Date: 2018/5/7 19:21
     * @authur: jiangcm
     */
    HttpCommandResultWithData repairRecords(RepairRecordsForm command);
    /**
     * @param command command
     * @Description: 维修项提交
     * @method: repairRecordSave
     * @Date: 2018/5/7 19:21
     * @authur: jiangcm
     */
    HttpCommandResultWithData repairRecordSave(RepairRecordSaveForm command) throws JsonProcessingException;
    /**
     * @param command command
     * @Description: 维修项详情查询
     * @method: repairRecordDetail
     * @Date: 2018/5/7 19:21
     * @authur: jiangcm
     */
    HttpCommandResultWithData repairRecordDetail(RepairRecordDetailForm command);
    /**
     * @param command
     * @Description: 二次外出
     * @method: timesRescue
     * @Date: 2018/10/15 16:17
     * @authur: jiangcm
     */

    HttpCommandResultWithData timesRescue(TimesRescueForm command) throws JsonProcessingException;
}
