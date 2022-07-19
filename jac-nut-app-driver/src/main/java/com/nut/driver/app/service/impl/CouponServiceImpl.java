package com.nut.driver.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.Page;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.dao.*;
import com.nut.driver.app.domain.*;
import com.nut.driver.app.entity.CarEntity;
import com.nut.driver.app.entity.CouponUserMappingEntity;
import com.nut.driver.app.form.CouponBranchListForm;
import com.nut.driver.app.form.CouponDetailForm;
import com.nut.common.constant.CouponVal;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.QRcodeUtil;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.entity.CouponApplicableEntity;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.form.CouponListForm;
import com.nut.driver.app.form.ScoreBindcarReceiveForm;
import com.nut.driver.app.pojo.CouponNationalThirdCarInfoPojo;
import com.nut.driver.app.pojo.UserInfoPojo;
import com.nut.driver.app.service.CouponService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 优惠券服务实现
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-18 15:22
 * @Version: 1.0
 */
@Service
@Slf4j
public class CouponServiceImpl extends DriverBaseService implements CouponService {

    @Autowired
    private CouponInfoDao couponInfoDao;

    @Autowired
    private CarDao carMapper;

    @Autowired
    private CouponUserMappingDao couponUserMappingDao;

    @Autowired
    private CouponApplicableDao couponApplicableDao;

    @Autowired
    private CouponNationalThirdCarMappingDao couponNationalThirdCarMappingDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserDao userMapper;

    @Autowired
    private CarTypeIntroduceDao carTypeIntroduceSonDao;

    @Value("${MAINTENANCE_TIME}")
    private String maintenanceTime;

    private long nationalThird = 1;
    @Value("${database_name}")
    private String DbName;


    @Override
    @Transactional(readOnly = true)
    public PagingInfo<MyCouponInfo> list(CouponListForm form) {
        Map<String, Object> param = new HashMap<>(16);
        UserEntity user = userDao.selectOne(new QueryWrapper<UserEntity>().eq(StringUtils.isNoneBlank(form.getUserId()), "uc_id", form.getUserId()));
        param.put("userId", user.getId());
        param.put("cumStatus", form.getCumStatus());
        // 查询是否有新注册手机号已经发放优惠券，进行绑定
        processBindNewRegPhoneCoupon(param);
        DriverBaseService.getPage(form);
        Page<MyCouponInfo> page = couponInfoDao.selectList(param);
        PagingInfo<MyCouponInfo> pageInfo = super.convertPagingToPage(page);
        List<MyCouponInfo> resultList = new ArrayList<>();
        for (MyCouponInfo mc : pageInfo.getList()) {
            //设置有效期
            if (StringUtil.isEmpty(mc.getInfoValid())) {
                mc.setInfoValidDate("使用时间无限制");
            } else {
                String infoValidDate = this.getValidDateDesc(mc.getInfoValid(), mc.getInfoValidDay(), mc.getInfoValidStartDate(), mc.getInfoValidEndDate(), mc.getCumGiveTime());
                mc.setInfoValidDate(infoValidDate);
            }
            //设置适用类型
            //线下券
            if (CouponVal.TYPE_OFFLINE.equals(mc.getInfoType())) {
                String applicable = this.getApplicable(mc.getInfoId(), mc.getApplicableType(), form.getAutoIncreaseId());
                mc.setApplicable(applicable);
                //线上券
            } else {
                mc.setApplicable("不限制");
                mc.setInfoContent("￥" + mc.getInfoValue());
                if (CouponVal.TYPE_FULLREDUCTION.equals(mc.getInfoType())) {
                    mc.setApplicable("满" + mc.getInfoValue() + "元可用");
                    mc.setInfoContent("￥" + mc.getFullDecre());
                }
            }
            if (CouponVal.APPLICABLE_TYPE_MAINTENANCE.equals(mc.getApplicableType())) {
//                mc.setApplicable("适用车辆底盘号：" + mc.getCarVIn());
                mc.setApplicable("");
            }
            //只保留日期
            if (StringUtil.isNotEmpty(mc.getCumGiveTime())) {
                mc.setCumGiveTime(mc.getCumGiveTime().substring(0, 10));
            }
            resultList.add(mc);
        }
        pageInfo.setList(resultList);
        log.info("coupon end return:{}", pageInfo);
        return pageInfo;
    }

