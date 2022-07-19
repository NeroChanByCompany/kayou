package com.nut.driver.app.task;

import com.nut.driver.app.dao.WorkOrderDao;
import com.nut.driver.app.domain.WorkOrder;
import com.nut.driver.app.form.ReviewOrderForm;
import com.nut.driver.app.service.EvaluateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@Component
@Slf4j
public class ScheduledTask {

    @Autowired
    private WorkOrderDao workOrderMapper;

    @Autowired
    private EvaluateService evaluateService;

    @Value("${workorder.evaluate.timeout:7200000}")
    private long timeout;

//    @Scheduled(fixedRate = 60000)
    public void scheduledTask() {
        List<WorkOrder> woLst = workOrderMapper.queryWorkOrderByStatus("240");

        List<Long> idLst = new ArrayList<>();
        long now = new Date().getTime();
        for (WorkOrder wo : woLst) {
            long endRepairTime = wo.getUpdateTime().getTime();
            if (now - endRepairTime > timeout) {
                ReviewOrderForm form = new ReviewOrderForm();
                form.setWoCode(wo.getWoCode());
                form.setStationId(wo.getStationId());
                form.setWholeStar("5");//系统默认评价调整为5分，调整时间：2021年1月26日13:04:21
                try {
                    evaluateService.reviewOrder(form);
                } catch (Exception e) {
                    log.error(">>>>>>>>>>>>>>>超时自动评价失败<<<<<<<<<<<<<");
                }
            }
        }

    }
}
