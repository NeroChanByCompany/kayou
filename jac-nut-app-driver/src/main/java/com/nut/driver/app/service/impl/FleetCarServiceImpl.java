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
import com.nut.driver.app.dao.*;
import com.nut.driver.app.domain.FltCarDriverBindRecord;
import com.nut.driver.app.domain.FltCarDriverMapping;
import com.nut.driver.app.domain.FltCarOwnerMapping;
import com.nut.driver.app.domain.FltFleetCarMapping;
import com.nut.driver.app.dto.FleetCarDetailDTO;
import com.nut.driver.app.dto.FleetCarsDto;
import com.nut.driver.app.entity.CarEntity;
import com.nut.driver.app.entity.FltFleetCarMappingEntity;
import com.nut.driver.app.entity.FltFleetUserMappingEntity;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.form.*;
import com.nut.driver.app.pojo.CarInfoPojo;
import com.nut.driver.app.pojo.UserInfoPojo;
import com.nut.driver.app.service.AsyncPushSystemMsgService;
import com.nut.driver.app.service.CarService;
import com.nut.driver.app.service.CommonCustomMaintainService;
import com.nut.driver.app.service.FleetCarService;
import com.nut.driver.common.constants.FleetRoleEnum;
import com.nut.driver.app.dto.CarOnlineStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service("FleetCarService")
public class FleetCarServiceImpl implements FleetCarService {

    private static final String YES = "1";
    private static final int BIND = 1;
    private static final int UNBIND = 2;
    private static final String SEPARATOR = ",";
    private static final int MULTIPLE_FLAG_CAR = 1;
    private static final int MULTIPLE_FLAG_DRIVER = 2;
    private static final int Y = 1;
    private static final int N = 0;


    @Autowired
    private FltFleetUserMappingDao fltFleetUserMappingMapper;

    @Autowired
    private UserDao userMapper;

    @Autowired
    private FltFleetCarMappingDao fltFleetCarMappingMapper;

    @Autowired
    private FltCarDriverMappingDao fltCarDriverMappingMapper;

    @Autowired
    private AsyncPushSystemMsgService asyncPushSystemMsgService;

    @Autowired
    private CommonCustomMaintainService commonCustomMaintainService;

    @Autowired
    private CarService carService;

    @Autowired
    private CarDao carMapper;

    @Autowired
    private ReverseGeoCodingClient queryGeographicalService;

    @Autowired
    private FltCarOwnerMappingDao fltCarOwnerMappingMapper;