    @Override
    @Transactional(readOnly = true)
    public MyCouponInfo detail(CouponDetailForm form) throws JsonProcessingException {
        DriverBaseService.getPage(form);
        Map<String, Object> param = new HashMap<>();
        MyCouponInfo myCouponInfo = null;
        param.put("userId", form.getAutoIncreaseId());
        param.put("cumId", form.getCumId());
        Page<MyCouponInfo> page = couponInfoDao.selectList(param);
        for (MyCouponInfo mc : page) {
            if (mc.getIsNationalThird() == nationalThird) {
                CouponNationalThirdCarInfoPojo nationalThirdCarToCoupon = couponNationalThirdCarMappingDao.getNationalThirdCarToCoupon(form.getAutoIncreaseId().toString(), mc.getCumId().toString());
                if (null != nationalThirdCarToCoupon) {
                    /**
                     * 已绑定国三车
                     */
                    mc.setIsBind("1");
                } else {
                    mc.setIsBind("0");
                }
            }
            //设置有效期
            if (StringUtil.isEmpty(mc.getInfoValid())) {
                mc.setInfoValidDate("使用时间无限制");
            } else {
                String infoValidDate = this.getValidDateDesc(mc.getInfoValid(), mc.getInfoValidDay(), mc.getInfoValidStartDate(), mc.getInfoValidEndDate(), mc.getCumGiveTime());
                mc.setInfoValidDate(infoValidDate);
            }
            //设置适用类型
            //线下券
            if (CouponVal.TYPE_OFFLINE.equals(mc.getInfoType())
                    || CouponVal.TYPE_BUY_VEHICLE.equals(mc.getInfoType())) {
                String applicable = this.getApplicable(mc.getInfoId(), mc.getApplicableType(), form.getAutoIncreaseId());
                mc.setApplicable(applicable);
                //线上券
            } else {
                mc.setApplicable("不限制");
                mc.setInfoContent("￥" + mc.getInfoValue());
                if (CouponVal.TYPE_FULLREDUCTION.equals(mc.getInfoType())) {
                    mc.setApplicable("满" + mc.getInfoValue() + "元可用");
                    mc.setInfoContent("￥" + mc.getFullDecre());
                }
            }
            //设置所有适用车型, 优惠券类型,1:线下优惠劵 2:购车代金券 3:线上代金券
            if (mc.getInfoType().equals("2")) {
                mc.setAllModels(selectApplicableModelName(Integer.valueOf(mc.getInfoId())));
            }
        }
        if (page != null && page.size() > 0) {
            myCouponInfo = page.get(0);
            if (this.isApplicable(form.getAutoIncreaseId(), myCouponInfo.getApplicableType(), myCouponInfo.getInfoId(), myCouponInfo.getInfoType()) || myCouponInfo.getInfoType().equals("5")) {
                Map<String, Object> qrMap = new HashMap<>(16);
                qrMap.put("qrcode", myCouponInfo.getCumNumber());
                qrMap.put("type", myCouponInfo.getInfoType());
                myCouponInfo.setCumNumberUrl(QRcodeUtil.generateQRcodePic(JsonUtil.toJson(qrMap)));
            } else {
                myCouponInfo.setCumNumber(null);
            }
        } else {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "用户没有该优惠券");
        }
        log.info("couponDetail end return:{}", myCouponInfo);
        return myCouponInfo;
    }

    @Override
    public int carIsBind(String vin) {
        return couponUserMappingDao.selectCarBind(vin);
    }

    @Override
    public int insertIntoCouponCarBind(Map<String, Object> params) {
        return couponUserMappingDao.insertIntoCouponCarBind(params);
    }

    @Override
    public HttpCommandResultWithData scoreBindcarReceive(ScoreBindcarReceiveForm form) throws ParseException {
        log.info("[scoreBindcarReceive]start");
        SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HttpCommandResultWithData<Object> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        Map<String, Object> param = new HashMap<>(1);
        param.put("nowDate", new Date());
        List<CouponInfo> couponInfoList = filterTotalCount(couponInfoDao.selectCouponInfoList(param));
        log.info("当前可发券数量：{}", couponInfoList.size());
        // 获取在当前时间点，能正常发放的首保券
        List<CouponInfo> couponInfoList1 = couponInfoDao.selectCouponInfoListByMaintenance(param);

        if (CollectionUtils.isNotEmpty(couponInfoList1)) {
            log.info("Success：绑车时间在首保券发放时间段之中！！！！");
            // 判断购车时间是否在活动时间之后
            Date date = null;
            date = slf.parse(maintenanceTime);
            Map<String, Object> param1 = new HashMap<>();
            param1.put("maintenanceTime", date);
            param1.put("carVin", form.getVin());
            Integer i = carMapper.carOfNumber(param1);
            log.info("i:{}", i);
            // 判断车辆购车时间是否在活动时间之后：是，券列表合并；不是，券列表不操作。
            if (i == 1) {
                log.info("Success：购车时间在活动开放时间之后！！！！");
                for (CouponInfo couponInfo : couponInfoList1) {
                    couponInfoList.add(couponInfo);
                }
            } else {
                log.info("Error：购车时间在活动开放时间之前！！！！");
            }
        } else {
            log.info("Error：绑车时间在首保券发放时间段之外！！！！");
        }

        if (log.isInfoEnabled()) {
            String ids = "";
            for (CouponInfo c : couponInfoList) {
                ids += c.getId() + ", ";
            }
            log.info("查询条数：{}, {}", couponInfoList.size(), ids);
        }

        if (CollectionUtils.isNotEmpty(couponInfoList)) {
            //查询车的信息
            CarEntity car = carMapper.selectByVin(form.getVin());
            CouponUserMapping couponUserMapping = null;
            UserEntity user = userMapper.findByUcId(form.getUserId());
            for (CouponInfo couponInfo : couponInfoList) {
                if (checkCar(couponInfo, car, user)) {
                    if (couponInfo.getTotalAmount() == null){
                        couponUserMapping = new CouponUserMapping();
                        couponUserMapping.setInfoId(couponInfo.getId());
                        couponUserMapping.setUserId(user.getId());
                        couponUserMapping.setCumNumber(UUID.randomUUID().toString().replaceAll("-", ""));
                        couponUserMapping.setCumStatus("1");
                        couponUserMapping.setGiveBranchType("2");
                        couponUserMapping.setCumVin(form.getVin());
                        couponUserMapping.setCumGiveTime(slf.format(new Date()));
                        couponUserMapping.setCreateTime(new Date());
                        couponUserMappingDao.insertSelective(couponUserMapping);
                    }
                    if (couponInfo.getTotalAmount() != null){
                        Integer count = couponUserMappingDao.selectCount(new QueryWrapper<CouponUserMappingEntity>().eq("info_id", couponInfo.getId()));
                        Integer forCount = couponInfo.getTotalAmount();
                        if (StringUtils.isNotBlank(couponInfo.getInfoTotalCount())) {
                            if (Integer.parseInt(couponInfo.getInfoTotalCount()) - count < couponInfo.getTotalAmount()) {
                                forCount = Integer.parseInt(couponInfo.getInfoTotalCount()) - count;
                            }
                        }
                        for (int i = 0; i < forCount; i++) {
                            couponUserMapping = new CouponUserMapping();
                            couponUserMapping.setInfoId(couponInfo.getId());
                            couponUserMapping.setUserId(user.getId());
                            couponUserMapping.setCumNumber(UUID.randomUUID().toString().replaceAll("-", ""));
                            couponUserMapping.setCumStatus("1");
                            couponUserMapping.setGiveBranchType("2");
                            couponUserMapping.setCumVin(form.getVin());
                            couponUserMapping.setCumGiveTime(slf.format(new Date()));
                            couponUserMapping.setCreateTime(new Date());
                            count++;
                            couponUserMappingDao.insertSelective(couponUserMapping);
                        }
                    }
                    if (couponInfoList1.contains(couponInfo)) {
                        log.info("发放首保券, userId: {}, vin: {}, couponId: {}, couponNumber: {}", couponUserMapping.getUserId(),
                                couponUserMapping.getCumVin(), couponUserMapping.getInfoId(), couponUserMapping.getCumNumber());
                    } else {
                        log.info("发放优惠券, userId: {}, vin: {}, couponId: {}, couponNumber: {}", couponUserMapping.getUserId(),
                                couponUserMapping.getCumVin(), couponUserMapping.getInfoId(), couponUserMapping.getCumNumber());
                    }

                    // todo 大并发时可能会出现超发的现象，后续判断是否有必须处理
                }
            }
        }
        log.info("[scoreBindcarReceive]end");
        return result;
    }

    @SneakyThrows
    public PagingInfo branchList(CouponBranchListForm form) {
        PagingInfo pagingInfo = new PagingInfo<>();
        DriverBaseService.getPage(form);
        try {
            Map<String, Object> param = new HashMap<>(16);
            param.put("infoId", form.getInfoId());
            param.put("areaCode", form.getAreaCode());
            param.put("DbName", this.DbName);
            Page<CouponExchangeBranch> page = couponInfoDao.queryBranchList(param);
            PagingInfo<CouponExchangeBranch> resultDto = super.convertPagingToPage(page);
            pagingInfo.setList(resultDto.getList());
            pagingInfo.setPage_total(resultDto.getPage_total());
            pagingInfo.setTotal(resultDto.getTotal());
        } catch (Exception e) {
            log.error("[branchList] Exception:", e);
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "系统异常");
        }
        log.info("branchList end return:{}",pagingInfo);
        return pagingInfo;
    }

    public void processBindNewRegPhoneCoupon(Map<String, Object> param) {
        List<CouponNewRegPhone> newRegPhones = couponUserMappingDao.queryBindNewRegPhoneCoupon(param);
        log.info("queryBindNewRegPhoneCoupon ={}", newRegPhones.toString());
        log.info("newRegPhones:{}", newRegPhones);
        for (CouponNewRegPhone newReg : newRegPhones) {
            param.put("cumId", newReg.getCumId());
            log.info("cumId:{}", newReg.getCumId());
            couponUserMappingDao.updateBindNewRegPhoneCoupon(param);
        }
    }

    private String getValidDateDesc(String valid, String validDay, String validStartDate, String validEndDate, String cumGiveTime) {
        String validDateDesc = "";
        switch (valid) {
            case "1":
                validDateDesc = "不限制";
                break;
            case "2":
                if (StringUtil.isNotEmpty(cumGiveTime) && StringUtil.isNotEmpty(validDay)) {
                    Date giveDate = DateUtil.parseDate(cumGiveTime.substring(0, 10));
                    validDateDesc = DateUtil.formatDate(giveDate) + "至" + DateUtil.formatDate(DateUtil.addDay(giveDate, Integer.valueOf(validDay)));
                }
                break;
            case "3":
                validDateDesc = validStartDate + "至" + validEndDate;
                break;
            default:
                break;
        }
        return validDateDesc;
    }

    private String getApplicable(String infoId, String applicableType, Long userId) {
        Map<String, Object> param = new HashMap<>(16);
        param.put("infoId", infoId);
        param.put("applicableType", applicableType);
        List<CouponApplicableEntity> couponApplicableList = couponApplicableDao.selectList(param);
        if (CollectionUtils.isNotEmpty(couponApplicableList)) {
            //车系设置
            if ("1".equals(applicableType)) {
                CouponApplicableEntity ca = couponApplicableList.get(0);
                if ("-1".equals(ca.getApplicableVehicleEmission()) && "-1".equals(ca.getApplicableVehicleModel()) && "-1".equals(ca.getApplicableVehiclePlatform())) {
                    return "全部车型可用";
                } else {
                    return "部分车型可用";
                }
                //指定底盘号
            } else if ("2".equals(applicableType)) {
                String number = couponApplicableList.get(0).getApplicableNumber();
                //return "底盘号" + number + "可用";
                return "";
                //指定车型号
            } else if ("3".equals(applicableType)) {
                String number = couponApplicableList.get(0).getApplicableNumber();
                return "车型号" + number + "可用";
                //指定手机号
            } else if ("4".equals(applicableType)) {
                UserInfoPojo byId = userDao.findById(userId);
                return "手机号" + byId.getPhone() + "可用";
            }
        }
        return null;
    }

    /**
     * 查询所有适用车型
     *
     * @param infoId
     * @return
     */
    public List<String> selectApplicableModelName(Integer infoId) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("infoId", infoId);
        List<String> modelName = couponInfoDao.selectApplicableModelName(param);
        return modelName;
    }

    /**
     * 判断车辆是否适用
     *
     * @param userId
     * @param infoId
     * @return
     */
    private boolean isApplicable(Long userId, String applicableType, String infoId, String infoType) {
        boolean b = false;
        //查询适用信息
        Map<String, Object> param = new HashMap<>(16);
        param.put("infoId", infoId);
        param.put("applicableType", applicableType);
        //当前优惠券适用的车型等
        List<CouponApplicableEntity> applicableList = couponApplicableDao.selectList(param);
        if ("4".equals(applicableType)) {
            UserEntity u = userDao.selectByPrimaryKey(userId);
            for (CouponApplicableEntity a : applicableList) {
                if (a.getApplicableNumber().equals(u.getPhone())) {
                    b = true;
                    break;
                }
            }
        } else {
            if (CouponVal.TYPE_BUY_VEHICLE.equals(infoType)) {
                return true;
            }
            //当前用户的车型信息等
            List<Map<String, Object>> ownerCarInfoList = couponApplicableDao.queryOwnerCarInfo(userId, DbName);
            if (CollectionUtils.isEmpty(ownerCarInfoList)) {
                return b;
            }
            //车系设置
            if ("1".equals(applicableType)) {
                boolean seriseIdMatch = false, carModelMatch = false, emissionCodeMatch = false, driveCodeMatch = false;
                OUT:
                for (Map<String, Object> myCouponInfo : ownerCarInfoList) {
                    for (CouponApplicableEntity ca : applicableList) {
                        Map<String, Object> params = new HashMap<>(16);
                        params.put("strains", ca.getApplicableVehicleModel());
                        params.put("platform", ca.getApplicableVehiclePlatform());
                        params.put("driver_type", ca.getApplicableVehicleDrive());
                        params.put("displacement", ca.getApplicableVehicleDisplacement());
                        params.put("emissionStandards", ca.getApplicableVehicleEmission());
                        List<String> carModels = carTypeIntroduceSonDao.queryCarModels(params);
                        if (carModels.contains(myCouponInfo.get("carModelCode"))) {
                            b = true;
                            break;
                        }
                    }
                }
                //指定底盘号
            } else if ("2".equals(applicableType)) {
                OUT:
                for (Map<String, Object> myCouponInfo : ownerCarInfoList) {
                    for (CouponApplicableEntity ca : applicableList) {
                        String number = ca.getApplicableNumber();
                        if (number.equals(myCouponInfo.get("carVin"))) {
                            b = true;
                            break OUT;
                        }
                    }
                }
                //指定车型号
            } else if ("3".equals(applicableType)) {
                OUT:
                for (Map<String, Object> myCouponInfo : ownerCarInfoList) {
                    for (CouponApplicableEntity ca : applicableList) {
                        String number = ca.getApplicableNumber();
                        if (number.equals(myCouponInfo.get("carModelCode"))) {
                            b = true;
                            break OUT;
                        }
                    }
                }
            } else if ("5".equals(applicableType)) {
                OUT:
                for (Map<String, Object> myCouponInfo : ownerCarInfoList) {
                    if (null != myCouponInfo.get("tStyle") && !myCouponInfo.get("tStyle").equals("")) {
                        for (CouponApplicableEntity ca : applicableList) {
                            String number = ca.getApplicableNumber();
                            if (number.equals(myCouponInfo.get("tStyle").toString())) {
                                b = true;
                                break OUT;
                            }
                        }
                    }
                }
            }
        }
        return b;
    }

    /**
     * 过滤掉已经达到发券数量的优惠券规则
     */
    private List<CouponInfo> filterTotalCount(List<CouponInfo> couponInfoList) {
        List<Long> infoIds = new ArrayList<>(couponInfoList.size());
        for (CouponInfo info : couponInfoList) {
            switch (info.getApplicableType()){
                case "1" :
                    infoIds.add(info.getId());
                    break;
                case "2" :
                    infoIds.add(info.getId());
                    break;
                case "4" :
                    infoIds.add(info.getId());
                    break;
                case "8" :
                    infoIds.add(info.getId());
                    break;
                default:
                    break;
            }
        }
        if (infoIds.isEmpty()) {
            log.info("过滤完成，没有自定义优惠券类型！！");
            return couponInfoList;
        }

        List<Map<String, Long>> infoCountResult = couponUserMappingDao.countByInfoIdInt(infoIds);
        Map<Long, Long> infoCount = new HashMap<>(infoCountResult.size());
        for (Map<String, Long> map : infoCountResult) {
            infoCount.put(map.get("info_id"), map.get("num"));
            log.info("优惠券id: {}, 已发数量: {}", map.get("info_id"), map.get("num"));
        }

        List<CouponInfo> result = new ArrayList<>(couponInfoList.size());
        for (CouponInfo info : couponInfoList) {
            if (infoCount.containsKey(info.getId()) && info.getInfoTotalCount() != null) {
                String totalCout = info.getInfoTotalCount().trim();
                if (NumberUtils.isDigits(totalCout) && Long.parseLong(totalCout) <= infoCount.get(info.getId())) {
                    log.info("优惠券已经超发, 优惠券Id: {}, 计划发放数量:{}, 实际发放数量: {}", info.getId(), totalCout, infoCount.get(info.getId()));
                    continue;
                }
            }
            // 过滤优惠券
            if ("8".equals(info.getApplicableType())) {
                log.info("过滤首保券：{}", info.getId());
                continue;
            }
            result.add(info);
        }
        return result;
    }

    /**
     * 限制车型、底盘号、或用户（手机号）
     *
     * @param couponInfo
     * @param car
     * @param user
     * @return
     */
    private boolean checkCar(CouponInfo couponInfo, CarEntity car, UserEntity user) {
        //老数据没有这些限制
        if (org.apache.commons.lang.StringUtils.isBlank(couponInfo.getCouponApplicableType())) {
            return true;
        }
        boolean flag = false;
        if (couponInfo.getCouponApplicableType().equals(CouponVal.APPLICABLE_TYPE_VIN) && car.getCarVin().contains(couponInfo.getApplicableNumber())) {
            flag = true;
        } else if (couponInfo.getCouponApplicableType().equals(CouponVal.APPLICABLE_TYPE_SERIES)) {
            List<CouponApplicable> couponApplicables = couponInfo.getCouponApplicables();
            for (CouponApplicable couponApplicable : couponApplicables) {
                if (couponApplicable.getApplicableVehicleModel().equals("-1") && couponApplicable.getApplicableVehiclePlatform().equals("-1")) {
                    flag = true;
                    break;
                } else if (couponApplicable.getApplicableVehiclePlatform().equals("-1") && couponApplicable.getApplicableVehicleModel().equals(car.getCarModelName())) {
                    flag = true;
                    break;
                } else if (couponApplicable.getApplicableVehicleModel().equals(car.getCarModelName()) &&
                        couponApplicable.getApplicableVehiclePlatform().equals(car.getCarSeries())) {
                    flag = true;
                    break;
                }
            }
        } else if (couponInfo.getCouponApplicableType().equals(CouponVal.APPLICABLE_TYPE_PHONE) && couponInfo.getApplicableNumber().equals(user.getPhone())) {
            flag = true;
        }
        return flag;
    }

}
