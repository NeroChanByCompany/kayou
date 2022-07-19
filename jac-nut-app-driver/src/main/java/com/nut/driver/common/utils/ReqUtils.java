package com.nut.driver.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.nut.common.utils.ThreadLocalCacheUtils;
import com.nut.driver.common.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: Http请求工具类
 * @author: hcb
 * @createTime: 2021/01/20 10:57
 * @version:1.0
 */
@Slf4j
public class ReqUtils {

    public static String getRequestHeader(String headerKey) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletRequestAttributes) {
            Object value = ThreadLocalCacheUtils.getByKey(headerKey);
            return null == value ? null : value.toString();
        } else {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            return request.getHeader(headerKey);
        }
    }

    /**
     * 获得Http客户端的ip
     *
     * @return
     */
    public static String getHttpClientIp() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletRequestAttributes) {
            return null;
        } else {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            return getHttpClientIp(request);
        }
    }

    /**
     * 获得Http客户端的ip
     *
     * @param req
     * @return
     */
    public static String getHttpClientIp(HttpServletRequest req) {
        String ip = req.getHeader(CommonConstants.HTTP_HEADER_X_FORWARDED_FOR);
        if (ip == null || ip.length() == 0 || CommonConstants.UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = req.getHeader(CommonConstants.HTTP_HEADER_PROXY_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || CommonConstants.UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = req.getHeader(CommonConstants.HTTP_HEADER_WL_PROXY_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || CommonConstants.UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        log.info("getHttpClientIp ip={}",ip);
        if (StringUtils.isBlank(ip)){
            return "127.0.0.1";
        }
        return ip.split(",")[0];
    }

    /**
     * description 获取Post请求Body
     * param [req]
     * return java.lang.String
     * author HuangChaobin
     * createTime 2020/06/02 19:45
     **/
    public static String getPostBody(HttpServletRequest req) {
        String reqBody = null;

        String method = req.getMethod();
        if (!CommonConstants.HTTP_REQUEST_METHOD_POST.equalsIgnoreCase(method)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = req.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(CommonConstants.DEFAULT_CHARACTER_ENCODING)));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if (0 == sb.length()) {
                Map<String, String> bodyMap = new HashMap<>();
                Map<String, String[]> parameterMap = req.getParameterMap();
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    for (String value : entry.getValue()) {
                        bodyMap.put(entry.getKey(), value);
                    }
                }
                reqBody = bodyMap.toString();
            } else {
                reqBody = sb.toString();
            }
        } catch (IOException e) {
            log.error("解析post参数时发生错误：{}", e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        return reqBody;
    }

    /**
     * 请求参数转JSON
     *
     * @param request
     * @return
     */
    public static JSONObject parsePostReq2Json(HttpServletRequest request) {
        JSONObject reqBody = new JSONObject();
        if (CommonConstants.HTTP_REQUEST_METHOD_POST.equalsIgnoreCase(request.getMethod())) {
            String contentType = null;
            Enumeration headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = (String) headerNames.nextElement();
                if ("Content-Type".equalsIgnoreCase(key)) {
                    contentType = request.getHeader(key);
                    break;
                }
            }
            if ("application/x-www-form-urlencoded".equalsIgnoreCase(contentType)) {
                Enumeration em = request.getParameterNames();
                while (em.hasMoreElements()) {
                    String k = em.nextElement().toString();
                    String v = request.getParameter(k);
                    reqBody.put(k, v);
                }
            } else {
                String jsonStr = getPostBody(request);
                reqBody = JSON.parseObject(jsonStr);
            }
        }

        return reqBody;
    }

    /**
     * 请求参数转对象
     *
     * @param request
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parsePostReq2Object(HttpServletRequest request, Class<T> clazz) {
        JSONObject reqBody = parsePostReq2Json(request);
        return JSON.parseObject(reqBody.toJSONString(), clazz);
    }

}
