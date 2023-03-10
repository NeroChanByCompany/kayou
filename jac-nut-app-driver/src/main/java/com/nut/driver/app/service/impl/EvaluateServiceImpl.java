package com.nut.driver.app.service.impl;

import com.github.pagehelper.Page;
import com.nut.driver.app.form.ReviewOrderForm;
import com.nut.common.constant.OperateCodeEnum;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.dao.*;
import com.nut.driver.app.dto.StationEvaluatesDto;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.entity.WorkOrderEntity;
import com.nut.driver.app.entity.WorkOrderEvaluateEntity;
import com.nut.driver.app.entity.WorkOrderOperateEntity;
import com.nut.driver.app.form.StationEvaluatesForm;
import com.nut.driver.app.pojo.ServiceStationInfoPojo;
import com.nut.driver.app.pojo.StationEvaluatesPojo;
import com.nut.driver.app.service.EvaluateService;
import com.nut.common.result.PagingInfo;
import com.nut.common.result.Result;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.RegexpUtils;
import com.nut.common.utils.StringUtil;

import com.nut.driver.app.service.PushMessageService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.app.driver.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-23 14:52
 * @Version: 1.0
 */
@Slf4j
@Service
public class EvaluateServiceImpl extends DriverBaseService implements EvaluateService {

    private static final String NO = "0";
    private static final String YES = "1";

    @Autowired
    private WorkOrderEvaluateDao workOrderEvaluateDao;

    @Autowired
    private WorkOrderDao workOrderDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private WorkOrderOperateDao workOrderOperateDao;

    @Autowired
    private ServiceStationDao serviceStationDao;

    @Autowired
    private PushMessageService pushMessageService;


    @Value("${database_name}")
    private String DatabaseName;

