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
            //????????????????????????
            for (CustomMaintainInfo customMaintainInfo : customMaintainInfos) {
                //?????????????????? ?????????????????????
                if (customMaintainInfo.getCustomMaintainType().equals(2) && (customMaintainInfo.getRemind() == null || customMaintainInfo.getRemind().equals(2))) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = sdf.parse(customMaintainInfo.getCustomMaintainDescribe());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //?????????????????????????????????????????????????????? ????????????????????????????????????
                    if (DateUtils.sameDate(date, new Date())) {
                        //???????????????
                        List<String> items = new ArrayList<>();
                        if (customMaintainInfo.getId() != null) {
                            items = customMaintainMapper.selectByMaintainId(customMaintainInfo.getId());
                        }
                        int i = 1;
                        for (String item : items) {
                            sb.append(i + "???").append(item).append(";");
                            i++;
                        }
                        maintainSendEntities.add(new MaintainSendEntity()
                                .setUserId(customMaintainInfo.getUserId())
                                .setMaintainInfoId(customMaintainInfo.getId())
                                .setTitle("????????????")
                                .setType("2")
                                .setContent("???????????????????????????" + customMaintainInfo.getVin().substring(customMaintainInfo.getVin().length() - 8) + "???" + "???"
                                        + customMaintainInfo.getCustomMaintainDescribe().substring(0, customMaintainInfo.getCustomMaintainDescribe().indexOf(" "))
                                        + "??????????????????????????????:" + sb.substring(0, sb.length() - 1) + "???")
                        );
                    }
                }
                //????????????????????? ????????????????????? ????????????????????????????????????
                if (customMaintainInfo.getCustomMaintainType().equals(1) && (customMaintainInfo.getRemind() == null || customMaintainInfo.getRemind().equals(2))) {
                    //?????????????????????
                    String mileage = getMellage(customMaintainInfo.getCarId());
                    //??????????????????
                    if (Math.ceil(Double.parseDouble(mileage)) == Math.ceil(Double.parseDouble(customMaintainInfo.getCustomMaintainDescribe()))) {
                        //???????????????
                        List<String> items = new ArrayList<>();
                        if (customMaintainInfo.getId() != null) {
                            items = customMaintainMapper.selectByMaintainId(customMaintainInfo.getId());
                        }
                        int i = 1;
                        for (String item : items) {
                            sb.append(i + "???").append(item).append(";");
                            i++;
                        }
                        maintainSendEntities.add(new MaintainSendEntity()
                                .setUserId(customMaintainInfo.getUserId())
                                .setMaintainInfoId(customMaintainInfo.getId())
                                .setTitle("????????????")
                                .setType("1")
                                .setContent("???????????????????????????" + customMaintainInfo.getVin().substring(customMaintainInfo.getVin().length() - 8) + "???" + "?????????"
                                        + mileage + "km?????????????????????????????????:"
                                        + sb.substring(0, sb.length() - 1) + "???")
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
        //??????????????????????????????????????????
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
     * ???????????????????????????
     *
     * @param carId
     * @return
     */
    public String getMellage(String carId) {

        String mileage = "0.00";
        // ?????????????????????
        String autoTerminal = carMapper.queryAutoTerminalByCarId(carId);
        if (StringUtil.isEmpty(autoTerminal)) {
            log.info("autoTerminal ?????????");
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
        log.info("???????????????????????????" + mileage);
        return mileage;
    }

    /**
     * ????????????????????????
     * @param autoTerminal
     * @return
     */
    public Map<Long, LinkedHashMap> queryLastLocation(List<Long> autoTerminal) {

        Map<Long, LinkedHashMap> map = new HashMap<>();
        // ????????????????????????
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
