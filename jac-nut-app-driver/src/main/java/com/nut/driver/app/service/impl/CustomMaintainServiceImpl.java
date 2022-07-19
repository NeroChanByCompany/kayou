package com.nut.driver.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.Page;

import com.google.common.collect.Lists;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.*;
import com.nut.driver.app.dao.CarDao;
import com.nut.driver.app.dao.CustomMaintainInfoDao;
import com.nut.driver.app.dao.CustomMaintainItemDao;
import com.nut.driver.app.dao.MaintainItemInfoDao;
import com.nut.driver.app.dto.*;
import com.nut.driver.app.entity.CustomMaintainInfoEntity;
import com.nut.driver.app.entity.CustomMaintainItemEntity;
import com.nut.driver.app.entity.MaintenanceItemInfoEntity;
import com.nut.driver.app.form.*;
import com.nut.driver.app.pojo.QueryCustomMaintainItemCountPojo;
import com.nut.driver.app.pojo.ValidateMaintainInfoPojo;
import com.nut.driver.app.service.CustomMaintainItemService;
import com.nut.driver.app.service.CustomMaintainService;
import com.nut.driver.app.service.QueryLocationService;
import com.nut.driver.common.constants.CommonConstants;
import com.nut.driver.common.utils.PageUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author liuBing
 * @Classname CustomMaintainServiceImpl
 * @Description TODO
 * @Date 2021/6/24 14:31
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class, readOnly = true)
public class CustomMaintainServiceImpl implements CustomMaintainService {
    @Autowired
    private CustomMaintainInfoDao customMaintainInfoDao;
    @Autowired
    private PageUtil pageUtil;

    @Autowired
    private MaintainItemInfoDao maintainItemInfoDao;


    @Autowired
    private QueryLocationService queryLocationService;

    @Autowired
    private CustomMaintainItemService customMaintainItemService;

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private CarDao carDao;

    @Autowired
    private CustomMaintainItemDao customMaintainItemDao;

    @Override
    public PagingInfo<QueryDriverMaintenanceDTO> queryDriverMaintenanceList(QueryDriverMaintenanceListForm from) {
        pageUtil.getPage(from);
        Page<QueryCustomMaintainItemCountPojo> page = customMaintainInfoDao.selectAvailableListByUserId(from);

        PagingInfo<QueryCustomMaintainItemCountPojo> pageInfo = pageUtil.convertPagingToPage(page);
        // 查询保养项目
        List<QueryDriverMaintenanceDTO> queryDriverMaintenanceDtoList =
                pageInfo.getList().stream().map(MaintainceCovertToDTO::covToQueryDriverMaintenanceDTO).collect(Collectors.toList());
        QueryDriverMaintenanceListDTO queryDriverMaintenanceListDto = new QueryDriverMaintenanceListDTO();
        queryDriverMaintenanceListDto.setList(queryDriverMaintenanceDtoList);
        PagingInfo<QueryDriverMaintenanceDTO> queryDriverMaintenanceDtoPagingInfo = new PagingInfo<>(pageInfo.getTotal(), queryDriverMaintenanceDtoList, from);
        log.info("queryDriverMaintenanceList end return:{}",queryDriverMaintenanceDtoPagingInfo);
        return queryDriverMaintenanceDtoPagingInfo;
    }

    @Override
    public MaintenanceInfoDTO queryMaintanceInfo(QueryMaintenanceInfoForm form) {
        MaintenanceInfoDTO maintenanceInfoDto;

        CustomMaintainInfoEntity customMaintainInfo = customMaintainInfoDao.queryMaintanceInfo(Long.parseLong(form.getMaintainId()));

        if (customMaintainInfo == null || customMaintainInfo.getStatus() == 0 || StringUtil.isNotEq(customMaintainInfo.getUserId(), form.getUserId())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "未发现此保养信息！");
        }

        maintenanceInfoDto = MaintainceCovertToDTO.maintainInfoToMaintenanceInfoDto(customMaintainInfo);
        // 保养项目列表
        List<CustomMaintainItemEntity> customMaintainItemList = customMaintainItemService.queryItemListByMaintainId(customMaintainInfo.getId());

        // 记录数
        maintenanceInfoDto.setMaintainItemCount(String.valueOf(customMaintainItemList.size()));
        // 查询保养项目列表，返回前三个
        if (!customMaintainItemList.isEmpty()) {
            List<MaintenanceItemInfoEntity> maintenanceItemInfoList =
                    maintainItemInfoDao.queryMaintainItemName(
                            customMaintainItemList
                                    .stream()
                                    .map(CustomMaintainItemEntity::getMaintainItemId).collect(Collectors.toList()))
                            .stream()
                            .map(name -> {
                                MaintenanceItemInfoEntity maintenanceItemInfo = new MaintenanceItemInfoEntity();
                                maintenanceItemInfo.setMaintainItemName(name);
                                return maintenanceItemInfo;
                            }).collect(Collectors.toList());
            maintenanceInfoDto.setMaintainItemList(maintenanceItemInfoList);
        }

