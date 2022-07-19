package com.nut.driver.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.driver.app.dto.ViolationDto;
import com.nut.driver.app.form.ViolationForm;
import com.nut.driver.app.service.ThirdPartyService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liuBing
 * @Classname ThirdPartyServiceImpl
 * @Description TODO
 * @Date 2021/8/20 13:21
 */
@Slf4j
@Service
public class ThirdPartyServiceImpl implements ThirdPartyService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${national.traffic.violation.url:http://open.liupai.net/weizhang/query?appkey=89ffb8b4d872549a&type={type}&plateno={plateno}&engineno={engineno}&frameno={frameno}}")
    private String violationUrl;

    @SneakyThrows
    @Override
    public List<ViolationDto> queryViolation(ViolationForm form) {
        Map<String,Object> map = new HashMap<>();
        map.put("type",form.getType());
        map.put("plateno",form.getPlateno());
        map.put("engineno",form.getEngineno());
        map.put("frameno",form.getFrameno());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = restTemplate.postForEntity(violationUrl,request,String.class,map);
        String body = result.getBody();
        if (StringUtils.isNotBlank(body)){
            JSONObject jsonObject = JSONObject.parseObject(body);
            if (!"200".equals(jsonObject.getString("status"))){
                ExceptionUtil.result(ECode.FALLBACK.code(),jsonObject.getString("msg"));
            }
            if (null == jsonObject.get("result")){
                ExceptionUtil.result(ECode.FALLBACK.code(), "暂无当前车辆违章信息");
            }
            List<ViolationDto> dtoList = castList(jsonObject.get("result"), ViolationDto.class);
            //过滤掉不是违章的违章信息
            List<ViolationDto> resultList = dtoList.stream().filter(violationDto -> violationDto.getState().equals(1) || violationDto.getState().equals(2)).collect(Collectors.toList());
            log.info("当前用户:{}违章记录共:{}",form.getUserId(),resultList);
            return resultList;
        }
        log.info("调用第三方接口无返回");
        return new ArrayList<>();
    }

    public  <T> List<T> castList(Object obj, Class<T> clazz)
    {
        List<T> result = new ArrayList<T>();
        if(obj instanceof List<?>)
        {
            for (Object o : (List<?>) obj)
            {
                String jsonString = JSONObject.toJSONString(o);
                T object = JSONObject.parseObject(jsonString, clazz);
                result.add(object);
            }
            return result;
        }
        return null;
    }
}
