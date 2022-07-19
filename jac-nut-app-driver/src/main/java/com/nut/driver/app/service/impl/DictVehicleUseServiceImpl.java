package com.nut.driver.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nut.driver.app.dao.DictVehicleUseDao;
import com.nut.driver.app.entity.DictVehicleUseEntity;
import com.nut.driver.app.service.DictVehicleUseService;

import java.util.List;

@Service("dictVehicleUseService")
@Slf4j
public class DictVehicleUseServiceImpl extends ServiceImpl<DictVehicleUseDao, DictVehicleUseEntity> implements DictVehicleUseService {

    /**
    * @Description：${todo}
    * @author YZL
    * @data 2021/6/29 9:32
    */
    public List<DictVehicleUseEntity> findAll() {
        return this.baseMapper.findAll();
    }
}