    public PagingInfo<StationEvaluatesDto> stationEvaluates(StationEvaluatesForm form) {
        PagingInfo<StationEvaluatesDto> resultPageInfo = new PagingInfo<>();
        // ????????????????????????????????????????????????????????????
        Page<StationEvaluatesPojo> pojoPageList = workOrderEvaluateDao.queryStationEvaluates(DatabaseName,
                Long.parseLong(form.getStationId()));

        if (pojoPageList != null && pojoPageList.getResult() != null) {
            // ????????????????????????
            List<StationEvaluatesPojo> pojosSelf = pojoPageList.getResult().stream()
                    .filter(e -> StringUtil.isEq(e.getDriverId(), form.getDriverId())).collect(Collectors.toList());
            List<StationEvaluatesPojo> pojosOther = pojoPageList.getResult().stream()
                    .filter(e -> StringUtil.isNotEq(e.getDriverId(), form.getDriverId())).collect(Collectors.toList());
            List<StationEvaluatesPojo> pojoList = new ArrayList<>();
            pojoList.addAll(pojosSelf);
            pojoList.addAll(pojosOther);

            pojoList = paging(pojoList, form.getPage_number(), form.getPage_size(), resultPageInfo);
            /* dto?????? */
            List<StationEvaluatesDto> dtoList = new ArrayList<>();
            for (StationEvaluatesPojo pojo : pojoList) {
                dtoList.add(pojoToDto(pojo));
            }
            resultPageInfo.setList(dtoList);
        }
        log.info("stationEvaluates end return:{}",resultPageInfo);
        return resultPageInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    public HttpCommandResultWithData reviewOrder(ReviewOrderForm form) {
        String errMsg = validateform(form);
        if (StringUtil.isNotEmpty(errMsg)) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), errMsg);
        }

        // ??????????????????
        WorkOrderEntity workOrder = workOrderDao.queryWorkOrderByWoCode(form.getWoCode());

        //????????????????????????????????????????????????????????????select count(1)  ??????0???????????????????????????????????????
        //????????????workOrder woStatus?????????????????? 250
        int num = workOrderDao.queryEvaluateNum(form.getWoCode());
        if (num > 0) {//true==>select count(1)
            //????????????????????????
            ExceptionUtil.result(2021, "???????????????????????????????????????");
        }

        Long userID = form.getAutoIncreaseId();


        UserEntity user = userDao.selectByPrimaryKey(userID);

        errMsg = validateState(user, workOrder);
        if (StringUtil.isNotEmpty(errMsg)) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), errMsg);
        }

        if (user == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "??????????????????");
        }

        // ????????????
        Date date = new Date();
        // ????????????????????????
        WorkOrderOperateEntity workOrderOperate = new WorkOrderOperateEntity();
        workOrderOperate.setWoCode(workOrder.getWoCode().trim());
        workOrderOperate.setOperateCode(OperateCodeEnum.OP_COMMENT.code());
        workOrderOperate.setTitle(OperateCodeEnum.OP_COMMENT.message());
        workOrderOperate.setUserId(userID.toString());
        workOrderOperate.setCreateTime(date);
        workOrderOperate.setUpdateTime(date);
        // ???????????????????????????json?????????????????????
        Map<String, Object> jsonMap = new LinkedHashMap<>(5);
        jsonMap.put("???????????????", Integer.parseInt(form.getWholeStar()));
        // 2.3.0 ??????????????????phone
        log.info("?????? ???????????????{} ??????", user == null ? "sysauto" : user.getPhone());
        jsonMap.put("???????????????", user == null ? "sysauto" : user.getPhone());
        jsonMap.put("???????????????", DateUtil.getDatePattern(date.getTime()));
        workOrderOperate.setTextJson(JsonUtil.toJson(jsonMap));
        workOrderOperateDao.insertSelective(workOrderOperate);
        // ?????????????????????
        WorkOrderEvaluateEntity workOrderEvaluate = new WorkOrderEvaluateEntity();
        workOrderEvaluate.setWoCode(form.getWoCode());
        workOrderEvaluate.setStationId(Long.parseLong(workOrder.getStationId()));
        workOrderEvaluate.setWholeStar(Integer.parseInt(form.getWholeStar()));
        workOrderEvaluate.setReviewLabel(form.getReviewLabel() == null ? "" : form.getReviewLabel());
        workOrderEvaluate.setOtherLabel(form.getOtherLabel() == null ? "" : form.getOtherLabel());
        if (YES.equals(form.getSelfPay())) {
            workOrderEvaluate.setCost(form.getCost());
        }
        if (StringUtil.isNotEmpty(form.getComeAgain())) {
            workOrderEvaluate.setComeAgain(Integer.parseInt(form.getComeAgain()));
        }
        workOrderEvaluate.setDiscontent(form.getDiscontent() == null ? "" : form.getDiscontent());
        workOrderEvaluate.setContent(form.getContent() == null ? "" : form.getContent());
        workOrderEvaluate.setUserId(userID);
        workOrderEvaluate.setCreateTime(date);
        workOrderEvaluateDao.insertSelective(workOrderEvaluate);
        // ?????????????????????
        WorkOrderEntity updateWorkOrder = new WorkOrderEntity();
        updateWorkOrder.setId(workOrder.getId());
        updateWorkOrder.setWoStatus(ServiceStationEnum.EVALUATED.code());
        updateWorkOrder.setUpdateTime(date);
        workOrderDao.updateByPrimaryKeySelective(updateWorkOrder);
        //??????
        try {
            // ???????????????
            Map<String, String> wildcardMap = new HashMap<>(2);
            wildcardMap.put("{?????????}", workOrder.getWoCode());
            String wildcard = JsonUtil.toJson(wildcardMap);
            // ????????????
            Map<String, String> messageExtraMap = new HashMap<>(1);
            // ?????????
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_CODE, workOrder.getWoCode());
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_STATUS, workOrder.getWoStatus().toString());
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_TYPE, workOrder.getWoType().toString());
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_APPO_TYPE, workOrder.getAppoType().toString());
            String messageExtra = JsonUtil.toJson(messageExtraMap);
            //???????????????????????????
            ServiceStationInfoPojo pojo = serviceStationDao.queryServiceStationInfo(DatabaseName,
                    workOrder.getStationId(), ServiceStationVal.JOB_TYPE_ADMIN);
            pushMessageService.pushToStation(PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_NINE, wildcard
                    , messageExtra, form.getUserId(), pojo.getUserId(), PushStaticLocarVal.PUSH_SHOW_TYPE_NOTICE);
        } catch (Exception e) {
            log.info("[reviewOrder] push Exception:{}", e.getMessage());
        }
        log.info("[reviewOrder]end");
        return Result.ok();
    }


    /**
     * pojo???dto
     */
    private static StationEvaluatesDto pojoToDto(StationEvaluatesPojo pojo) {
        StationEvaluatesDto dto = new StationEvaluatesDto();
        dto.setRatedId(pojo.getRatedId());
        dto.setDriverId(pojo.getDriverId());
        dto.setWoCode(pojo.getWoCode());
        dto.setUserName(pojo.getUserName());
        dto.setCarType(pojo.getCarType());
        dto.setRate(pojo.getRate());
        dto.setDate(DateUtil.format(DateUtil.getDate_pattern_dot, pojo.getDate()));
        dto.setContent(pojo.getContent());
        return dto;
    }

    /**
     * ????????????
     */
    private String validateform(ReviewOrderForm form) {
        if (YES.equals(form.getSelfPay()) && StringUtil.isEmpty(form.getCost())) {
            return "?????????????????????";
        }
        if (StringUtil.isNotEmpty(form.getCost())) {
            if (!RegexpUtils.validateInfo(form.getCost(), RegexpUtils.NON_NEGATIVE_DECIMAL_LESS_THAN_100K)) {
                return "???????????????????????????";
            }
        }
        return "";
    }

    private String validateState(UserEntity user, WorkOrderEntity workOrder) {
        if (workOrder == null) {
            return "???????????????????????????";
        }
        // ????????????????????????????????????
        if (workOrder.getWoStatus() != ServiceStationEnum.WORK_DONE.code()) {
            return "??????????????????????????????";
        }
        return "";
    }

}
