package com.nut.driver.app.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.http.HttpUtil;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.client.BigDataClient;
import com.nut.driver.app.dao.CarDao;
import com.nut.driver.app.domain.TemHum;
import com.nut.driver.app.dto.TripCarListDto;
import com.nut.driver.app.entity.ColdCarsEntity;
import com.nut.driver.app.form.ColdCarForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 冷链车相关接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-08-31 16:25
 * @Version: 1.0
 */
@RestController
@Api(tags = "冷链车接口")
@Slf4j
public class ColdCarController {

    @Autowired
    private BigDataClient bigDataClient;

    @Autowired
    private CarDao carDao;

    @PostMapping(value = "/getColdCarInfo")
    @ApiIgnore
    @LoginRequired
    @ApiOperation("获取冷链车信息")
    @ApiImplicitParam(name = "carVin", value = "车辆底盘号", dataType = "String")
    @SneakyThrows
    public HttpCommandResultWithData getColdCarInfo(@RequestBody ColdCarForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        String terminalId = carDao.selectTerminalIdByCarVin(form.getCarVin());
        Map<String, Object> map = new HashMap<>();
        List<String> fields = new ArrayList<>();
        List<Long> terminalIds = new ArrayList<>();
        fields.add("temperatureCount");
        fields.add("temperatureHumidityList");
        fields.add("coldChainMagnetic1");
        fields.add("coldChainMagnetic2");
        fields.add("condenserSwitch");
        map.put("fields", fields);
        if (terminalId.isEmpty()) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("该车无车联网功能");
            return result;
        } else {
            terminalIds.add(Long.valueOf(terminalId));
        }
//        terminalIds.add(17255730481L);
        map.put("terminalIds", terminalIds);
        log.info("map:{}", map);
        JSONObject jsonObject = bigDataClient.queryColdCarInfo(JSONObject.parseObject(JSON.toJSONString(map)));
        log.info("jsonObject:{}", jsonObject);
        if (Objects.isNull(jsonObject)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此车暂无数据");
            return result;
        }
        JSONArray jsonArray0 = jsonObject.getJSONArray("data");
        if (Objects.nonNull(jsonObject.get("resultCode"))
                && jsonObject.get("resultCode").toString().equals("200")
                && jsonObject.get("data") != null
                && jsonArray0.size() != 0) {
            String temperatureHumidityList = jsonObject.getJSONArray("data").getJSONObject(0).getString("temperatureHumidityList");
            String str = StringEscapeUtils.unescapeJavaScript(temperatureHumidityList);
            List<TemHum> list = JSONArray.parseArray(StringEscapeUtils.unescapeJavaScript(str), TemHum.class);
            for (TemHum e : list) {
                e.setTemperature((Double.parseDouble(e.getTemperature()) / 100) + "" + "℃");
                e.setHumidity((Double.parseDouble(e.getHumidity()) / 100) + "" + "%");
            }
            log.info("数据处理完成");
            result.setData(list);
        }
        return result;
    }

    @PostMapping(value = "/getColdCars")
    @ApiIgnore
    @LoginRequired
    @ApiOperation("获取冷链车信息")
    @ApiImplicitParam(name = "carVin", value = "车辆底盘号", dataType = "String")
    @SneakyThrows
    public HttpCommandResultWithData getColdCars(@RequestBody ColdCarForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        // 查询用户车辆及车辆基础信息列表
        List<ColdCarsEntity> cars = carDao.queryTripCarsByCarIds2(form.getAutoIncreaseId());
        if (StringUtil.isNotEmpty(form.getCarVin())) {
            List<ColdCarsEntity> cars2 = new ArrayList<>();
            for (ColdCarsEntity carsEntity : cars) {
                if (carsEntity.getCarNumber().indexOf(form.getCarVin()) != -1) {
                    cars2.add(carsEntity);
                } else if (carsEntity.getVin().indexOf(form.getCarVin()) != -1) {
                    cars2.add(carsEntity);
                }
            }
            result.setData(cars2);
        } else {
            result.setData(cars);
        }
        return result;
    }

    public static void main(String[] args) {
        String str1 = "abcdefg";
        int result1 = str1.indexOf("ab");
        System.out.println(result1);
        int result2 = str1.indexOf("yzl");
        System.out.println(result2);

        if (result1 != -1) {
            System.out.println("字符串str中包含子串“ab”" + result1);
        } else {
            System.out.println("字符串str中不包含子串“ab”" + result1);
        }
    }
}
