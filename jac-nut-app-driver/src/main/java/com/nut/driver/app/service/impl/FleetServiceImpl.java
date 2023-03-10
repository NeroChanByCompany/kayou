package com.nut.driver.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.constant.AppTypeEnum;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.dao.*;
import com.nut.driver.app.domain.FltCarDriverMapping;
import com.nut.driver.app.domain.FltFleet;
import com.nut.driver.app.domain.FltFleetUserMapping;
import com.nut.driver.app.dto.*;
import com.nut.driver.app.entity.FltFleetCarMappingEntity;
import com.nut.driver.app.entity.FltFleetEntity;
import com.nut.driver.app.entity.FltFleetUserMappingEntity;
import com.nut.driver.app.form.*;
import com.nut.driver.app.pojo.FleetInfoPojo;
import com.nut.driver.app.service.*;
import com.nut.driver.common.constants.FleetRoleEnum;
import com.nut.driver.common.utils.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service("FleetService")
@Slf4j
public class FleetServiceImpl implements FleetService {

    private static final String YES = "1";

    @Autowired
    private FleetCarService fleetCarService;

    @Autowired
    private CarDao carMapper;

    @Autowired
    private CommonCustomMaintainService commonCustomMaintainService;

    @Autowired
    private FltFleetCarMappingDao fltFleetCarMappingMapper;

    @Autowired
    private AdminService adminService;

    @Autowired
    private FltFleetUserMappingDao fltFleetUserMappingMapper;

    @Autowired
    private DriverService driverService;

    @Autowired
    private FltFleetDao fltFleetMapper;

    @Autowired
    private AsyncPushSystemMsgService asyncPushSystemMsgService;

    @Autowired
    private FltCarDriverMappingDao fltCarDriverMappingMapper;

    @Autowired
    private UserDao userMapper;

    @Autowired
    private PageUtil pageUtil;

