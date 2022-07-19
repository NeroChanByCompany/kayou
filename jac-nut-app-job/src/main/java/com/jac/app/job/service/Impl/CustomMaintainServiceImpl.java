package com.jac.app.job.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.util.StringUtil;
import com.jac.app.job.client.LocationServiceClient;
import com.jac.app.job.common.Result;
import com.jac.app.job.constant.IxinPushStaticLocarVal;
import com.jac.app.job.domain.CustomMaintainInfo;
import com.jac.app.job.domain.UserMessageRecord;
import com.jac.app.job.entity.CustomMaintainEntity;
import com.jac.app.job.entity.MaintainSendEntity;
import com.jac.app.job.mapper.CarMapper;
import com.jac.app.job.mapper.CustomMaintainMapper;
import com.jac.app.job.mapper.UserMessageRecordMapper;
import com.jac.app.job.service.CustomMaintainService;
import com.jac.app.job.util.DateUtils;
import com.jac.app.job.vo.GetCarLastLocationByAutVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liuBing
 * @Classname CustomMaintainService
 * @Description TODO
 * @Date 2021/8/12 16:20
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, readOnly = true)
public class CustomMaintainServiceImpl extends ServiceImpl<CustomMaintainMapper, CustomMaintainEntity> implements CustomMaintainService {

    @Autowired
    CustomMaintainMapper customMaintainMapper;

    @Autowired
    LocationServiceClient locationServiceClient;

    @Autowired
    UserMessageRecordMapper userMessageRecordMapper;

    @Autowired
    CarMapper carMapper;

