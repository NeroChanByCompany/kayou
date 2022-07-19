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
     * 短信模板
     */
    @Value("${sms.template.add.admin:我们的车队需要您，您已成为{车队名称}的管理员了，快来下载体验吧~下载地址：{下载地址}}")
    private String smsTemplate;

    @Value("${app.download.site:http://}")
    private String downloadSite;

    private static final String SEPARATOR = ",";

    @Override
    @Transactional
    public HttpCommandResultWithData bind(FleetAdminBindForm form) {
        // 校验登录用户是否有绑定管理员的权限
        List<FltFleetUserMappingEntity> queryList = fltFleetUserMappingDao.selectByTeamId(form.getTeamId());
        if (queryList.stream().noneMatch(e -> e.getUserId().equals(form.getAutoIncreaseId())
                && e.getRole() == FleetRoleEnum.CREATOR.code())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "无操作权限");
        }

        // 支持多选
        String[] idStr = form.getAdminId().split(SEPARATOR);
        List<Long> ids = Stream.of(idStr).filter(StringUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList());
        for (Long adminId : ids) {
            // 校验管理员用户ID是否已经与车队存在绑定关系
            if (queryList.stream().anyMatch(e -> e.getUserId().equals(adminId)
                    && e.getRole() == FleetRoleEnum.ADMIN.code())) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "管理员已在车队内");
            }

            // 插入数据库
            insertRecord(form, adminId);
            // 推送 TODO
            pushOrSms(adminId, form.getTeamId(), form.getUserId());
        }
        log.info("fleetAdminBind retrun:{}", Result.ok());
        return Result.ok();
    }

    /**
     * @Description：解除绑定
     * @author YZL
     * @data 2021/6/29 13:34
     */
    @Override
    public HttpCommandResultWithData unbind(FleetAdminUnbindForm form) {
        // 校验登录用户是否有解绑管理员的权限
        List<FltFleetUserMappingEntity> queryList = fltFleetUserMappingDao.selectByTeamId(form.getTeamId());
        if (queryList.stream().noneMatch(e -> e.getUserId().equals(form.getAutoIncreaseId())
                && e.getRole() == FleetRoleEnum.CREATOR.code())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "无操作权限");
        }
        // 支持多选
        String[] idStr = form.getAdminId().split(SEPARATOR);
        List<Long> ids = Stream.of(idStr).filter(StringUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList());
        for (Long adminId : ids) {
            // 删除绑定关系数据
            deleteRecord(queryList, adminId);
            // 异步更新自定义保养
            commonCustomMaintainService.updateUserCustomMaintain(adminId);
            // 推送 TODO
            push(adminId, form.getTeamId(), form.getUserId());
        }
        log.info("fleetAdminUnbind end");
        return null;
    }

    /**
     * 数据库中插入记录
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
     * 推送或发短信
     */
    private void pushOrSms(Long adminId, Long teamId, String senderId) {
        UserEntity user = userDao.selectByPrimaryKey(adminId);
        if (user == null || StringUtil.isEmpty(user.getPhone())) {
            return;
        }
        if (StringUtil.isNotEmpty(user.getUcId())) {
            // 已注册，推送
            asyncPushSystemMsgService.pushAddAdmin(user.getUcId(), teamId, senderId);
        } else {
            // 未注册，短信
            FltFleet fltFleet = fltFleetDao.selectByPrimaryKey(teamId);
            String teamName = "车队";
            if (fltFleet != null) {
                teamName = fltFleet.getName();
            }
            SendSmsForm com = new SendSmsForm();
            com.setPhone(user.getPhone());
            String content = smsTemplate.replace("{车队名称}", teamName).replace("{下载地址}", downloadSite);
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
     * 删除管理员
     */
    private void deleteRecord(List<FltFleetUserMappingEntity> queryList, Long adminId) {
        log.info("[deleteRecord]start");
        // 删除绑定关系数据
        Optional<FltFleetUserMappingEntity> delRecordOp = queryList.stream()
                .filter(e -> e.getUserId().equals(adminId) && e.getRole() == FleetRoleEnum.ADMIN.code())
                .findFirst();
        log.info("[deleteRecord]record present:{}", delRecordOp.isPresent());
        delRecordOp.ifPresent(e -> fltFleetUserMappingDao.deleteByPrimaryKey(e.getId()));
        log.info("[deleteRecord]end");
    }

    /**
     * 推送
     */
    private void push(Long adminId, Long teamId, String senderId) {
        UserEntity user = userDao.selectByPrimaryKey(adminId);
        if (user == null || StringUtil.isEmpty(user.getUcId())) {
            return;
        }
        asyncPushSystemMsgService.pushRemovedFromFleet(user.getUcId(), teamId, senderId);
    }
}
