package com.nut.servicestation.app.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.HttpUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.servicestation.app.dao.CarDao;
import com.nut.servicestation.app.dao.WorkOrderDao;
import com.nut.servicestation.app.dao.WorkOrderOperatePhotoDao;
import com.nut.servicestation.app.domain.Car;
import com.nut.servicestation.app.domain.WorkOrder;
import com.nut.servicestation.app.dto.OrderReportDetailDto;
import com.nut.servicestation.app.dto.OrderReportDto;
import com.nut.servicestation.app.dto.OrderReportPhotoDto;
import com.nut.servicestation.app.dto.WoCalculationPhotosDto;
import com.nut.servicestation.app.entity.WorkOrderLogEntity;
import com.nut.servicestation.app.enums.ImageEnum;
import com.nut.servicestation.app.enums.WorkOrderLogEnum;
import com.nut.servicestation.app.form.PartForm;
import com.nut.servicestation.app.form.WoInfoForm;
import com.nut.servicestation.app.form.WorkOrderForm;
import com.nut.servicestation.app.service.AsyncService;
import com.nut.servicestation.app.service.MutualDmsService;
import com.nut.servicestation.app.service.WorkOrderLogService;
import com.nut.servicestation.common.wrapper.GetClwRoInfoStub;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*
 *  @author wuhaotian 2021/7/6
 */
@Slf4j
@Service("MutualDmsService")
public class MutualDmsServiceImpl implements MutualDmsService {


    @Value("${dms.workOrderReportUrl}")
    private String workOrderReportUrl;  //工单上报DMS的url

    @Value("${mes.vehicleProductSearchUrl}")
    private String vehicleProductSearchUrl;  //工单上报DMS的url

    @Autowired
    private WorkOrderDao workOrderMapper;

    @Autowired
    private CarDao carMapper;

    @Autowired
    private WorkOrderOperatePhotoDao workOrderOperatePhotoMapper;

    @Autowired
    private GetClwRoInfoStub getClwRoInfoStub;
    @Autowired
    WorkOrderLogService workOrderLogService;

    @Autowired
    AsyncService asyncService;

