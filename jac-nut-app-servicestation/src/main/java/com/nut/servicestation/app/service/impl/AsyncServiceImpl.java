package com.nut.servicestation.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.servicestation.app.dto.OrderReportDetailDto;
import com.nut.servicestation.app.dto.OrderReportDto;
import com.nut.servicestation.app.entity.WorkOrderLogEntity;
import com.nut.servicestation.app.enums.WorkOrderLogEnum;
import com.nut.servicestation.app.service.AsyncService;
import com.nut.servicestation.app.service.WorkOrderLogService;
import com.nut.servicestation.common.wrapper.GetClwRoInfoStub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

/**
 * @author liuBing
 * @Classname AsyncServiceImpl
 * @Description TODO 异步调用
 * @Date 2021/8/16 14:09
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class,readOnly = true)
public class AsyncServiceImpl implements AsyncService {

    @Autowired
    WorkOrderLogService workOrderLogService;

    @Autowired
    GetClwRoInfoStub getClwRoInfoStub;
    /**
     * 异步推送工单 记录到日志表中
     *
     * @param orderReportDetailDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData pushTicket(OrderReportDetailDto orderReportDetailDto, JSONArray paramArray, OrderReportDto orderReportDto) {
        String pas = paramArray.toString();
        log.info("开始推送工单图片集：{}",orderReportDto.getCheckImgList());
        log.info("开始推送工单：{}, 照片发送数量{}, 发送字节数: {}", JSON.toJSONString(orderReportDetailDto), orderReportDto.getCheckImgList().size(), pas.getBytes().length);
        QueryWrapper<WorkOrderLogEntity> wrapper = new QueryWrapper<WorkOrderLogEntity>().eq("wo_code", orderReportDetailDto.getAppRoNo()).orderBy(true,false,"update_time");
        wrapper.last("limit 1");
        WorkOrderLogEntity workOrderLog = workOrderLogService.getOne(wrapper);
        try {
            GetClwRoInfoStub.NewOperation newOperation = new GetClwRoInfoStub.NewOperation();
            newOperation.setIn(paramArray.toString());
            GetClwRoInfoStub.NewOperationResponse newOperationResponse = getClwRoInfoStub.newOperation(newOperation);
            log.info("工单推送报文:{}", JSONObject.toJSONString(newOperation));
            if (Objects.nonNull(newOperationResponse)) {
                log.info("工单推送完成，返回结果={}", newOperationResponse.getOut());
                if (newOperationResponse.getOut().contains("106") || newOperationResponse.getOut().contains("105")) {
                    log.info("工单{}推送成功，返回结果{}",orderReportDetailDto.getAppRoNo(), newOperationResponse.getOut());
                    //保存到日志表
                    if (workOrderLog != null){
                        workOrderLogService.updateById(workOrderLog.setUpdateTime(new Date()).setStatus(WorkOrderLogEnum.NORMAL.getCode()).setMessage(newOperationResponse.getOut()));
                    }else {
                        workOrderLogService.saveOrUpdate(new WorkOrderLogEntity().setWoCode(orderReportDetailDto.getAppRoNo()).setStatus(WorkOrderLogEnum.CANCELLATION.getCode()).setMessage(newOperationResponse.getOut()).setCreateTime(new Date()).setUpdateTime(new Date()),new UpdateWrapper<WorkOrderLogEntity>().eq("wo_code",orderReportDetailDto.getAppRoNo()));
                    }
                    return Result.ok();
                } else {
                    log.info("工单{}推送失败，返回结果{}", orderReportDetailDto.getAppRoNo(), newOperationResponse.getOut());
                    //保存到日志表
                    if (workOrderLog != null){
                        workOrderLogService.updateById(workOrderLog.setUpdateTime(new Date()).setStatus(WorkOrderLogEnum.CANCELLATION.getCode()).setMessage(newOperationResponse.getOut()));
                    }else {
                        workOrderLogService.saveOrUpdate(new WorkOrderLogEntity().setWoCode(orderReportDetailDto.getAppRoNo()).setStatus(WorkOrderLogEnum.CANCELLATION.getCode()).setMessage(newOperationResponse.getOut()).setCreateTime(new Date()).setUpdateTime(new Date()),new UpdateWrapper<WorkOrderLogEntity>().eq("wo_code",orderReportDetailDto.getAppRoNo()));
                    }
                    return Result.result(ECode.CLIENT_ERROR.code(), newOperationResponse.getOut());
                }

            }
        } catch (Exception e) {
            log.error("上报工单出现异常 error ={}", e);
            //保存到日志表
            if (workOrderLog != null){
                workOrderLogService.updateById(workOrderLog.setUpdateTime(new Date()).setStatus(WorkOrderLogEnum.CANCELLATION.getCode()).setMessage(e.getMessage()));
            }else {
                workOrderLogService.saveOrUpdate(new WorkOrderLogEntity().setWoCode(orderReportDetailDto.getAppRoNo()).setStatus(WorkOrderLogEnum.CANCELLATION.getCode()).setMessage(orderReportDetailDto.getAppRoNo()).setCreateTime(new Date()).setUpdateTime(new Date()),new UpdateWrapper<WorkOrderLogEntity>().eq("wo_code",orderReportDetailDto.getAppRoNo()));
            }
            log.info("工单上报异常");
            return Result.result(ECode.CLIENT_ERROR.code(), e.getMessage());
        }

        return Result.ok();
    }

}