    @Override
    public HttpCommandResultWithData query(UserFleetsForm command) {
        log.info("[query]start");
        HttpCommandResultWithData<UserFleetsDto> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // ???????????????
        List<FleetInfoPojo> queryList = fltFleetUserMappingMapper.selectByUserId(command.getAutoIncreaseId());
        log.info("[query]queryList.size:{}", queryList.size());
        // ??????????????????????????????????????????????????????
        Map<Long, FleetInfoPojo> fleetById = new HashMap<>(16);
        for (FleetInfoPojo pojo : queryList) {
            Long key = pojo.getTeamId();
            if (fleetById.containsKey(key)) {
                // ???????????????
                if (compareRole(fleetById.get(key), pojo) < 0) {
                    fleetById.put(key, pojo);
                }
            } else {
                // ???????????????
                fleetById.put(key, pojo);
            }
        }
        log.info("[query]distinct size:{}", fleetById.keySet().size());

        // ??????
        Predicate<FleetInfoPojo> filter = e -> true;
        if (StringUtil.isNotEmpty(command.getName())) {
            filter = e -> e.getName().contains(command.getName());
        }
        List<FleetInfoPojo> fleets = fleetById.values().stream().filter(filter).collect(Collectors.toList());

        // ??????
        PagingInfo<FleetInfoPojo> pagingInfo;
        if (YES.equals(command.getReturnAll())) {
            pagingInfo = new PagingInfo<>();
            pagingInfo.setTotal(fleets.size());
            pagingInfo.setPage_total(fleets.isEmpty() ? 0 : 1);
            pagingInfo.setList(fleets);
        } else {
            pagingInfo = pageUtil.paging(fleets, command.getPage_number(), command.getPage_size());
        }
        log.info("[query]elements in page:{}", pagingInfo.getList().size());

        // ????????????
        List<UserFleetsDto.FleetInfo> dtoList = new ArrayList<>();
        if (pagingInfo.getList().size() > 0) {
            List<Long> teamIds = pagingInfo.getList().stream().map(FleetInfoPojo::getTeamId).collect(Collectors.toList());
            List<FleetInfoPojo> countList = fltFleetCarMappingMapper.countCarsByTeamIdIn(teamIds);
            log.info("[query]countList.size:{}", countList.size());
            // ???map
            Map<Long, Integer> countMap = countList.stream().collect(Collectors.toMap(FleetInfoPojo::getTeamId, FleetInfoPojo::getCarTotal));
            for (FleetInfoPojo pojo : pagingInfo.getList()) {
                UserFleetsDto.FleetInfo dto = pojoToDto(pojo);
                dto.setCarTotal(countMap.getOrDefault(pojo.getTeamId(), 0));
                dtoList.add(dto);
            }
        }

        UserFleetsDto data = new UserFleetsDto();
        Long allCarTotal = carMapper.getUserCarNum(command.getAutoIncreaseId());
        if (allCarTotal != null) {
            data.setCarTotal(allCarTotal.intValue());
        } else {
            data.setCarTotal(0);
        }
        data.setTotal(pagingInfo.getTotal());
        data.setPage_total(pagingInfo.getPage_total());
        data.setList(dtoList);
        result.setData(data);
        log.info("[query]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData detail(UserFleetDetailForm command) throws JsonProcessingException {
        log.info("[detail]start");
        HttpCommandResultWithData<UserFleetDetailDto> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // ???????????????
        List<FleetInfoPojo> queryList = fltFleetUserMappingMapper.selectByUserIdAndTeamId(command.getAutoIncreaseId(), command.getTeamId());
        log.info("[detail]queryList.size:{}", queryList.size());
        if (queryList.isEmpty()) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("????????????????????????");
            return result;
        }

        // ??????????????????
        FleetInfoPojo fleet = queryList.get(0);
        for (FleetInfoPojo pojo : queryList) {
            if (compareRole(fleet, pojo) < 0) {
                fleet = pojo;
            }
        }
        UserFleetDetailDto data = pojoToDetailDto(fleet);
        setAdmin(command.getTeamId(), data);
        setDriver(command.getTeamId(), data);
        setCar(command.getTeamId(), data);
        result.setData(data);
        log.info("[detail]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData update(UserFleetUpdateForm command) {
        log.info("[update]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // ????????????????????????????????????????????????
        List<FltFleetUserMappingEntity> queryList = fltFleetUserMappingMapper.selectByTeamId(command.getTeamId());
        log.info("[update]queryList.size:{}", queryList.size());
        if (queryList.stream().noneMatch(e -> e.getUserId().equals(command.getAutoIncreaseId())
                && e.getRole() == FleetRoleEnum.CREATOR.code())) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("???????????????");
            return result;
        }

        // ??????????????????
        FltFleet fltFleet = fltFleetMapper.selectByPrimaryKey(command.getTeamId());
        if (fltFleet == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("????????????????????????");
            return result;
        }

        // ???????????????
        updateRecord(fltFleet.getId(), command);
        // ??????
        push(command.getTeamId(), fltFleet.getName(), command.getName(), command.getUserId());
        log.info("[update]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData add(UserFleetAddForm command) {
        log.info("[add]start");
        HttpCommandResultWithData<Map> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // ???????????????
        Long newTeamId = insertRecord(command);

        // ????????????ID
        Map<String, Long> data = new HashMap<>(3);
        data.put("teamId", newTeamId);
        result.setData(data);
        log.info("[add]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData delete(UserFleetDeleteForm command) {
        log.info("[delete]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // ????????????????????????????????????????????????
        List<FltFleetUserMappingEntity> queryList = fltFleetUserMappingMapper.selectByTeamId(command.getTeamId());
        log.info("[delete]queryList.size:{}", queryList.size());
        if (queryList.stream().noneMatch(e -> e.getUserId().equals(command.getAutoIncreaseId())
                && e.getRole() == FleetRoleEnum.CREATOR.code())) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("???????????????");
            return result;
        }

        // ???????????????????????????
        String teamName = "";
        FltFleet fltFleet = fltFleetMapper.selectByPrimaryKey(command.getTeamId());
        if (fltFleet != null) {
            teamName = fltFleet.getName();
        }

        // ????????????????????????
        deleteRecord(queryList, command);
        // ??????
        push(queryList, teamName, command.getUserId());
        log.info("[delete]end");
        return result;
    }
    /**
     * ??????
     */
    private void push(List<FltFleetUserMappingEntity> queryList, String teamName, String senderId) {
        log.info("[push]start");
        List<Long> userIds = queryList.stream().map(FltFleetUserMappingEntity::getUserId).distinct().collect(Collectors.toList());
        if (userIds.size() > 0) {
            List<String> receiverIds = userMapper.queryUcIdById(userIds);
            if (receiverIds.size() > 0) {
                asyncPushSystemMsgService.pushDeleteFleet(String.join(",", receiverIds), teamName, senderId);
            }
        }
        log.info("[push]end");
    }
    /**
     * ????????????????????????
     */
    private void deleteRecord(List<FltFleetUserMappingEntity> queryList, UserFleetDeleteForm command) {
        log.info("[deleteRecord]start");
        // ???????????????????????????
        int cnt = fltFleetMapper.deleteByPrimaryKey(command.getTeamId());
        log.info("[deleteRecord]flt_fleet deleted:{}", cnt);

        // ?????????????????????????????????
        List<Long> idList = queryList.stream().map(FltFleetUserMappingEntity::getId).collect(Collectors.toList());
        if (idList.size() > 0) {
            cnt = fltFleetUserMappingMapper.batchDelete(idList);
            log.info("[deleteRecord]flt_fleet_user_mapping deleted:{}", cnt);
        }

        // ??????????????????????????????
        List<FltFleetCarMappingEntity> fltFleetCarMappings = fltFleetCarMappingMapper.selectByTeamId(command.getTeamId());
        idList = fltFleetCarMappings.stream().map(FltFleetCarMappingEntity::getId).collect(Collectors.toList());
        if (idList.size() > 0) {
            cnt = fltFleetCarMappingMapper.batchDelete(idList);
            log.info("[deleteRecord]flt_fleet_car_mapping deleted:{}", cnt);
        }

        // ??????????????????????????????
        List<FltCarDriverMapping> fltCarDriverMappings = fltCarDriverMappingMapper.selectByTeamId(command.getTeamId());
        idList = fltCarDriverMappings.stream().map(FltCarDriverMapping::getId).collect(Collectors.toList());
        if (idList.size() > 0) {
            cnt = fltCarDriverMappingMapper.batchDelete(idList);
            log.info("[deleteRecord]flt_car_driver_mapping deleted:{}", cnt);
        }

        // ???????????????????????????
        List<Long> userIds = queryList.stream().map(FltFleetUserMappingEntity::getUserId).distinct().collect(Collectors.toList());
        for (Long relatedUserId : userIds) {
            commonCustomMaintainService.updateUserCustomMaintain(relatedUserId);
        }
        log.info("[deleteRecord]end");
    }
    /**
     * ???????????????
     */
    private Long insertRecord(UserFleetAddForm command) {
        log.info("[insertRecord]start");
        // ???????????????
        FltFleet insFleet = new FltFleet();
        insFleet.setName(command.getName());
        insFleet.setAvatar(command.getAvatar());
        insFleet.setCreateType(AppTypeEnum.APP_C.code());
        Date now = new Date();
        insFleet.setCreateTime(now);
        insFleet.setUpdateTime(now);
        fltFleetMapper.insertSelective(insFleet);
        log.info("[insertRecord]flt_fleet id:{}", insFleet.getId());

        // ???????????????????????????
        FltFleetUserMapping insFleetUserMapping = new FltFleetUserMapping();
        insFleetUserMapping.setUserId(command.getAutoIncreaseId());
        insFleetUserMapping.setTeamId(insFleet.getId());
        insFleetUserMapping.setRole(FleetRoleEnum.CREATOR.code());
        insFleetUserMapping.setCreateType(AppTypeEnum.APP_C.code());
        insFleetUserMapping.setCreateUserId(command.getAutoIncreaseId());
        insFleetUserMapping.setCreateTime(now);
        insFleetUserMapping.setUpdateTime(now);
        fltFleetUserMappingMapper.insertSelective(insFleetUserMapping);
        log.info("[insertRecord]end");
        return insFleet.getId();
    }

    /**
     * ??????
     */
    private void push(Long teamId, String oldName, String newName, String senderId) {
        log.info("[push]start");
        if (StringUtil.isNotEmpty(newName) && StringUtil.isNotEq(oldName, newName)) {
            asyncPushSystemMsgService.pushEditCarTeam(teamId, oldName, newName, senderId);
        }
        log.info("[push]end");
    }
    /**
     * ???????????????
     */
    private void updateRecord(Long id, UserFleetUpdateForm command) {
        log.info("[updateRecord]start");
        if (StringUtil.isNotEmpty(command.getName()) || StringUtil.isNotEmpty(command.getAvatar())) {
            FltFleet updRecord = new FltFleet();
            updRecord.setId(id);
            if (StringUtil.isNotEmpty(command.getName())) {
                updRecord.setName(command.getName());
            }
            if (StringUtil.isNotEmpty(command.getAvatar())) {
                updRecord.setAvatar(command.getAvatar());
            }
            updRecord.setUpdateTime(new Date());
            fltFleetMapper.updateByPrimaryKeySelective(updRecord);
        }
        log.info("[updateRecord]end");
    }

    /**
     * ????????????
     */
    private int compareRole(FleetInfoPojo a, FleetInfoPojo b) {
        return FleetRoleEnum.getWeight(a.getRole()) - FleetRoleEnum.getWeight(b.getRole());
    }
    /**
     * ????????????
     */
    private UserFleetsDto.FleetInfo pojoToDto(FleetInfoPojo pojo) {
        UserFleetsDto.FleetInfo dto = new UserFleetsDto.FleetInfo();
        dto.setTeamId(pojo.getTeamId());
        dto.setName(pojo.getName());
        dto.setAvatar(pojo.getAvatar());
        return dto;
    }
    /**
     * ????????????
     */
    private UserFleetDetailDto pojoToDetailDto(FleetInfoPojo pojo) {
        UserFleetDetailDto dto = new UserFleetDetailDto();
        dto.setName(pojo.getName());
        dto.setAvatar(pojo.getAvatar());
        dto.setCreatorName(pojo.getCreatorName());
        dto.setCreatorPhone(pojo.getCreatorPhone());
        dto.setRole(pojo.getRole());
        return dto;
    }
    /**
     * ????????????????????????8??????
     */
    private void setAdmin(Long teamId, UserFleetDetailDto dto) {
        log.info("[setAdmin]start");
        FleetAdminsForm com = new FleetAdminsForm();
        com.setTeamId(teamId);
        com.setPage_number("0");
        com.setPage_size("8");
        HttpCommandResultWithData<PagingInfo<FleetAdminsDto>> res = adminService.query(com);
        if (res.getResultCode() == ECode.SUCCESS.code() && res.getData() != null && res.getData().getList() != null) {
            log.info("[setAdmin]dataList.size:{}", res.getData().getList().size());
            List<UserFleetDetailDto.UserInfo> admins = new ArrayList<>();
            for (FleetAdminsDto d : res.getData().getList()) {
                UserFleetDetailDto.UserInfo admin = new UserFleetDetailDto.UserInfo();
                admin.setNickname(d.getNickname());
                admin.setPhone(d.getPhone());
                admin.setUserId(d.getUserId());
                admins.add(admin);
            }
            dto.setAdminList(admins);
            dto.setAdminTotal(res.getData().getTotal());
        }
        log.info("[setAdmin]end");
    }
    /**
     * ?????????????????????8??????
     */
    private void setDriver(Long teamId, UserFleetDetailDto dto) {
        log.info("[setDriver]start");
        FleetDriversForm com = new FleetDriversForm();
        com.setTeamId(teamId);
        com.setPage_number("0");
        com.setPage_size("8");
        HttpCommandResultWithData<PagingInfo<FleetDriversDto>> res = driverService.query(com);
        if (res.getResultCode() == ECode.SUCCESS.code() && res.getData() != null && res.getData().getList() != null) {
            log.info("[setDriver]dataList.size:{}", res.getData().getList().size());
            List<UserFleetDetailDto.UserInfo> drivers = new ArrayList<>();
            for (FleetDriversDto d : res.getData().getList()) {
                UserFleetDetailDto.UserInfo driver = new UserFleetDetailDto.UserInfo();
                driver.setNickname(d.getNickname());
                driver.setPhone(d.getPhone());
                driver.setUserId(d.getUserId());
                drivers.add(driver);
            }
            dto.setDriverList(drivers);
            dto.setDriverTotal(res.getData().getTotal());
        }
        log.info("[setDriver]end");
    }
    /**
     * ?????????????????????5??????
     */
    private void setCar(Long teamId, UserFleetDetailDto dto) throws JsonProcessingException {
        log.info("[setCar]start");
        FleetCarsForm com = new FleetCarsForm();
        com.setTeamId(teamId);
        com.setPage_number("0");
        com.setPage_size("5");
        HttpCommandResultWithData<PagingInfo<FleetCarsDto>> res = fleetCarService.query(com);
        if (res.getResultCode() == ECode.SUCCESS.code() && res.getData() != null && res.getData().getList() != null) {
            log.info("[setCar]dataList.size:{}", res.getData().getList().size());
            List<UserFleetDetailDto.CarInfo> cars = new ArrayList<>();
            for (FleetCarsDto d : res.getData().getList()) {
                UserFleetDetailDto.CarInfo car = new UserFleetDetailDto.CarInfo();
                car.setCarId(d.getCarId());
                car.setCarNumber(d.getCarNumber());
                car.setChassisNum(d.getChassisNum());
                car.setLon(d.getLon());
                car.setLat(d.getLat());
                car.setLocation(d.getLocation());
                cars.add(car);
            }
            dto.setCarList(cars);
            dto.setCarTotal(res.getData().getTotal());
        }
        log.info("[setCar]end");
    }
}
