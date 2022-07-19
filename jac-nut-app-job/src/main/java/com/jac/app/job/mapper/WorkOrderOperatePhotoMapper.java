package com.jac.app.job.mapper;

import com.jac.app.job.dto.WoCalculationPhotosDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liuBing
 * @Classname WorkOrderOperatePhotoMapper
 * @Description TODO
 * @Date 2021/8/16 16:48
 */
@Mapper
public interface WorkOrderOperatePhotoMapper {
    List<WoCalculationPhotosDto> queryWoCalculationPhotosByWoCode(String woCode);
}
