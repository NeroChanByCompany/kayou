package com.nut.driver.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.Page;
import com.nut.common.assembler.ReverseGeoCodingClient;
import com.nut.common.constant.AppTypeEnum;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.client.ToolsClient;
import com.nut.driver.app.domain.FltFleet;
import com.nut.driver.app.dto.CarOnlineStatusDto;
import com.nut.driver.app.dto.FleetDriversDto;
import com.nut.driver.app.entity.FltFleetEntity;
import com.nut.driver.app.entity.FltFleetUserMappingEntity;
import com.nut.driver.app.form.*;
import com.nut.driver.app.pojo.UserInfoPojo;
import com.nut.driver.app.service.CommonCustomMaintainService;
import com.nut.driver.app.form.SendSmsForm;
import com.nut.driver.app.dao.FltCarDriverMappingDao;
import com.nut.driver.app.dao.FltFleetDao;
import com.nut.driver.app.dao.FltFleetUserMappingDao;
import com.nut.driver.app.dao.UserDao;
import com.nut.driver.app.domain.FltFleetUserMapping;
import com.nut.driver.app.dto.FleetDriverDetailDto;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.pojo.CarInfoPojo;
import com.nut.driver.app.service.AsyncPushSystemMsgService;
import com.nut.driver.app.service.CarService;
import com.nut.driver.app.service.DriverService;
import com.nut.driver.common.constants.FleetRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("DriverService")
@Slf4j
public class DriverServiceImpl implements DriverService {

    private static final String YES = "1";
    private static final String SEPARATOR = ",";
    private static final int Y = 1;
    private static final int N = 0;

    @Autowired
    private CommonCustomMaintainService commonCustomMaintainService;

    @Autowired
    private FltFleetDao fltFleetMapper;

    @Autowired
    private AsyncPushSystemMsgService asyncPushSystemMsgService;

    @Autowired
    private FltFleetUserMappingDao fltFleetUserMappingMapper;

    @Autowired
    private FltCarDriverMappingDao fltCarDriverMappingMapper;

    @Autowired
    private CarService carService;

    @Autowired
    private UserDao userMapper;

    @Autowired
    private ReverseGeoCodingClient queryGeographicalService;


    /**
     * 短信模板
     */
    @Value("${sms.template.add.driver:我们的车队需要您，您已成为{车队名称}的司机了，快来下载体验吧~下载地址：{下载地址}}")
    private String smsTemplate;
    @Value("${app.download.site:http://}")
    private String downloadSite;

    @Autowired
    private ToolsClient toolsClient;


