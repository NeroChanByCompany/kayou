package com.nut.locationservice.app.service.impl;

import com.nut.locationservice.app.dto.GetTotalMilAndOilDto;
import com.nut.locationservice.app.pojo.QueryByDayPojo;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @Description: 查询位置云当日总运营里程、油耗
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-17 13:36
 * @Version: 1.0
 */
@Service
@Slf4j
public class GetTotalMilAndOilServiceImpl {

    /**
     * 获取位置云车辆状态接口地址
     */
    @Value("${local.cloud.getTotalMilAndOilUrl:http://10.30.50.146:32481/location/milcon/queryByDay}")
    private String getTotalMilAndOilUrl;

    /**
     * 当日全量车里程油耗
     */
    @SneakyThrows
    public GetTotalMilAndOilDto getTotalMilAndOil() {
        log.info("[getTotalMilAndOil]start");
        GetTotalMilAndOilDto result = new GetTotalMilAndOilDto();
        // 总里程
        Double totalMil = 0.0D;
        // 总油耗
        Double totalOil = 0.0D;

        // 获取当前0点秒级时间戳
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long time = calendar.getTimeInMillis() / 1000;

        // 封装查询条件
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("startTime", time);
        paramMap.put("endTime", time);
        paramMap.put("queryAll", true);
        RestTemplate restTemplate = new RestTemplate();
        HttpCommandResultWithData cloudResult = restTemplate.postForObject(getTotalMilAndOilUrl, paramMap, HttpCommandResultWithData.class);

        if (cloudResult != null && ECode.SUCCESS.code() == cloudResult.getResultCode() && cloudResult.getData() != null) {
            List<QueryByDayPojo> list = JsonUtil.toList(JsonUtil.toJson(cloudResult.getData()), QueryByDayPojo.class);

            if (list != null && list.size() > 0) {
                for (QueryByDayPojo queryByDayPojo : list) {
                    totalMil += queryByDayPojo.getMMilage();
                    totalOil += queryByDayPojo.getFuel();
                }
            }
        }

        result.setTotalMil(totalMil);
        result.setTotalOil(totalOil);

        if (totalMil != 0.0) {
            result.setOilPerHud(totalOil / totalMil * 100);
        } else {
            result.setOilPerHud(0.0D);
        }
        log.info("[getTotalMilAndOil]end");
        return result;
    }
}