        // 查询最大公里数
        String mileage = queryLocationService.getMellage(maintenanceInfoDto.getCarId());
        maintenanceInfoDto.setMileage(mileage);
        log.info("queryMaintenanceInfo end return:{}",maintenanceInfoDto);
        return maintenanceInfoDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaintainceInfo(QueryMaintenanceInfoForm form) {
        // 校验数据状态
        CustomMaintainInfoEntity customMaintainInfo = customMaintainInfoDao.selectByPrimaryKey(Long.parseLong(form.getMaintainId()));
        if (customMaintainInfo == null || customMaintainInfo.getStatus() == 0 || StringUtil.isNotEq(customMaintainInfo.getUserId(), form.getUserId())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "未发现此保养信息！");
        }

        // 0：需要保养车辆；1：未到保养车辆；2：已完成保养车辆
        if (customMaintainInfo.getStatus() == CommonConstants.CONST_INTEGER_ZERO) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "该保养信息应删除！");
        }

        // 未到保养的车辆,如果类型为时间类型,需要删除redis key
        if (customMaintainInfo.getMaintainStatus() == CommonConstants.CONST_INTEGER_ONE && customMaintainInfo.getCustomMaintainType() == CommonConstants.CONST_INTEGER_TWO) {
            log.info("删除id为{}保养信息提醒方式为时间提醒，需要删除redis key!", form.getMaintainId());
            // 计算时间间隔
            redisComponent.del(StringUtil.concat("CUSTOM_MAINTAIN:", form.getUserId(), ":", form.getMaintainId()));
        }


        customMaintainInfo = new CustomMaintainInfoEntity();
        customMaintainInfo.setId(Long.parseLong(form.getMaintainId()));
        // 已完成
        customMaintainInfo.setStatus(0);
        // 更新数据
        customMaintainInfoDao.updateByPrimaryKeySelective(customMaintainInfo);
        log.info("deleteMaintenance end return:{null}");
    }

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addValidate(AddMaintenanceInfoForm form) {
        // 校验信息
        String validateMessage = validateAddMaintainInfo(form);
        if (validateMessage != null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), validateMessage);
        }

        // 插入数据
        CustomMaintainInfoEntity customMaintainInfo =
                MaintainceCovertToDTO.AddMaintenanceInfoCommandToCustomMaintainInfo(form);

        // 插入自定义保养表
       customMaintainInfoDao.insertSelective(customMaintainInfo);
       Long id = customMaintainInfo.getId();

        // 保养项目为空的时候,不进行插入
        if (!StringUtil.isEmpty(form.getMaintainItemIdList())) {
            // 批量插入自定义保养表
            List<CustomMaintainItemEntity> customMaintainItemList = getItemStream(form.getMaintainItemIdList()).map(item -> {
                CustomMaintainItemEntity customMaintainItem = new CustomMaintainItemEntity();
                customMaintainItem.setMaintainItemId(Integer.parseInt(item));
                customMaintainItem.setMaintainId(id);
                return customMaintainItem;
            }).collect(Collectors.toList());

            customMaintainItemDao.insertItemList(customMaintainItemList);
        }


        // 添加完保养信息后，如果是时间提醒，则插入redis数据，key为用户id和自定义保养信息id，CUSTOM_MAINTAIN:USER_ID:MAINTAIN_ID
        if (StringUtil.isEq(CommonConstants.CONST_INTEGER_TWO, form.getMaintainType())) {
            log.info("id为{}保养信息提醒方式为时间提醒，需要将时间添加到缓存中!", id);
            // 计算时间间隔
            redisSetKeyLiveTime(
                    StringUtil.concat("CUSTOM_MAINTAIN:",
                            form.getUserId(), ":",
                            id.toString()),
                    form.getMaintainDescribe());
        }
        log.info("addMaintenance end return:{null}");
    }

    @SneakyThrows
    @Override
    public void editValidate(EditMaintenanceInfoForm form) {
        log.info("CustomMaintainService---editValidate:---begin-----");
        CustomMaintainInfoEntity customMaintainInfo = customMaintainInfoDao.selectByPrimaryKey(Long.parseLong(form.getMaintainId()));
        if (customMaintainInfo == null || customMaintainInfo.getStatus() == CommonConstants.CONST_INTEGER_ZERO || StringUtil.isNotEq(customMaintainInfo.getUserId(), form.getUserId())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "未发现此保养信息！");
        }

        // 未到保养的才能修改
        if (customMaintainInfo.getMaintainStatus() != CommonConstants.CONST_INTEGER_ONE) {

            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "只有未完成的自定义保养才可以进行修改！");
        }
        // 验证信息
        String validateMessage = validateEditMaintainInfo(form, CommonConstants.CONST_INTEGER_ONE);
        if (validateMessage != null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), validateMessage);
        }
    }

    @SneakyThrows
    @Override
    public void editMaintainInfo(EditMaintenanceInfoForm form) {
        log.info("CustomMaintainService---editMaintainInfo:---begin-----");

        // 修改数据
        CustomMaintainInfoEntity customMaintainInfo =
                MaintainceCovertToDTO.editMaintenanceInfoCommandToCustomMaintainInfo(form);
        customMaintainInfoDao.updateByPrimaryKeySelective(customMaintainInfo);

        if (StringUtil.isEq(String.valueOf(CommonConstants.CONST_INTEGER_TWO), form.getMaintainType())) {
            log.info("id为{}保养信息提醒方式为时间提醒，需要将时间添加到缓存中!", form.getMaintainId());
            // 计算时间间隔
            redisSetKeyLiveTime(
                    StringUtil.concat("CUSTOM_MAINTAIN:",
                            form.getUserId(), ":",
                            form.getMaintainId()),
                    form.getMaintainDescribe());
        }

        log.info("CustomMaintainService---editMaintainInfo:请求参数:{}", form.toString());
        log.info("CustomMaintainService---editMaintainInfo:---end-----");
    }


    /**
     * 修改信息验证
     *
     * @param form
     * @param clientFlg 1:司机，2：车主
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String validateEditMaintainInfo(EditMaintenanceInfoForm form, int clientFlg) throws InvocationTargetException, IllegalAccessException {
        ValidateMaintainInfoPojo validateMaintainInfoPojo = new ValidateMaintainInfoPojo();
        validateMaintainInfoPojo.setCarId(form.getCarId());
        validateMaintainInfoPojo.setCarNumber(form.getCarNumber());
        validateMaintainInfoPojo.setMaintainDescribe(form.getMaintainDescribe());
        validateMaintainInfoPojo.setMaintainType(form.getMaintainType());
        return this.validateMaintainCommonInfo(validateMaintainInfoPojo);
    }


    /**
     * 验证信息
     *
     * @param
     * @return
     */
    private String validateAddMaintainInfo(AddMaintenanceInfoForm from) throws InvocationTargetException, IllegalAccessException {

        ValidateMaintainInfoPojo validateMaintainInfoPojo = new ValidateMaintainInfoPojo();

        validateMaintainInfoPojo.setCarId(from.getCarId());
        validateMaintainInfoPojo.setCarNumber(from.getCarNumber());
        validateMaintainInfoPojo.setMaintainDescribe(from.getMaintainDescribe());
        validateMaintainInfoPojo.setMaintainType(from.getMaintainType());

        // teamId为空时取当前车队ID（兼容老版本）
//        if(StringUtil.isEmpty(command.getTeamId())){
//            command.setTeamId(command.getCurrentTeamId());
//        }
//        if(StringUtil.isEmpty(command.getTeamId())){
//            return "车队ID为空";
//        }

        String result = this.validateMaintainCommonInfo(validateMaintainInfoPojo);
        // 验证通过
        if (result == null) {
            // 校验保养项目在数据库中是否有值
            // 保养项目为空时,不校验
            if (StringUtil.isEmpty(from.getMaintainItemIdList())) {
                return null;
            }
            return customMaintainItemService.validateItem(from.getMaintainItemIdList());
        }
        return result;

    }

    private String validateMaintainCommonInfo(ValidateMaintainInfoPojo pojo) {
        String maintainTypeOne = "1";
        String maintainTypeTwo = "2";
        String mileageConstZero = "0";
        //1：里程；2：时间
        String maintainType = pojo.getMaintainType();
        String describe = pojo.getMaintainDescribe();
        // 里程需要大于总里程
        if (maintainTypeOne.equals(maintainType)) {
            // 验证里程数为整数
            if (!RegexpUtils.validateInfo(describe, RegexpUtils.POSITIVE_INTEGER_REGEXP)) {
                return "提醒方式为里程时，公里数必须为正整数！";
            }
            // 获取里程
            String mileage = this.getSingleCarMileage(pojo.getCarId());
            if (StringUtil.isEq(mileageConstZero, mileage)) {
                return "获取不到此车辆的里程数据,无法进行操作!";
            }

            // 判断输入里程与缓存里程，输入里程小于总里程
            try {
                BigDecimal totalMileage = new BigDecimal(mileage);
                BigDecimal maintainDescribe = new BigDecimal(describe);
                if (totalMileage.compareTo(maintainDescribe) != -1) {
                    return "提醒方式为里程时，里程数需大于总里程方可生效！";
                }
            } catch (Exception e) {
                log.error("===== CustomMaintainService--validateMaintainCommonInfo:Exception =====", e);
            }
        } else if (maintainTypeTwo.equals(maintainType)) {
            // 验证时间格式
            if (!RegexpUtils.validateInfo(describe, RegexpUtils.DATE_BARS_REGEXP_HOUR_MIN_SECOND)) {
                return "提醒方式为时间时，请检查时间格式是否正确，如2020-12-12 01:01:01！";
            }
            // 验证时间是否大于当前时间,res时间大于0则表示提醒时间小于系统时间
            Long res = DateUtil.diffNowDateTime(describe);
            if (res > 0) {
                return "提醒方式为时间时，提醒时间应大于系统时间！";
            }
        }
        return null;
    }

    public String getSingleCarMileage(String carId) {
        String vin = carDao.getVinByCarId(carId);
        String totalMileage = "0.00";
        try {
            HttpCommandResultWithData totalMileageResult = this.queryCarMileage(Lists.newArrayList(vin), "km");
            if (totalMileageResult != null && totalMileageResult.getData() != null) {
                List<Map<String, String>> mileageList = (List<Map<String, String>>) totalMileageResult.getData();
                if (mileageList.size() > 0) {
                    Map<String, String> mileageMap = mileageList.get(0);
                    if (CollectionUtils.size(mileageMap) > 0 && StringUtil.isNotEmpty(mileageMap.get("totalMileage"))) {
                        //总里程
                        totalMileage = mileageMap.get("totalMileage");
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return totalMileage;
    }


    public HttpCommandResultWithData queryCarMileage(List<String> vinList, String unit) throws IOException {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        // 调用位置云末次位置接口获取实时信息
        GetCarLocationForm com = new GetCarLocationForm();
        String vinStringList = StringUtils.join(vinList, ",");
        com.setVins(vinStringList);
        HttpCommandResultWithData carLocationResult = queryLocationService.carLocation(com);
        List<Map<String, String>> returnList = new ArrayList<>();
        if (carLocationResult.getResultCode() == ECode.SUCCESS.code()) {
            Map<String, Object> dataMap = JsonUtil.toMap(JsonUtil.toJson(carLocationResult.getData()));
            if (dataMap != null && dataMap.size() > 0) {
                for (Object carLocationJson : dataMap.values()) {
                    try {
                        CarLocationOutputDto carLocation = JsonUtil.fromJson(JsonUtil.toJson(carLocationJson), CarLocationOutputDto.class);
                        Map<String, String> returnMap = new HashMap<>(16);
                        returnMap.put("vin", carLocation.getVin());
                        if (carLocation.getMileage() == null) {
                            returnMap.put("totalMileage", null);
                        } else {
                            if ("km".equals(unit)) {
                                returnMap.put("totalMileage", String.valueOf(carLocation.getMileage()));
                            } else if ("m".equals(unit)) {
                                returnMap.put("totalMileage", String.valueOf(new Double(carLocation.getMileage().doubleValue() * 1000D).longValue()));
                            }
                        }
                        returnList.add(returnMap);
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            } else {
                log.info("调用位置云末次位置接口返回结果为空，vins:{}", vinStringList);
            }
            result.setData(returnList);
            result.setResultCode(ECode.SUCCESS.code());
            result.setMessage(ECode.SUCCESS.message());
        } else {
            log.info("调用位置云末次位置接口返回 code:{}, message:{}, vins:{}", carLocationResult.getResultCode(), carLocationResult.getMessage(), vinStringList);
            result.setResultCode(carLocationResult.getResultCode());
            result.setMessage(carLocationResult.getMessage());
        }
        return result;
    }

    protected Stream<String> getItemStream(String itemString) {
        return Stream.of(itemString.split(","));
    }

    private void redisSetKeyLiveTime(String key, String time) {
        long t = Math.abs(DateUtil.diffNowDateTime(time)) - 300;
        if (t <= 0) {
            // 向前回退5分钟,redis有延迟
            t = 5L;
        }
        log.info("自定义保养信息时间提醒添加缓存，缓存key:{}，自定义保养信息失效时间为:{}秒", key, t);
        redisComponent.set(key, "1", t);
        log.info("自定义保养信息时间提醒添加缓存成功，缓存key:{}，自定义保养信息id:{}秒", key, t);
    }
}
