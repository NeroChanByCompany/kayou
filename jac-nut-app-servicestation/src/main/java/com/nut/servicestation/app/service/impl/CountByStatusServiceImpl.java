package com.nut.servicestation.app.service.impl;

import com.nut.common.constant.ServiceStationVal;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.dao.CarStationStayOvertimeDao;
import com.nut.servicestation.app.dao.WoInfoDao;
import com.nut.servicestation.app.dao.WorkOrderDao;
import com.nut.servicestation.app.dto.CountByStatusDto;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.form.CountByStatusForm;
import com.nut.servicestation.app.pojo.CountWoPojo;
import com.nut.servicestation.app.service.CountByStatusService;
import com.nut.servicestation.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 *  @author wuhaotian 2021/7/6
 */
@Slf4j
@Service("CountByStatusService")
public class CountByStatusServiceImpl implements CountByStatusService {


    @Autowired
    private UserService userService;

    @Autowired
    private WoInfoDao woInfoMapper;

    @Autowired
    private WorkOrderDao workOrderMapper;

    @Autowired
    private CarStationStayOvertimeDao carStationStayOvertimeMapper;

    @Override
    public HttpCommandResultWithData countByStatus(CountByStatusForm command) {
        log.info("[countByStatus]start");
        HttpCommandResultWithData<CountByStatusDto> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        CountByStatusDto resultDto = new CountByStatusDto();
        // 查询用户信息
        UserInfoDto userInfo = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfo == null) {
            log.info("[countByStatus]userInfo is null");
            result.setData(resultDto);
            return result;
        }
        log.info("[countByStatus]role:{}", userInfo.getRoleCode());
        List<Integer> statusList = new ArrayList<>();
        if (userInfo.getRoleCode() == ServiceStationVal.JOB_TYPE_ADMIN) {
            // 管理员账号，初步查询 【待接受、待接车、待检查】 工单
            statusList.add(ServiceStationEnum.TO_BE_ACCEPTED.code());
            statusList.add(ServiceStationEnum.TO_RECEIVE.code());
            statusList.add(ServiceStationEnum.INSPECTING.code());
            statusList.add(ServiceStationEnum.WORK_DONE.code());
            statusList.add(ServiceStationEnum.EVALUATED.code());
            /**
             * 添加功能：管理员合并业务员功能，业务员功能不动
             */
            statusList.add(ServiceStationEnum.TO_TAKE_OFF.code());
            statusList.add(ServiceStationEnum.TO_RECEIVE.code());
            statusList.add(ServiceStationEnum.INSPECTING.code());
            statusList.add(ServiceStationEnum.WORK_DONE.code());
            statusList.add(ServiceStationEnum.REPAIRING.code());
        } else {
            // 普通业务员账号，初步查询 【待出发、待接车、待检查、待维修】 工单
            statusList.add(ServiceStationEnum.TO_TAKE_OFF.code());
            statusList.add(ServiceStationEnum.TO_RECEIVE.code());
            statusList.add(ServiceStationEnum.INSPECTING.code());
            statusList.add(ServiceStationEnum.REPAIRING.code());
        }

        // 查询数据库
        Map<String, Object> param = new HashMap<>(2);
        param.put("stationId", userInfo.getServiceStationId());
        param.put("list", statusList);
        //查询服务类型为保外（保外保养3和保外维修7）的工单号
        List<String> woCode = workOrderMapper.queryWocodeByServiceType();
        param.put("woCodeList",(woCode.isEmpty() || woCode.size() == 0) ? "" : woCode);
        List<CountWoPojo> woList = woInfoMapper.queryWoByStationAndStatusIn(param);
        log.info("[countByStatus]woList.size:{}", woList.size());

