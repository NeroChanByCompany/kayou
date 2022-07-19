package com.nut.truckingteam.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.StringUtil;
import com.nut.truckingteam.app.dao.TeamDao;
import com.nut.truckingteam.app.dao.UserDao;
import com.nut.truckingteam.app.dto.CarOilWearDto;
import com.nut.truckingteam.app.form.GetTeamCarsOilWearForm;
import com.nut.truckingteam.app.form.User;
import com.nut.truckingteam.app.pojo.CarRolePojo;
import com.nut.truckingteam.app.service.TripAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/*
 *  @author wuhaotian 2021/7/10
 */
@Slf4j
@Service("TripAnalysisService")
public class TripAnalysisServiceImpl implements TripAnalysisService {

    @Autowired
    private TeamDao teamMapper;
    @Autowired
    private UserDao userDao;

    @Override
    public HttpCommandResultWithData getTeamCarsOilWear(GetTeamCarsOilWearForm command) {
        log.info("[getTeamCarsOilWear]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData<>();

        QueryWrapper<User> wrapper =  new QueryWrapper<User>();
        wrapper.eq(StringUtils.isNotBlank(command.getUserId()),"uc_id",command.getUserId());
        User user = userDao.selectOne(wrapper);
        // 查询相关车辆ID
        List<CarRolePojo> carRelations = teamMapper.queryRankCarsByUserIdForMyCars(user.getId());
        log.info("[getTeamCarsOilWear]carRelations.size:{}", carRelations.size());

        // 全部车辆ID
        List<String> carIds = carRelations.stream().map(CarRolePojo::getCarId).distinct().collect(Collectors.toList());
        log.info("[getTeamCarsOilWear]carIds.size:{}", carIds.size());

        if (carIds.isEmpty()) {
            result.setData(new ArrayList<>());
        } else {
            Map<String, Object> param = new HashMap<>();
            param.put("carIds", carIds);
            param.put("carModels", StringUtil.split(command.getModelId(), ","));
            List<CarOilWearDto> retList = teamMapper.queryCarsInfo(param);
            result.setData(retList);
        }
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        log.info("[getTeamCarsOilWear]end");
        return result;
    }
}
