package com.nut.servicestation.app.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.dao.AreaStationDao;
import com.nut.servicestation.app.dao.CarDao;
import com.nut.servicestation.app.dao.CouponInfoDao;
import com.nut.servicestation.app.dao.UserDao;
import com.nut.servicestation.app.domain.*;
import com.nut.servicestation.app.dto.CouponInfoDto;
import com.nut.servicestation.app.dto.StationDto;
import com.nut.servicestation.app.form.*;
import com.nut.servicestation.app.service.Coupon2Service;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.service.impl
 * @Author: yzl
 * @CreateTime: 2021-07-26 14:00
 * @Version: 1.0
 */
@Service("Coupon2Service")
@Slf4j
public class Coupon2ServiceImpl implements Coupon2Service {

    @Value("${database_name}")
    private String DbName;

    @Autowired
    private CouponInfoDao couponInfoDao;

    @Autowired
    private AreaStationDao areaStationDao;

    @Autowired
    private CarDao carDao;

    @Override
    @Transactional
    public HttpCommandResultWithData add(Coupon2Form form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        String vin = form.getVin();
        // ???????????????????????????
        List<Long> listId = carDao.queryUcIdByVin0(vin);    // 1.??????????????????????????????????????????userId??????
        Long userId = 0L;                                   //
        if (listId.size() == 0){
            // ???????????????????????????????????????????????????userId
            log.info("??????vin??????????????????????????????");
            result.setMessage("??????vin??????????????????????????????");
            result.setResultCode(ECode.SERVER_ERROR.code());
            return result;
        }else if(listId.size() == 1){
            // ??????????????????????????????????????????????????????
            userId = listId.get(0);
        }else if(listId.size() > 1){
            log.info("???????????????????????????????????????APP????????????????????????????????????????????????");
            userId = carDao.queryUcIdByVin2(vin);
        }
        CouponInfo couponInfo = formToCouponInfo(form);
        couponInfoDao.insertSelective(couponInfo);  // ??????coupon_info???
        CouponApplicable couponApplicable = formToApplicable(couponInfo.getId(), form);
        couponInfoDao.batchInsertApplicable(couponApplicable);  // ??????coupon_applicable???
        List<BranchInfo> list = new ArrayList<>();  // ?????????????????????
        if (form.getIsAll() == null || form.getIsAll().equals("1")) {   // ??????????????????
            list = couponInfoDao.queryBranchList(DbName);
        } else {         // ??????????????????
            if (form.getStationList().size() != 0) {
                for (String id : form.getStationList()) {
                    BranchInfo branchInfo = new BranchInfo();
                    branchInfo.setBranchId(id);
                    list.add(branchInfo);
                }
            } else {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("????????????????????????");
                return result;
            }
        }
        List<CouponExchange> exchangeList = new ArrayList<>();
        for (BranchInfo info : list) {
            CouponExchange couponExchange = new CouponExchange();
            couponExchange.setInfoId(couponInfo.getId());
            couponExchange.setExchangeBranchType("2");
            couponExchange.setExchangeBranchId(info.getBranchId());
            couponExchange.setCreateTime(new Date());
            exchangeList.add(couponExchange);
        }
        couponInfoDao.batchInsertExchange(exchangeList);  // ???????????????????????????????????????
        result = giveNow(couponInfo, userId);    // ??????
        return result;
    }

    @Override
    public HttpCommandResultWithData list(QueryCouponListForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        List<CouponGiveInfo> list = new ArrayList<>();
        try {
            list = couponInfoDao.queryListByServiceCode(form.getServiceCode());
            for (CouponGiveInfo info : list) {
                String str = info.getEndDate();
                if (str != null){
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate lastTime = LocalDate.parse(str, fmt);
                    LocalDate nowTime = LocalDate.now();
                    Long daysDiff = ChronoUnit.DAYS.between(lastTime, nowTime);
                    if (daysDiff >= 1){
                        info.setCouponStatus("3");
                    }
                }

            }
            result.setData(list);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("????????????????????????");
        }
        return result;
    }

