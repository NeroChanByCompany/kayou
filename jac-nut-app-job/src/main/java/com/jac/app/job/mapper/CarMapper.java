package com.jac.app.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jac.app.job.domain.Car;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liuBing
 * @Classname carMapper
 * @Description TODO
 * @Date 2021/8/16 16:47
 */
@Mapper
public interface CarMapper{
    /**
     * 根据底盘号查询车辆ID和车牌号
     */
    Car selectCarByCarVin(String chassisNum);
    /**
     * 查询终端通信号
     * @param carId
     * @return
     */
    String queryAutoTerminalByCarId(String carId);

    /**
     * 根据sim卡号查询车牌号
     * @param autoTerminal sim卡号
     * @return
     */
    Car selectCarBySimNum(String autoTerminal);
}