    @Override
    public HttpCommandResultWithData vehicleProductSearch(PartForm command) throws Exception {
        log.info("[vehicleProductSearch]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        Map<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("VIN", command.getVin());
        if (StringUtils.isNotBlank(command.getItems())) {
            mapParam.put("ITEMS", command.getItems());
        }

        List param = new ArrayList();
        param.add(mapParam);
        Map<String, Object> requestJson = new HashMap<String, Object>();
        requestJson.put("vehicleConfigQuery", param);
        log.info("查询PI的车辆配置查询接口请求参数url = {}, params = {}", vehicleProductSearchUrl, JsonUtil.toJson(requestJson));
        long startTime = new Date().getTime();
        String httpResult = HttpUtil.postJson4Authorization(vehicleProductSearchUrl, JsonUtil.toJson(requestJson), null);
        log.info("查询PI车辆配置查询接口返回的内容：{}", httpResult);
        log.info("查询PI车辆配置查询接口用时：{}s", (new Date().getTime() - startTime) / 1000);
        JSONObject resultJson = JSON.parseObject(httpResult);
        if (resultJson.containsKey("MT_CLW_TO_ME_CLPZCX_IN_SEND")) {
            JSONObject data = resultJson.getJSONObject("MT_CLW_TO_ME_CLPZCX_IN_SEND");
            if (data.containsKey("RETURN")) {
                result.setData(data.getJSONObject("RETURN"));
            } else {
                result.setResultCode(ECode.SERVER_ERROR.code());
                result.setMessage("调用PI没有查到数据");
            }
        } else {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("调用PI没有查到数据");
        }
        log.info("[vehicleProductSearch]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData uploadWorkOrderReport(WoInfoForm command) throws Exception {
        log.info("[uploadWorkOrderReport--工单上报到江淮DMS]start");
        HttpCommandResultWithData<Map> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        String woCode = command.getWoCode();
        Map<String, String> param = new HashMap<>();
        param.put("woCode", woCode);
        WorkOrder workOrder = workOrderMapper.selectByWoCode(param);


        if (null == workOrder) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("未获取到该工单信息");
            return result;
        }

        String woStatus = String.valueOf(workOrder.getWoStatus());
        if (StringUtils.equals("400", woStatus)) {
            result.setResultCode(ECode.NO_DATA.code());
            result.setMessage("该工单已经上报成功，请刷新页面");
            return result;
        }

        if (!StringUtils.equals("240", woStatus) && !StringUtils.equals("250", woStatus)) {
            result.setResultCode(ECode.NO_DATA.code());
            result.setMessage("该工单信息状态不是已评价或维修完成");
            return result;
        }

        //获取车牌号车牌号
        String carLicense = null;
        if (null != workOrder.getChassisNum()) {
            Car car = carMapper.selectCarByCarVin(workOrder.getChassisNum());
            if (car != null) {
                carLicense = car.getCarNumber();
            }
        }
        OrderReportDetailDto orderReportDetailDto = new OrderReportDetailDto();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //orderReportDetailDto.setVin("L1903480");//测试专用
        orderReportDetailDto.setVin(workOrder.getChassisNum().substring(workOrder.getChassisNum().length() - 8));//车辆底盘号
        //orderReportDetailDto.setDealerMame("江淮轻卡");//预约服务站测试专用
        orderReportDetailDto.setDealerMame(workOrder.getStationName());//预约服务站
        orderReportDetailDto.setAppRoNo(workOrder.getWoCode());//工单编号
        if (null != workOrder.getTimeCreate())//建单时间
        {
            orderReportDetailDto.setRoStartTime(workOrder.getTimeAccept().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime().format(formatter));
        } else {
            orderReportDetailDto.setRoStartTime("");
        }
        //orderReportDetailDto.setLicenseNo("皖A03480");//车牌号---测试专用
        orderReportDetailDto.setLicenseNo(carLicense != null ? carLicense : "");//车牌号
        //orderReportDetailDto.setDealerCode("S66666");//服务站代码---测试专用
        orderReportDetailDto.setDealerCode(workOrder.getStationCode() != null ? workOrder.getStationCode() : "");//服务站代码
        orderReportDetailDto.setOwnerName(workOrder.getAppoUserName() != null ? workOrder.getAppoUserName() : "");//用户姓名
        orderReportDetailDto.setOwnerMobile(workOrder.getAppoUserPhone() != null ? workOrder.getAppoUserPhone() : "");//用户电话
        if (null != workOrder.getAppoArriveTime())//预约到站时间
        {
            orderReportDetailDto.setArriveStation(workOrder.getAppoArriveTime().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime().format(formatter));
        } else {
            orderReportDetailDto.setArriveStation("");
        }
        String roType = "400客服";
        if (workOrder.getAppoType() == 2) {
            roType = "APP预约";
        } else if (workOrder.getAppoType() == 3) {
            roType = "服务站建单";
        }
        orderReportDetailDto.setRoType(roType);//工单类型
        orderReportDetailDto.setMileage(workOrder.getMileage() != null ? workOrder.getMileage() : "");//行驶里程
        orderReportDetailDto.setDelName(workOrder.getSendToRepairName() != null ? workOrder.getSendToRepairName() : "");//报修人姓名
        orderReportDetailDto.setDelMobile(workOrder.getSendToRepairPhone() != null ? workOrder.getSendToRepairPhone() : "");//报修人电话
        orderReportDetailDto.setFaultDescription(workOrder.getUserComment() != null ? workOrder.getUserComment() : "");//故障描述

        //获取工单图片!!!
        List<OrderReportPhotoDto> orderReportPhotoDtoList = new ArrayList<>();
        List<WoCalculationPhotosDto> woCalculationPhotosDtos = workOrderOperatePhotoMapper.queryWoCalculationPhotosByWoCode(woCode);
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
  /*      JSONArray imgList = new JSONArray();
        for (OrderReportPhotoDto dto : orderReportPhotoDtoList) {
            JSONObject img = new JSONObject();
            img.put("IMG_NAME", dto.getIMG_NAME());
            img.put("IMG_URL", dto.getIMG_URL());
            img.put("BASE64_STR", dto.getBASE64_STR());
            imgList.add(img);
        }
        JSONObject checkImgList = new JSONObject();
        checkImgList.put("checkImgList", imgList);
        paramArray.add(checkImgList);*/
        //将工单异步推送到dms
        log.info("开启异步推送dms工单");
        HttpCommandResultWithData resultWithData = asyncService.pushTicket(orderReportDetailDto, paramArray, orderReportDto);
        if (resultWithData.getResultCode() != 200){
            return resultWithData;
        }
        // 更新工单
        WorkOrder updateEntity = new WorkOrder();
        updateEntity.setId(workOrder.getId());
        updateEntity.setWoStatus(400);
        workOrderMapper.updateByPrimaryKeySelective(updateEntity);
        log.info("返回信息：工单上报成功");
        log.info("[uploadWorkOrderReport--工单上报到江淮DMS]end");
        return result;
    }
}
