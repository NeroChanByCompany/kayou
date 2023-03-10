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
    private String workOrderReportUrl;  //????????????DMS???url

    @Value("${mes.vehicleProductSearchUrl}")
    private String vehicleProductSearchUrl;  //????????????DMS???url

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
        log.info("??????PI???????????????????????????????????????url = {}, params = {}", vehicleProductSearchUrl, JsonUtil.toJson(requestJson));
        long startTime = new Date().getTime();
        String httpResult = HttpUtil.postJson4Authorization(vehicleProductSearchUrl, JsonUtil.toJson(requestJson), null);
        log.info("??????PI??????????????????????????????????????????{}", httpResult);
        log.info("??????PI?????????????????????????????????{}s", (new Date().getTime() - startTime) / 1000);
        JSONObject resultJson = JSON.parseObject(httpResult);
        if (resultJson.containsKey("MT_CLW_TO_ME_CLPZCX_IN_SEND")) {
            JSONObject data = resultJson.getJSONObject("MT_CLW_TO_ME_CLPZCX_IN_SEND");
            if (data.containsKey("RETURN")) {
                result.setData(data.getJSONObject("RETURN"));
            } else {
                result.setResultCode(ECode.SERVER_ERROR.code());
                result.setMessage("??????PI??????????????????");
            }
        } else {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("??????PI??????????????????");
        }
        log.info("[vehicleProductSearch]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData uploadWorkOrderReport(WoInfoForm command) throws Exception {
        log.info("[uploadWorkOrderReport--?????????????????????DMS]start");
        HttpCommandResultWithData<Map> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        String woCode = command.getWoCode();
        Map<String, String> param = new HashMap<>();
        param.put("woCode", woCode);
        WorkOrder workOrder = workOrderMapper.selectByWoCode(param);


        if (null == workOrder) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("???????????????????????????");
            return result;
        }

        String woStatus = String.valueOf(workOrder.getWoStatus());
        if (StringUtils.equals("400", woStatus)) {
            result.setResultCode(ECode.NO_DATA.code());
            result.setMessage("?????????????????????????????????????????????");
            return result;
        }

        if (!StringUtils.equals("240", woStatus) && !StringUtils.equals("250", woStatus)) {
            result.setResultCode(ECode.NO_DATA.code());
            result.setMessage("???????????????????????????????????????????????????");
            return result;
        }

        //????????????????????????
        String carLicense = null;
        if (null != workOrder.getChassisNum()) {
            Car car = carMapper.selectCarByCarVin(workOrder.getChassisNum());
            if (car != null) {
                carLicense = car.getCarNumber();
            }
        }
        OrderReportDetailDto orderReportDetailDto = new OrderReportDetailDto();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //orderReportDetailDto.setVin("L1903480");//????????????
        orderReportDetailDto.setVin(workOrder.getChassisNum().substring(workOrder.getChassisNum().length() - 8));//???????????????
        //orderReportDetailDto.setDealerMame("????????????");//???????????????????????????
        orderReportDetailDto.setDealerMame(workOrder.getStationName());//???????????????
        orderReportDetailDto.setAppRoNo(workOrder.getWoCode());//????????????
        if (null != workOrder.getTimeCreate())//????????????
        {
            orderReportDetailDto.setRoStartTime(workOrder.getTimeAccept().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime().format(formatter));
        } else {
            orderReportDetailDto.setRoStartTime("");
        }
        //orderReportDetailDto.setLicenseNo("???A03480");//?????????---????????????
        orderReportDetailDto.setLicenseNo(carLicense != null ? carLicense : "");//?????????
        //orderReportDetailDto.setDealerCode("S66666");//???????????????---????????????
        orderReportDetailDto.setDealerCode(workOrder.getStationCode() != null ? workOrder.getStationCode() : "");//???????????????
        orderReportDetailDto.setOwnerName(workOrder.getAppoUserName() != null ? workOrder.getAppoUserName() : "");//????????????
        orderReportDetailDto.setOwnerMobile(workOrder.getAppoUserPhone() != null ? workOrder.getAppoUserPhone() : "");//????????????
        if (null != workOrder.getAppoArriveTime())//??????????????????
        {
            orderReportDetailDto.setArriveStation(workOrder.getAppoArriveTime().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime().format(formatter));
        } else {
            orderReportDetailDto.setArriveStation("");
        }
        String roType = "400??????";
        if (workOrder.getAppoType() == 2) {
            roType = "APP??????";
        } else if (workOrder.getAppoType() == 3) {
            roType = "???????????????";
        }
        orderReportDetailDto.setRoType(roType);//????????????
        orderReportDetailDto.setMileage(workOrder.getMileage() != null ? workOrder.getMileage() : "");//????????????
        orderReportDetailDto.setDelName(workOrder.getSendToRepairName() != null ? workOrder.getSendToRepairName() : "");//???????????????
        orderReportDetailDto.setDelMobile(workOrder.getSendToRepairPhone() != null ? workOrder.getSendToRepairPhone() : "");//???????????????
        orderReportDetailDto.setFaultDescription(workOrder.getUserComment() != null ? workOrder.getUserComment() : "");//????????????

        //??????????????????!!!
        List<OrderReportPhotoDto> orderReportPhotoDtoList = new ArrayList<>();
        List<WoCalculationPhotosDto> woCalculationPhotosDtos = workOrderOperatePhotoMapper.queryWoCalculationPhotosByWoCode(woCode);
        if (CollectionUtil.isNotEmpty(woCalculationPhotosDtos)) {
            for (WoCalculationPhotosDto woCalculationPhotosDto : woCalculationPhotosDtos) {
                OrderReportPhotoDto orderReportPhotoDto = new OrderReportPhotoDto();
                //??????????????????
                orderReportPhotoDto.setIMG_NAME(ImageEnum.getValueByCode(woCalculationPhotosDto.getType()));
                //??????URL
                orderReportPhotoDto.setIMG_URL(woCalculationPhotosDto.getUrl() != null ? woCalculationPhotosDto.getUrl() : "");
//                orderReportPhotoDto.setBASE64_STR(imageToBase64ByOnline(woCalculationPhotosDto.getUrl()));  // ????????????????????????
                orderReportPhotoDto.setBASE64_STR("");  //???DMS???????????????????????????
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
        //????????????????????????dms
        log.info("??????????????????dms??????");
        HttpCommandResultWithData resultWithData = asyncService.pushTicket(orderReportDetailDto, paramArray, orderReportDto);
        if (resultWithData.getResultCode() != 200){
            return resultWithData;
        }
        // ????????????
        WorkOrder updateEntity = new WorkOrder();
        updateEntity.setId(workOrder.getId());
        updateEntity.setWoStatus(400);
        workOrderMapper.updateByPrimaryKeySelective(updateEntity);
        log.info("?????????????????????????????????");
        log.info("[uploadWorkOrderReport--?????????????????????DMS]end");
        return result;
    }
}
