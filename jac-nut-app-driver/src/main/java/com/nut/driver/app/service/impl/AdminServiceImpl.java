package com.nut.driver.app.service.impl;

import com.github.pagehelper.Page;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.dao.FltFleetUserMappingDao;
import com.nut.driver.app.dao.UserDao;
import com.nut.driver.app.dto.FleetAdminsDto;
import com.nut.driver.app.entity.FltFleetUserMappingEntity;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.form.FleetAdminQuitForm;
import com.nut.driver.app.form.FleetAdminsForm;
import com.nut.driver.app.pojo.UserInfoPojo;
import com.nut.driver.app.service.AdminService;
import com.nut.driver.app.service.AsyncPushSystemMsgService;
import com.nut.driver.app.service.CommonCustomMaintainService;
import com.nut.driver.common.constants.FleetRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("AdminService")
@Slf4j
public class AdminServiceImpl implements AdminService {


    private static final String YES = "1";
    private static final String SEPARATOR = ",";

    @Autowired
    private CommonCustomMaintainService commonCustomMaintainService;

    @Autowired
    private FltFleetUserMappingDao fltFleetUserMappingMapper;

    @Autowired
    private UserDao userMapper;

    @Autowired
    private AsyncPushSystemMsgService asyncPushSystemMsgService;

    @Override
    public HttpCommandResultWithData<PagingInfo<FleetAdminsDto>> query(FleetAdminsForm command) {
        log.info("[query]start");
        HttpCommandResultWithData<PagingInfo<FleetAdminsDto>> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        if (!YES.equals(command.getReturnAll())) {
            DriverBaseService.getPage(command);
        }
        // 查询数据库
        Page<UserInfoPojo> queryList = fltFleetUserMappingMapper.selectByTeamIdAndRole(command.getTeamId(),
                FleetRoleEnum.ADMIN.code(), command.getKeyword());
        log.info("[query]queryList.size:{}", queryList.size());

        PagingInfo<FleetAdminsDto> pagingInfo = new PagingInfo<>();
        if (YES.equals(command.getReturnAll())) {
            pagingInfo.setTotal(queryList.size());
            pagingInfo.setPage_total(queryList.isEmpty() ? 0 : 1);
            pagingInfo.setList(pojoToDto(queryList));
        } else {
            pagingInfo.setTotal(queryList.getTotal());
            pagingInfo.setPage_total(queryList.getPages());
            pagingInfo.setList(pojoToDto(queryList));
        }
        result.setData(pagingInfo);
        log.info("fleetAdmins end return:{}", pagingInfo);
        return result;
    }

    @Override
    public HttpCommandResultWithData quit(FleetAdminQuitForm command) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询数据库
        List<FltFleetUserMappingEntity> queryList = fltFleetUserMappingMapper.selectByTeamId(command.getTeamId());
        log.info("[quit]queryList.size:{}", queryList.size());
        // 删除绑定关系数据
        deleteRecord(queryList, command.getAutoIncreaseId());
        // 异步更新自定义保养 TODO
        commonCustomMaintainService.updateUserCustomMaintain(command.getAutoIncreaseId());
        // 推送 TODO
        push(queryList, command.getTeamId(), command.getUserId());
        log.info("fleetAdminQuit end");
        return result;
    }

    /**
     * 推送
     */
    private void push(List<FltFleetUserMappingEntity> queryList, Long teamId, String senderId) {
        log.info("[push]start");
        Optional<FltFleetUserMappingEntity> creatorRecordOp = queryList.stream()
                .filter(e -> e.getRole() == FleetRoleEnum.CREATOR.code())
                .findFirst();
        if (creatorRecordOp.isPresent()) {
            UserEntity user = userMapper.selectByPrimaryKey(creatorRecordOp.get().getUserId());
            if (user == null || StringUtil.isEmpty(user.getUcId())) {
                log.info("[push]user ucid is null");
                return;
            }
            asyncPushSystemMsgService.pushQuitFleet(user.getUcId(), teamId, "管理员", senderId);
        } else {
            log.info("[push]creator doesn't exist:{}", teamId);
        }
        log.info("[push]end");
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
        delRecordOp.ifPresent(e -> fltFleetUserMappingMapper.deleteByPrimaryKey(e.getId()));
        log.info("[deleteRecord]end");
    }

    /**
     * 类型转换
     */
    private List<FleetAdminsDto> pojoToDto(List<UserInfoPojo> pojos) {
        List<FleetAdminsDto> result = new ArrayList<>();
        for (UserInfoPojo pojo : pojos) {
            FleetAdminsDto dto = new FleetAdminsDto();
            dto.setNickname(pojo.getName());
            dto.setPhone(pojo.getPhone());
            dto.setUserId(pojo.getId());
            result.add(dto);
        }
        return result;
    }

}
