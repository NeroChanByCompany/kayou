package com.nut.driver.app.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.utils.CharaterUtil;
import com.nut.common.utils.DateUtil;
import com.nut.driver.app.client.LocationServiceClient;
import com.nut.driver.app.dao.CarDao;
import com.nut.driver.app.dao.MaintainRuleDao;
import com.nut.driver.app.dao.UserDao;
import com.nut.driver.app.dto.*;
import com.nut.driver.app.entity.CarEntity;
import com.nut.driver.app.entity.CarMaintainEntity;
import com.nut.driver.app.entity.CarMaintainRecordEntity;
import com.nut.driver.app.entity.MaintainRuleEntity;
import com.nut.driver.app.enums.SumTypeEnum;
import com.nut.driver.app.form.GetCarLocationForm;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.NumberFormatUtil;
import com.nut.driver.app.dao.MaintainNewDao;
import com.nut.driver.app.form.QueryCarMaintainListForm;
import com.nut.driver.app.form.QueryCarNewMaintainDetailsForm;
import com.nut.driver.app.form.UpdateCarNewMaintainFrom;
import com.nut.driver.app.pojo.*;
import com.nut.driver.app.service.MaintainNewService;
import com.nut.driver.app.service.MaintainRuleService;
import com.nut.driver.common.config.RemindMaintainInterface;
import com.nut.driver.common.utils.DateUtils;
import com.nut.driver.common.utils.PageUtil;
import com.nut.driver.common.wrapper.MaintainCategoryEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author liuBing
 * @Classname MaintainNewServiceImpl
 * @Description TODO
 * @Date 2021/6/23 17:17
 */
