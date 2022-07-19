package com.nut.servicestation.app.service.impl;

import com.nut.common.constant.ServiceStationVal;
import com.nut.common.enums.AppTypeEnum;
import com.nut.common.enums.LoginTypeEnum;
import com.nut.common.enums.UserListenerEnum;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.LonLatUtil;
import com.nut.common.utils.RedisComponent;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.dao.UserDao;
import com.nut.servicestation.app.dao.WorkOrderDao;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.entity.UserListenerLogEntity;
import com.nut.servicestation.app.enums.LockStatusEnum;
import com.nut.servicestation.app.form.*;
import com.nut.servicestation.app.service.UserListenerLogService;
import com.nut.servicestation.app.service.UserService;
import com.nut.servicestation.common.component.TokenComponent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author liuBing
 * @Classname UserServiceImpl
 * @Description TODO
 * @Date 2021/6/28 15:47
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, readOnly = true)
public class UserServiceImpl implements UserService {

    private static final String STATION_ALL_ENABLE = "all";
    @Autowired
    private WorkOrderDao workOrderDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TokenComponent tokenComponent;

    @Value("${DatabaseName:jac_tsp_dev}")
    private String DatabaseName;

    @Value("${workOrder.initiative.station:none}")
    private String stationCodes;

    /**
     * 指定有离线工单权限的服务站
     */
    @Value("${workOrder.offline.station:none}")
    private String stationCodesOfflineWoEn;

    @Autowired
    private UserDao userServiceMapper;

    @Resource
    UserListenerLogService logService;

