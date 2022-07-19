package com.nut.driver.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.client.TspClient;
import com.nut.driver.app.dto.OilExceptionDownOutDto;
import com.nut.driver.app.dto.OilExceptionUpOutDto;
import com.nut.driver.app.form.OilFaultForm;
import com.nut.driver.common.component.BaiduMapComponent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @Description: 油耗异常查询
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.controller
 * @Author: yzl
 * @CreateTime: 2021-07-23 17:40
 * @Version: 1.0
 */
@RestController
@Api(tags = "油耗异常查询")
@Slf4j
public class OilFaultController {

    @Autowired
    private TspClient tspClient;

    @Autowired
    private BaiduMapComponent baiduMapComponent;

    // 总体油耗记录
    @PostMapping("/queryOilFault")
    @ApiImplicitParams({@ApiImplicitParam(name = "chassisNum", value = "车辆底盘号", dataType = "String"),
            @ApiImplicitParam(name = "dateStr", value = "时间格式", dataType = "String")})
    public HttpCommandResultWithData queryOilFault(@RequestBody OilFaultForm form) {
        Long time = Long.parseLong(form.getDateStr());
        LocalDate localDate = Instant.ofEpochMilli(time).atZone(ZoneOffset.ofHours(8)).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        String chassisNum = form.getChassisNum();
        String dateStr = localDate.format(formatter);
        log.info("调用Tsp接口，请求参数：chassisNum:{}, dateStr:{}", chassisNum, dateStr);
        JSONObject jsonObject = tspClient.queryOilFaultApp(chassisNum, dateStr, 2, 1);
        // TODO: 2021/8/4 逆地理解析
        if (jsonObject.get("resultCode").toString().equals("200")) {
            JSONObject data = jsonObject.getJSONObject("data");
            List<OilExceptionDownOutDto> list0 = (List<OilExceptionDownOutDto>) data.getJSONObject("downDtoList").get("list");
            ObjectMapper mapper = new ObjectMapper();
            List<OilExceptionDownOutDto> list = mapper.convertValue(list0, new TypeReference<List<OilExceptionDownOutDto>>() {
            });
            String total = data.getJSONObject("downDtoList").get("total").toString();
            data.remove("downDtoList");
            if (Integer.parseInt(total) > 2) {
                data.put("moreData", "1");
            } else {
                data.put("moreData", "0");
            }
            for (OilExceptionDownOutDto dto : list) {
                String sLat = dto.getStartPlacelatitude().toString();
                String sLon = dto.getStartPlacelongitude().toString();
                String eLat = dto.getStartPlacelatitude().toString();
                String eLon = dto.getEndPlacelongitude().toString();
                String sLocation = baiduMapComponent.reverseGeocoding(sLat, sLon, "bd09ll").getString("formatted_address");
                String eLocation = baiduMapComponent.reverseGeocoding(eLat, eLon, "bd09ll").getString("formatted_address");
                dto.setStartLocation(sLocation);
                dto.setEndLocation(eLocation);
            }
            data.put("downDtoList", list);
            result.setData(data);
        } else if(jsonObject.get("resultCode").toString().equals("400")){
            result.setResultCode(400);
            result.setMessage("底盘号不正确");
        }else {
            result.setResultCode(507);
            result.setMessage("服务器内部错误");
        }
        return result;
    }

