package com.nut.driver.app.service.impl;

import com.nut.common.constant.AppTypeEnum;
import com.nut.common.constant.FleetRoleEnum;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.client.ToolsClient;
import com.nut.driver.app.dao.FltFleetDao;
import com.nut.driver.app.dao.UserDao;
import com.nut.driver.app.domain.FltFleet;
import com.nut.driver.app.domain.FltFleetUserMapping;
import com.nut.driver.app.entity.FltFleetEntity;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.form.FleetAdminBindForm;
import com.nut.driver.app.form.FleetAdminUnbindForm;
import com.nut.driver.app.form.SendSmsForm;
import com.nut.driver.app.service.AsyncPushSystemMsgService;
import com.nut.driver.app.service.CommonCustomMaintainService;
import com.nut.driver.app.service.FltFleetUserMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nut.driver.app.dao.FltFleetUserMappingDao;
import com.nut.driver.app.entity.FltFleetUserMappingEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("fltFleetUserMappingService")
@Slf4j
public class FltFleetUserMappingServiceImpl extends ServiceImpl<FltFleetUserMappingDao, FltFleetUserMappingEntity> implements FltFleetUserMappingService {

    @Autowired
    private AsyncPushSystemMsgService asyncPushSystemMsgService;

    @Autowired
    private CommonCustomMaintainService commonCustomMaintainService;

    @Autowired
    private FltFleetUserMappingDao fltFleetUserMappingDao;

    @Autowired
    private FltFleetDao fltFleetDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ToolsClient toolsClient;

    /**
     * ????????????
     */
    @Value("${sms.template.add.admin:???????????????????????????????????????{????????????}???????????????????????????????????????~???????????????{????????????}}")
    private String smsTemplate;

    @Value("${app.download.site:http://}")
    private String downloadSite;

    private static final String SEPARATOR = ",";

    @Override
    @Transactional
    public HttpCommandResultWithData bind(FleetAdminBindForm form) {
        // ???????????????????????????????????????????????????
        List<FltFleetUserMappingEntity> queryList = fltFleetUserMappingDao.selectByTeamId(form.getTeamId());
        if (queryList.stream().noneMatch(e -> e.getUserId().equals(form.getAutoIncreaseId())
                && e.getRole() == FleetRoleEnum.CREATOR.code())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "???????????????");
        }

        // ????????????
        String[] idStr = form.getAdminId().split(SEPARATOR);
        List<Long> ids = Stream.of(idStr).filter(StringUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList());
        for (Long adminId : ids) {
            // ?????????????????????ID???????????????????????????????????????
            if (queryList.stream().anyMatch(e -> e.getUserId().equals(adminId)
                    && e.getRole() == FleetRoleEnum.ADMIN.code())) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "????????????????????????");
            }

            // ???????????????
            insertRecord(form, adminId);
            // ?????? TODO
            pushOrSms(adminId, form.getTeamId(), form.getUserId());
        }
        log.info("fleetAdminBind retrun:{}", Result.ok());
        return Result.ok();
    }

    /**
     * @Description???????????????
     * @author YZL
     * @data 2021/6/29 13:34
     */
    @Override
    public HttpCommandResultWithData unbind(FleetAdminUnbindForm form) {
        // ???????????????????????????????????????????????????
        List<FltFleetUserMappingEntity> queryList = fltFleetUserMappingDao.selectByTeamId(form.getTeamId());
        if (queryList.stream().noneMatch(e -> e.getUserId().equals(form.getAutoIncreaseId())
                && e.getRole() == FleetRoleEnum.CREATOR.code())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "???????????????");
        }
        // ????????????
        String[] idStr = form.getAdminId().split(SEPARATOR);
        List<Long> ids = Stream.of(idStr).filter(StringUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList());
        for (Long adminId : ids) {
            // ????????????????????????
            deleteRecord(queryList, adminId);
            // ???????????????????????????
            commonCustomMaintainService.updateUserCustomMaintain(adminId);
            // ?????? TODO
            push(adminId, form.getTeamId(), form.getUserId());
        }
        log.info("fleetAdminUnbind end");
        return null;
    }

    /**
     * ????????????????????????
     */
    private void insertRecord(FleetAdminBindForm form, Long adminId) {
        FltFleetUserMappingEntity insRecord = new FltFleetUserMappingEntity();
        insRecord.setUserId(adminId);
        insRecord.setTeamId(form.getTeamId());
        insRecord.setRole(FleetRoleEnum.ADMIN.code());
        insRecord.setCreateType(AppTypeEnum.APP_C.code());
        insRecord.setCreateUserId(form.getAutoIncreaseId());
        Date now = new Date();
        insRecord.setCreateTime(now);
        insRecord.setUpdateTime(now);
        this.baseMapper.insertSelective(insRecord);
    }

    /**
     * ??????????????????
     */
    private void pushOrSms(Long adminId, Long teamId, String senderId) {
        UserEntity user = userDao.selectByPrimaryKey(adminId);
        if (user == null || StringUtil.isEmpty(user.getPhone())) {
            return;
        }
        if (StringUtil.isNotEmpty(user.getUcId())) {
            // ??????????????????
            asyncPushSystemMsgService.pushAddAdmin(user.getUcId(), teamId, senderId);
        } else {
            // ??????????????????
            FltFleet fltFleet = fltFleetDao.selectByPrimaryKey(teamId);
            String teamName = "??????";
            if (fltFleet != null) {
                teamName = fltFleet.getName();
            }
            SendSmsForm com = new SendSmsForm();
            com.setPhone(user.getPhone());
            String content = smsTemplate.replace("{????????????}", teamName).replace("{????????????}", downloadSite);
            com.setContent(content);
            try {
                toolsClient.sendSms(com);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.info("[pushOrSms]end");
    }

    /**
     * ???????????????
     */
    private void deleteRecord(List<FltFleetUserMappingEntity> queryList, Long adminId) {
        log.info("[deleteRecord]start");
        // ????????????????????????
        Optional<FltFleetUserMappingEntity> delRecordOp = queryList.stream()
                .filter(e -> e.getUserId().equals(adminId) && e.getRole() == FleetRoleEnum.ADMIN.code())
                .findFirst();
        log.info("[deleteRecord]record present:{}", delRecordOp.isPresent());
        delRecordOp.ifPresent(e -> fltFleetUserMappingDao.deleteByPrimaryKey(e.getId()));
        log.info("[deleteRecord]end");
    }

    /**
     * ??????
     */
    private void push(Long adminId, Long teamId, String senderId) {
        UserEntity user = userDao.selectByPrimaryKey(adminId);
        if (user == null || StringUtil.isEmpty(user.getUcId())) {
            return;
        }
        asyncPushSystemMsgService.pushRemovedFromFleet(user.getUcId(), teamId, senderId);
    }
}
