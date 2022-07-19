package com.nut.servicestation.app.service.impl;

import com.github.pagehelper.Page;
import com.nut.common.constant.CouponVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.dao.CouponInfoDao;
import com.nut.servicestation.app.dao.CouponUserMappingDao;
import com.nut.servicestation.app.domain.CouponApproval;
import com.nut.servicestation.app.domain.CouponInfo;
import com.nut.servicestation.app.domain.CouponInfoDetail;
import com.nut.servicestation.app.domain.CouponUserMapping;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.form.CouponApprovalListForm;
import com.nut.servicestation.app.form.CouponDetailForm;
import com.nut.servicestation.app.form.CouponExchangeForm;
import com.nut.servicestation.app.service.CouponService;
import com.nut.servicestation.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/7
 */
@Slf4j
@Service("CouponService")
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponInfoDao couponInfoMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private CouponUserMappingDao couponUserMappingMapper;


    @Override
    public HttpCommandResultWithData detail(CouponDetailForm command) {
        log.info("[detail]start");
        HttpCommandResultWithData<Object> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        //查询优惠券
        CouponInfoDetail detail = couponInfoMapper.queryCouponDetail(command);
        if(detail == null){
            result.setMessage("没有该优惠券");
            result.setResultCode(ECode.CLIENT_ERROR.code());
            return result;
        }
        //设置有效期
        if (StringUtil.isEmpty(detail.getInfoValid())) {
            detail.setInfoValidDate("使用时间无限制");
        } else {
            String infoValidDate = this.getValidDateDesc(detail.getInfoValid(), detail.getInfoValidDay(), detail.getInfoValidStartDate(), detail.getInfoValidEndDate(), detail.getCumGiveTime());
            detail.setInfoValidDate(infoValidDate);
        }
        //校验优惠券是否可用
        Map<String, Object> param = new HashMap<>(16);
        param.put("cumNumber", command.getCumNumber());
//        UserInfoDto userInfo = userInfoService.getUserInfoFromRedis(command.getToken());
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        param.put("teamId", Long.valueOf(userInfoDto.getServiceStationId()));
        HttpCommandResultWithData validResult = this.validCouponAvailable(detail.getInfoType(), param);
        if(validResult != null){
            return validResult;
        }
        result.setData(detail);
        log.info("[detail]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData approvalList(CouponApprovalListForm command) {
        log.info("[approvalList]start");
        HttpCommandResultWithData<Object> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
//        UserInfoDto userInfo = userInfoService.getUserInfoFromRedis(command.getToken());
        UserInfoDto userInfoDto =userService.getUserInfoByUserId(command.getUserId(), false);
        PagingInfo pagingInfo = new PagingInfo<>();
        ServiceStationBaseService.getPage(command, true);
        if (StringUtil.isNotEmpty(command.getCumApprovalTimeStart())) {
            command.setCumApprovalTimeStart(command.getCumApprovalTimeStart() + " 00:00:00");
        }
        if (StringUtil.isNotEmpty(command.getCumApprovalTimeEnd())) {
            command.setCumApprovalTimeEnd(command.getCumApprovalTimeEnd() + " 23:59:59");
        }
        try {
            Map<String, Object> param = new HashMap<>(16);
            param.put("cumApprovalTimeStart", command.getCumApprovalTimeStart());
            param.put("cumApprovalTimeEnd", command.getCumApprovalTimeEnd());
            param.put("userPhone", command.getUserPhone());
            param.put("branchType", "2");
            param.put("branchId", Long.valueOf(userInfoDto.getServiceStationId()));
            Page<CouponApproval> page = couponInfoMapper.queryApprovalList(param);
            PagingInfo<CouponApproval> resultDto = ServiceStationBaseService.convertPagingToPage(page);
            for(CouponApproval ca : resultDto.getList()){
                ca.setInfoValidDate(this.getValidDateDesc(ca.getInfoValid(), ca.getInfoValidDay(), ca.getInfoValidStartDate(), ca.getInfoValidEndDate()));
            }
            pagingInfo.setList(resultDto.getList());
            pagingInfo.setPage_total(resultDto.getPage_total());
            pagingInfo.setTotal(resultDto.getTotal());
            result.setData(pagingInfo);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("系统异常");
            log.error("[approvalList] Exception:", e);
        }
        log.info("[approvalList]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData exchange(CouponExchangeForm command) {
        log.info("[exchange]start");
        HttpCommandResultWithData<Object> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        //校验优惠券是否可用
        CouponUserMapping cum = couponUserMappingMapper.selectByPrimaryKey(Long.valueOf(command.getCumId()));
        CouponInfo ci = couponInfoMapper.selectByPrimaryKey(cum.getInfoId());
        Map<String, Object> param = new HashMap<>(16);
        param.put("cumId", command.getCumId());
//        UserInfoDto userInfo = userInfoService.getUserInfoFromRedis(command.getToken());
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        param.put("teamId", Long.valueOf(userInfoDto.getServiceStationId()));
        HttpCommandResultWithData validResult = this.validCouponAvailable(ci.getInfoType(), param);
        if(validResult != null){
            return validResult;
        }
        //兑换
        CouponUserMapping update = new CouponUserMapping();
        update.setCumId(Long.valueOf(command.getCumId()));
        update.setCumStatus("2");
        update.setCumExchangeTime(DateUtil.getFormatNowDate(DateUtil.time_pattern));
        update.setExchangeBranchId(Long.valueOf(userInfoDto.getServiceStationId()));
        update.setExchangeBranchType("2");
        update.setCumApprovalTime(DateUtil.getFormatNowDate(DateUtil.time_pattern));
        update.setCumApprovalStatus("2");
        couponUserMappingMapper.updateByPrimaryKeySelective(update);
        log.info("[exchange]end");
        return result;
    }

    private String getValidDateDesc(String valid, String validDay, String validStartDate, String validEndDate, String cumGiveTime) {
        String validDateDesc = "";
        switch(valid){
            case "1":
                validDateDesc = "不限制";
                break;
            case "2":
                if(StringUtil.isNotEmpty(cumGiveTime) && StringUtil.isNotEmpty(validDay)){
                    Date giveDate = DateUtil.parseDate(cumGiveTime.substring(0, 10));
                    validDateDesc = DateUtil.formatDate(giveDate)+"至"+ DateUtil.formatDate(DateUtil.addDay(giveDate, Integer.valueOf(validDay)));
                }
                break;
            case "3":
                validDateDesc = validStartDate+"至"+validEndDate;
                break;
            default:
                break;
        }
        return validDateDesc;
    }
    /**
     * 校验优惠券
     * @param param
     * @return
     */
    private HttpCommandResultWithData validCouponAvailable(String infoType, Map<String, Object> param) {
        if(CouponVal.TYPE_OFFLINE.equals(infoType) || CouponVal.TYPE_BUY_VEHICLE.equals(infoType)) {
            long count = couponInfoMapper.countCouponBranch(param);
            if (count == 0) {
                return new HttpCommandResultWithData(ECode.COUPON_INVALID.code(), "该优惠券与网点不匹配");
            }
        }
        Map<String, String> statusMap = couponInfoMapper.queryCouponStatus(param);
        if("2".equals(statusMap.get("cumStatus"))){
            return new HttpCommandResultWithData(ECode.CLIENT_ERROR.code(), "优惠券已使用");
        }
        String infoValid = statusMap.get("infoValid");
        if("2".equals(infoValid)){
            Date cumGiveTime = DateUtil.parseDate(statusMap.get("cumGiveTime"), DateUtil.time_pattern);
            Date validDate = DateUtil.addDay(cumGiveTime, Integer.valueOf(statusMap.get("infoValidDay")));
            if(2 == DateUtil.diffNowDate(DateUtil.formatTime(validDate).substring(0, 10))){
                return new HttpCommandResultWithData(ECode.CLIENT_ERROR.code(), "优惠券已过期");
            }
        }
        if("3".equals(infoValid)){
            if(2 == DateUtil.diffNowDate(statusMap.get("infoValidEndDate"))) {
                return new HttpCommandResultWithData(ECode.CLIENT_ERROR.code(), "优惠券已过期");
            }
            if(1 == DateUtil.diffNowDate(statusMap.get("infoValidStartDate"))) {
                return new HttpCommandResultWithData(ECode.CLIENT_ERROR.code(), "优惠券未到使用期");
            }
        }
        if("3".equals(statusMap.get("infoStatus")) || "4".equals(statusMap.get("infoStatus"))){
            return new HttpCommandResultWithData(ECode.CLIENT_ERROR.code(), "优惠券已停兑");
        }
        return null;
    }
    private String getValidDateDesc(String valid, String validDay, String validStartDate, String validEndDate) {
        String validDateDesc = "";
        switch(valid){
            case "1":
                validDateDesc = "不限制";
                break;
            case "2":
                validDateDesc = "发放后"+validDay+"日内有效";
                break;
            case "3":
                validDateDesc = validStartDate+"至"+validEndDate;
                break;
            default:
                break;
        }
        return validDateDesc;
    }
}