    // 加油记录
    @PostMapping("/queryOilFaultAppUp")
    @ApiImplicitParams({@ApiImplicitParam(name = "chassisNum", value = "车辆底盘号", dataType = "String"),
            @ApiImplicitParam(name = "dateStr", value = "时间格式", dataType = "String"),
            @ApiImplicitParam(name = "page_size", value = "每页最大个数", dataType = "Integer"),
            @ApiImplicitParam(name = "page_number", value = "页码", dataType = "Integer")
    })
    public HttpCommandResultWithData queryOilFaultAppUp(@RequestBody OilFaultForm form) {
        Long time = Long.parseLong(form.getDateStr());
        LocalDate localDate = Instant.ofEpochMilli(time).atZone(ZoneOffset.ofHours(8)).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        String chassisNum = form.getChassisNum();
        String dateStr = localDate.format(formatter);
        Integer pageSize = Integer.parseInt(form.getPage_size());
        Integer pageNumber = Integer.parseInt(form.getPage_number());
        log.info("调用Tsp接口，请求参数：chassisNum:{}, dateStr:{}", chassisNum, dateStr);
        JSONObject jsonObject = tspClient.queryOilExceptionAppUp(chassisNum, dateStr, pageSize, pageNumber);
        if (jsonObject.get("resultCode").toString().equals("200")) {
            JSONObject data = jsonObject.getJSONObject("data");
            List<OilExceptionUpOutDto> list0 = (List<OilExceptionUpOutDto>) data.getJSONObject("upDtoList").get("list");
            ObjectMapper mapper = new ObjectMapper();
            List<OilExceptionUpOutDto> list = mapper.convertValue(list0, new TypeReference<List<OilExceptionUpOutDto>>() {
            });
            String total = data.getJSONObject("upDtoList").get("total").toString();
            data.remove("upDtoList");
            if (Integer.parseInt(total) > 2) {
                data.put("moreData", "1");
            } else {
                data.put("moreData", "0");
            }
            for (OilExceptionUpOutDto dto : list) {
                String sLat = dto.getStartPlacelatitude().toString();
                String sLon = dto.getStartPlacelongitude().toString();
                String eLat = dto.getStartPlacelatitude().toString();
                String eLon = dto.getEndPlacelongitude().toString();
                String sLocation = baiduMapComponent.reverseGeocoding(sLat, sLon, "bd09ll").getString("formatted_address");
                String eLocation = baiduMapComponent.reverseGeocoding(eLat, eLon, "bd09ll").getString("formatted_address");
                dto.setStartLocation(sLocation);
                dto.setEndLocation(eLocation);
            }
            data.put("upDtoList", list);
            result.setData(data);
        } else if(jsonObject.get("resultCode").toString().equals("400")){
            result.setResultCode(400);
            result.setMessage("底盘号不正确");
        }else {
            result.setResultCode(507);
            result.setMessage("服务器内部错误");
        }
        return result;
    }

    // 油耗异常减少记录
    @PostMapping("/queryOilFaultAppDown")
    @ApiImplicitParams({@ApiImplicitParam(name = "chassisNum", value = "车辆底盘号", dataType = "String"),
            @ApiImplicitParam(name = "dateStr", value = "时间格式", dataType = "String"),
            @ApiImplicitParam(name = "page_size", value = "每页最大个数", dataType = "Integer"),
            @ApiImplicitParam(name = "page_number", value = "页码", dataType = "Integer")
    })
    public HttpCommandResultWithData queryOilFaultAppDown(@RequestBody OilFaultForm form) {
        Long time = Long.parseLong(form.getDateStr());
        LocalDate localDate = Instant.ofEpochMilli(time).atZone(ZoneOffset.ofHours(8)).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        String chassisNum = form.getChassisNum();
        String dateStr = localDate.format(formatter);
        Integer pageSize = Integer.parseInt(form.getPage_size());
        Integer pageNumber = Integer.parseInt(form.getPage_number());
        log.info("调用Tsp接口，请求参数：chassisNum:{}, dateStr:{}", chassisNum, dateStr);
        JSONObject jsonObject = tspClient.queryOilFaultAppDown(chassisNum, dateStr, pageSize, pageNumber);
        if (jsonObject.get("resultCode").toString().equals("200")) {
            JSONObject data = jsonObject.getJSONObject("data");
            List<OilExceptionDownOutDto> list0 = (List<OilExceptionDownOutDto>) data.getJSONObject("downDtoList").get("list");
            ObjectMapper mapper = new ObjectMapper();
            List<OilExceptionDownOutDto> list = mapper.convertValue(list0, new TypeReference<List<OilExceptionDownOutDto>>() {
            });
            data.remove("downDtoList");
            for (OilExceptionDownOutDto dto : list) {
                String sLat = dto.getStartPlacelatitude().toString();
                String sLon = dto.getStartPlacelongitude().toString();
                String eLat = dto.getStartPlacelatitude().toString();
                String eLon = dto.getEndPlacelongitude().toString();
                String sLocation = baiduMapComponent.reverseGeocoding(sLat, sLon, "bd09ll").getString("formatted_address");
                String eLocation = baiduMapComponent.reverseGeocoding(eLat, eLon, "bd09ll").getString("formatted_address");
                dto.setStartLocation(sLocation);
                dto.setEndLocation(eLocation);
            }
            data.put("downDtoList", list);
            result.setData(data);
        } else if(jsonObject.get("resultCode").toString().equals("400")){
            result.setResultCode(400);
            result.setMessage("底盘号不正确");
        }else {
            result.setResultCode(507);
            result.setMessage("服务器内部错误");
        }
        return result;
    }

}
