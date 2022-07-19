package com.jac.app.job.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.dozermapper.core.Mapper;
import com.jac.app.job.client.ToolsClient;
import com.jac.app.job.common.Result;
import com.jac.app.job.constant.IxinPushStaticLocarVal;
import com.jac.app.job.entity.MaintainSendEntity;
import com.jac.app.job.entity.WorkOrderEntity;
import com.jac.app.job.entity.WorkOrderLogEntity;
import com.jac.app.job.enums.WorkOrderLogEnum;
import com.jac.app.job.service.CustomMaintainService;
import com.jac.app.job.service.WorkOrderLogService;
import com.jac.app.job.service.WorkOrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * @author liuBing
 * @Classname XxlJobTask
 * @Description TODO 定时任务配置中心
 * @Date 2021/8/9 14:37
 */

@Component
public class SimpleXxlJob {

    private static Logger logger = LoggerFactory.getLogger(SimpleXxlJob.class);
    @Autowired
    WorkOrderLogService workOrderLogService;
    @Autowired
    WorkOrderService workOrderService;
    @Autowired
    Mapper convert;
    @Autowired
    CustomMaintainService customMaintainService;

    @Autowired
    ToolsClient toolsClient;

    /**
     * 定时推送失败工单
     * TODO，通过XXL-JOB手动执行指定工单号进行推送
     * TODO，日志输出需要推送的记录条数
     * TODO，日志输出本次定时任务工单的执行结果，如，工单号1:成功，工单2：失败
     */
    @XxlJob("pushTicketJob")
    public ReturnT<String> pushTicketJob(String param) throws Exception {
        MDC.put("traceId", UUID.randomUUID().toString().replaceAll("-", ""));
        logger.info("====================定时推送失败工单 start=======================");
        QueryWrapper<WorkOrderLogEntity> wrapper = new QueryWrapper<WorkOrderLogEntity>().eq("status", WorkOrderLogEnum.CANCELLATION.getCode()).orderBy(true, false, "update_time").last("limit 10");
        List<WorkOrderLogEntity> list = workOrderLogService.list(wrapper);
        if (!list.isEmpty()){
            for (WorkOrderLogEntity entity : list) {
                //查询失败工单
                WorkOrderEntity order = workOrderService.getOne(new QueryWrapper<WorkOrderEntity>().eq("wo_code", entity.getWoCode()));
                logger.info("查询失败工单：{}",order);
                if (order != null){
                     String woCode = workOrderService.pushWorkOrder(order);
                    logger.info("对接servicestation回调：{}",woCode);
                }
            }
        }
        logger.info("====================定时推送失败工单 end param:{}=======================",list);
        return ReturnT.SUCCESS;
    }



    /**
     * 自定义保养消息推送
     */
    @XxlJob("maintenancePushJob")
    public ReturnT<String> maintenancePushJob(String param) throws Exception {
        logger.info("====================保养定时推送 start=======================");
        //查询所有需要推送消息的列表 用户id 消息推送title+content
        List<MaintainSendEntity> maintains = customMaintainService.selectIsMaintains();
        //判断数据是否为空
        if (!maintains.isEmpty()) {
            for (MaintainSendEntity maintain : maintains) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", maintain.getUserId());
                jsonObject.put("content", maintain.getContent());
                jsonObject.put("title", maintain.getTitle());
                //推送消息
                Result jsonObjectResult = toolsClient.JPushMes(jsonObject);
                logger.info("推送消息回调:{}",jsonObjectResult);
                logger.info("消息推送开始,推送内容{}", maintain);
                //修改状态为已推送
                customMaintainService.updatePushed(maintain.getMaintainInfoId(), maintain.getType());
                //将消息存储到usermessage中
                maintain.setMessageType(Integer.parseInt(IxinPushStaticLocarVal.MESSAGE_TYPE_MAINTAIN));
                maintain.setMessageTypeName(IxinPushStaticLocarVal.MESSAGE_TYPE_NAME_MAINTENANCE);
                customMaintainService.addMessage(maintain);
                logger.info("====================保养定时推送 end=======================");
            }
        }
        return ReturnT.SUCCESS;
    }



}
