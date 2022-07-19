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
        // 个人评价置顶展示，查询全部数据，手动分页
        Page<StationEvaluatesPojo> pojoPageList = workOrderEvaluateDao.queryStationEvaluates(DatabaseName,
                Long.parseLong(form.getStationId()));

        if (pojoPageList != null && pojoPageList.getResult() != null) {
            // 个人评价置顶展示
            List<StationEvaluatesPojo> pojosSelf = pojoPageList.getResult().stream()
                    .filter(e -> StringUtil.isEq(e.getDriverId(), form.getDriverId())).collect(Collectors.toList());
            List<StationEvaluatesPojo> pojosOther = pojoPageList.getResult().stream()
                    .filter(e -> StringUtil.isNotEq(e.getDriverId(), form.getDriverId())).collect(Collectors.toList());
            List<StationEvaluatesPojo> pojoList = new ArrayList<>();
            pojoList.addAll(pojosSelf);
            pojoList.addAll(pojosOther);

            pojoList = paging(pojoList, form.getPage_number(), form.getPage_size(), resultPageInfo);
            /* dto转换 */
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

        // 查询工单信息
        WorkOrderEntity workOrder = workOrderDao.queryWorkOrderByWoCode(form.getWoCode());

        //查询工单评价记录（查询条件为工单号，如果select count(1)  大于0则表示已经存在该评价记录）
        //同时查看workOrder woStatus是不是已评价 250
        int num = workOrderDao.queryEvaluateNum(form.getWoCode());
        if (num > 0) {//true==>select count(1)
            //表示该工单已评价
            ExceptionUtil.result(2021, "该工单已评价，无需再次评价");
        }

        Long userID = form.getAutoIncreaseId();


        UserEntity user = userDao.selectByPrimaryKey(userID);

        errMsg = validateState(user, workOrder);
        if (StringUtil.isNotEmpty(errMsg)) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), errMsg);
        }

        if (user == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "此用户不存在");
        }

        // 系统时间
        Date date = new Date();
        // 添加工单操作记录
        WorkOrderOperateEntity workOrderOperate = new WorkOrderOperateEntity();
        workOrderOperate.setWoCode(workOrder.getWoCode().trim());
        workOrderOperate.setOperateCode(OperateCodeEnum.OP_COMMENT.code());
        workOrderOperate.setTitle(OperateCodeEnum.OP_COMMENT.message());
        workOrderOperate.setUserId(userID.toString());
        workOrderOperate.setCreateTime(date);
        workOrderOperate.setUpdateTime(date);
        // 服务记录显示内容用json字符串格式保存
        Map<String, Object> jsonMap = new LinkedHashMap<>(5);
        jsonMap.put("评价星级：", Integer.parseInt(form.getWholeStar()));
        // 2.3.0 评价账号改存phone
        log.info("【【 评价账号：{} 】】", user == null ? "sysauto" : user.getPhone());
        jsonMap.put("评价账号：", user == null ? "sysauto" : user.getPhone());
        jsonMap.put("评价时间：", DateUtil.getDatePattern(date.getTime()));
        workOrderOperate.setTextJson(JsonUtil.toJson(jsonMap));
        workOrderOperateDao.insertSelective(workOrderOperate);
        // 插入工单评价表
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
        // 更新工单表信息
        WorkOrderEntity updateWorkOrder = new WorkOrderEntity();
        updateWorkOrder.setId(workOrder.getId());
        updateWorkOrder.setWoStatus(ServiceStationEnum.EVALUATED.code());
        updateWorkOrder.setUpdateTime(date);
        workOrderDao.updateByPrimaryKeySelective(updateWorkOrder);
        //推送
        try {
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(2);
            wildcardMap.put("{工单号}", workOrder.getWoCode());
            String wildcard = JsonUtil.toJson(wildcardMap);
            // 扩展信息
            Map<String, String> messageExtraMap = new HashMap<>(1);
            // 工单号
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_CODE, workOrder.getWoCode());
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_STATUS, workOrder.getWoStatus().toString());
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_TYPE, workOrder.getWoType().toString());
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_APPO_TYPE, workOrder.getAppoType().toString());
            String messageExtra = JsonUtil.toJson(messageExtraMap);
            //获取通知接收人信息
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
     * pojo转dto
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
     * 校验入参
     */
    private String validateform(ReviewOrderForm form) {
        if (YES.equals(form.getSelfPay()) && StringUtil.isEmpty(form.getCost())) {
            return "需输入自费金额";
        }
        if (StringUtil.isNotEmpty(form.getCost())) {
            if (!RegexpUtils.validateInfo(form.getCost(), RegexpUtils.NON_NEGATIVE_DECIMAL_LESS_THAN_100K)) {
                return "自费金额格式不正确";
            }
        }
        return "";
    }

    private String validateState(UserEntity user, WorkOrderEntity workOrder) {
        if (workOrder == null) {
            return "未获取到该工单信息";
        }
        // 校验工单状态是否为已完成
        if (workOrder.getWoStatus() != ServiceStationEnum.WORK_DONE.code()) {
            return "当前工单状态无法评价";
        }
        return "";
    }

}