    @Override
    public HttpCommandResultWithData detail(FleetDriverDetailForm command) throws JsonProcessingException {
        log.info("[detail]start");
        HttpCommandResultWithData<FleetDriverDetailDto> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询用户信息
        UserEntity user = userMapper.selectByPrimaryKey(command.getDriverId());
        if (user == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("未查询到用户信息");
            return result;
        }
        FleetDriverDetailDto data = new FleetDriverDetailDto();
        data.setNickname(user.getName());
        data.setPhone(user.getPhone());

        // 查询驾驶车辆
        List<CarInfoPojo> queryList = fltCarDriverMappingMapper.selectByTeamIdAndUserId(command.getTeamId(), command.getDriverId());
        log.info("[detail]queryList.size:{}", queryList.size());
        List<String> chassisNumList = queryList.stream().map(CarInfoPojo::getCarVin).collect(Collectors.toList());

        // 调用位置云接口查询车辆状态
        Map<String, CarOnlineStatusDto> outMap = new HashMap<>(16);
        HttpCommandResultWithData cloudResult = carService.getRealTimeInfo(chassisNumList, outMap);
        if (cloudResult.getResultCode() != ECode.SUCCESS.code()) {
            result.setResultCode(cloudResult.getResultCode());
            result.setMessage(cloudResult.getMessage());
            return result;
        }

        // 补全位置信息
        List<FleetDriverDetailDto.CarInfo> carList = new ArrayList<>();
        for (CarInfoPojo car : queryList) {
            FleetDriverDetailDto.CarInfo dto = pojoToDto(car);
            if (outMap.get(car.getCarVin()) != null) {
                CarOnlineStatusDto carOnlineStatusDto = outMap.get(car.getCarVin());
                dto.setLon(carOnlineStatusDto.getLon());
                dto.setLat(carOnlineStatusDto.getLat());
                if (StringUtil.isNotEmpty(carOnlineStatusDto.getLon()) && StringUtil.isNotEmpty(carOnlineStatusDto.getLat())) {
                    dto.setLocation(queryGeographicalService.getPosition(carOnlineStatusDto.getLat(), carOnlineStatusDto.getLon()));
                }
            }
            carList.add(dto);
        }
        log.info("[detail]position info set");
        data.setCarList(carList);
        result.setData(data);
        log.info("[detail]end");
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public HttpCommandResultWithData bind(FleetDriverBindForm command) {
        log.info("[bind]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        UserEntity user = userMapper.selectOne(new QueryWrapper<UserEntity>().eq(StringUtils.isNotBlank(command.getUserId()), "uc_id", command.getUserId()));

        // 校验登录用户是否有绑定司机的权限
        List<FltFleetUserMappingEntity> queryList = fltFleetUserMappingMapper.selectByTeamId(command.getTeamId());
        log.info("[bind]queryList.size:{}", queryList.size());


        if (queryList.stream().noneMatch(e -> e.getUserId().equals(user.getId())
                && (e.getRole() == FleetRoleEnum.CREATOR.code() || e.getRole() == FleetRoleEnum.ADMIN.code()))) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("无操作权限");
            return result;
        }

        // 支持多选
        String[] idStr = command.getDriverId().split(SEPARATOR);
        List<Long> ids = Stream.of(idStr).filter(StringUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList());
        for (Long driverId : ids) {
            // 校验司机用户ID是否已经与车队存在绑定关系
            if (queryList.stream().anyMatch(e -> e.getUserId().equals(driverId)
                    && e.getRole() == FleetRoleEnum.DRIVER.code())) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("司机已在车队内");
                return result;
            }

            // 插入数据库
            insertRecord(command, driverId);
            // 推送
            pushOrSms(driverId, command.getTeamId(), command.getUserId());
        }
        log.info("[bind]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData unbind(FleetDriverUnbindForm command) {
        log.info("[unbind]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        UserEntity user = userMapper.selectOne(new QueryWrapper<UserEntity>().eq(StringUtils.isNotBlank(command.getUserId()), "uc_id", command.getUserId()));

        // 校验登录用户是否有解绑司机的权限
        List<FltFleetUserMappingEntity> queryList = fltFleetUserMappingMapper.selectByTeamId(command.getTeamId());
        log.info("[unbind]queryList.size:{}", queryList.size());

        if (queryList.stream().noneMatch(e ->  e.getUserId().equals(user.getId())
                &&(e.getRole() == FleetRoleEnum.CREATOR.code() || e.getRole() == FleetRoleEnum.ADMIN.code()))) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("无操作权限");
            return result;
        }

        // 支持多选
        String[] idStr = command.getDriverId().split(SEPARATOR);
        List<Long> ids = Stream.of(idStr).filter(StringUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList());
        for (Long driverId : ids) {
            // 删除绑定关系数据
            deleteDriverAndCar(queryList, command.getTeamId(), driverId);
            // 异步更新自定义保养
            commonCustomMaintainService.updateUserCustomMaintain(driverId);
            // 推送
            push(driverId, command.getTeamId(), command.getUserId());
        }
        log.info("[unbind]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData<PagingInfo<FleetDriversDto>> query(FleetDriversForm command) {
        log.info("[query]start");
        HttpCommandResultWithData<PagingInfo<FleetDriversDto>> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        if (!YES.equals(command.getReturnAll())) {
            DriverBaseService.getPage(command);
        }
        // 查询数据库
        Page<UserInfoPojo> queryList = fltFleetUserMappingMapper.selectByTeamIdAndRole(command.getTeamId(),
                FleetRoleEnum.DRIVER.code(), command.getKeyword());
        log.info("[query]queryList.size:{}", queryList.size());

        // 查询已经绑定的数据
        List<Long> exclusiveDrivers = null;
        if (queryList.size() > 0 && StringUtil.isNotEmpty(command.getExclusiveCarId())) {
            exclusiveDrivers = getExclusiveUserIds(command.getTeamId(), command.getExclusiveCarId());
        }

        PagingInfo<FleetDriversDto> pagingInfo = new PagingInfo<>();
        if (YES.equals(command.getReturnAll())) {
            pagingInfo.setTotal(queryList.size());
            pagingInfo.setPage_total(queryList.isEmpty() ? 0 : 1);
            pagingInfo.setList(pojoToDto(queryList, exclusiveDrivers));
        } else {
            pagingInfo.setTotal(queryList.getTotal());
            pagingInfo.setPage_total(queryList.getPages());
            pagingInfo.setList(pojoToDto(queryList, exclusiveDrivers));
        }
        result.setData(pagingInfo);
        log.info("[query]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData quit(FleetDriverQuitForm command) {
        log.info("[quit]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询数据库
        List<FltFleetUserMappingEntity> queryList = fltFleetUserMappingMapper.selectByTeamId(command.getTeamId());
        log.info("[quit]queryList.size:{}", queryList.size());
        // 删除绑定关系数据
        deleteDriverAndCar(queryList, command.getTeamId(), command.getAutoIncreaseId());
        // 异步更新自定义保养
        commonCustomMaintainService.updateUserCustomMaintain(command.getAutoIncreaseId());
        // 推送
        push(queryList, command.getTeamId(), command.getUserId());
        log.info("[quit]end");
        return result;
    }

    /**
     * 查询已经与车辆绑定的司机
     */
    private List<Long> getExclusiveUserIds(Long teamId, String carId) {
        List<UserInfoPojo> queryList = fltCarDriverMappingMapper.selectByTeamIdAndCarId(teamId, carId);
        log.info("[getExclusiveUserIds]teamId:{}||carId:{}||size:{}", teamId, carId, queryList.size());
        return queryList.stream().map(UserInfoPojo::getId).collect(Collectors.toList());
    }

    /**
     * 类型转换
     */
    private FleetDriverDetailDto.CarInfo pojoToDto(CarInfoPojo pojo) {
        FleetDriverDetailDto.CarInfo dto = new FleetDriverDetailDto.CarInfo();
        dto.setCarId(pojo.getCarId());
        dto.setCarNumber(pojo.getCarNumber());
        dto.setChassisNum(pojo.getCarVin());
        return dto;
    }
    /**
     * 数据库中插入记录
     */
    private void insertRecord(FleetDriverBindForm command, Long driverId) {
        log.info("[insertRecord]start");
        FltFleetUserMapping insRecord = new FltFleetUserMapping();
        insRecord.setUserId(driverId);
        insRecord.setTeamId(command.getTeamId());
        insRecord.setRole(FleetRoleEnum.DRIVER.code());
        insRecord.setCreateType(AppTypeEnum.APP_C.code());
        insRecord.setCreateUserId(command.getAutoIncreaseId());
        Date now = new Date();
        insRecord.setCreateTime(now);
        insRecord.setUpdateTime(now);
        fltFleetUserMappingMapper.insertSelective(insRecord);
        log.info("[insertRecord]end");
    }
    /**
     * 推送或发短信
     */
    private void pushOrSms(Long driverId, Long teamId, String senderId) {
        log.info("[pushOrSms]start");
        UserEntity user = userMapper.selectByPrimaryKey(driverId);
        if (user == null || StringUtil.isEmpty(user.getPhone())) {
            log.info("[pushOrSms]user phone is null");
            return;
        }
        if (StringUtil.isNotEmpty(user.getUcId())) {
            // 已注册，推送
            asyncPushSystemMsgService.pushAddDriver(user.getUcId(), teamId, senderId);
        } else {
            // 未注册，短信
            FltFleet fltFleet = fltFleetMapper.selectByPrimaryKey(teamId);
            String teamName = "车队";
            if (fltFleet != null) {
                teamName = fltFleet.getName();
            }
            SendSmsForm com = new SendSmsForm();
            com.setPhone(user.getPhone());
            String content = smsTemplate.replace("{车队名称}", teamName).replace("{下载地址}", downloadSite);
            log.info("[pushOrSms]sms content:{}", content);
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
     * 推送
     */
    private void push(Long driverId, Long teamId, String senderId) {
        log.info("[push]start");
        UserEntity user = userMapper.selectByPrimaryKey(driverId);
        if (user == null || StringUtil.isEmpty(user.getUcId())) {
            log.info("[push]user ucid is null");
            return;
        }
        asyncPushSystemMsgService.pushRemovedFromFleet(user.getUcId(), teamId, senderId);
        log.info("[push]end");
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
            asyncPushSystemMsgService.pushQuitFleet(user.getUcId(), teamId, "司机", senderId);
        } else {
            log.info("[push]creator doesn't exist:{}", teamId);
        }
        log.info("[push]end");
    }
    /**
     * 删除司机和驾驶车辆
     */
    private void deleteDriverAndCar(List<FltFleetUserMappingEntity> queryList, Long teamId, Long driverId) {
        log.info("[deleteDriverAndCar]start");
        // 删除绑定关系数据
        Optional<FltFleetUserMappingEntity> delRecordOp = queryList.stream()
                .filter(e -> e.getUserId().equals(driverId) && e.getRole() == FleetRoleEnum.DRIVER.code())
                .findFirst();
        log.info("[deleteDriverAndCar]record present:{}", delRecordOp.isPresent());
        delRecordOp.ifPresent(e -> fltFleetUserMappingMapper.deleteByPrimaryKey(e.getId()));

        // 如果被解绑司机在车队内与车辆有绑定关系，删除
        List<CarInfoPojo> driveCarList = fltCarDriverMappingMapper.selectByTeamIdAndUserId(teamId, driverId);
        log.info("[deleteDriverAndCar]driveCarList.size:{}", driveCarList.size());
        if (driveCarList.size() > 0) {
            fltCarDriverMappingMapper.deleteByTeamIdAndUserId(teamId, driverId);
        }
        log.info("[deleteDriverAndCar]end");
    }
    /**
     * 类型转换
     */
    private List<FleetDriversDto> pojoToDto(List<UserInfoPojo> pojos, List<Long> exclusiveDrivers) {
        List<FleetDriversDto> result = new ArrayList<>();
        for (UserInfoPojo pojo : pojos) {
            FleetDriversDto dto = new FleetDriversDto();
            dto.setNickname(pojo.getName());
            dto.setPhone(pojo.getPhone());
            dto.setUserId(pojo.getId());
            if (exclusiveDrivers != null) {
                if (exclusiveDrivers.contains(pojo.getId())) {
                    dto.setBound(Y);
                } else {
                    dto.setBound(N);
                }
            }
            result.add(dto);
        }
        return result;
    }
}
