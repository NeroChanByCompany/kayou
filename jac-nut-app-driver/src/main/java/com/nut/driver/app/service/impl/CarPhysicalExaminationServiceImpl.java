package com.nut.driver.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.HttpUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.dao.CarDao;
import com.nut.driver.app.dao.PhyExaRecordDao;
import com.nut.driver.app.domain.Car;
import com.nut.driver.app.domain.PhyExaRecord;
import com.nut.driver.app.dto.*;
import com.nut.driver.app.entity.CarEntity;
import com.nut.driver.app.entity.PhyExaRecordEntity;
import com.nut.driver.app.form.QueryCarPhysicalExaminationDetailForm;
import com.nut.driver.app.form.QueryCarPhysicalExaminationForm;
import com.nut.driver.app.pojo.PhyExaPojo;
import com.nut.driver.app.service.CarPhysicalExaminationService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-29 15:39
 * @Version: 1.0
 */
@Slf4j
@Service
public class CarPhysicalExaminationServiceImpl extends DriverBaseService implements CarPhysicalExaminationService {

    @Autowired
    private CarDao carDao;

    @Autowired
    private PhyExaRecordDao phyExaRecordDao;

    @Value("${big_data.dirver_base_url}")
    private String baseUrl;

    /**
    * @Description：车辆体检列表
    * @author YZL
    * @data 2021/6/29 15:55
    */
    @Override
    @SneakyThrows
    public CarPhyExaDto carPhysicalExamination(QueryCarPhysicalExaminationForm form) {
        List<PhyExaPojo> cars=carDao.queryPhyCarsByUserId(form.getAutoIncreaseId());
        // 过滤
        if (StringUtil.isNotEmpty(form.getCarNumber())) {
            cars = cars.stream().filter(p -> p.getCarNumber().contains(form.getCarNumber().toUpperCase())).collect(Collectors.toList());
        }
        CarPhyExaDto carPhyExaDto = new CarPhyExaDto();
        if (cars.size() == 0) {
            carPhyExaDto.setTotal(0L);
            carPhyExaDto.setPage_total(0L);
            carPhyExaDto.setCarNumber(0);
            carPhyExaDto.setFaultCarNumber(0);
            return carPhyExaDto;
        }
        // 查询体检信息
        List<String> carIds = cars.stream().map(PhyExaPojo::getCarId).collect(Collectors.toList());
        List<PhyExaRecordEntity> perList = phyExaRecordDao.queryPhyExaRecordByCars(carIds);

        Map<String, PhyExaRecordEntity> perMap = new HashMap<>(perList.size());
        if (perList.size() > 0) {
            perMap = perList.stream().collect(Collectors.toMap(PhyExaRecordEntity::getCarId, phyExaRecord -> phyExaRecord));
        }

        List<CarListPhyExaDto> list = new ArrayList<>(cars.size());
        CarListPhyExaDto carListPhyExaDto;
        // 故障车辆数
        int faultCarNum = 0;
        // 故障总数
        int faultNum = 0;
        for (PhyExaPojo phyExaPojo : cars) {
            carListPhyExaDto = new CarListPhyExaDto();
            carListPhyExaDto.setCarId(phyExaPojo.getCarId());
            carListPhyExaDto.setCarNum(phyExaPojo.getCarNumber());

            if(StringUtils.isNotBlank(phyExaPojo.getTerminalId())){
                carListPhyExaDto.setHasNetwork("1");
            }else {
                carListPhyExaDto.setHasNetwork("0");
            }

            PhyExaRecordEntity phyExaRecord = perMap.get(phyExaPojo.getCarId());
            if (phyExaRecord == null) {
                carListPhyExaDto.setFaultNumber(0);
                carListPhyExaDto.setLastPhyExaTime(DateUtil.getDatePattern(System.currentTimeMillis()));
            } else {
                carListPhyExaDto.setFaultNumber(phyExaRecord.getFaultNumber());
                carListPhyExaDto.setLastPhyExaTime(DateUtil.getDatePattern(phyExaRecord.getPhyExaTime()));
            }
            if (carListPhyExaDto.getFaultNumber() > 0) {
                faultCarNum += 1;
            }
            faultNum += carListPhyExaDto.getFaultNumber();
            list.add(carListPhyExaDto);
        }
        // 排序
        list = list.stream().sorted(Comparator.comparing(CarListPhyExaDto::getLastPhyExaTime).reversed())
                .collect(Collectors.toList());

        // 分页
        PagingInfo<CarListPhyExaDto> pagingInfo = new PagingInfo<>();
        list = paging(list, form.getPage_number(), form.getPage_size(), pagingInfo);

        carPhyExaDto.setCarNumber(cars.size());
        carPhyExaDto.setFaultCarNumber(faultCarNum);
        carPhyExaDto.setFaultNumber(faultNum);
        carPhyExaDto.setTotal(pagingInfo.getTotal());
        carPhyExaDto.setPage_total(pagingInfo.getPage_total());
        carPhyExaDto.setList(list);

        return carPhyExaDto;
    }