    @Override
    public List<MaintainSendEntity> selectIsMaintains() {
        log.info("CustomMaintainService---selectIsMaintains:---begin-----");

        List<CustomMaintainInfo> customMaintainInfos = customMaintainMapper.queryMaintanceInfos();
        List<MaintainSendEntity> maintainSendEntities = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        log.info("customMaintainInfos list :{}",customMaintainInfos);
        if (!customMaintainInfos.isEmpty()) {
            //判断时间是否过期
            for (CustomMaintainInfo customMaintainInfo : customMaintainInfos) {
                //根据时间判断 并且从未通知过
                if (customMaintainInfo.getCustomMaintainType().equals(2) && (customMaintainInfo.getRemind() == null || customMaintainInfo.getRemind().equals(2))) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = sdf.parse(customMaintainInfo.getCustomMaintainDescribe());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //如果时间等于今天，那么就向发送该消息 并且查出需要保养的保养项
                    if (DateUtils.sameDate(date, new Date())) {
                        //查出保养项
                        List<String> items = new ArrayList<>();
                        if (customMaintainInfo.getId() != null) {
                            items = customMaintainMapper.selectByMaintainId(customMaintainInfo.getId());
                        }
                        int i = 1;
                        for (String item : items) {
                            sb.append(i + "、").append(item).append(";");
                            i++;
                        }
                        maintainSendEntities.add(new MaintainSendEntity()
                                .setUserId(customMaintainInfo.getUserId())
                                .setMaintainInfoId(customMaintainInfo.getId())
                                .setTitle("保养提醒")
                                .setType("2")
                                .setContent("您的爱车【底盘号：" + customMaintainInfo.getVin().substring(customMaintainInfo.getVin().length() - 8) + "】" + "在"
                                        + customMaintainInfo.getCustomMaintainDescribe().substring(0, customMaintainInfo.getCustomMaintainDescribe().indexOf(" "))
                                        + "需要进行保养，保养项:" + sb.substring(0, sb.length() - 1) + "。")
                        );
                    }
                }
                //根据公里数判断 并且从未通知过 并且查出需要保养的保养项
                if (customMaintainInfo.getCustomMaintainType().equals(1) && (customMaintainInfo.getRemind() == null || customMaintainInfo.getRemind().equals(2))) {
                    //获取最大公里数
                    String mileage = getMellage(customMaintainInfo.getCarId());
                    //已行驶里程数
                    if (Math.ceil(Double.parseDouble(mileage)) == Math.ceil(Double.parseDouble(customMaintainInfo.getCustomMaintainDescribe()))) {
                        //查出保养项
                        List<String> items = new ArrayList<>();
                        if (customMaintainInfo.getId() != null) {
                            items = customMaintainMapper.selectByMaintainId(customMaintainInfo.getId());
                        }
                        int i = 1;
                        for (String item : items) {
                            sb.append(i + "、").append(item).append(";");
                            i++;
                        }
                        maintainSendEntities.add(new MaintainSendEntity()
                                .setUserId(customMaintainInfo.getUserId())
                                .setMaintainInfoId(customMaintainInfo.getId())
                                .setTitle("保养提醒")
                                .setType("1")
                                .setContent("您的爱车【底盘号：" + customMaintainInfo.getVin().substring(customMaintainInfo.getVin().length() - 8) + "】" + "已行驶"
                                        + mileage + "km，需要进行保养，保养项:"
                                        + sb.substring(0, sb.length() - 1) + "。")
                        );
                    }
                }
            }
        }
        log.info("CustomMaintainService---selectIsMaintains:---end-----");
        return maintainSendEntities;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePushed(Long maintainInfoId, String type) {
        log.info("CustomMaintainService---updatePushed:---begin-----");
        CustomMaintainInfo record = new CustomMaintainInfo();
        record.setId(maintainInfoId);
        record.setRemind(1);
        record.setMaintainStatus(0);
        record.setCustomMaintainType(Integer.parseInt(type));
        customMaintainMapper.updateByPrimaryKeySelective(record);
        log.info("CustomMaintainService---updatePushed:---end-----");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMessage(MaintainSendEntity maintain) {
        UserMessageRecord userMessageRecord = new UserMessageRecord();
        userMessageRecord.setMessageId(UUID.randomUUID().toString().replaceAll("-", ""));
        userMessageRecord.setTitle(maintain.getTitle());
        userMessageRecord.setContent(maintain.getContent());
        userMessageRecord.setReceiverId(maintain.getUserId());
        userMessageRecord.setPushShowType(Integer.parseInt(IxinPushStaticLocarVal.PUSH_SHOW_TYPE_USER));
        userMessageRecord.setMessageCode(Integer.parseInt(IxinPushStaticLocarVal.MESSAGE_CODE_YUN_YING));
        userMessageRecord.setUserVisible(Integer.parseInt(IxinPushStaticLocarVal.IS_USER_VISIBLE));
        userMessageRecord.setReceiveAppType(Integer.parseInt(IxinPushStaticLocarVal.PUSH_RECEIVETYPE_ZERO));
        userMessageRecord.setMessageType(maintain.getMessageType());
        userMessageRecord.setMessageTypeName(maintain.getMessageTypeName());
        //目前固定死，后续根据业务更改
        userMessageRecord.setSenderId("40b90633dee8433ca189f91273031c11");
        userMessageRecord.setSendTime(System.currentTimeMillis());
        userMessageRecord.setReadFlag(IxinPushStaticLocarVal.UNREAD);
        userMessageRecord.setMessageExtra("");
        userMessageRecord.setType(Integer.parseInt(IxinPushStaticLocarVal.PUSH_TYPE_NOTICE));
        userMessageRecord.setStype(Integer.parseInt(IxinPushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_MAINTENANCE));
        userMessageRecord.setReceiveState(Integer.parseInt(IxinPushStaticLocarVal.RECEIVE_STATE));
        userMessageRecord.setCreateTime(new Date());
        userMessageRecord.setUpdateTime(new Date());
        userMessageRecordMapper.insertSelective(userMessageRecord);
    }


    /**
     * 获取末次位置总里程
     *
     * @param carId
     * @return
     */
    public String getMellage(String carId) {

        String mileage = "0.00";
        // 查询终端通信号
        String autoTerminal = carMapper.queryAutoTerminalByCarId(carId);
        if (StringUtil.isEmpty(autoTerminal)) {
            log.info("autoTerminal 是空的");
            return mileage;
        }
        List<Long> autoTerminals = new ArrayList<>();
        autoTerminals.add(Long.valueOf(autoTerminal));
        Map<Long, LinkedHashMap> map = queryLastLocation(autoTerminals);
        if (map != null && !map.isEmpty()) {
            for (LinkedHashMap lastLocationDto : map.values()) {
                if (lastLocationDto != null && lastLocationDto.get("mileage") != null) {
                    double mileageDouble = Double.parseDouble(String.valueOf(lastLocationDto.get("mileage")));
                    mileage = String.valueOf(mileageDouble);
                    break;
                }
            }
        }
        log.info("返回的最大公里数：" + mileage);
        return mileage;
    }

    /**
     * 查询车辆末次位置
     * @param autoTerminal
     * @return
     */
    public Map<Long, LinkedHashMap> queryLastLocation(List<Long> autoTerminal) {

        Map<Long, LinkedHashMap> map = new HashMap<>();
        // 查询车辆位置信息
        GetCarLastLocationByAutVO locVO = new GetCarLastLocationByAutVO();
        locVO.setTerminalIdList(autoTerminal);
        Result locationResult = locationServiceClient.getCarLastLocationByAut(locVO);
        if (locationResult != null) {
            if (locationResult.getResultCode() != Result.SUCCESS_CODE) {
                log.info("getCarLocation errMessage : " + locationResult.getMessage());
                return map;
            }
            map = (Map<Long, LinkedHashMap>) locationResult.getData();
        }
        log.info("result data :" + map);
        return map;
    }
}
