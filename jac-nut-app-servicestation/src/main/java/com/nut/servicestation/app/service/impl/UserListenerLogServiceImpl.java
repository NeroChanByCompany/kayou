package com.nut.servicestation.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nut.common.enums.UserListenerEnum;
import com.nut.servicestation.app.dao.UserListenerLogDao;
import com.nut.servicestation.app.entity.UserListenerLogEntity;
import com.nut.servicestation.app.service.UserListenerLogService;
import com.nut.servicestation.common.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

/**
 * @author liuBing
 * @Classname UserListenerLogServiceImpl
 * @Description TODO 用户动作监听实现类
 * @Date 2021/9/13 11:13
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class,readOnly = true)
public class UserListenerLogServiceImpl extends ServiceImpl<UserListenerLogDao,UserListenerLogEntity> implements UserListenerLogService {

    @Resource
    HttpServletRequest request;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveActionListener(UserListenerLogEntity entity) {

        switch (Objects.requireNonNull(UserListenerEnum.getEnum(entity.getAction()))){
            case LOGIN:
                entity.setLoginTime(new Date());
                break;
            case LOGOUT:
                entity.setLogoutTime(new Date());
                break;
            case CANCELLATION:
                entity.setCancellationTime(new Date());
                break;
            default:
                break;
        }
        String ip = getHttpClientIp(request);
        entity.setLoginIp(ip)
              .setCreateTime(new Date())
              .setUpdateTime(new Date());
        save(entity);
    }

    /**
     * 获得Http客户端的ip
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
        return ip.split(",")[0];
    }
}