    /**
    * @Description：车辆体检详情
    * @author YZL
    * @data 2021/6/29 16:09
    */
    @Override
    @SneakyThrows
    public CarPhyExaDetailDto carPhysicalExaminationDetail(QueryCarPhysicalExaminationDetailForm form) {
        // 查询上次体检结果
        PhyExaRecord phyExaRecord = phyExaRecordDao.queryPhyExaRecordByCarId(form.getCarId());
        if (phyExaRecord == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(),"查询车辆不存在");
        }
        // 详情信息
        CarPhyExaDetailDto carPhyExaDetailDto = new CarPhyExaDetailDto();
        // 上次体检结果信息
        CarPhyExaLastExaDto carPhyExaLastExaDto = new CarPhyExaLastExaDto();
        if (phyExaRecord.getFaultNumber() == null) {
            carPhyExaLastExaDto.setFaultNumber(0);
            carPhyExaLastExaDto.setLastPhyExaTime(DateUtil.getDatePattern(System.currentTimeMillis()));
        } else {
            carPhyExaLastExaDto.setFaultNumber(phyExaRecord.getFaultNumber());
            carPhyExaLastExaDto.setLastPhyExaTime(DateUtil.getDatePattern(phyExaRecord.getPhyExaTime()));
            String faultIds = phyExaRecord.getFaultIds();
            if (StringUtil.isNotEmpty(faultIds)) {
                List<Long> faultIdList = new ArrayList<>();
                for (String s : faultIds.split(",")) {
                    if (StringUtils.isNotBlank(s)){
                        faultIdList.add(Long.valueOf(s));
                    }
                }
                if (CollectionUtils.isEmpty(faultIdList)){
                    phyExaRecord.setFaultNumber(0);
                    phyExaRecord.setPhyExaTime(System.currentTimeMillis());
                    phyExaRecordDao.updateByPrimaryKey(phyExaRecord);
                    CarPhyExaAgainExaDto carPhyExaAgainExaDto = new CarPhyExaAgainExaDto();
                    carPhyExaAgainExaDto.setFaultNumber(0);
                    carPhyExaDetailDto.setAgainExa(carPhyExaAgainExaDto);
                    return carPhyExaDetailDto;
                }
                // 查询故障信息
                List<CarPhyExaFaultDetailDto> faultList = phyExaRecordDao.queryFaultByIds(faultIdList);
                carPhyExaLastExaDto.setList(faultList);
            }
        }
        carPhyExaDetailDto.setLastExa(carPhyExaLastExaDto);
        // 不需要重新体检
        if (form.getType() == 0) {
            return carPhyExaDetailDto;
        }
        // 重新体检
        // 查询故障信息
        CarPhyExaAgainExaDto carPhyExaAgainExaDto = new CarPhyExaAgainExaDto();
        List<Long> againFaultIds=new ArrayList<>();
        carPhyExaAgainExaDto.setFaultNumber(0);
        String  url=baseUrl+"/location/fault/currentQuery";
//        Map<String,Object> map = new HashMap<>();
        JSONObject map =new JSONObject();
        List<Long> commIds=new ArrayList<>();
        CarEntity car=carDao.selectByPrimaryKey(form.getCarId());
        //查询车辆 terminalIds 号;
        if(car.getAutoTerminal()!=null){
            commIds.add(Long.parseLong(car.getAutoTerminal()));
            map.put("terminalIds",commIds);
            String res = HttpUtil.postHttps(url, map);
            Map<String, Object> resMap = JsonUtil.toMap(res);
            List<CurrentQueryFaultResponseDto> currentQueryList = new ArrayList<>();
            //处理接口返回数据
            if (Objects.nonNull(resMap.get("resultCode")) && resMap.get("resultCode").toString().equals("200") && resMap.get("data") != null) {
                String JSONStr = JSON.toJSONString(resMap.get("data"));
                Map<String, Object> dataMap = JsonUtil.toMap(JSONStr);
                String dataStr=JSON.toJSONString(dataMap.get("list"));
                currentQueryList = JSON.parseArray(dataStr, CurrentQueryFaultResponseDto.class);
            }
            carPhyExaAgainExaDto.setFaultNumber(currentQueryList.size());
            //封装当前体检信息
            List<CarPhyExaFaultDetailDto> faultList=new ArrayList<>();
            for (int i = 0; i < currentQueryList.size(); i++) {
                CarPhyExaFaultDetailDto carPhyDto=new CarPhyExaFaultDetailDto();
                carPhyDto.setSpn(currentQueryList.get(i).getSpn());
                carPhyDto.setFmi(currentQueryList.get(i).getFmi());
                CarPhyExaFaultDetailDto cDto= phyExaRecordDao.queryFaultBySpnFmi(currentQueryList.get(i));
                if(cDto!=null){
                    carPhyDto.setId(cDto.getId());
                    carPhyDto.setFaultCode(cDto.getFaultCode());
                    carPhyDto.setFaultDesc(cDto.getFaultDesc());
                }
                faultList.add(carPhyDto);
            }
            carPhyExaAgainExaDto.setList(faultList);
            againFaultIds = faultList.stream().map(CarPhyExaFaultDetailDto::getId).collect(Collectors.toList());
        }
        carPhyExaDetailDto.setAgainExa(carPhyExaAgainExaDto);
        // 体检结果写进体检记录表
        phyExaRecord.setFaultNumber(carPhyExaAgainExaDto.getFaultNumber());
        phyExaRecord.setPhyExaTime(System.currentTimeMillis());
        if (againFaultIds.size() > 0) {
            phyExaRecord.setFaultIds(StringUtils.join(againFaultIds.toArray(), ","));
        }else {
            phyExaRecord.setFaultIds(null);
        }
        if (phyExaRecord.getId() == null){
            phyExaRecordDao.insertSelective(phyExaRecord);
        }else {
            phyExaRecordDao.updateByPrimaryKey(phyExaRecord);
        }
        return carPhyExaDetailDto;
    }
}