        // 过滤结果并统计
        if (userInfo.getRoleCode() == ServiceStationVal.JOB_TYPE_ADMIN) {
            // 管理员账号
            // 待接受工单数
            long count = woList.stream().filter(e -> compare(e.getWoStatus(), ServiceStationEnum.TO_BE_ACCEPTED.code()) == 0).count();
            resultDto.setAcceptCnt(count);
            // 进出站 待接车工单数
            count = woList.stream().filter(e -> compare(e.getWoType(), ServiceStationVal.STATION_SERVICE) == 0
                    && compare(e.getWoStatus(), ServiceStationEnum.TO_RECEIVE.code()) == 0).count();
            resultDto.setStationReceiveCnt(count);
            // 进站待检查
            count = woList.stream().filter(e -> compare(e.getWoType(), ServiceStationVal.STATION_SERVICE) == 0
                    && compare(e.getWoStatus(), ServiceStationEnum.INSPECTING.code()) == 0).count();
            resultDto.setStationInspectCnt(count);
            // 进出站 属于当前账号待维修工单数
            count = woList.stream().filter(e -> compare(e.getWoType(), ServiceStationVal.STATION_SERVICE) == 0
                    && compare(e.getWoStatus(), ServiceStationEnum.REPAIRING.code()) == 0
                    && StringUtil.isEq(e.getAssignTo(), command.getUserId())).count();
            resultDto.setStationRepairCnt(count);

            List<CountWoPojo> userWoList = woList.stream().filter(e -> compare(e.getWoType(), ServiceStationVal.OUTSIDE_RESCUE) == 0
                    && StringUtil.isEq(e.getAssignTo(), command.getUserId())).collect(Collectors.toList());
            // 外出救援 待出发工单数
            count = userWoList.stream().filter(e -> compare(e.getWoStatus(), ServiceStationEnum.TO_TAKE_OFF.code()) == 0).count();
            resultDto.setRescueLeaveCnt(count);
            // 外出救援 待接车工单数
            count = userWoList.stream().filter(e -> compare(e.getWoStatus(), ServiceStationEnum.TO_RECEIVE.code()) == 0).count();
            resultDto.setRescueReceiveCnt(count);
            // 外出救援 待检查工单数
            count = userWoList.stream().filter(e -> compare(e.getWoStatus(), ServiceStationEnum.INSPECTING.code()) == 0).count();
            resultDto.setRescueInspectCnt(count);
            // 外出救援 待维修工单数
            count = userWoList.stream().filter(e -> compare(e.getWoStatus(), ServiceStationEnum.REPAIRING.code()) == 0).count();
            resultDto.setRescueRepairCnt(count);
            // 出站已评价
            count = woList.stream().filter(e -> compare(e.getWoType(), ServiceStationVal.OUTSIDE_RESCUE) == 0
                    && compare(e.getWoStatus(), ServiceStationEnum.EVALUATED.code()) == 0).count()
                    + woList.stream().filter(e -> compare(e.getWoType(), ServiceStationVal.OUTSIDE_RESCUE) == 0
                    && compare(e.getWoStatus(), ServiceStationEnum.WORK_DONE.code()) == 0).count();
            resultDto.setOutStationEvaluateCnt(count);
            // 进站已评价
            count = woList.stream().filter(e -> compare(e.getWoType(), ServiceStationVal.STATION_SERVICE) == 0
                    && compare(e.getWoStatus(), ServiceStationEnum.EVALUATED.code()) == 0).count()
                    + woList.stream().filter(e -> compare(e.getWoType(), ServiceStationVal.STATION_SERVICE) == 0
                    && compare(e.getWoStatus(), ServiceStationEnum.WORK_DONE.code()) == 0).count();
            resultDto.setInStationEvaluateCnt(count);
        } else {
            // 普通业务员账号
            // 进出站 待维修工单数
            long count = woList.stream().filter(e -> compare(e.getWoType(), ServiceStationVal.STATION_SERVICE) == 0
                    && compare(e.getWoStatus(), ServiceStationEnum.REPAIRING.code()) == 0
                    && StringUtil.isEq(e.getAssignTo(), command.getUserId())).count();
            resultDto.setStationRepairCnt(count);

            count = woList.stream().filter(e -> compare(e.getWoType(), ServiceStationVal.STATION_SERVICE) == 0
                    && compare(e.getWoStatus(), ServiceStationEnum.INSPECTING.code()) == 0
                    && StringUtil.isEq(e.getAssignTo(), command.getUserId())).count();
            resultDto.setStationInspectCnt(count);

            List<CountWoPojo> userWoList = woList.stream().filter(e -> compare(e.getWoType(), ServiceStationVal.OUTSIDE_RESCUE) == 0
                    && StringUtil.isEq(e.getAssignTo(), command.getUserId())).collect(Collectors.toList());

            // 外出救援 待出发工单数
            count = userWoList.stream().filter(e -> compare(e.getWoStatus(), ServiceStationEnum.TO_TAKE_OFF.code()) == 0).count();
            resultDto.setRescueLeaveCnt(count);
            // 外出救援 待接车工单数
            count = userWoList.stream().filter(e -> compare(e.getWoStatus(), ServiceStationEnum.TO_RECEIVE.code()) == 0).count();
            resultDto.setRescueReceiveCnt(count);
            // 外出救援 待检查工单数
            count = userWoList.stream().filter(e -> compare(e.getWoStatus(), ServiceStationEnum.INSPECTING.code()) == 0).count();
            resultDto.setRescueInspectCnt(count);
            // 外出救援 待维修工单数
            count = userWoList.stream().filter(e -> compare(e.getWoStatus(), ServiceStationEnum.REPAIRING.code()) == 0).count();
            resultDto.setRescueRepairCnt(count);
            // 出站已评价
            count = woList.stream().filter(e -> compare(e.getWoStatus(), ServiceStationEnum.EVALUATED.code()) == 0).count();
            resultDto.setOutStationEvaluateCnt(count);
            // 进站已评价
            count = woList.stream().filter(e -> compare(e.getWoStatus(), ServiceStationEnum.EVALUATED.code()) == 0).count();
            resultDto.setInStationEvaluateCnt(count);
        }

        // 进站预警数量
        Long stationStayWarnCnt = carStationStayOvertimeMapper.queryInStationWarningCount(userInfo.getServiceCode());
        //抢单数量
        Long orderStationCount = woInfoMapper.queryWorkOrderStationCount(userInfo.getServiceStationId());
        resultDto.setStationStayWarnCnt(stationStayWarnCnt);
        resultDto.setGrabAnOrder(orderStationCount);
        result.setData(resultDto);
        log.info("[countByStatus]end");
        return result;
    }

    /**
     * 回避null比较两个整型数的大小
     */
    private int compare(Integer a, Integer b) {
        if (a == null && b == null) {
            return 0;
        } else if (a == null) {
            return Integer.valueOf(0).compareTo(b);
        } else if (b == null) {
            return a.compareTo(0);
        } else {
            return a.compareTo(b);
        }
    }
}
