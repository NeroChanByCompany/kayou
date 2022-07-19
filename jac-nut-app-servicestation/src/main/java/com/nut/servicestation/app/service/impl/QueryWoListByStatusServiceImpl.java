package com.nut.servicestation.app.service.impl;

import com.github.pagehelper.Page;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.dao.WorkOrderDao;
import com.nut.servicestation.app.dto.QueryWoListByStatusDto;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.form.QueryWoListByStatusForm;
import com.nut.servicestation.app.form.queryOrderListForm;
import com.nut.servicestation.app.pojo.QueryWoListByStatusPojo;
import com.nut.servicestation.app.service.QueryWoListByStatusService;
import com.nut.servicestation.app.service.UserService;
import com.nut.servicestation.common.utils.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@Service("QueryWoListByStatusService")
public class QueryWoListByStatusServiceImpl implements QueryWoListByStatusService {

    @Autowired
    private UserService queryUserInfoService;
    @Autowired
    private WorkOrderDao workOrderMapper;
    @Autowired
    PageUtil pageUtil;



    @Override
    public HttpCommandResultWithData queryWoListByStatus(QueryWoListByStatusForm command) {
        log.info("****************queryWoListByStatus Start*****************");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        PagingInfo<QueryWoListByStatusDto> resultPageInfo = new PagingInfo<>();
        UserInfoDto userInfoDto = queryUserInfoService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此用户不存在！");
            return result;
        }
        /* 角色权限：普通维修工账号，显示账号下所有待出发工单,管理员可账号，显示服务站所有工单状态下的工单 */
        int woStatus = Integer.parseInt(command.getWoStatus());
        List<Integer> woStatusList = new ArrayList<>();
        woStatusList.add(woStatus);
        int one = 0;
        if (woStatus == 250){
            woStatusList.add(240);
            one = 1;
        }
        switch (woStatus) {
            case ServiceStationVal.TO_BE_ACCEPTED:
                // 修改申请审核中-待接受
                woStatusList.add(ServiceStationEnum.MODIFY_APPLYING_ACCEPT.code());
                // 拒单申请审核中
                woStatusList.add(ServiceStationEnum.REFUSE_APPLYING.code());
                break;
            case ServiceStationVal.TO_TAKE_OFF:
                // 修改申请审核中-待出发
                woStatusList.add(ServiceStationEnum.MODIFY_APPLYING_TAKEOFF.code());
                woStatusList.add(ServiceStationEnum.CLOSE_APPLYING_TAKEOFF.code());
                break;
            case ServiceStationVal.TO_RECEIVE:
                // 修改申请审核中-待接车
                woStatusList.add(ServiceStationEnum.MODIFY_APPLYING_RECEIVE.code());
                woStatusList.add(ServiceStationEnum.CLOSE_APPLYING_RECEIVE.code());
                break;
            case ServiceStationVal.INSPECTING:
                // 关闭申请审核中-检查
                woStatusList.add(ServiceStationEnum.CLOSE_APPLYING_INSPECT.code());
                break;
            case ServiceStationVal.REPAIRING:
                // 关闭申请审核中-维修
                woStatusList.add(ServiceStationEnum.CLOSE_APPLYING_REPAIR.code());
                break;
            case ServiceStationVal.WORK_DONE:
                // 工单关闭-拒单
                woStatusList.add(ServiceStationEnum.CLOSE_REFUSED.code());
                // 工单关闭-关闭申请-检查
                woStatusList.add(ServiceStationEnum.CLOSE_INSPECT.code());
                // 工单关闭-关闭申请-维修
                woStatusList.add(ServiceStationEnum.CLOSE_REPAIR.code());
                // 工单关闭-取消救援
                woStatusList.add(ServiceStationEnum.CLOSE_RESCUE.code());
                // 已评价
                woStatusList.add(ServiceStationEnum.EVALUATED.code());
                // 工单关闭-关闭申请-待出发
                woStatusList.add(ServiceStationEnum.CLOSE_TAKEOFF.code());
                // 工单关闭-关闭申请-待接车
                woStatusList.add(ServiceStationEnum.CLOSE_RECEIVE.code());
                // 已报单
                woStatusList.add(ServiceStationEnum.HAS_BEEN_REPORTED.code());
                break;
            default:
                break;
        }
        Map<String, Object> param = new HashMap<>(10);
        param.put("woStatus", woStatus);
        param.put("woStatusList", woStatusList);
        param.put("keyValue", command.getKeyValue());
        // 判断角色
        param.put("role", userInfoDto.getRoleCode() == ServiceStationVal.JOB_TYPE_SALESMAN ? userInfoDto.getRoleCode() : "");
        param.put("stationId", userInfoDto.getServiceStationId());
        // 选择查看工单
        param.put("woType", "0".equals(command.getWoType()) ? "" : command.getWoType());
        param.put("userId", command.getUserId());
        param.put("acceptedWoStatus", ServiceStationEnum.TO_BE_ACCEPTED.code());
        param.put("timeCloseStart", StringUtil.isNotEmpty(command.getTimeCloseStart()) ? (command.getTimeCloseStart() + " 00:00:00") : "");
        param.put("timeCloseEnd", StringUtil.isNotEmpty(command.getTimeCloseEnd()) ? (command.getTimeCloseEnd() + " 23:59:59") : "");
        if(one == 1){
            //查询服务类型为保外（保外保养3和保外维修7）的工单号
            List<String> woCode = workOrderMapper.queryWocodeByServiceType();
            param.put("woCodeList",(woCode.isEmpty() || woCode.size() == 0) ? "" : woCode);
        }else{
            param.put("woCodeList","");
        }
        // 工单状态为240已完成时，是服务APP查询历史工单，不在SQL中分页，手动分页。
        if (woStatus != ServiceStationVal.WORK_DONE) {
            ServiceStationBaseService.getPage(command, true);
        }
        Page<QueryWoListByStatusPojo> pojoPageList = workOrderMapper.queryWoListByStatus(param);
        /* dto转换 */
        List<QueryWoListByStatusDto> dtoList = new ArrayList<>();
        if (pojoPageList != null && pojoPageList.getResult() != null) {
            List<QueryWoListByStatusPojo> pojoList = pojoPageList.getResult();
            for (QueryWoListByStatusPojo oneWorkOrder : pojoList) {
                oneWorkOrder.setOutOfInsurance(0);
                //根据工单号查询 关联的维修项目的服务类型
                List<Integer> serviceTypeList = workOrderMapper.getRelationServiceTypeList(oneWorkOrder.getWoCode());
                if (serviceTypeList.contains(3) || serviceTypeList.contains(7)) {
                    oneWorkOrder.setOutOfInsurance(1);
                }
            }
            // 工单状态为240已完成时，是服务APP查询历史工单，不在SQL中分页，手动分页。
            if (woStatus == ServiceStationVal.WORK_DONE) {
                if (CollectionUtils.isNotEmpty(pojoList)) {
                    // 重新排序
                    pojoList = sortAgain(pojoList);
                    // 手动分页
                    pojoList = ServiceStationBaseService.paging(pojoList, command.getPage_number(), command.getPage_size(), resultPageInfo);
                } else {
                    resultPageInfo.setPage_total(0);
                    resultPageInfo.setTotal(0);
                }
            }
            for (QueryWoListByStatusPojo pojo : pojoList) {
                dtoList.add(pojoToDto(pojo, woStatus));
            }
            if (woStatus != ServiceStationVal.WORK_DONE) {
                resultPageInfo.setPage_total(pojoPageList.getPages());
                resultPageInfo.setTotal(pojoPageList.getTotal());
            }
            resultPageInfo.setList(dtoList);
        }
        result.setData(resultPageInfo);
        log.info("****************queryWoListByStatus End*****************");
        return result;
    }

    @Override
    public PagingInfo<QueryWoListByStatusPojo> queryOrderList(queryOrderListForm command) {
        log.info("****************queryOrderListByStatus Start*****************");
        PagingInfo<QueryWoListByStatusPojo> resultPageInfo = new PagingInfo<>();
        UserInfoDto userInfoDto = queryUserInfoService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(),"此用户不存在！");
        }
        pageUtil.getPage(command);
        //查询当前用户下所有抢单信息
        Page<QueryWoListByStatusPojo> pojoPageList =  workOrderMapper.queryOrderList(userInfoDto.getServiceStationId());
        log.info("****************queryOrderListByStatus end param:{}*****************",pojoPageList);
        return pageUtil.convertPagingToPage(pojoPageList);
    }

    /**
     * POJO 转 DTO
     * <h2>【注意！】增加新的工单状态枚举时需要密切关注的代码！搜tag: ATTENTION_add_new_woStatus_DOUBLE_CHECK</h2>
     *
     * @param pojo     工单信息
     * @param woStatus 工单状态
     */
    public QueryWoListByStatusDto pojoToDto(QueryWoListByStatusPojo pojo, int woStatus) {
        QueryWoListByStatusDto dto = new QueryWoListByStatusDto();
        // 底盘号
        dto.setChassisNum(StringUtil.isEmpty(pojo.getChassisNum()) ? "" : pojo.getChassisNum());
        // 车牌号
        dto.setCarNumber(StringUtil.isEmpty(pojo.getCarNumber()) ? "" : pojo.getCarNumber());
        // 车辆位置
        dto.setCarLocation(pojo.getCarLocation());
        // 车辆纬度
        dto.setCarLat(pojo.getCarLat());
        // 车辆经度
        dto.setCarLon(pojo.getCarLon());
        // 工单号
        dto.setWoCode(pojo.getWoCode());
        // 工单状态
        dto.setWoStatus(pojo.getWoStatus().toString());
        // 工单状态内容
        if (pojo.getWoStatus() == ServiceStationEnum.WORK_DONE.code()) {
            dto.setWoStatusValue(ServiceStationEnum.WORK_DONE.message());
        } else if (pojo.getWoStatus() == ServiceStationEnum.CLOSE_REFUSED.code()
                || pojo.getWoStatus() == ServiceStationEnum.CLOSE_INSPECT.code()
                || pojo.getWoStatus() == ServiceStationEnum.CLOSE_REPAIR.code()
                || pojo.getWoStatus() == ServiceStationEnum.CLOSE_RESCUE.code()
                || pojo.getWoStatus() == ServiceStationEnum.CLOSE_TAKEOFF.code()
                || pojo.getWoStatus() == ServiceStationEnum.CLOSE_RECEIVE.code()) {
            dto.setWoStatusValue("工单关闭");
        } else if (pojo.getWoStatus() == ServiceStationEnum.EVALUATED.code()) {
            dto.setWoStatusValue("已完成");
        } else {
            dto.setWoStatusValue(pojo.getWoStatusValue());
        }
        // 工单类型
        dto.setWoType(pojo.getWoType());
        if (dto.getWoType() == ServiceStationVal.STATION_SERVICE) {
            // 进站工单 外出类型0
            dto.setRescueType(0);
        } else {
            // 外出类型
            dto.setRescueType(pojo.getRescueType());
        }
        // 预约方式 1： 400客服，2：司机App
        dto.setAppointmentType(pojo.getAppointmentType());
        // 预约项目	1：维修项目，2：保养项目，3：维修和保养项目
        int appointmentItem = 1;
        if (StringUtil.isNotEmpty(pojo.getMaintainItem()) && StringUtil.isNotEmpty(pojo.getRepairItem())) {
            appointmentItem = 3;
        } else if (StringUtil.isNotEmpty(pojo.getMaintainItem())) {
            appointmentItem = 2;
        }
        dto.setAppointmentItem(appointmentItem);
        // 工单时间
        dto.setWorkTime(setWorkTime(pojo, woStatus));
        // 协议标识
        dto.setProtocolMark(pojo.getProtocolMark());
        // 指派人员
        dto.setAssignTo(pojo.getAssignTo());
        //关联的维修项目是否有保外服务类型 1是 0否
        dto.setOutOfInsurance(pojo.getOutOfInsurance());
        return dto;
    }
    /**
     * @param pojo 工单信息
     * @return java.lang.String
     * @Description: 工单时间
     * @method: setWoCodeTime
     */
    public String setWorkTime(QueryWoListByStatusPojo pojo, int woStatus) {
        int woType = pojo.getWoType();
        String workTime = "";
        try {
            /* 待接受工单：建单时间 */
            if (ServiceStationEnum.TO_BE_ACCEPTED.code() == woStatus) {
                workTime = DateUtil.format(DateUtil.time_pattern, pojo.getTimeCreate());
            } else if (ServiceStationVal.OUTSIDE_RESCUE == woType && ServiceStationEnum.TO_RECEIVE.code() == woStatus) {
                // 外出救援-待接车 : 出发时间
                workTime = DateUtil.format(DateUtil.time_pattern, pojo.getTimeDepart());
            } else if (ServiceStationEnum.INSPECTING.code() == woStatus) {
                // 待检查工单：接车时间
                workTime = DateUtil.format(DateUtil.time_pattern, pojo.getTimeReceive());
            } else if (ServiceStationEnum.REPAIRING.code() == woStatus) {
                // 待维修工单：检查结束时间
                workTime = DateUtil.format(DateUtil.time_pattern, pojo.getTimeInspected());
            } else if (ServiceStationEnum.WORK_DONE.code() == woStatus) {
                // 完成的工单：工单结束时间（维修完成或客服同意关闭）
                workTime = DateUtil.format(DateUtil.time_pattern, pojo.getTimeClose());
            } else if ((ServiceStationVal.STATION_SERVICE == woType && ServiceStationEnum.TO_RECEIVE.code() == woStatus) || ServiceStationEnum.TO_TAKE_OFF.code() == woStatus) {
                // 进出站-待接车工单/外出救援-待出发：接单时间
                workTime = DateUtil.format(DateUtil.time_pattern, pojo.getTimeAccept());
            } else {
                //其他
                workTime = DateUtil.format(DateUtil.time_pattern, pojo.getTimeClose());
            }
        } catch (Exception e) {
            log.error("[QueryWoListByStatusService][setWorkTime]Exception:", e);
        }
        return workTime;
    }
    /**
     * 对数据进行重新排序（优先显示未评价的工单，已评价工单后置显示。）
     * @param list 历史工单数组
     * @return List<QueryWoListByStatusPojo>
     */
    private static List<QueryWoListByStatusPojo> sortAgain(List<QueryWoListByStatusPojo> list) {
        List<QueryWoListByStatusPojo> resultList = new ArrayList<>();
        List<QueryWoListByStatusPojo> closeList = new ArrayList<>();
        List<QueryWoListByStatusPojo> evaluateList = new ArrayList<>();
        List<QueryWoListByStatusPojo> billNotSubList = new ArrayList<>();
        // 列表优先显示未评价的工单，优先顺序：结算单未提交——>待评价——>各类关闭状态——>已评价工单。
        for (QueryWoListByStatusPojo pojo : list) {
            // 优先添加未提交结算单
            if (pojo.getProtocolMark() == 2 && (pojo.getBillSubStatus() == 1 || pojo.getBillSubStatus() == 3)) {
                billNotSubList.add(pojo);
            } else {
                if (pojo.getWoStatus().equals(ServiceStationVal.EVALUATED)) {
                    evaluateList.add(pojo);
                } else if (pojo.getWoStatus().equals(ServiceStationVal.WORK_DONE)) {
                    resultList.add(pojo);
                } else {
                    closeList.add(pojo);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(closeList)) {
            resultList.addAll(closeList);
        }
        if (CollectionUtils.isNotEmpty(evaluateList)) {
            resultList.addAll(evaluateList);
        }

        billNotSubList.addAll(resultList);
        return billNotSubList;
    }
}
