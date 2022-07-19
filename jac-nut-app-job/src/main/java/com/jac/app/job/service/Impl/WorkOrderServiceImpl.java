package com.jac.app.job.service.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jac.app.job.domain.Car;
import com.jac.app.job.dto.OrderReportDetailDto;
import com.jac.app.job.dto.OrderReportDto;
import com.jac.app.job.dto.OrderReportPhotoDto;
import com.jac.app.job.dto.WoCalculationPhotosDto;
import com.jac.app.job.entity.WorkOrderEntity;
import com.jac.app.job.entity.WorkOrderLogEntity;
import com.jac.app.job.enums.ImageEnum;
import com.jac.app.job.mapper.CarMapper;
import com.jac.app.job.mapper.WorkOrderLogMapper;
import com.jac.app.job.mapper.WorkOrderMapper;
import com.jac.app.job.mapper.WorkOrderOperatePhotoMapper;
import com.jac.app.job.service.AsyncService;
import com.jac.app.job.service.WorkOrderLogService;
import com.jac.app.job.service.WorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuBing
 * @Classname WorkOrderLogServiceImpl
 * @Description TODO
 * @Date 2021/8/16 9:58
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrderEntity> implements WorkOrderService {

    @Autowired
    AsyncService asyncService;

    @Autowired
    CarMapper carMapper;
    @Autowired
    WorkOrderOperatePhotoMapper workOrderOperatePhotoMapper;
    private Map<String, String> accessoryNameMap = new HashMap<>();
    @Override
    public String pushWorkOrder(WorkOrderEntity form) {

        //获取车牌号车牌号
        String carLicense = null;
        if (null != form.getChassisNum()) {
            Car car = carMapper.selectCarByCarVin(form.getChassisNum());
            if (car != null) {
                carLicense = car.getCarNumber();
            }
        }
        OrderReportDetailDto orderReportDetailDto = new OrderReportDetailDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //orderReportDetailDto.setVin("L1903480");//测试专用
        orderReportDetailDto.setVin(form.getChassisNum().substring(form.getChassisNum().length() - 8));//车辆底盘号
        //orderReportDetailDto.setDealerMame("江淮轻卡");//预约服务站测试专用
        orderReportDetailDto.setDealerMame(form.getStationName());//预约服务站
        orderReportDetailDto.setAppRoNo(form.getWoCode());//工单编号
        if (null != form.getTimeCreate())//建单时间
        {
            orderReportDetailDto.setRoStartTime(form.getTimeAccept().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime().format(formatter));
        } else {
            orderReportDetailDto.setRoStartTime("");
        }
        //orderReportDetailDto.setLicenseNo("皖A03480");//车牌号---测试专用
        orderReportDetailDto.setLicenseNo(carLicense != null ? carLicense : "");//车牌号
        //orderReportDetailDto.setDealerCode("S66666");//服务站代码---测试专用
        orderReportDetailDto.setDealerCode(form.getStationCode() != null ? form.getStationCode() : "");//服务站代码
        orderReportDetailDto.setOwnerName(form.getAppoUserName() != null ? form.getAppoUserName() : "");//用户姓名
        orderReportDetailDto.setOwnerMobile(form.getAppoUserPhone() != null ? form.getAppoUserPhone() : "");//用户电话
        if (null != form.getAppoArriveTime())//预约到站时间
        {
            orderReportDetailDto.setArriveStation(form.getAppoArriveTime().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime().format(formatter));
        } else {
            orderReportDetailDto.setArriveStation("");
        }
        String roType = "400客服";
        if (form.getAppoType() == 2) {
            roType = "APP预约";
        } else if (form.getAppoType() == 3) {
            roType = "服务站建单";
        }
        orderReportDetailDto.setRoType(roType);//工单类型
        orderReportDetailDto.setMileage(form.getMileage() != null ? form.getMileage() : "");//行驶里程
        orderReportDetailDto.setDelName(form.getSendToRepairName() != null ? form.getSendToRepairName() : "");//报修人姓名
        orderReportDetailDto.setDelMobile(form.getSendToRepairPhone() != null ? form.getSendToRepairPhone() : "");//报修人电话
        orderReportDetailDto.setFaultDescription(form.getUserComment() != null ? form.getUserComment() : "");//故障描述

        //获取工单图片!!!
        List<OrderReportPhotoDto> orderReportPhotoDtoList = new ArrayList<>();
        List<WoCalculationPhotosDto> woCalculationPhotosDtos = workOrderOperatePhotoMapper.queryWoCalculationPhotosByWoCode(form.getWoCode());
        if (CollectionUtil.isNotEmpty(woCalculationPhotosDtos)) {
            for (WoCalculationPhotosDto woCalculationPhotosDto : woCalculationPhotosDtos) {

                OrderReportPhotoDto orderReportPhotoDto = new OrderReportPhotoDto();
                //图片名称描述
                orderReportPhotoDto.setIMG_NAME(ImageEnum.getValueByCode(woCalculationPhotosDto.getType()));
                //图片URL
                orderReportPhotoDto.setIMG_URL(woCalculationPhotosDto.getUrl() != null ? woCalculationPhotosDto.getUrl() : "");
//                orderReportPhotoDto.setBASE64_STR(imageToBase64ByOnline(woCalculationPhotosDto.getUrl()));  // 不再发送图片信息
                orderReportPhotoDto.setBASE64_STR("");  //给DMS发送空图片解码信息
                orderReportPhotoDtoList.add(orderReportPhotoDto);
            }
        }

        OrderReportDto orderReportDto = new OrderReportDto();
        orderReportDto.setCheckImgList(orderReportPhotoDtoList);
        JSONArray paramArray = new JSONArray();
        paramArray.add(orderReportDetailDto);
        paramArray.add(orderReportDto);
        asyncService.pushTicket(orderReportDetailDto,paramArray,orderReportDto);
        log.info("pushWorkOrder end return:{}",orderReportDetailDto);
        return form.getWoCode();
    }

}
