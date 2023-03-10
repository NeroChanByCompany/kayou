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
     * ???????????????????????????????????????
     */
    @Value("${workOrder.offline.station:none}")
    private String stationCodesOfflineWoEn;

    @Autowired
    private UserDao userServiceMapper;

    @Resource
    UserListenerLogService logService;

    /**
     * ?????????????????????????????????
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
//            ExceptionUtil.result(ECode.PARAM_FAIL.code(), "??????????????????????????????????????????????????????");
//        }
        // ??????????????????
        // JOB_TYPE???1=?????????2=?????????
        // ACCOUNT_TYPE???1=??????????????????2=?????????3=????????????4=???????????????5=?????????
        List<UserInfoDto> dtoList = userDao.queryLoginInfoSql(DatabaseName, form.getUserName());
        if (dtoList != null && !dtoList.isEmpty()) {
            // ????????????
            dto = dtoList.get(0);
            if (dto.getStationEnable() == 0) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "????????????????????????????????????????????????");
            }
            if (StringUtil.isEq(dto.getLockAccount(), LockStatusEnum.SHUT_DOWN.getCode())) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "?????????????????????????????????????????????");
            }
            if (StringUtil.isNotEq(dto.getAccountPwd(), form.getPassword())) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "????????????????????????");
            }
            // ??????
            if (dto.getRoleCode() != ServiceStationVal.JOB_TYPE_ADMIN) {
                // ?????????
                dto.setRoleCode(ServiceStationVal.JOB_TYPE_SALESMAN);
            }
            // ???????????????
            dto.setServiceStationLon(LonLatUtil.convertLonLat(dto.getServiceStationLon()));
            dto.setServiceStationLat(LonLatUtil.convertLonLat(dto.getServiceStationLat()));

            // ????????????????????????????????????
            dto.setStationWoCreatable(getStationWoCreateAuth(dto.getServiceCode()) ? 1 : 0);

            // ???????????????????????????
            dto.setOfflineWoEnable(getOfflineWoEnableAuth(dto.getServiceCode()) ? 1 : 0);

            // ?????????????????? ????????????3
            dto.setSecondaryCreateWoRange(getSecondaryCreateWoRange(dto.getServiceCode()));
            //??????????????????key
            log.info("??????????????????key????????????={},key={}", form.getUserName(), form.getSendMessageKey());
            userServiceMapper.updateUserInfoByAccountName(DatabaseName, null, null, form.getUserName(), form.getSendMessageKey());
            // json????????????
            String json = JsonUtil.toJson(dto);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", dto.getAccountId());
            map.put("phone",form.getUserName());
            map.put("json", json);
            // ??????token
            String token = tokenComponent.generateToken(map);
            dto.setToken(token);
        } else {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "????????????????????????");
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
            log.info("????????????????????????,????????????:{}", e.getMessage());
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
        // ??????????????????
        List<UserInfoDto> dtoList = userDao.queryLoginInfoSql(DatabaseName, form.getUserId());
        log.info("[getUserInfoByUserId]dtoList.size:{}", dtoList == null ? 0 : dtoList.size());
        if (dtoList != null && !dtoList.isEmpty()) {
            UserInfoDto dto = dtoList.get(0);
            if (StringUtil.isEq(form.getOldPassword(), dto.getAccountPwd())) {
                userDao.updatePwdByAccountName(DatabaseName, form.getNewPassword(), form.getUserId());
            } else {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "??????????????????");
            }
            return dto;
        } else {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "??????????????????");
        }
        log.info("[updatePassword]end");
        return new UserInfoDto();
    }

    @Override
    @Transactional
    public UserInfoDto updateUserInfo(UpdateUserInfoForm form) {
        log.info("[updateUserInfo]start");
        if (StringUtil.isEmpty(form.getAccountName()) && StringUtil.isEmpty(form.getPhone())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "???????????????????????????????????????");
        }
        // ??????????????????
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
            log.info("????????????????????????,????????????:{}", e.getMessage());
        }
    }


    /**
     * ??????userId??????????????????
     *
     * @param userId        userId
     * @param needCheckAuth ??????????????????????????????
     * @return UserInfoDto
     */
    @Override
    public UserInfoDto getUserInfoByUserId(String userId, boolean needCheckAuth) {
        List<UserInfoDto> dtoList = userDao.queryLoginInfoSql(DatabaseName, userId);
        log.info("[getUserInfoByUserId]dtoList.size:{}", dtoList == null ? 0 : dtoList.size());
        if (dtoList != null && !dtoList.isEmpty()) {
            UserInfoDto dto = dtoList.get(0);
            // ??????
            if (dto.getRoleCode() != ServiceStationVal.JOB_TYPE_ADMIN) {
                // ?????????
                dto.setRoleCode(ServiceStationVal.JOB_TYPE_SALESMAN);
            }
            // ???????????????
            dto.setServiceStationLon(LonLatUtil.convertLonLat(dto.getServiceStationLon()));
            dto.setServiceStationLat(LonLatUtil.convertLonLat(dto.getServiceStationLat()));

            if (needCheckAuth) {
                // ????????????????????????????????????
                dto.setStationWoCreatable(getStationWoCreateAuth(dto.getServiceCode()) ? 1 : 0);
                // ???????????????????????????
                dto.setOfflineWoEnable(getOfflineWoEnableAuth(dto.getServiceCode()) ? 1 : 0);
                // ?????????????????? ????????????3
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
     * ????????????????????????????????????
     */
    private boolean getStationWoCreateAuth(String stationCode) {
        log.info("[getStationWoCreateAuth]stationCode:{}||uptimeSwitch:{}", stationCode, uptimeSwitch);
        return true;
        /*if (uptimeSwitch) {
            // ??????crm webservice????????????
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
            // ???????????????????????????????????????????????????????????????
            return STATION_ALL_ENABLE.equals(stationCodes) || Arrays.asList(stationCodes.split(",")).contains(stationCode);
        }*/
    }

    /**
     * ???????????????????????????
     */
    private boolean getOfflineWoEnableAuth(String stationCode) {
        log.info("[getOfflineWoEnableAuth]stationCode:{}||config:{}", stationCode, stationCodesOfflineWoEn);
        // ?????????????????????
        return STATION_ALL_ENABLE.equals(stationCodes) || Arrays.asList(stationCodes.split(",")).contains(stationCode);
    }

    /**
     * ???????????????????????????
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
     * ?????????????????????accountId
     * @param accountId
     * @return
     */
    @Override
    public String queryUserNameById(Long accountId) {
        return userDao.queryUserNameById(DatabaseName,accountId);
    }



}
