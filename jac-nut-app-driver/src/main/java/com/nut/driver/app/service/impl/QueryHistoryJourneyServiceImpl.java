package com.nut.driver.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.Result;
import com.nut.common.utils.HttpUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.driver.app.dao.WorkOrderDao;
import com.nut.driver.app.entity.WorkOrderEntity;
import com.nut.driver.app.form.QueryHistoryJourneyForm;
import com.nut.driver.app.service.QueryHistoryJourneyService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-29 11:13
 * @Version: 1.0
 */
@Service
@Slf4j
public class QueryHistoryJourneyServiceImpl extends DriverBaseService implements QueryHistoryJourneyService {
    @Value("${big_data.dirver_base_url}")
    private String baseUrl;

    @Autowired
    private WorkOrderDao workOrderDao;

    @Override
    @SneakyThrows
    public List QueryHistoryJourney(QueryHistoryJourneyForm form) {
        WorkOrderEntity workOrderEntity = workOrderDao.queryWorkOrderByWoCode(form.getWoCode());
        if (workOrderEntity == null) {
            ExceptionUtil.result(1100, "工单号不存在");
        }
        String url = baseUrl + "/location/track/queryOrder";
//        Map<String, Object> map = new HashMap<>();
//        map.put("terminalId", form.getWoCode());
//        map.put("startTime", workOrder.getCreateTime().getTime());
//        map.put("endTime", DateUtils.addDays(workOrder.getCreateTime(), 30).getTime());
//        map.put("startTime", 1577808000L);
//        map.put("endTime", 4102329600L);
        JSONObject map  =new JSONObject();
        map.put("terminalId", form.getWoCode());
//        map.put("startTime", workOrder.getCreateTime().getTime());
//        map.put("endTime", DateUtils.addDays(workOrder.getCreateTime(), 30).getTime());
        map.put("startTime", 1577808000L);
        map.put("endTime", 4102329600L);
        log.info("请求地址: {}, 参数: {}", url, map.toString());
        String res = HttpUtil.postHttps(url, map);
        log.info("返回结果: {}", res);
        Map<String, Object> resMap = JsonUtil.toMap(res);
        List<Map> points = null;
        if (resMap.containsKey("data")) {
            points = (List<Map>) resMap.get("data");
            for (Map point : points) {
                if (point.containsKey("longitude")) {
                    point.put("longitude", (Integer) point.get("longitude") * 1.0 / 1000000);
                }
                if (point.containsKey("latitude")) {
                    point.put("latitude", (Integer) point.get("latitude") * 1.0 / 1000000);
                }
            }
        }
        return points;
    }
}
