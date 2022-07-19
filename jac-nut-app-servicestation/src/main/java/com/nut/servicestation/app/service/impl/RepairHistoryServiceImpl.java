package com.nut.servicestation.app.service.impl;

import com.github.pagehelper.Page;
import com.nut.common.constant.OperateCodeEnum;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.dao.WorkOrderOperateDao;
import com.nut.servicestation.app.dto.RepairHistoryDto;
import com.nut.servicestation.app.form.RepairHistoryForm;
import com.nut.servicestation.app.service.RepairHistoryService;
import com.nut.servicestation.common.constants.ChargeTypeEnum;
import com.nut.servicestation.common.constants.DealTypeEnum;
import com.nut.servicestation.common.constants.PayTypeEnum;
import com.nut.servicestation.common.constants.ServiceTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/5
 */
@Slf4j
@Service("RepairHistoryService")
public class RepairHistoryServiceImpl extends ServiceStationBaseService implements RepairHistoryService {



    @Autowired
    private WorkOrderOperateDao workOrderOperateMapper;



    @Override
    public HttpCommandResultWithData queryRepairHistory(RepairHistoryForm command) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        Map<String, Object> param = new HashMap<>(3);
        param.put("chassisNum", command.getChassisNum());
        //操作状态为维修状态
        param.put("operateCode", OperateCodeEnum.OP_REPAIR.code());
        param.put("woStatus", ServiceStationEnum.WORK_DONE.code());
        //分页查询
        getPage(command, true);
        Page<RepairHistoryDto> page = workOrderOperateMapper.queryRepairHistory(param);
        PagingInfo<RepairHistoryDto> pagingInfo = convertPagingToPage(page);
        translate(pagingInfo);
        result.setData(pagingInfo);

        return result;
    }
    /**
     * 将编码值转换为汉字
     */
    private void translate(PagingInfo<RepairHistoryDto> pagingInfo) {
        for (RepairHistoryDto dto : pagingInfo.getList()) {
            if (StringUtil.isNotEmpty(dto.getServiceType())) {
                try {
                    dto.setServiceType(ServiceTypeEnum.getMessage(Integer.parseInt(dto.getServiceType())));
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
            if (StringUtil.isNotEmpty(dto.getChargeType())) {
                try {
                    dto.setChargeType(ChargeTypeEnum.getMessage(Integer.parseInt(dto.getChargeType())));
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
            if (StringUtil.isNotEmpty(dto.getDealType())) {
                try {
                    dto.setDealType(DealTypeEnum.getMessage(Integer.parseInt(dto.getDealType())));
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
            if (StringUtil.isNotEmpty(dto.getPayType())) {
                try {
                    dto.setPayType(PayTypeEnum.getMessage(Integer.parseInt(dto.getPayType())));
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
        }
    }
}