    @Override
    public HttpCommandResultWithData detail(QueryCouponDetailForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        List<StationDto> list = new ArrayList<>();
        try {
            CouponGiveDetail infoDetail = couponInfoDao.queryDetailById(Integer.valueOf(form.getCouponId()));
            if (infoDetail == null) {
                result.setResultCode(ECode.SERVER_ERROR.code());
                result.setMessage("????????????????????????????????????");
            }
            list = areaStationDao.getStationListById(DbName, Integer.valueOf(form.getCouponId()));
            infoDetail.setValidTime();
            infoDetail.setList(list);
            result.setData(infoDetail);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("????????????");
        }
        return result;
    }

    private CouponInfo formToCouponInfo(Coupon2Form form) {
        CouponInfo couponInfo = new CouponInfo();
        couponInfo.setInfoType("1");  // 1:???????????????
        couponInfo.setInfoName(form.getCouponName());  // ??????
        couponInfo.setInfoContent(form.getCouponMessage()); // ?????????
        couponInfo.setInfoValue(form.getCouponValue());  // ?????????
        couponInfo.setInfoValid(form.getCouponValid());  // ??????????????????
        couponInfo.setInfoValidStartDate(form.getStartTime());  // ???????????????
        couponInfo.setInfoValidEndDate(form.getEndTime());  // ???????????????
        couponInfo.setUpdateUser(form.getServiceCode());  // ?????????
        couponInfo.setServiceStationCode(form.getServiceCode());  // ???????????????-???????????????
        couponInfo.setExchangeType("2");  // 2:??????????????????
        couponInfo.setGiveType("1");  // 1:????????????
        couponInfo.setApplicableType("2");  // 4:???????????????
        couponInfo.setInfoTotalCountType("1");  // 1:?????????????????????
        couponInfo.setInfoStatus("1");  // 1:?????????????????????
        couponInfo.setCreateTime(new Date());  // ????????????
        return couponInfo;
    }

    private CouponApplicable formToApplicable(Long id, Coupon2Form form) {
        CouponApplicable couponApplicable = new CouponApplicable();
        couponApplicable.setInfoId(id);
        couponApplicable.setApplicableType("2");
        couponApplicable.setApplicableNumber(form.getVin());
        couponApplicable.setCreateTime(new Date());
        return couponApplicable;
    }

    // ????????????
    @Async
    @Transactional
    public HttpCommandResultWithData giveNow(CouponInfo couponInfo, Long userId) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        CouponUserMapping cum = new CouponUserMapping();
        cum.setInfoId(couponInfo.getId());
        cum.setUserId(userId);
        cum.setCumGiveTime(DateUtil.formatTime(new Date()));
        cum.setCumStatus("1");
        cum.setGiveBranchType("2");
        cum.setCumNumber(generateCode());
        if (couponInfo.getInfoValid().equals("1")) {
            cum.setInfoValid("1");
        }
        if (couponInfo.getInfoValid().equals("3")) {
            cum.setInfoValid("3");
            cum.setInfoValidStartDate(couponInfo.getInfoValidStartDate());
            cum.setInfoValidEndDate(couponInfo.getInfoValidEndDate());
        }
        couponInfoDao.batchInsertCUM(cum);
        return result;
    }

    // ??????????????????
    private String generateCode() {
        StringBuilder uid = new StringBuilder();
        //??????16??????????????????
        Random rd = new SecureRandom();
        for (int i = 0; i < 16; i++) {
            //??????0-1???2????????????
            int type = rd.nextInt(2);
            switch (type) {
                case 0:
                    //0-9????????????
                    uid.append(rd.nextInt(10));
                    break;
                case 1:
                    //ASCII???65-90???????????????,??????????????????
                    uid.append((char) (rd.nextInt(25) + 65));
                    break;
                default:
                    break;
            }
        }
        return uid.toString();
    }


}


