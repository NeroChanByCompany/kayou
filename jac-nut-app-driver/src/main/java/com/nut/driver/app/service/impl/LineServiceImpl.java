package com.nut.driver.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nut.app.driver.entity.FitFleetLineEntity;
import com.nut.common.exception.ExceptionUtil;
import com.nut.driver.app.form.LineForm;
import com.nut.driver.app.dao.FitFleetLineDao;
import com.nut.driver.app.dao.FltFleetDao;
import com.nut.driver.app.domain.FltFleet;
import com.nut.driver.app.domain.FltFleetLine;
import com.nut.driver.app.domain.FltFleetLineRoute;
import com.nut.driver.app.domain.FltFleetLineRouteCar;
import com.nut.driver.app.entity.FltFleetEntity;
import com.nut.driver.app.service.LineService;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("LineService")
@Slf4j
public class LineServiceImpl implements LineService {

    @Autowired
    private FltFleetDao fltFleetMapper;
    @Autowired
    private FitFleetLineDao fitFleetLineDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData addLine(LineForm command) throws Exception {
        log.info("[addLine]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        FltFleet fltFleet = fltFleetMapper.selectByPrimaryKey(Long.parseLong(command.getFleetId()));
        if (null == fltFleet) {
            result.setResultCode(ECode.PARAM_FAIL.code());
            result.setMessage("车队ID在系统中不存在");
            return result;
        }

        FltFleetLine fltFleetLine = new FltFleetLine();
        fltFleetLine.setFleetId(command.getFleetId());
        fltFleetLine.setStartCityName(command.getStartCityName());
        fltFleetLine.setEndCityName(command.getEndCityName());
        fltFleetLine.setCreateUserId(command.getUserId());
        fltFleetLine.setCreateTime(new Date());

        //线路信息插入数据库
        fltFleetMapper.insertFleetLine(fltFleetLine);
        long lineId = fltFleetLine.getId();


        result.setData(lineId);
        log.info("[addLine]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData list() throws Exception {
        log.info("[list]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        Long lineId = null;
        List<FltFleetLine> fleetLineList = fltFleetMapper.selectFltFleetLineList(lineId);
        result.setData(fleetLineList);
        log.info("[list]end");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData deleteLine(LineForm command) throws Exception {
        log.info("[deleteLine]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        if (command.getId() == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "id不能为空");
        }

        long lineId = Long.parseLong(command.getId());
        //线路信息
        fltFleetMapper.deleteFleetLineById(lineId);

        //途经点
        //fltFleetMapper.deleteFleetLineRouteBylineId(lineId);

        //fltFleetMapper.deleteFleetLineRouteCarBylineId(lineId);
        log.info("[deleteLine]end");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData updateLine(LineForm command) throws Exception {
        log.info("[updateLine]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        if (command.getId() == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "id不能为空");
        }

        long lineId = Long.parseLong(command.getId());
//wht
        LambdaQueryWrapper<FitFleetLineEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FitFleetLineEntity::getId, lineId);
        FitFleetLineEntity fitFleetLineEntity = new FitFleetLineEntity();
        fitFleetLineEntity.setFleetId(command.getFleetId());
        fitFleetLineEntity.setStartCityName(command.getStartCityName());
        fitFleetLineEntity.setEndCityName(command.getEndCityName());
        fitFleetLineDao.update(fitFleetLineEntity, queryWrapper);
//        fltFleetMapper.update(fltFleetLine , queryWrapper);
        /*//绑定车辆
        if(null != command.getCarIds()){
            //删除历史数据
            fltFleetMapper.deleteFleetLineRouteCarBylineId(lineId);
            //添加新的数据
            FltFleetLineRouteCar fltFleetLineRouteCar = null;
            String[] ids = command.getCarIds().split("&");
            for (String carId : ids) {
                fltFleetLineRouteCar = new FltFleetLineRouteCar();

                fltFleetLineRouteCar.setLineId(lineId);
                fltFleetLineRouteCar.setCarId(Long.valueOf(carId));
                fltFleetLineRouteCar.setCreateUserId(command.getUserId());
                fltFleetLineRouteCar.setCreateTime(new Date());
                fltFleetMapper.insertFleetLineRouteCar(fltFleetLineRouteCar);
            }
        }*/
        log.info("[updateLine]end");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData detail(LineForm command) throws Exception {
        log.info("[detail]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        if (command.getId() == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "id不能为空");
        }

        Long lineId = Long.parseLong(command.getId());
        List<FltFleetLine> fltFleetLines = fltFleetMapper.selectFltFleetLineList(lineId);
        log.info("查询线路信息，查询条件id={},查询结果数:{}", lineId, fltFleetLines.size());
        if (fltFleetLines.size() > 1) {
            result.setResultCode(500);
            result.setMessage("数据库存在多条数据");
            return result;
        }
        FltFleetLine fltFleetLine = fltFleetLines.get(0);
        List<FltFleetLineRoute> fltFleetLineRouteList = fltFleetMapper.selectfltFleetLineRouteListByLineId(lineId);
        if (CollectionUtils.isNotEmpty(fltFleetLineRouteList)) {
            StringBuilder routeString = new StringBuilder();
            for (FltFleetLineRoute fltFleetLineRoute : fltFleetLineRouteList) {
                routeString.append("#" + fltFleetLineRoute.getRouteCityCode() + "&" + fltFleetLineRoute.getRouteCityName());
            }
            fltFleetLine.setRoutes(routeString.toString().substring(1));
        }
        List<FltFleetLineRouteCar> fltFleetLineRouteCarList = fltFleetMapper.selectfltFleetLineRouteCarListByLineId(lineId);
        if (CollectionUtils.isNotEmpty(fltFleetLineRouteCarList)) {
            StringBuilder routeCarString = new StringBuilder();
            for (FltFleetLineRouteCar fltFleetLineRouteCar : fltFleetLineRouteCarList) {
                routeCarString.append("&" + fltFleetLineRouteCar.getCarId());
            }
            fltFleetLine.setCarIds(routeCarString.toString().substring(1));
        }
        result.setData(fltFleetLine);
        log.info("[detail]end");
        return result;
    }


    @Override
    public List<FitFleetLineEntity> userList(LineForm form) {
        //车队相关数据
        List<FitFleetLineEntity> fitFleetLineEntities = fitFleetLineDao.selectGroupByName(form.getUserId());
        log.info("list end return:{}", fitFleetLineEntities);
        return fitFleetLineEntities;
    }
}