    @Override
    public HttpCommandResultWithData carDriverBind(CarDriverBindForm command) {
        log.info("[carDriverBind]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        UserEntity user = userMapper.selectOne(new QueryWrapper<UserEntity>().eq(StringUtils.isNotBlank(command.getUserId()), "uc_id", command.getUserId()));

        // 校验登录用户是否有给车辆绑定司机的权限
        List<FltFleetUserMappingEntity> queryList = fltFleetUserMappingMapper.selectByTeamId(command.getTeamId());
        log.info("[carDriverBind]queryList.size:{}", queryList.size());
        if (queryList.stream().noneMatch(e -> e.getUserId().equals(user.getId())
                && (e.getRole() == FleetRoleEnum.CREATOR.code() || e.getRole() == FleetRoleEnum.ADMIN.code()))) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("无操作权限");
            return result;
        }

        // 支持多选
        List<String> carIds = new ArrayList<>();
        List<Long> driverIds = new ArrayList<>();
        if (command.getMultipleFlag() == MULTIPLE_FLAG_CAR) {
            // 一个司机绑定多个车辆
            carIds = Stream.of(command.getCarId().split(SEPARATOR)).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
            driverIds.add(Long.parseLong(command.getMasterDriverId()));
        } else if (command.getMultipleFlag() == MULTIPLE_FLAG_DRIVER) {
            // 一个车辆绑定多个司机
            carIds.add(command.getCarId());
            driverIds = Stream.of(command.getMasterDriverId().split(SEPARATOR)).filter(StringUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList());
            if(StringUtils.isNotBlank(command.getCopilotDriverId())){
                driverIds.add(Long.parseLong(command.getCopilotDriverId()));
            }
        } else {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("多选标识格式不正确");
            return result;
        }

        // 二重循环中有一层只循环一次
        for (String carId : carIds) {
            for (Long driverId : driverIds) {
                // 校验车辆与车队、司机与车队是否有绑定关系
                if (queryList.stream().noneMatch(e -> e.getUserId().equals(driverId)
                        && e.getRole() == FleetRoleEnum.DRIVER.code())) {
                    result.setResultCode(ECode.CLIENT_ERROR.code());
                    result.setMessage("只能绑定车队内司机");
                    return result;
                }
                FltFleetCarMappingEntity fltFleetCarMapping = fltFleetCarMappingMapper.selectByTeamIdAndCarId(command.getTeamId(), carId);
                if (fltFleetCarMapping == null) {
                    result.setResultCode(ECode.CLIENT_ERROR.code());
                    result.setMessage("只能绑定车队内车辆");
                    return result;
                }

                // 校验被绑定司机是否在车队内已经与被绑定车辆存在绑定关系
                FltCarDriverMapping existEntity = fltCarDriverMappingMapper.selectByTeamCarUser(command.getTeamId(), carId, driverId);
                if (existEntity != null) {
                    result.setResultCode(ECode.CLIENT_ERROR.code());
                    result.setMessage("车辆与司机已经绑定");
                    return result;
                }

                // 插入数据库
                insertRecord(command, carId, driverId);
                // 推送
                push(driverId, command.getTeamId(), carId, command.getUserId(), BIND);
            }
        }
        log.info("[carDriverBind]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData carDriverUnbind(CarDriverUnbindForm command) {
        log.info("[carDriverUnbind]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        UserEntity user = userMapper.selectOne(new QueryWrapper<UserEntity>().eq(StringUtils.isNotBlank(command.getUserId()), "uc_id", command.getUserId()));

        // 校验登录用户是否有将车辆与司机解绑的权限
        List<FltFleetUserMappingEntity> queryList = fltFleetUserMappingMapper.selectByTeamId(command.getTeamId());
        log.info("[carDriverUnbind]queryList.size:{}", queryList.size());
        if (queryList.stream().noneMatch(e -> e.getUserId().equals(user.getId())
                && (e.getRole() == FleetRoleEnum.CREATOR.code() || e.getRole() == FleetRoleEnum.ADMIN.code()))) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("无操作权限");
            return result;
        }

        // 支持多选
        List<String> carIds = new ArrayList<>();
        List<Long> driverIds = new ArrayList<>();
        if (command.getMultipleFlag() == MULTIPLE_FLAG_CAR) {
            // 一个司机解绑多个车辆
            carIds = Stream.of(command.getCarId().split(SEPARATOR)).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
            driverIds.add(Long.parseLong(command.getDriverId()));
        } else if (command.getMultipleFlag() == MULTIPLE_FLAG_DRIVER) {
            // 一个车辆解绑多个司机
            carIds.add(command.getCarId());
            driverIds = Stream.of(command.getDriverId().split(SEPARATOR)).filter(StringUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList());
        } else {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("多选标识格式不正确");
            return result;
        }

        // 二重循环中有一层只循环一次
        for (String carId : carIds) {
            for (Long driverId : driverIds) {
                // 删除绑定关系数据
                FltCarDriverMapping existEntity = fltCarDriverMappingMapper.selectByTeamCarUser(command.getTeamId(), carId, driverId);
                if (existEntity != null) {

                    //插入数据库记录解绑记录
                    FltCarDriverBindRecord fltCarDriverBindRecord = new FltCarDriverBindRecord();
                    fltCarDriverBindRecord.setTeamId(command.getTeamId());
                    fltCarDriverBindRecord.setCarId(carId);
                    fltCarDriverBindRecord.setIsMasterDriver(String.valueOf(existEntity.getIsMasterDriver()));
                    fltCarDriverBindRecord.setUserId(driverId);
                    fltCarDriverBindRecord.setCreateUserId(command.getAutoIncreaseId());
                    fltCarDriverBindRecord.setCreateTime(new Date());
                    fltCarDriverBindRecord.setBind("0");
                    fltCarDriverBindRecord.setIsFirstBind("2");
                    fltCarDriverMappingMapper.insertFltCarDriverRecord(fltCarDriverBindRecord);

                    fltCarDriverMappingMapper.deleteByPrimaryKey(existEntity.getId());
                }
                // 推送
                push(driverId, command.getTeamId(), carId, command.getUserId(), UNBIND);
                // 异步更新自定义保养
                commonCustomMaintainService.updateUserCustomMaintain(driverId);
            }
        }
        log.info("[carDriverUnbind]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData<PagingInfo<FleetCarsDto>> query(FleetCarsForm command) throws JsonProcessingException {
        log.info("[query]start");
        HttpCommandResultWithData<PagingInfo<FleetCarsDto>> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        if (!YES.equals(command.getReturnAll())) {
            DriverBaseService.getPage(command);
        }
        // 查询数据库
        Page<CarInfoPojo> queryList = fltFleetCarMappingMapper.selectCarByTeamId(command.getTeamId(), command.getKeyword());
        log.info("[query]queryList.size:{}", queryList.size());
        List<String> chassisNumList = queryList.stream().map(CarInfoPojo::getCarVin).collect(Collectors.toList());

        // 查询已经绑定的数据
        List<String> exclusiveCars = null;
        if (queryList.size() > 0 && command.getExclusiveDriverId() != null) {
            exclusiveCars = getExclusiveCarIds(command.getTeamId(), command.getExclusiveDriverId());
        }

        // 调用位置云接口查询车辆状态 TODO
        Map<String, CarOnlineStatusDto> outMap = new HashMap<>(16);
        HttpCommandResultWithData cloudResult = carService.getRealTimeInfo(chassisNumList, outMap);
        if (cloudResult.getResultCode() != ECode.SUCCESS.code()) {
            result.setResultCode(cloudResult.getResultCode());
            result.setMessage(cloudResult.getMessage());
            return result;
        }

        // 补全位置信息
        List<FleetCarsDto> carList = new ArrayList<>();
        for (CarInfoPojo car : queryList) {
            FleetCarsDto dto = pojoToDto(car, exclusiveCars);
            if (outMap.get(car.getCarVin()) != null) {
                CarOnlineStatusDto carOnlineStatusDto = outMap.get(car.getCarVin());
                dto.setLon(carOnlineStatusDto.getLon());
                dto.setLat(carOnlineStatusDto.getLat());
                // 逆地理信息在筛选分页之后再补全
            }
            carList.add(dto);
        }
        log.info("[query]position info set");

        PagingInfo<FleetCarsDto> pagingInfo = new PagingInfo<>();
        if (YES.equals(command.getReturnAll())) {
            pagingInfo.setTotal(queryList.size());
            pagingInfo.setPage_total(queryList.isEmpty() ? 0 : 1);
            pagingInfo.setList(carList);
        } else {
            pagingInfo.setTotal(queryList.getTotal());
            pagingInfo.setPage_total(queryList.getPages());
            pagingInfo.setList(carList);
        }

        // 逆地理
        completeGeoPositionInfo(pagingInfo.getList());
        result.setData(pagingInfo);
        log.info("[query]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData bind(FleetCarBindForm command) {
        log.info("[bind]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 校验登录用户是否有给车队绑定车辆的权限
        List<FltFleetUserMappingEntity> queryList = fltFleetUserMappingMapper.selectByTeamId(command.getTeamId());
        log.info("[bind]queryList.size:{}", queryList.size());
        List<Integer> roles = Arrays.asList(FleetRoleEnum.CREATOR.code(), FleetRoleEnum.ADMIN.code(), FleetRoleEnum.DRIVER.code());
        if (queryList.stream().noneMatch(e -> e.getUserId().equals(command.getAutoIncreaseId()) && roles.contains(e.getRole()))) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("无操作权限");
            return result;
        }

        // 支持多选
        String[] idStr = command.getCarId().split(SEPARATOR);
        List<String> ids = Stream.of(idStr).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
        for (String carId : ids) {
            // 校验被绑定车辆与用户是否有绑定关系
            FltCarOwnerMapping fltCarOwnerMapping = fltCarOwnerMappingMapper.selectByCarIdAndUserId(carId, command.getAutoIncreaseId());
            if (fltCarOwnerMapping == null) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("只能添加个人已绑定的车辆");
                return result;
            }

            // 校验车辆ID是否已经与车队存在绑定关系
            FltFleetCarMappingEntity fltFleetCarMapping = fltFleetCarMappingMapper.selectByTeamIdAndCarId(command.getTeamId(), carId);
            if (fltFleetCarMapping != null) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("车辆已在车队内");
                return result;
            }

            // 插入数据库
            insertRecord(command, carId);
        }
        log.info("[bind]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData unbind(FleetCarUnbindForm command) {
        log.info("[unbind]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 校验登录用户是否有将车队与车解绑的权限
        List<FltFleetUserMappingEntity> queryList = fltFleetUserMappingMapper.selectByTeamId(command.getTeamId());
        log.info("[bind]queryList.size:{}", queryList.size());
        if (queryList.stream().noneMatch(e -> e.getUserId().equals(command.getAutoIncreaseId())
                && e.getRole() == FleetRoleEnum.CREATOR.code())) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("无操作权限");
            return result;
        }

        // 支持多选
        String[] idStr = command.getCarId().split(SEPARATOR);
        List<String> ids = Stream.of(idStr).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
        for (String carId : ids) {
            // 删除绑定关系数据
            deleteCarAndDriver(command.getTeamId(), carId);
        }
        log.info("[unbind]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData detail(FleetCarDetailForm command) throws JsonProcessingException {
        log.info("[detail]start");
        HttpCommandResultWithData<FleetCarDetailDTO> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询车辆信息
        CarEntity car = carMapper.selectByPrimaryKey(command.getCarId());
        if (car == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("未查询到车辆信息");
            return result;
        }
        FleetCarDetailDTO data = new FleetCarDetailDTO();
        data.setCarNumber(car.getCarNumber());
        data.setChassisNum(car.getCarVin());

        // 调用位置云接口查询车辆状态
        Map<String, CarOnlineStatusDto> outMap = new HashMap<>(16);
        // 提高性能，填写搜索关键字时不返回位置信息
        if (StringUtil.isEmpty(command.getKeyword())) {
            HttpCommandResultWithData cloudResult = carService.getRealTimeInfo(Collections.singletonList(car.getCarVin()), outMap);
            if (cloudResult.getResultCode() != ECode.SUCCESS.code()) {
                result.setResultCode(cloudResult.getResultCode());
                result.setMessage(cloudResult.getMessage());
                return result;
            }
        }

        // 补全位置信息
        if (outMap.get(car.getCarVin()) != null) {
            CarOnlineStatusDto carOnlineStatusDto = outMap.get(car.getCarVin());
            data.setLon(carOnlineStatusDto.getLon());
            data.setLat(carOnlineStatusDto.getLat());
            if (StringUtil.isNotEmpty(carOnlineStatusDto.getLon()) && StringUtil.isNotEmpty(carOnlineStatusDto.getLat())) {
                data.setLocation(queryGeographicalService.getPosition(carOnlineStatusDto.getLat(), carOnlineStatusDto.getLon()));
            }
        }
        log.info("[detail]position info set");

        // 查询司机
        List<UserInfoPojo> queryList = fltCarDriverMappingMapper.selectByTeamIdAndCarId(command.getTeamId(), command.getCarId());
        log.info("[detail]queryList.size:{}", queryList.size());
        // 筛选
        if (StringUtil.isNotEmpty(command.getKeyword())) {
            queryList = queryList.stream().filter(e -> (e.getName() != null && e.getName().contains(command.getKeyword()))
                    || (e.getPhone() != null && e.getPhone().contains(command.getKeyword()))).collect(Collectors.toList());
        }
        data.setDriverList(pojoToDto(queryList));
        result.setData(data);
        log.info("[detail]end");
        return result;
    }
    /**
     * 类型转换
     */
    private List<FleetCarDetailDTO.DriverInfo> pojoToDto(List<UserInfoPojo> pojos) {
        List<FleetCarDetailDTO.DriverInfo> result = new ArrayList<>();
        for (UserInfoPojo pojo : pojos) {
            FleetCarDetailDTO.DriverInfo dto = new FleetCarDetailDTO.DriverInfo();
            dto.setNickname(pojo.getName());
            dto.setPhone(pojo.getPhone());
            dto.setUserId(pojo.getId());
            dto.setIsMasterDriver(pojo.getIsMasterDriver());
            result.add(dto);
        }
        return result;
    }

    /**
     * 删除车辆和司机绑定
     */
    private void deleteCarAndDriver(Long teamId, String carId) {
        log.info("[deleteCarAndDriver]start");
        // 删除绑定关系数据
        FltFleetCarMappingEntity existEntity = fltFleetCarMappingMapper.selectByTeamIdAndCarId(teamId, carId);
        if (existEntity != null) {
            fltFleetCarMappingMapper.deleteByPrimaryKey(existEntity.getId());
        }

        // 如果被解绑车辆在车队内与司机有绑定关系，删除
        List<UserInfoPojo> carDriverList = fltCarDriverMappingMapper.selectByTeamIdAndCarId(teamId, carId);
        log.info("[deleteCarAndDriver]carDriverList.size:{}", carDriverList.size());
        if (carDriverList.size() > 0) {
            fltCarDriverMappingMapper.deleteByTeamIdAndCarId(teamId, carId);
            // 异步更新自定义保养
            List<Long> userIds = carDriverList.stream().map(UserInfoPojo::getId).distinct().collect(Collectors.toList());
            for (Long driverId : userIds) {
                commonCustomMaintainService.updateUserCustomMaintain(driverId);
            }
        }
        log.info("[deleteCarAndDriver]end");
    }

    /**
     * 数据库中插入记录
     */
    private void insertRecord(FleetCarBindForm command, String singleCarId) {
        log.info("[insertRecord]start");
        FltFleetCarMapping insRecord = new FltFleetCarMapping();
        insRecord.setTeamId(command.getTeamId());
        insRecord.setCarId(singleCarId);
        insRecord.setCreateType(AppTypeEnum.APP_C.code());
        insRecord.setCreateUserId(command.getAutoIncreaseId());
        Date now = new Date();
        insRecord.setCreateTime(now);
        insRecord.setUpdateTime(now);
        fltFleetCarMappingMapper.insertSelective(insRecord);
        log.info("[insertRecord]end");
    }

    /**
     * 补全逆地理信息
     */
    private void completeGeoPositionInfo(List<FleetCarsDto> carList) {
        log.info("[completeGeoPositionInfo]start");
        if (carList != null) {
            for (FleetCarsDto dto : carList) {
                if (StringUtil.isNotEmpty(dto.getLon()) && StringUtil.isNotEmpty(dto.getLat())) {
                    dto.setLocation(queryGeographicalService.getPosition(dto.getLat(), dto.getLon()));
                }
            }
        }
        log.info("[completeGeoPositionInfo]end");
    }
    /**
     * 类型转换
     */
    private FleetCarsDto pojoToDto(CarInfoPojo pojo, List<String> exclusiveCars) {
        FleetCarsDto dto = new FleetCarsDto();
        dto.setCarId(pojo.getCarId());
        dto.setCarNumber(pojo.getCarNumber());
        dto.setChassisNum(pojo.getCarVin());
        if (exclusiveCars != null) {
            if (exclusiveCars.contains(pojo.getCarId())) {
                dto.setBound(Y);
            } else {
                dto.setBound(N);
            }
        }
        return dto;
    }
    /**
     * 查询已经与司机绑定的车辆
     */
    private List<String> getExclusiveCarIds(Long teamId, Long driverId) {
        List<CarInfoPojo> queryList = fltCarDriverMappingMapper.selectByTeamIdAndUserId(teamId, driverId);
        log.info("[getExclusiveCarIds]teamId:{}||driverId:{}||size:{}", teamId, driverId, queryList.size());
        return queryList.stream().map(CarInfoPojo::getCarId).collect(Collectors.toList());
    }

    /**
     * 数据库中插入记录
     */
    private void insertRecord(CarDriverBindForm command, String singleCarId, Long singleDriverId) {
        log.info("[insertRecord]start");
        FltCarDriverMapping insRecord = new FltCarDriverMapping();
        insRecord.setTeamId(command.getTeamId());
        insRecord.setCarId(singleCarId);
        insRecord.setUserId(singleDriverId);
        insRecord.setIsMasterDriver(StringUtils.equals(String.valueOf(singleDriverId), command.getMasterDriverId()) ? "1" : "0");
        insRecord.setCreateUserId(command.getAutoIncreaseId());
        Date now = new Date();
        insRecord.setCreateTime(now);
        insRecord.setUpdateTime(now);
        fltCarDriverMappingMapper.insertSelective(insRecord);

        List<FltCarDriverBindRecord> fltCarDriverBindRecordList = fltCarDriverMappingMapper.selectFltCarDriverRecordByCarId(singleCarId);
        FltCarDriverBindRecord fltCarDriverBindRecord = new FltCarDriverBindRecord();
        fltCarDriverBindRecord.setTeamId(command.getTeamId());
        fltCarDriverBindRecord.setCarId(singleCarId);
        fltCarDriverBindRecord.setIsMasterDriver(StringUtils.equals(String.valueOf(singleDriverId), command.getMasterDriverId()) ? "1" : "0");
        fltCarDriverBindRecord.setUserId(singleDriverId);
        fltCarDriverBindRecord.setCreateUserId(command.getAutoIncreaseId());
        fltCarDriverBindRecord.setCreateTime(new Date());
        fltCarDriverBindRecord.setBind("1");
        if(fltCarDriverBindRecordList != null && fltCarDriverBindRecordList.size() > 0){
            fltCarDriverBindRecord.setIsFirstBind("0");
        }else {
            fltCarDriverBindRecord.setIsFirstBind("1");
        }
        fltCarDriverMappingMapper.insertFltCarDriverRecord(fltCarDriverBindRecord);
        log.info("[insertRecord]end");
    }
    /**
     * 推送
     */
    private void push(Long driverId, Long teamId, String carId, String senderId, int type) {
        log.info("[push]start");
        UserEntity user = userMapper.selectByPrimaryKey(driverId);
        if (user == null || StringUtil.isEmpty(user.getUcId())) {
            log.info("[push]user ucid is null");
            return;
        }
        if (type == BIND) {
            asyncPushSystemMsgService.pushCarDriverBind(user.getUcId(), teamId, carId, senderId);
        } else if (type == UNBIND) {
            asyncPushSystemMsgService.pushCarDriverUnbind(user.getUcId(), teamId, carId, senderId);
        }
        log.info("[push]end");
    }
}
