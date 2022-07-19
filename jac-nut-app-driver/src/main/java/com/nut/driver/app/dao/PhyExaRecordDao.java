package com.nut.driver.app.dao;

import com.nut.driver.app.domain.PhyExaRecord;
import com.nut.driver.app.dto.CarPhyExaFaultDetailDto;
import com.nut.driver.app.dto.CurrentQueryFaultResponseDto;
import com.nut.driver.app.entity.PhyExaRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 车辆体检
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-29 15:50:21
 */
@Mapper
public interface PhyExaRecordDao extends BaseMapper<PhyExaRecordEntity> {

    List<PhyExaRecordEntity> queryPhyExaRecordByCars(List<String> list);

    PhyExaRecord queryPhyExaRecordByCarId(String carId);

    List<CarPhyExaFaultDetailDto> queryFaultByIds(List<Long> list);

    CarPhyExaFaultDetailDto queryFaultBySpnFmi(CurrentQueryFaultResponseDto currentQueryFaultResponseDto);

    int insertSelective(PhyExaRecord record);

    int updateByPrimaryKey(PhyExaRecord record);

}
