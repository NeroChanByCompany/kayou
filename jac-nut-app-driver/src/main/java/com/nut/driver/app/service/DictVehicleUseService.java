package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.driver.app.entity.DictVehicleUseEntity;

import java.util.List;
import java.util.Map;

/**
 * 车辆用途字段表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-29 09:29:42
 */
public interface DictVehicleUseService extends IService<DictVehicleUseEntity> {

    /**
    * @Description：${todo}
    * @author YZL
    * @data 2021/6/29 9:32
    */
    List<DictVehicleUseEntity> findAll();
}