    /**
     * 对接完好率中心接口开关
     */
    @Value("${syncUptimeSwitch:false}")
    private boolean uptimeSwitch;


    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoDto login(LoginForm form) {
        log.info("[login]start");
//        int order140Count = workOrderDao.queryOutOrderNum(form.getDeviceId(), form.getUserName());
        UserInfoDto dto = new UserInfoDto();
//        if (order140Count > 0) {
//            ExceptionUtil.result(ECode.PARAM_FAIL.code(), "该账号有未完成的工单，不允许切换手机");
//        }
        // 查询用户信息
        // JOB_TYPE：1=经理；2=业务员
        // ACCOUNT_TYPE：1=系统管理员；2=车厂；3=服务站；4=运输公司；5=经销商
        List<UserInfoDto> dtoList = userDao.queryLoginInfoSql(DatabaseName, form.getUserName());
        if (dtoList != null && !dtoList.isEmpty()) {
            // 用户信息
            dto = dtoList.get(0);
            if (dto.getStationEnable() == 0) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "您的服务站已被停用，请联系管理员");
            }
            if (StringUtil.isEq(dto.getLockAccount(), LockStatusEnum.SHUT_DOWN.getCode())) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "您的账号已被停用，请联系管理员");
            }
            if (StringUtil.isNotEq(dto.getAccountPwd(), form.getPassword())) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "用户名或密码错误");
            }
            // 角色
            if (dto.getRoleCode() != ServiceStationVal.JOB_TYPE_ADMIN) {
                // 业务员
                dto.setRoleCode(ServiceStationVal.JOB_TYPE_SALESMAN);
            }
            // 经纬度转换
            dto.setServiceStationLon(LonLatUtil.convertLonLat(dto.getServiceStationLon()));
            dto.setServiceStationLat(LonLatUtil.convertLonLat(dto.getServiceStationLat()));

            // 服务站是否有自主建单权限
            dto.setStationWoCreatable(getStationWoCreateAuth(dto.getServiceCode()) ? 1 : 0);

            // 是否有离线工单权限
            dto.setOfflineWoEnable(getOfflineWoEnableAuth(dto.getServiceCode()) ? 1 : 0);

            // 二网建单范围 一网默认3
            dto.setSecondaryCreateWoRange(getSecondaryCreateWoRange(dto.getServiceCode()));
            //更新推送消息key
            log.info("更新推送消息key，用户名={},key={}", form.getUserName(), form.getSendMessageKey());
            userServiceMapper.updateUserInfoByAccountName(DatabaseName, null, null, form.getUserName(), form.getSendMessageKey());
            // json格式转换
            String json = JsonUtil.toJson(dto);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", dto.getAccountId());
            map.put("phone",form.getUserName());
            map.put("json", json);
            // 生成token
            String token = tokenComponent.generateToken(map);
            dto.setToken(token);
        } else {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "用户名或密码错误");
        }

        try {
            UserListenerLogEntity entity = new UserListenerLogEntity()
                    .setToken(dto.getToken())
                    .setVersion(form.getVersion())
                    .setAction(UserListenerEnum.LOGIN.getCode())
                    .setAppType(AppTypeEnum.SERVICE.getCode())
                    .setUcId(dto.getAccountId())
                    .setLoginType(LoginTypeEnum.PASSWORD.getCode())
                    .setVersionType(Integer.parseInt(StringUtils.isBlank(form.getVersionType()) ? "1" : form.getVersionType()));
            logService.saveActionListener(entity);
        } catch (Exception e) {
            log.info("登录日志存储异常,异常原因:{}", e.getMessage());
        }
        return dto;
    }

    @Override
    public UserInfoDto getUserInfo(GetUserInfoForm form) {
        return getUserInfoByUserId(form.getUserId(), true);
    }

    @Override
    @Transactional
    public UserInfoDto updatePassword(UpdatePasswordForm form) {
        log.info("[updatePassword]start");
        // 查询用户信息
        List<UserInfoDto> dtoList = userDao.queryLoginInfoSql(DatabaseName, form.getUserId());
        log.info("[getUserInfoByUserId]dtoList.size:{}", dtoList == null ? 0 : dtoList.size());
        if (dtoList != null && !dtoList.isEmpty()) {
            UserInfoDto dto = dtoList.get(0);
            if (StringUtil.isEq(form.getOldPassword(), dto.getAccountPwd())) {
                userDao.updatePwdByAccountName(DatabaseName, form.getNewPassword(), form.getUserId());
            } else {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "旧密码不正确");
            }
            return dto;
        } else {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "此用户不存在");
        }
        log.info("[updatePassword]end");
        return new UserInfoDto();
    }

    @Override
    @Transactional
    public UserInfoDto updateUserInfo(UpdateUserInfoForm form) {
        log.info("[updateUserInfo]start");
        if (StringUtil.isEmpty(form.getAccountName()) && StringUtil.isEmpty(form.getPhone())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "姓名和联系方式至少修改一个");
        }
        // 更新用户信息
        userDao.updateUserInfoByAccountName(DatabaseName, form.getAccountName(), form.getPhone(), form.getUserId(), null);
        List<UserInfoDto> userInfoDtos = userDao.queryLoginInfoSql(DatabaseName, form.getUserId());
        UserInfoDto userInfoDto = new UserInfoDto();
        if (!userInfoDtos.isEmpty()) {
            userInfoDto = userInfoDtos.get(0);
        }
        return userInfoDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(LogoutForm form) {
        UserInfoDto userInfo = userDao.queryUserInfo(DatabaseName, form.getUserId());
        if (userInfo != null) {
          userDao.updateById(DatabaseName,userInfo.getAccountId());
        }
        tokenComponent.destroyToken(form.getToken());
        try {
            UserListenerLogEntity entity = new UserListenerLogEntity()
                    .setToken(form.getToken())
                    .setVersion(form.getVersion())
                    .setAction(UserListenerEnum.LOGOUT.getCode())
                    .setAppType(AppTypeEnum.SERVICE.getCode())
                    .setUcId(form.getUserId())
                    .setVersionType(Integer.parseInt(StringUtils.isBlank(form.getVersionType()) ? "1" : form.getVersionType()));
            logService.saveActionListener(entity);
        } catch (Exception e) {
            log.info("登录日志存储异常,异常原因:{}", e.getMessage());
        }
    }


    /**
     * 根据userId获取用户信息
     *
     * @param userId        userId
     * @param needCheckAuth 是否需要查询建单权限
     * @return UserInfoDto
     */
    @Override
    public UserInfoDto getUserInfoByUserId(String userId, boolean needCheckAuth) {
        List<UserInfoDto> dtoList = userDao.queryLoginInfoSql(DatabaseName, userId);
        log.info("[getUserInfoByUserId]dtoList.size:{}", dtoList == null ? 0 : dtoList.size());
        if (dtoList != null && !dtoList.isEmpty()) {
            UserInfoDto dto = dtoList.get(0);
            // 角色
            if (dto.getRoleCode() != ServiceStationVal.JOB_TYPE_ADMIN) {
                // 业务员
                dto.setRoleCode(ServiceStationVal.JOB_TYPE_SALESMAN);
            }
            // 经纬度转换
            dto.setServiceStationLon(LonLatUtil.convertLonLat(dto.getServiceStationLon()));
            dto.setServiceStationLat(LonLatUtil.convertLonLat(dto.getServiceStationLat()));

            if (needCheckAuth) {
                // 服务站是否有自主建单权限
                dto.setStationWoCreatable(getStationWoCreateAuth(dto.getServiceCode()) ? 1 : 0);
                // 是否有离线工单权限
                dto.setOfflineWoEnable(getOfflineWoEnableAuth(dto.getServiceCode()) ? 1 : 0);
                // 二网建单范围 一网默认3
                dto.setSecondaryCreateWoRange(getSecondaryCreateWoRange(dto.getServiceCode()));
            }
            return dto;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancellation(LogoutForm form) {
        userDao.updateLockByAccountName(DatabaseName,form.getUserId(),LockStatusEnum.SHUT_DOWN.getCode());
        tokenComponent.destroyToken(form.getToken());
    }


    /**
     * 服务站是否有自主建单权限
     */
    private boolean getStationWoCreateAuth(String stationCode) {
        log.info("[getStationWoCreateAuth]stationCode:{}||uptimeSwitch:{}", stationCode, uptimeSwitch);
        return true;
        /*if (uptimeSwitch) {
            // 调用crm webservice查询权限
//            String responseOk = "0";
//            String authOk = "1";
//            DFLIntactRateAPPInformationServiceStub.WhetherBuildNum param = new DFLIntactRateAPPInformationServiceStub.WhetherBuildNum();
//            param.setIdeNum(stationCode);
//            try {
//                logger.info("[getStationWoCreateAuth]web service start");
//                DFLIntactRateAPPInformationServiceStub.WhetherBuildNumResponse response = webServiceClient.whetherBuildNum(param);
//                logger.info("[getStationWoCreateAuth]web service end||status:{}||message:{}||flag:{}",
//                        response.getStatus(), response.getMsg(), response.getUsefulFalg());
//                return responseOk.equals(response.getStatus()) && authOk.equals(response.getUsefulFalg());
//            } catch (RemoteException | NullPointerException e) {
//                logger.error(e.getMessage(), e);
//            }
            return true;
        } else {
            logger.info("[getStationWoCreateAuth]stationCodes:{}", stationCodes);
            // 不与完好率中心接口对接时，可手动指定服务站
            return STATION_ALL_ENABLE.equals(stationCodes) || Arrays.asList(stationCodes.split(",")).contains(stationCode);
        }*/
    }

    /**
     * 是否有离线工单权限
     */
    private boolean getOfflineWoEnableAuth(String stationCode) {
        log.info("[getOfflineWoEnableAuth]stationCode:{}||config:{}", stationCode, stationCodesOfflineWoEn);
        // 手动指定服务站
        return STATION_ALL_ENABLE.equals(stationCodes) || Arrays.asList(stationCodes.split(",")).contains(stationCode);
    }

    /**
     * 二网服务站建单范围
     */
    private Integer getSecondaryCreateWoRange(String stationCode) {
        log.info("[getSecondaryCreateWoRange]stationCode:{}", stationCode);
        Integer re = 3;
        try {
            re = userDao.querySecondaryCreateWoRange(DatabaseName, stationCode);
            if (re == null) {
                re = 3;
            }
        } catch (Exception e) {
            log.error("[getSecondaryCreateWoRange]error", e);
        }
        return re;
    }

    /**
     * 查询账号名根据accountId
     * @param accountId
     * @return
     */
    @Override
    public String queryUserNameById(Long accountId) {
        return userDao.queryUserNameById(DatabaseName,accountId);
    }



}
