package com.nut.tools.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: 跨域设置
 * @author: hcb
 * @createTime: 2021/01/23 11:20
 * @version:1.0
 */
@WebFilter(filterName = "corsFilter", urlPatterns = {"/*"})
@Component
@Order(3)
@Slf4j
public class CorsFilter implements Filter {

    @Value("${cors.setting.switch}")
    private Boolean corsSettingSwitch;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        // 判断是否需要跨域设置
        if (corsSettingSwitch) {
            // 设置允许跨域请求的域名
            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
            // 设置允许的方法
            httpServletResponse.setHeader("Access-Control-Allow-Methods", "*");
            // 设置允许的header
            httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
            // 是否允许证书 不再默认开启
            httpServletResponse.setHeader("Access-Control-Allow-Credentials", "*");
            // 跨域允许时间
            httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}