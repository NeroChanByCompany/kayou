package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.domain.WorkOrderOperatePhoto;
import com.nut.servicestation.app.dto.WoCalculationPhotosDto;
import com.nut.servicestation.app.form.UploadPhotoInfoForm;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/2
 */
@Mapper
public interface WorkOrderOperatePhotoDao {

    /**
     * 根据工单号、操作ID 查询 照片列表
     * @param map 工单号、操作ID
     * @return 照片列表
     */
    List<WorkOrderOperatePhoto> selectByWoCodeAndOperateId(Map map);

    List<WoCalculationPhotosDto> queryWoCalculationPhotosByWoCode(String woCode);

    /**
     * 根据工单号、操作ID、时间戳查询照片数量
     *   用于判断是否已上传
     * @param command
     * @return
     */
    int queryPicNum(UploadPhotoInfoForm command);


    int insertSelective(WorkOrderOperatePhoto record);
}
