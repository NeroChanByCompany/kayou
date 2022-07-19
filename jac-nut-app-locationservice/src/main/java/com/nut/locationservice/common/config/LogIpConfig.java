package com.nut.locationservice.common.config;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @description: 获取本机IP
 * @author: hcb
 * @createTime: 2021/02/18 19:07
 * @version:1.0
 */
public class LogIpConfig extends ClassicConverter {

    private static final Logger log = LoggerFactory.getLogger(LogIpConfig.class);

    private static String webIP;
    static {
        try {
            webIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("获取日志IP异常", e);
            webIP = null;
        }
    }

    @Override
    public String convert(ILoggingEvent event) {
        return webIP;
    }
}