@Slf4j
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class MaintainNewServiceImpl implements MaintainNewService {

    @Autowired
    private CarDao carDao;
    @Autowired
    private MaintainNewDao maintainNewDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private LocationServiceClient locationServiceClient;

    @Autowired
    private RemindMaintainInterface remindMaintainInterface;
    @Autowired
    private PageUtil pageUtil;

    @Autowired
    private MaintainRuleService maintainRuleService;


    @Value("${special_regex:\\\\t|\\\\r|\\\\n}")
    private String specialRegex;

    @Value("${maintain_record_separator:!=!}")
    private String separator;

    /**
     * ??????????????????
     *
     * @param form
     * @return
     */
    @Override
    @Deprecated
    public PagingInfo<OldMaintainPojo> queryCarMaintainPage(QueryCarMaintainListForm form) {
        return new PagingInfo<OldMaintainPojo>().setList(new ArrayList<OldMaintainPojo>());
    }


    @Override
    public PagingInfo<MaintainPojo> getCarMaintainPage(QueryCarMaintainListForm form) {
        /**1 ???????????????????????????*/
        List<PhyExaPojo> cars = carDao.queryPhyCarsByUserId(form.getAutoIncreaseId());
        log.info("[??????????????????????????????????????????????????? start]");
        if (CollectionUtils.isEmpty(cars)) {
            return new PagingInfo<>();
        }
        /**4 ??????????????????????????????*/
        log.info("[?????????????????????????????? start]");
        List<PhyExaPojo> phyExaPojos = new ArrayList<>();
        List<MaintainRuleEntity> maintainRuleEntities = maintainRuleService.list();
        for (MaintainRuleEntity maintainRuleEntity : maintainRuleEntities) {
            for (PhyExaPojo pojo : cars) {
                if (StringUtils.isNotBlank(maintainRuleEntity.getStructureNum()) && StringUtils.isNotBlank(pojo.getCode())) {
                    if (maintainRuleEntity.getStructureNum().contains(pojo.getCode())) {
                        if (StringUtils.isNotBlank(maintainRuleEntity.getMaintainContent())) {
                            pojo.setMaintainContent(maintainRuleEntity.getMaintainContent());
                        }
                        if (maintainRuleEntity.getFirstMileage() != null) {
                            pojo.setFirstMileage(Long.valueOf(maintainRuleEntity.getFirstMileage()));
                        }
                        if (maintainRuleEntity.getFirstMonth() != null) {
                            pojo.setFirstMonth(Long.valueOf(maintainRuleEntity.getFirstMonth()));
                        }
                        if (maintainRuleEntity.getRoutineMileage() != null) {
                            pojo.setRoutineMileage(Long.valueOf(maintainRuleEntity.getRoutineMileage()));
                        }
                        if (maintainRuleEntity.getRoutineMonth() != null) {
                            pojo.setRoutineMonth(Long.valueOf(maintainRuleEntity.getRoutineMonth()));
                        }
                        phyExaPojos.add(pojo);
                    }
                }
            }
        }

        //??????????????????
        log.info("[????????????????????????????????? start]");
        Map<String, Object> locationMap = queryRealTimeData(phyExaPojos);
        log.info("[?????????????????? start]");
        List<MaintainPojo> maintainPojos = assembleRemainingMileage(phyExaPojos, locationMap);
        //????????????????????????????????????????????????
        List<MaintainPojo> groupMainTainPojos = this.groupByCount(maintainPojos);
        //????????????
        PagingInfo<MaintainPojo> maintainPojoPagingInfo = carMaintainPaging(form, groupMainTainPojos);
        log.info("queryCarMaintainPage end return:{}", maintainPojoPagingInfo);
        return maintainPojoPagingInfo;
    }

    /**
     * ????????????
     *
     * @param maintainPojos
     * @return
     */
    private List<MaintainPojo> groupByCount(List<MaintainPojo> maintainPojos) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<MaintainPojo> list = new ArrayList<>();
        Map<String, List<MaintainPojo>> listMap = maintainPojos.stream().filter(maintainPojo -> {
            if (StringUtils.isNotBlank(maintainPojo.getVin())) {
                return true;
            }
            return false;
        }).collect(Collectors.groupingBy(MaintainPojo::getVin));
        for (Map.Entry<String, List<MaintainPojo>> stringListEntry : listMap.entrySet()) {
            List<MaintainPojo> maintainPojoList = stringListEntry.getValue();
            if (maintainPojoList != null) {
                MaintainPojo pojo = new MaintainPojo();
                List<MaintainPojo> pojoList = maintainPojoList.stream()
                        .sorted(Comparator.comparing(MaintainPojo::getRemainingMileage, Comparator.nullsLast(Double::compareTo))
                                .thenComparing(MaintainPojo::getNextDate, Comparator.nullsLast(String::compareTo)))
                        .collect(Collectors.toList());
                if (StringUtils.isNotBlank(pojoList.get(0).getVin())) {
                    pojo.setVin(pojoList.get(0).getVin());
                }
                if (StringUtils.isNotBlank(pojoList.get(0).getCarNumber())) {
                    pojo.setCarNumber(pojoList.get(0).getCarNumber());
                }
                if (pojoList.size() > 0) {
                    pojo.setNextCount(String.valueOf(pojoList.size()));
                }
                if (pojoList.get(0).getMaintainType() != null) {
                    pojo.setMaintainType(pojoList.get(0).getMaintainType());
                }
                if (pojoList.get(0).getRemainingMileage() != null) {
                    pojo.setRemainingMileage(pojoList.get(0).getRemainingMileage());
                }
                if (StringUtils.isNotBlank(pojoList.get(0).getNextDate())) {
                    pojo.setNextDate(pojoList.get(0).getNextDate());
                }

                list.add(pojo);
            }

        }
        return list;
    }


    @Override
    public CarDetailedMaintenanceDTO getCarNewMaintainDetails(QueryCarNewMaintainDetailsForm form) {
        //1????????????????????????????????????????????????
        PhyExaPojo pojo = carDao.selectPhyByVin(form.getCarVin());
        //2????????????????????????????????????
        CarEntity car = new CarEntity();
        if (StringUtils.isNotBlank(pojo.getCarVin())) {
            car.setCarVin(pojo.getCarVin());
        }
        String actualMileage = getActualMileage(car);
        //3???????????????????????????
        List<MaintainRuleEntity> list = maintainRuleService.list();
        //??????????????????????????????????????????
        List<PhyExaPojo> pojos = list.stream().map(maintainRuleEntity -> {
                    if (StringUtils.isNotBlank(maintainRuleEntity.getStructureNum()) && StringUtils.isNotBlank(pojo.getCode())) {
                        if (maintainRuleEntity.getStructureNum().contains(pojo.getCode())) {
                            PhyExaPojo phyExaPojo = new PhyExaPojo();
                            if (StringUtils.isNotBlank(maintainRuleEntity.getMaintainContent())) {
                                phyExaPojo.setMaintainContent(maintainRuleEntity.getMaintainContent());
                            }
                            if (maintainRuleEntity.getFirstMileage() != null) {
                                phyExaPojo.setFirstMileage(Long.valueOf(maintainRuleEntity.getFirstMileage()));
                            }
                            if (maintainRuleEntity.getFirstMonth() != null) {
                                phyExaPojo.setFirstMonth(Long.valueOf(maintainRuleEntity.getFirstMonth()));
                            }
                            if (maintainRuleEntity.getRoutineMileage() != null) {
                                phyExaPojo.setRoutineMileage(Long.valueOf(maintainRuleEntity.getRoutineMileage()));
                            }
                            if (maintainRuleEntity.getRoutineMonth() != null) {
                                phyExaPojo.setRoutineMonth(Long.valueOf(maintainRuleEntity.getRoutineMonth()));
                            }
                            if (maintainRuleEntity.getSumType() != null) {
                                phyExaPojo.setSumType(maintainRuleEntity.getSumType());
                            }
                            if (StringUtils.isNotBlank(pojo.getCarId())) {
                                phyExaPojo.setCarId(pojo.getCarId());
                            }
                            if (StringUtils.isNotBlank(pojo.getCarNumber())) {
                                phyExaPojo.setCarNumber(pojo.getCarNumber());
                            }
                            if (StringUtils.isNotBlank(pojo.getCarVin())) {
                                phyExaPojo.setCarVin(pojo.getCarVin());
                            }
                            if (StringUtils.isNotBlank(pojo.getCode())) {
                                phyExaPojo.setCode(pojo.getCode());
                            }
                            return phyExaPojo;
                        }

                    }
                    return new PhyExaPojo();
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        //?????????????????????????????????
        CarDetailedMaintenanceDTO carDetailedMaintenanceDTO = this.sortedMaintainDateil(form, pojos, actualMileage, pojo);
        log.info("getCarNewMaintainDetails end return:{}", carDetailedMaintenanceDTO);
        return carDetailedMaintenanceDTO;
    }

    /**
     * ????????????????????????
     *
     * @param pojos
     * @param actualMileage
     * @return
     */
    private CarDetailedMaintenanceDTO sortedMaintainDateil(QueryCarNewMaintainDetailsForm form, List<PhyExaPojo> pojos, String actualMileage, PhyExaPojo exaPojo) {
        CarDetailedMaintenanceDTO carDetailedMaintenanceDTO = new CarDetailedMaintenanceDTO();
        CarNewMaintainDTO carNewMaintainDTO = new CarNewMaintainDTO();
        AtomicInteger count = new AtomicInteger(0);
        List<MaintainDetailsPojo> collect = pojos.stream().map(
                phyExaPojo -> {
                    MaintainDetailsPojo pojo = new MaintainDetailsPojo();
                    Long mileage = 0L;
                    Date oldDate = null;
                    //??????????????????
                    if (StringUtils.isNotBlank(phyExaPojo.getCarVin())) {
                        oldDate = carDao.queryCarSales(phyExaPojo.getCarVin());
                    }
                    if (StringUtils.isNotBlank(actualMileage)) {
                        mileage = Long.parseLong(actualMileage);
                    }
                    //????????????
                    if (phyExaPojo.getFirstMileage() != null || phyExaPojo.getFirstMonth() != null) {
                        if (phyExaPojo.getFirstMileage() != null) {
                            //??????????????????
                            Long fLong = (phyExaPojo.getFirstMileage() - mileage);
                            //??????????????????????????????
                            if (fLong > 0) {
                                pojo.setRemainingMileage(Double.valueOf(fLong));
                                pojo.setItemsName(SumTypeEnum.getMessage(phyExaPojo.getSumType()) + "--" + "??????--" + phyExaPojo.getMaintainContent());
                            }
                            if (1000 > fLong && fLong > 0) {
                                pojo.setMaintainType(1);
                                count.set(count.incrementAndGet());
                            }
                        }
                        if (pojo.getRemainingMileage() == null) {
                            if (phyExaPojo.getRoutineMileage() != null) {
                                long pMileage = 0L;
                                //??????????????????
                                if (phyExaPojo.getFirstMileage() != null) {
                                    pMileage = mileage - phyExaPojo.getFirstMileage();
                                    pMileage = pMileage > 0 ? pMileage : 0L;
                                } else {
                                    pMileage = mileage;
                                }
                                //??????????????????????????????
                                if (mileage > 0) {
                                    long rLong = phyExaPojo.getRoutineMileage() - (mileage % phyExaPojo.getRoutineMileage());
                                    if (rLong > 0) {
                                        pojo.setRemainingMileage(Double.valueOf(rLong));
                                        pojo.setItemsName(SumTypeEnum.getMessage(phyExaPojo.getSumType()) + "--" + "??????--" + phyExaPojo.getMaintainContent());
                                    }
                                    if (1000 > rLong && rLong > 0) {
                                        pojo.setMaintainType(1);
                                        count.set(count.incrementAndGet());
                                    }
                                }
                            }
                        }
                    }

                    //??????????????????
                    if (phyExaPojo.getFirstMonth() != null && oldDate != null) {
                        DateTime firstDate = cn.hutool.core.date.DateUtil.offsetMonth(oldDate, phyExaPojo.getFirstMonth().intValue());
                        long firstLongDate = cn.hutool.core.date.DateUtil.between(new Date(), firstDate, DateUnit.DAY, false);
                        if (0 <= firstLongDate) {
                            pojo.setNextDate(cn.hutool.core.date.DateUtil.format(firstDate, "yyyy-MM-dd"));
                            pojo.setItemsName(SumTypeEnum.getMessage(phyExaPojo.getSumType()) + "--" + "??????--" + phyExaPojo.getMaintainContent());
                        }
                        if (0 <= firstLongDate && firstLongDate < 5) {
                            if (pojo.getMaintainType() == null) {
                                count.set(count.incrementAndGet());
                            }
                            pojo.setMaintainType(1);
                        }
                    }
                    //????????????????????????????????????????????????
                    if (StringUtils.isBlank(pojo.getNextDate())) {
                        if (phyExaPojo.getRoutineMonth() != null) {
                            //??????????????????
                            if (phyExaPojo.getFirstMonth() != null && oldDate != null) {
                                oldDate = cn.hutool.core.date.DateUtil.offsetMonth(oldDate, phyExaPojo.getFirstMonth().intValue());
                            }
                            if (oldDate != null) {
                                Date newDate = cn.hutool.core.date.DateUtil.offsetMonth(oldDate, phyExaPojo.getRoutineMonth().intValue());
                                Date now = new Date();
                                while (0 < now.compareTo(newDate)) {
                                    newDate = cn.hutool.core.date.DateUtil.offsetMonth(newDate, phyExaPojo.getRoutineMonth().intValue());
                                }
                                long routingLongDate = cn.hutool.core.date.DateUtil.between(now, newDate, DateUnit.DAY, false);
                                if (0 <= routingLongDate) {
                                    pojo.setNextDate(cn.hutool.core.date.DateUtil.format(newDate, "yyyy-MM-dd"));
                                    pojo.setItemsName(SumTypeEnum.getMessage(phyExaPojo.getSumType()) + "--" + "??????--" + phyExaPojo.getMaintainContent());
                                }
                                if (0 <= routingLongDate && routingLongDate < 5) {
                                    if (pojo.getMaintainType() == null) {
                                        count.set(count.incrementAndGet());
                                    }
                                    pojo.setMaintainType(1);
                                }
                            }
                        }
                    }
                    pojo.setVin(phyExaPojo.getCarVin());
                    return pojo;
                }
        ).filter(
                maintainPojo -> StringUtils.isNotBlank(maintainPojo.getVin())
        ).sorted(
                Comparator.comparing(MaintainDetailsPojo::getRemainingMileage, Comparator.nullsLast(Double::compareTo))
                        .thenComparing(MaintainDetailsPojo::getNextDate, Comparator.nullsLast(String::compareTo))
        ).collect(Collectors.toList());

        PagingInfo<MaintainDetailsPojo> page = carMaintainDateilPaging(form, collect);
        carNewMaintainDTO.setCarId(exaPojo.getCarId());
        carNewMaintainDTO.setCarNumber(exaPojo.getCarNumber());
        carNewMaintainDTO.setChassisNum(exaPojo.getCarVin());
        carNewMaintainDTO.setTotalmileage(actualMileage);
        carNewMaintainDTO.setNextMaintenanceCount(count.get());
        carDetailedMaintenanceDTO.setCarNewMaintainDTO(carNewMaintainDTO);
        MaintainDetailsDTO maintainDetailsDTO = new MaintainDetailsDTO();
        maintainDetailsDTO.setList(page.getList());
        maintainDetailsDTO.setTotal(page.getTotal());
        Long pageTotal = page.getPage_total();
        maintainDetailsDTO.setPage_total(pageTotal.intValue());
        carDetailedMaintenanceDTO.setMaintainDetailsDTO(maintainDetailsDTO);
        return carDetailedMaintenanceDTO;
    }


    @Override
    public CarMaintainDetailDataDTO queryCarNewMaintainDetails(QueryCarNewMaintainDetailsForm form) {
        /**1???????????????????????????????????????*/
        pageUtil.getPage(form);
        Page<CarMaintainNewDetailPojo> page = maintainNewDao.queryCarNewMaintainDetail(form);
        CarEntity car = carDao.selectByVin(form.getCarVin());
        /**2????????????????????????????????????*/
        String actualMileage = getActualMileage(car);
        /**3?????????????????????-????????????*/
        CarNewMaintainDTO carDto = assembleCarNewMaintainDto(actualMileage, form.getCarVin(), car, page.getResult());
        /**4?????????????????????-????????????*/
        log.info("[?????????{}??????????????????-????????????start]", form.getCarVin());
        List<StatisticsCarMaintainPojo> statisticsList = maintainNewDao.queryCarMaintainCountByVin(form.getCarVin());
        Map<String, Integer> map = statisticsList.stream().collect(Collectors
                .toMap(StatisticsCarMaintainPojo::getMtcType, StatisticsCarMaintainPojo::getMtcTypeCount));
        CarNewMaintainDetailDTO maintainDetailDto = assembleCarNewMaintainDetailDto(actualMileage, page);
        /**5?????????????????????-??????????????????*/
        CarNewMaintainProgramCountDTO carNewMaintainProgramCountDto = assembleCarNewMaintainProgramCountDto(map);
        /**6?????????????????????*/
        log.info("[?????????{}????????????????????????start]", form.getCarVin());
        CarMaintainDetailDataDTO data = assembleCarMaintainDetailDataDto(carDto, maintainDetailDto,
                carNewMaintainProgramCountDto);
        log.info("queryCarNewMaintainDetails end return:{}", data);
        return data;
    }

    @Override
    public PagingInfo<CarMaintainRecordDTO> queryNewMaintainRecord(QueryCarNewMaintainDetailsForm form) {
        /**1?????????????????????????????????????????????*/
        pageUtil.getPage(form);
        Page<CarMaintainRecordPojo> page = maintainNewDao.queryNewMaintainRecord(form.getCarVin());
        /**2??????????????????????????????????????????????????????4??????*/
        log.info("[???????????????????????????{}]", form.getCarVin());
        List<CarMaintainRecordDTO> carMaintainRecordDtos = convertToCarMaintainRecordDtoList(page.getResult());
        PagingInfo<CarMaintainRecordDTO> pagingInfo = convertToPagingInfo(page, carMaintainRecordDtos);
        log.info("queryNewMaintainRecord end return:{}", form);
        return pagingInfo;
    }

    @Override
    public void updateCarNewMaintain(UpdateCarNewMaintainFrom form) {
        /**1?????????????????????*/
        List<CarMaintainEntity> carMaintains = maintainNewDao.queryCarMaintainByIds(Lists.newArrayList(form.getCarMaintainIdList().split(",")));
        if (CollectionUtils.isEmpty(carMaintains)) {
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "????????????id??????????????????????????????");
        }
        /**2????????????????????????????????????*/
        CarEntity car = carDao.selectByVin(carMaintains.get(0).getCarVin());
        String actualMileage = getActualMileage(car);
        /**3????????????????????????????????????*/
        log.info("[?????????{}?????????start]", carMaintains.get(0).getCarVin());
        checkMileageAndTime(actualMileage, form, carMaintains);
        /**4???????????????????????????*/
        updateCarMaintain(carMaintains, form);
        /**5???????????????????????????*/
        saveCarMaintainRecord(carMaintains, carMaintains.get(0).getCarVin(), carMaintains.get(0).getCarNumber(), form);
        log.info("updateCarNewMaintain end return:{null}");
    }


    /**
     * ?????????????????????????????????id
     *
     * @param autoIncreaseId
     */
    private List<String> getCarIdList(Long autoIncreaseId) {
        /*1?????????????????????**/
        List<PhyExaPojo> cars = carDao.queryPhyCarsByUserId(autoIncreaseId);
        List<String> carIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cars)) {
            carIdList = cars.stream().map(m -> m.getCarId()).collect(Collectors.toList());
        }

        return carIdList;
    }

    private List<String> assembleCarVin(Long autoIncreaseId) {
        UserInfoPojo pojo = userDao.findById(autoIncreaseId);
        if (Objects.nonNull(pojo) && StringUtils.isNotEmpty(pojo.getPhone())) {
            return maintainNewDao.queryMaintainVehicleTubeByPhone(pojo.getPhone());
        }
        return null;
    }


    /**
     * ???????????????????????????????????????????????????
     */
    private Map<String, Object> queryRealTimeData(List<PhyExaPojo> phyExaPojos) {
        // ???????????????????????????????????????????????????
        Map<String, Object> locateCarMap = new HashMap<>();
        try {
            GetCarLocationForm form = new GetCarLocationForm();
            List<String> mainVins = phyExaPojos.stream().map(PhyExaPojo::getCarVin).collect(Collectors.toList());
            form.setVins(StringUtils.join(mainVins, ","));
            HttpCommandResultWithData result = locationServiceClient.carLocation(form);
            if (result != null && ECode.SUCCESS.code() == result.getResultCode()) {
                // ??????????????????map
                locateCarMap = (Map<String, Object>) result.getData();
            }

        } catch (Exception e) {

        }
        return locateCarMap;
    }

    /**
     * ??????????????????
     *
     * @param pojoList
     * @param locationMap
     */
    private List<MaintainPojo> assembleRemainingMileage(List<PhyExaPojo> pojoList, Map<String, Object> locationMap) {
        return pojoList.stream()
                .map(phyExaPojo -> {
                    MaintainPojo pojo = new MaintainPojo();
                    CarLocationOutputDto locationDto = null;
                    try {
                        locationDto = JsonUtil.fromJson(JsonUtil.toJson(locationMap.get(phyExaPojo.getCarVin())), CarLocationOutputDto.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    Date oldDate = null;
                    //??????????????????
                    if (StringUtils.isNotBlank(phyExaPojo.getCarVin())) {
                        oldDate = carDao.queryCarSales(phyExaPojo.getCarVin());
                    }
                    if (Objects.isNull(locationDto) || Objects.isNull(locationDto.getMileage())) {
                        locationDto = new CarLocationOutputDto();
                        locationDto.setMileage(0D);
                    }
                    //????????????
                    if (phyExaPojo.getFirstMileage() != null || phyExaPojo.getFirstMonth() != null) {
                        if (phyExaPojo.getFirstMileage() != null) {
                            //??????????????????
                            Long fLong = (phyExaPojo.getFirstMileage() - locationDto.getMileage().longValue());
                            //??????????????????????????????
                            if (fLong > 0) {
                                pojo.setRemainingMileage(Double.valueOf(fLong));
                            }
                            if (1000 > fLong && fLong > 0) {
                                pojo.setMaintainType(1);
                            }
                        }
                        if (pojo.getRemainingMileage() == null) {
                            if (phyExaPojo.getRoutineMileage() != null) {
                                long mileage = 0L;
                                //??????????????????
                                if (phyExaPojo.getFirstMileage() != null) {
                                    mileage = locationDto.getMileage().longValue() - phyExaPojo.getFirstMileage();
                                    mileage = mileage > 0 ? mileage : 0L;
                                } else {
                                    mileage = locationDto.getMileage().intValue();
                                }
                                //??????????????????????????????
                                if (mileage > 0) {
                                    long rLong = phyExaPojo.getRoutineMileage() - (mileage % phyExaPojo.getRoutineMileage());
                                    if (rLong > 0) {
                                        pojo.setRemainingMileage(Double.valueOf(rLong));
                                    }
                                    if (1000 > rLong && rLong > 0) {
                                        pojo.setMaintainType(1);
                                    }
                                }
                            }
                        }
                    }

                    //??????????????????
                    if (phyExaPojo.getFirstMonth() != null && oldDate != null) {
                        DateTime firstDate = cn.hutool.core.date.DateUtil.offsetMonth(oldDate, phyExaPojo.getFirstMonth().intValue());
                        long firstLongDate = cn.hutool.core.date.DateUtil.between(new Date(), firstDate, DateUnit.DAY, false);
                        if (0 <= firstLongDate) {
                            pojo.setNextDate(cn.hutool.core.date.DateUtil.format(firstDate, "yyyy-MM-dd"));
                        }
                        if (0 <= firstLongDate && firstLongDate < 5) {
                            pojo.setMaintainType(1);
                        }
                    }
                    //????????????????????????????????????????????????
                    if (StringUtils.isBlank(pojo.getNextDate())) {
                        if (phyExaPojo.getRoutineMonth() != null) {
                            //??????????????????
                            if (phyExaPojo.getFirstMonth() != null && oldDate != null) {
                                oldDate = cn.hutool.core.date.DateUtil.offsetMonth(oldDate, phyExaPojo.getFirstMonth().intValue());
                            }
                            if (oldDate != null) {
                                Date newDate = cn.hutool.core.date.DateUtil.offsetMonth(oldDate, phyExaPojo.getRoutineMonth().intValue());
                                Date now = new Date();
                                while (0 < now.compareTo(newDate)) {
                                    newDate = cn.hutool.core.date.DateUtil.offsetMonth(newDate, phyExaPojo.getRoutineMonth().intValue());
                                }
                                long routingLongDate = cn.hutool.core.date.DateUtil.between(now, newDate, DateUnit.DAY, false);
                                if (0 <= routingLongDate) {
                                    pojo.setNextDate(cn.hutool.core.date.DateUtil.format(newDate, "yyyy-MM-dd"));
                                }
                                if (0 <= routingLongDate && routingLongDate < 5) {
                                    pojo.setMaintainType(1);
                                }
                            }
                        }
                    }
                    pojo.setVin(phyExaPojo.getCarVin());
                    pojo.setCarNumber(phyExaPojo.getCarNumber());
                    return pojo;
                })
                .collect(Collectors.toList());
    }

    /**
     * @param form
     * @param pojoList
     */
    private PagingInfo<MaintainPojo> carMaintainPaging(QueryCarMaintainListForm form, List<MaintainPojo> pojoList) {
        PagingInfo<MaintainPojo> data = new PagingInfo<>();
        PagingInfo<MaintainPojo> pagingInfo = pageUtil.paging(pojoList, form.getPage_number(), form.getPage_size());
        data.setTotal(pagingInfo.getTotal());
        data.setPage_total(pagingInfo.getPage_total());
        data.setList(pagingInfo.getList());
        return data;
    }

    /**
     * @param form
     * @param pojoList
     */
    private PagingInfo<MaintainDetailsPojo> carMaintainDateilPaging(QueryCarNewMaintainDetailsForm form, List<MaintainDetailsPojo> pojoList) {
        PagingInfo<MaintainDetailsPojo> data = new PagingInfo<>();
        PagingInfo<MaintainDetailsPojo> pagingInfo = pageUtil.paging(pojoList, form.getPage_number(), form.getPage_size());
        data.setTotal(pagingInfo.getTotal());
        data.setPage_total(pagingInfo.getPage_total());
        data.setList(pagingInfo.getList());
        return data;
    }


    /**
     * ????????????????????????
     */
    private String getActualMileage(CarEntity car) {
        String mileage = "0";
        try {
            if (Objects.nonNull(car) && StringUtils.isNotEmpty(car.getAutoTerminal())) {
                Object data = locationServiceClient.lastLocation(new ArrayList<Long>() {{
                    add(Long.valueOf(car.getAutoTerminal()));
                }}).getData();
                List<LastLocationPojo> response = JSON.parseArray(data.toString(), LastLocationPojo.class);
                if (CollectionUtils.isNotEmpty(response)) {
                    mileage = String.valueOf(response.get(0).getSm());
                }
            }
        } catch (Exception e) {

        }
        return mileage;
    }

    /**
     * ??????????????????-????????????
     */
    private CarNewMaintainDTO assembleCarNewMaintainDto(String actualMileage, String carVin, CarEntity car, List<CarMaintainNewDetailPojo> pojos) {
        CarNewMaintainDTO dto = new CarNewMaintainDTO();
        if (Objects.nonNull(car)) {
            dto.setCarId(car.getId());
            dto.setCarNumber(car.getCarNumber());
        }
        dto.setChassisNum(carVin);
        dto.setTotalmileage(actualMileage);
        if (CollectionUtils.isNotEmpty(pojos)) {
            dto.setProtocolType(pojos.get(0).getProtocolType());
        } else {
            dto.setProtocolType("0");
        }
        return dto;
    }

    /**
     * ??????????????????-????????????
     */
    private CarNewMaintainDetailDTO assembleCarNewMaintainDetailDto(String actualMileage,
                                                                    Page<CarMaintainNewDetailPojo> page) {
        CarNewMaintainDetailDTO dto = new CarNewMaintainDetailDTO();
        dto.setPage_total(page.getPages());
        dto.setTotal(page.getTotal());
        dto.setList(convertToCarMaintainDetailItemDto(actualMileage, page.getResult()));
        return dto;
    }

    /**
     * ??????????????????-??????????????????
     */
    private List<CarNewMaintainItemlDTO> convertToCarMaintainDetailItemDto(String actualMileage,
                                                                           List<CarMaintainNewDetailPojo> list) {
        List<CarNewMaintainItemlDTO> dtoList = new ArrayList<>();
        for (CarMaintainNewDetailPojo pojo : list) {
            CarNewMaintainItemlDTO itemlDto = new CarNewMaintainItemlDTO();
            itemlDto.setMtcItem(CharaterUtil.replaceSpecialChar(pojo.getMtcItem(), specialRegex));
            itemlDto.setCarMaintainId(String.valueOf(pojo.getCarMaintainId()));
            itemlDto.setProtocolType(pojo.getProtocolType());
            itemlDto.setNextMaintainMileage(pojo.getNextMaintainMileage());
            itemlDto.setNextMaintainTime(pojo.getNextMaintainTime());
            itemlDto.setLastMaintainMileage(pojo.getLastMaintainMileage());
            itemlDto.setLastMaintainTime(pojo.getLastMaintainTime());
            calculateRemainMaintainMeliage(actualMileage, pojo.getNextMaintainTime(), itemlDto);
            dtoList.add(itemlDto);
        }
        return dtoList;
    }

    /**
     * ???????????????????????????????????????
     */
    private void calculateRemainMaintainMeliage(String actualMileage, String nextMaintainTime,
                                                CarNewMaintainItemlDTO itemlDto) {
        int bgColor = 0;
        /**?????????????????????????????????0*/
        Double dValueMileage = StringUtils.isEmpty(itemlDto.getNextMaintainMileage()) ? remindMaintainInterface.getPreRemindMileage() :
                NumberFormatUtil.digitalSub(itemlDto.getNextMaintainMileage(), actualMileage).doubleValue();
        Long dValueTime = StringUtils.isEmpty(nextMaintainTime) ? remindMaintainInterface.getPreRemindDay() : DateUtil.until(new Date(), DateUtil.parseDate(nextMaintainTime));
        itemlDto.setRemainMaintainMeliage(StringUtils.isEmpty(itemlDto.getNextMaintainMileage()) ? "" :
                dValueMileage > 0 ? NumberFormatUtil.parseString(dValueMileage) : "0");
        /**1??????????????????/????????????????????????*/
        if ((dValueMileage < remindMaintainInterface.getPreRemindMileage() && dValueMileage >= 0)
                || (dValueTime >= 0 && dValueTime < remindMaintainInterface.getPreRemindDay())) {
            bgColor = 1;
        }
        /**2???????????????????????????????????????*/
        if (dValueMileage < 0 || dValueTime < 0) {
            bgColor = 2;
        }
        itemlDto.setBgColor(bgColor);
    }


    /**
     * ??????????????????-??????????????????
     */
    private CarNewMaintainProgramCountDTO assembleCarNewMaintainProgramCountDto(Map<String, Integer> map) {
        CarNewMaintainProgramCountDTO dto = new CarNewMaintainProgramCountDTO();
        dto.setReplaceProgramCount(getProgramCount(map, MaintainCategoryEnum.replaceCategory.code()));
        dto.setCheckProgramCount(getProgramCount(map, MaintainCategoryEnum.checkCategory.code()));
        dto.setLubricateProgramCount(getProgramCount(map, MaintainCategoryEnum.lubricateCategory.code()));
        return dto;
    }

    private Integer getProgramCount(Map<String, Integer> map, String key) {
        Integer count = map.get(key);
        return count != null ? count : 0;
    }

    /**
     * ????????????????????????
     *
     * @param carDto
     * @param maintainDetailDto
     * @param carNewMaintainProgramCountDto
     * @return
     */
    private CarMaintainDetailDataDTO assembleCarMaintainDetailDataDto(CarNewMaintainDTO carDto,
                                                                      CarNewMaintainDetailDTO maintainDetailDto, CarNewMaintainProgramCountDTO carNewMaintainProgramCountDto) {
        CarMaintainDetailDataDTO data = new CarMaintainDetailDataDTO();
        data.setCarDto(carDto);
        data.setMaintainDetailDto(maintainDetailDto);
        data.setCarNewMaintainProgramCountDto(carNewMaintainProgramCountDto);
        return data;
    }


    /**
     * CarMaintainRecordPojo->CarMaintainRecordDto
     */
    private List<CarMaintainRecordDTO> convertToCarMaintainRecordDtoList(
            List<CarMaintainRecordPojo> carMaintainRecordPojoList) {
        List<CarMaintainRecordDTO> dtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(carMaintainRecordPojoList)) {
            carMaintainRecordPojoList.stream().forEach(o -> {
                CarMaintainRecordDTO dto = new CarMaintainRecordDTO();
                dto.setMaintainMileageTime(o.getMaintainMileageTime());
                dto.setMaintainMileageMileage(o.getMaintainMileageMileage());
                dto.setOpDate(o.getOpDate());
                dto.setMaintenanceProgramList(Lists.newArrayList(o.getMaintenanceProgramStr().split(separator)));
                if (o.getMaintainSource() == 1) {
                    dto.setOpUser(o.getOpUser().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
                } else {
                    dto.setOpUser(o.getOpUser());
                }
                dtoList.add(dto);
            });
        }
        return dtoList;
    }

    /**
     * ????????????
     */
    private PagingInfo<CarMaintainRecordDTO> convertToPagingInfo(Page<CarMaintainRecordPojo> page,
                                                                 List<CarMaintainRecordDTO> carMaintainRecordDtos) {
        PagingInfo<CarMaintainRecordDTO> pagingInfo = new PagingInfo<>();
        pagingInfo.setPage_total(page.getPages());
        pagingInfo.setTotal(page.getTotal());
        pagingInfo.setList(carMaintainRecordDtos);
        return pagingInfo;
    }


    /**
     * ?????????????????????
     */
    private void checkMileageAndTime(String actualMileage, UpdateCarNewMaintainFrom form, List<CarMaintainEntity> carMaintains) {
        StringBuilder builder = new StringBuilder("");
        Boolean flag1 = false;//??????????????????????????????????????????
        Boolean flag2 = false;//??????????????????????????????????????????
        Boolean flag3 = false;//??????????????????????????????
        Boolean flag4 = false;//??????????????????????????????????????????
        for (CarMaintainEntity carMaintain : carMaintains) {
            if (carMaintain.getIsCheck() == 1) {
                continue;//???????????????????????????????????????
            }
            double dValueMileage1 = NumberFormatUtil.digitalSub(actualMileage, form.getMaintainMileage())
                    .doubleValue();
            double dValueMileage2 = NumberFormatUtil
                    .digitalSub(form.getMaintainMileage(), carMaintain.getLastMaintainMileage())
                    .doubleValue();
            long dValueTime1 = DateUtil.until(DateUtil.parseDate(form.getMaintainTime()), new Date());
            long dValueTime2 = DateUtil
                    .until(carMaintain.getLastMaintainTime(), DateUtil.parseDate(form.getMaintainTime()));
            if (dValueMileage1 < 0) {//??????????????????????????????????????????
                flag1 = true;
            }
            if (dValueMileage2 < 0) {//??????????????????????????????????????????
                flag2 = true;
            }
            if (dValueTime1 < 0) {//??????????????????????????????
                flag3 = true;
            }
            if (dValueTime2 < 0) {//??????????????????????????????????????????
                flag4 = true;
            }
        }
        /** ???????????????????????????????????????????????????????????????????????????*/
        if (flag1) {
            builder.append("??????????????????????????????????????????");
        } else if (flag2) {
            builder.append("??????????????????????????????????????????");
        } else if (flag3) {
            builder.append("??????????????????????????????");
        } else if (flag4) {
            builder.append("??????????????????????????????????????????");
        }
        if (!"".equals(builder.toString())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), builder.toString());
        }
    }


    /**
     * ????????????????????????
     */
    private void updateCarMaintain(List<CarMaintainEntity> carMaintains, UpdateCarNewMaintainFrom form) {
        carMaintains.stream().forEach(o -> {
            o.setLastMaintainTime(DateUtil.parseDate(form.getMaintainTime()));
            o.setLastMaintainMileage(form.getMaintainMileage());
            o.setIsCheck(2);
            o.setUpdateUser(form.getAccPhone());
            o.setUpdateDate(new Date());
            Integer maintainType = calculateNextMaintainMileage(o, form.getMaintainMileage());
            calculateNextMaintainTime(o, form.getMaintainTime(), maintainType);
        });
        log.info("[??????????????????????????????:{}start]", carMaintains.toString());
        for (CarMaintainEntity carMaintainEntity : carMaintains) {
            maintainNewDao.updateCarMaintain(carMaintainEntity);
        }
        //maintainNewMapper.batchUpdateCarMaintain(carMaintains);
        log.info("[??????????????????????????????end]");
    }


    /**
     * ???????????????????????????
     *
     * @param entity
     * @param maintainMileage
     * @return
     */
    private Integer calculateNextMaintainMileage(CarMaintainEntity entity, String maintainMileage) {
        /**???????????????????????????  0??????  1????????? 2?????????  3?????????*/
        Integer maintainType = 0;
        /**??????????????????????????????????????????*/
        if (StringUtils.isEmpty(maintainMileage)) {
            return maintainType;
        }
        Boolean mailMileageFlag = StringUtils.isNotEmpty(entity.getMaiMileage());//??????????????????
        Boolean fstMileageFlag = StringUtils.isNotEmpty(entity.getFstMileage());//??????????????????
        Boolean invMileageFlag = StringUtils.isNotEmpty(entity.getInvMileage());//??????????????????
        log.info("[mailMileageFlag is {},fstMileageFlag is {}, invMileageFlag is {} start:]", mailMileageFlag, fstMileageFlag, invMileageFlag);
        log.info("[mailMileage is {},fstMileage is {}, invMileage is {} start:]", entity.getMaiMileage(), entity.getFstMileage(), entity.getInvMileage());
        if (!mailMileageFlag && !fstMileageFlag && !invMileageFlag) {//??????????????????????????????????????????
            entity.setNextMaintainMileage(null);
        } else if (mailMileageFlag && !fstMileageFlag && !invMileageFlag) {//?????????????????????????????????????????????
            entity.setNextMaintainMileage(NumberFormatUtil.digitalAdd(entity.getMaiMileage(), maintainMileage));//??????????????? + ??????
            maintainType = 1;
        } else if (!mailMileageFlag && fstMileageFlag && !invMileageFlag) {//?????????????????????????????????????????????
            entity.setNextMaintainMileage(NumberFormatUtil.digitalAdd(entity.getFstMileage(), maintainMileage));//??????????????? + ??????
            maintainType = 2;
        } else if (!mailMileageFlag && !fstMileageFlag && invMileageFlag) {//?????????????????????????????????????????????
            entity.setNextMaintainMileage(NumberFormatUtil.digitalAdd(entity.getInvMileage(), maintainMileage));//??????????????? + ??????
            maintainType = 3;
        } else if (mailMileageFlag && fstMileageFlag && !invMileageFlag) {//????????????????????????????????????????????????
            entity.setNextMaintainMileage(NumberFormatUtil.digitalAdd(entity.getFstMileage(), maintainMileage));//??????????????? + ??????
            maintainType = 2;
        } else if (mailMileageFlag && !fstMileageFlag && invMileageFlag) {//????????????????????????????????????????????????
            entity.setNextMaintainMileage(NumberFormatUtil.digitalAdd(entity.getInvMileage(), maintainMileage));//??????????????? + ??????
            maintainType = 3;
        } else if (!mailMileageFlag && fstMileageFlag && invMileageFlag) {//????????????????????????????????????????????????
            entity.setNextMaintainMileage(NumberFormatUtil.digitalAdd(entity.getInvMileage(), maintainMileage));//??????????????? + ??????
            maintainType = 3;
        } else {//???????????????????????????????????????????????????
            if (NumberFormatUtil.digitalSub(entity.getMaiMileage(), maintainMileage).doubleValue() > 0
                    && !entity.getMaiMileage().equals(entity.getInvMileage())) {//??????????????????????????????????????????????????????????????????????????????????????? + ??????
                entity.setNextMaintainMileage(NumberFormatUtil.digitalAdd(entity.getFstMileage(), maintainMileage));
                maintainType = 2;
            } else {
                entity.setNextMaintainMileage(NumberFormatUtil.digitalAdd(entity.getInvMileage(), maintainMileage));//???????????????+????????????
                maintainType = 3;
            }
        }
        return maintainType;
    }


    /**
     * @param entity
     * @param maintainTime
     * @param maintainType
     */
    private void calculateNextMaintainTime(CarMaintainEntity entity, String maintainTime, Integer maintainType) {
        if (StringUtils.isEmpty(maintainTime)) {
            return;
        }
        String invTime = "";
        switch (maintainType) {
            case 0://?????????????????????????????????????????????????????????????????????????????????
                invTime = getNoneFirstTime(entity.getMaiMonth(), entity.getInvMonth(), entity.getFstMonth());
                break;
            case 1: //?????????????????????????????????????????????????????????????????????????????????
                invTime = getNoneFirstTime(entity.getMaiMonth(), entity.getFstMonth(), entity.getInvMonth());
                break;
            case 2: //?????????????????????????????????????????????????????????????????????
                invTime = getNoneFirstTime(entity.getFstMonth(), entity.getInvMonth(), entity.getMaiMonth());
                break;
            case 3://?????????????????????????????????????????????????????????????????????????????????
                invTime = getNoneFirstTime(entity.getInvMonth(), entity.getFstMonth(), entity.getMaiMonth());
                break;
            default:
                invTime = "";
        }
        if (StringUtils.isNotEmpty(invTime)) {
            entity.setNextMaintainTime(toCalcNextMaintainTime(DateUtil.plusDays(DateUtil.parseDate(maintainTime),
                    Double.valueOf(Double.valueOf(invTime) * 30).longValue()), invTime));
        } else {
            entity.setNextMaintainTime(null);
        }

    }


    /**
     * ?????????????????????????????????
     * ??????????????????????????????????????????
     *
     * @param firstTime
     * @param secondTime
     * @param threeTime
     * @return
     */
    private String getNoneFirstTime(String firstTime, String secondTime, String threeTime) {
        return StringUtils.isNotEmpty(firstTime) ? firstTime : StringUtils.isNotEmpty(secondTime) ? secondTime : StringUtils.isNotEmpty(threeTime) ? threeTime : "";
    }

    /**
     * ????????????????????????????????????
     *
     * @param date
     * @param invTime
     * @return
     */
    private Date toCalcNextMaintainTime(Date date, String invTime) {
        if (date.compareTo(new Date()) > 0) {
            return date;
        } else {
            return toCalcNextMaintainTime(DateUtil.plusDays(date, Double.valueOf(Double.valueOf(invTime) * 30).longValue()), invTime);
        }
    }


    /**
     * ????????????????????????
     */
    private void saveCarMaintainRecord(List<CarMaintainEntity> pojoList, String carVin, String carNumber,
                                       UpdateCarNewMaintainFrom form) {
        log.info("[?????????{}?????????????????????start]", carVin);
        CarMaintainRecordEntity record = new CarMaintainRecordEntity();
        record.setCarVin(carVin);
        record.setCarNumber(carNumber);
        List<String> programList = pojoList.stream().map(m -> CharaterUtil.replaceSpecialChar(m.getMtcItem(), specialRegex)).collect(Collectors.toList());
        record.setMaintenanceProgramStr(StringUtils.join(programList.toArray(), separator));
        record.setMaintainMileageTime(DateUtil.parseDate(form.getMaintainTime()));
        record.setMaintainMileageMileage(Double.valueOf(form.getMaintainMileage()));
        record.setMaintainSource(form.getMaintainSource());
        if (form.getMaintainSource() == 1) {//?????????
            UserInfoPojo user = userDao.findById(form.getAutoIncreaseId());
            record.setOpUser(Objects.nonNull(user) ? user.getPhone() : "");
        } else if (form.getMaintainSource() == 2) {//?????????
            record.setOpUser(form.getAccountNickname());
        } else {//CRM
            record.setOpUser("??????????????????");
        }
        record.setOptDate(new Date());
        maintainNewDao.insertCarMaintainRecord(record);

    }

}
