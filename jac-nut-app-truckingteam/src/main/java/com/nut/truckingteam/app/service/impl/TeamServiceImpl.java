package com.nut.truckingteam.app.service.impl;

import com.nut.common.result.PagingInfo;
import com.nut.truckingteam.app.dao.CarDao;
import com.nut.truckingteam.app.dao.TeamDao;
import com.nut.truckingteam.app.dto.CarDto;
import com.nut.truckingteam.app.dto.CarModelAndSeriseDto;
import com.nut.truckingteam.app.form.GetDriverCarsForm;
import com.nut.truckingteam.app.form.GetTeamCarsModelAndSeriseForm;
import com.nut.truckingteam.app.pojo.CarInfoPojo;
import com.nut.truckingteam.app.pojo.CarRolePojo;
import com.nut.truckingteam.app.service.TeamService;
import com.nut.truckingteam.app.service.impl.TruckTeamBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.truckingteam.app.service.ipml
 * @Author: yzl
 * @CreateTime: 2021-06-27 14:49
 * @Version: 1.0
 */
@Slf4j
@Service
public class TeamServiceImpl extends TruckTeamBaseService implements TeamService {
    private static final String YES = "1";

    @Autowired
    private TeamDao teamMapper;

    @Autowired
    private CarDao carDao;

    @Override
    public PagingInfo<CarDto> getDriverCars(GetDriverCarsForm form) {
        // 查询相关车辆
        List<CarRolePojo> carRolePojos = carDao.selectUserRelatedCar(form.getAutoIncreaseId());

        // 全部车辆ID
        List<String> carIds = carRolePojos.stream().map(CarRolePojo::getCarId).distinct().collect(Collectors.toList());

        // 查询车辆基础信息
        List<CarInfoPojo> cars = new ArrayList<>();
        if (carIds.size() > 0) {
            cars = carDao.selectByCarIdIn(carIds);
        }

        List<CarDto> dtoList = new ArrayList<>();
        for (CarInfoPojo pojo : cars) {
            CarDto dto = new CarDto();
            dto.setCarId(pojo.getCarId());
            dto.setCarNumber(pojo.getCarNumber());
            dto.setVin(pojo.getVin());
            dtoList.add(dto);
        }

        // 分页
        PagingInfo<CarDto> pagingInfo = new PagingInfo<>();
        if (!YES.equals(form.getReturnAll())) {
            dtoList = paging(dtoList, form.getPage_number(), form.getPage_size(), pagingInfo);
        } else {
            pagingInfo.setTotal(dtoList.isEmpty() ? 0 : 1);
            pagingInfo.setTotal(dtoList.size());
        }
        pagingInfo.setList(dtoList);
        return pagingInfo;
    }

    @Override
    public List<CarModelAndSeriseDto> getTeamCarsModelAndSerise(GetTeamCarsModelAndSeriseForm command) {
        log.info("--------getTeamCarsModelAndSerise start--------");
        List<CarDto> dtoList = teamMapper.queryRankCarsByUserId(command.getAutoIncreaseId());
        if (dtoList == null) {
            log.info("--------getTeamCarsModelAndSerise END--------");
            return Collections.emptyList();
        }

        Map<String, CarDto> dtoMap = new HashMap<>(dtoList.size());
        for (CarDto carDto : dtoList) {
            if (carDto == null) {
                continue;
            }
            if (StringUtils.isBlank(carDto.getCarModelId())) {
                continue;
            }
            if (dtoMap.containsKey(carDto.getCarModelId())) {
                continue;
            }
            dtoMap.put(carDto.getCarModelId(), carDto);
        }

        List<CarModelAndSeriseDto> modelAndSeriseDtos = new ArrayList<>(dtoMap.keySet().size());
        for (String key : dtoMap.keySet()) {
            CarDto carDto = dtoMap.get(key);
            CarModelAndSeriseDto dto = new CarModelAndSeriseDto();
            dto.setEngine(carDto.getEngine());
            dto.setModelId(carDto.getCarModelId());
            dto.setModelName(carDto.getCarModelName());
            dto.setSeriseId(carDto.getCarSeriesId());
            dto.setSeriseName(carDto.getCarSeriesName());
            dto.setEmission(carDto.getEmission());
            dto.setDriverTypeName(carDto.getDriverTypeName());
            dto.setCarTypeName(carDto.getCarTypeName());
            modelAndSeriseDtos.add(dto);
        }

        log.info("--------getTeamCarsModelAndSerise END--------");
        return modelAndSeriseDtos;
    }

    /*@Autowired
    private CarDao carDao;

    private static final String YES = "1";

    *//**
     * 作为司机和管理的车队车辆列表(去重)
     *//*
    public PagingInfo<CarDto> getDriverCars(GetDriverCarsForm command) {
        log.info("[getDriverCars]start");

        // 查询相关车辆ID
        List<CarRolePojo> carRelations = carDao.selectUserRelatedCar(command.getAutoIncreaseId());
        log.info("[getDriverCars]carRelations.size:{}", carRelations.size());

        // 全部车辆ID
        List<String> carIds = carRelations.stream().map(CarRolePojo::getCarId).distinct().collect(Collectors.toList());
        log.info("[getDriverCars]carIds.size:{}", carIds.size());

        // 查询车辆基础信息
        List<CarInfoPojo> cars = new ArrayList<>();
        if (carIds.size() > 0) {
            cars = carDao.selectByCarIdIn(carIds);
        }
        log.info("[getDriverCars]cars.size:{}", cars.size());

        List<CarDto> dtoList = new ArrayList<>();
        for (CarInfoPojo pojo : cars) {
            CarDto dto = new CarDto();
            dto.setCarId(pojo.getCarId());
            dto.setCarNumber(pojo.getCarNumber());
            dto.setVin(pojo.getVin());
            dtoList.add(dto);
        }

        // 分页
        PagingInfo<CarDto> pagingInfo = new PagingInfo<>();
        if (!YES.equals(command.getReturnAll())) {
            dtoList = paging(dtoList, command.getPage_number(), command.getPage_size(), pagingInfo);
        } else {
            pagingInfo.setPage_total(dtoList.isEmpty() ? 0 : 1);
            pagingInfo.setTotal(dtoList.size());
        }
        pagingInfo.setList(dtoList);
        log.info("[getDriverCars]end");
        return pagingInfo;
    }*/

}
