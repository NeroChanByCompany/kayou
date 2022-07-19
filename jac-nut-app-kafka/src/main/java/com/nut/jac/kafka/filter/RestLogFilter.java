package com.nut.jac.kafka.filter;


import com.nut.jac.kafka.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @description: Rest请求日志Filter
 * @author: hcb
 * @createTime: 2021/01/20 10:56
 * @version:1.0
 */
@WebFilter(filterName = "restLogFilter", urlPatterns = {"/*"})
@Component
@Order(5)
@Slf4j
public class RestLogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 日志输出请求概要信息
        this.logReq(servletRequest);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * 日志输出请求概要信息
     **/
    private void logReq(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String ip = this.getHttpClientIp(request);
        String method = request.getMethod();
        String url = request.getRequestURI();
        log.info("收到[来源IP={}][{}]请求：{}", ip, method, url);
    }

    /**
     * 获得Http客户端的ip
     *
     * @param req
     * @return
     */
    private String getHttpClientIp(HttpServletRequest req) {
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
        return ip;
    }
}