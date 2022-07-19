package com.nut.servicestation.app.service.impl;

import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.DateUtil;
import com.nut.servicestation.app.dao.CouponUserMappingDao;
import com.nut.servicestation.app.dao.WoInfoDao;
import com.nut.servicestation.app.dao.WorkOrderDao;
import com.nut.servicestation.app.domain.CouponUserMapping;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.form.AcceptVinForm;
import com.nut.servicestation.app.form.CouponExchangeForm;
import com.nut.servicestation.app.service.FirstConfirmedService;
import com.nut.servicestation.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/7
 */
@Slf4j
@Service("FirstConfirmedService")
public class FirstConfirmedServiceImpl implements FirstConfirmedService {


    @Autowired
    private WoInfoDao woInfoMapper;
    @Autowired
    private WorkOrderDao workOrderMapper;
    @Autowired
    private CouponUserMappingDao couponUserMappingMapper;
    @Autowired
    private UserService userService;


    @Override
    public HttpCommandResultWithData qualificationTest(AcceptVinForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        Map<String, Object> pamrm = new HashMap<>();
        // 此车所有的工单
        List<String> woAllCodeList = woInfoMapper.queryAllCodeByVin(command.getCarVin());
        log.info("【检验首保资格】start");
        /* 首保判断 */
        log.info("进入for循环进行判断");
        Long firstCnt = workOrderMapper.queryFirstCountByChuNum(command.getCarVin());
        if (firstCnt != null && firstCnt.longValue() > 0) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("该车辆已执行过首保！");
            return result;
        }
        log.info("【检验首保资格】end");
        log.info("【查询未完成的工单】start");
        List<String> woCodeList = woInfoMapper.queryCodeByVin(command.getCarVin());
        if (woCodeList.isEmpty()) {
            result.setMessage("当前此车辆没有正在执行的工单号");
            pamrm.put("woCode", null);
        } else {
            result.setMessage("当前此车辆有正在执行的工单号");
            pamrm.put("woCode", woCodeList.get(0));
        }
        log.info("【查询未完成的工单】end");
        result.setData(pamrm);
        return result;
    }

    @Override
    public HttpCommandResultWithData firstExchange(CouponExchangeForm command) {
        log.info("[FirstExchange]start");
        HttpCommandResultWithData<Object> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        result.setMessage("使用成功");
        // 获取首保券信息
        CouponUserMapping cum = couponUserMappingMapper.selectByPrimaryKey(Long.valueOf(command.getCumId()));
        // 获取兑换网点信息
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        // 判断首保券是否被使用
        if("2".equals(cum.getCumStatus())){
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("首保券已被使用");
        }
        // 首保券更新
        CouponUserMapping update = new CouponUserMapping();
        update.setCumId(Long.valueOf(command.getCumId()));
        update.setCumStatus("2");
        update.setCumExchangeTime(DateUtil.getFormatNowDate(DateUtil.time_pattern));
        update.setExchangeBranchId(Long.valueOf(userInfoDto.getServiceStationId()));
        update.setExchangeBranchType("2");
        update.setCumApprovalTime(DateUtil.getFormatNowDate(DateUtil.time_pattern));
        update.setCumApprovalStatus("2");
        couponUserMappingMapper.updateByPrimaryKeySelective(update);
        log.info("[FirstExchange]end");
        return result;
    }
}
