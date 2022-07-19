package com.nut.servicestation.app.dao;

import com.github.pagehelper.Page;
import com.nut.servicestation.app.domain.WorkOrderOperate;
import com.nut.servicestation.app.dto.RepairHistoryDto;
import com.nut.servicestation.app.dto.RepairRecordsDto;
import com.nut.servicestation.app.pojo.RepairRecordDetailPojo;
import com.nut.servicestation.app.pojo.WoRepairPhotoPojo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/2
 */
public interface WorkOrderOperateDao {

    /**
     * 查询操作唯一标识 唯一性
     * @param record
     * @return
     */
    Integer selectByOperateId(WorkOrderOperate record);

    int insertSelective(WorkOrderOperate record);

    /**
     * 查询维修项（查询所有多次外出救援的维修项）
     * @param param
     * @return
     */
    Page<RepairRecordsDto> selectByWoCode(Map<String, Object> param);
    /**
     * 维修历史查询
     * @param param
     * @return
     */
    Page<RepairHistoryDto> queryRepairHistory(Map<String, Object> param);
    /**
     * 查询操作唯一标识 唯一性
     * @param record
     * @return
     */
    Integer selectByOperateIdAndFinished(WorkOrderOperate record);
    /**
     * 删除分段提交的未完成维修项
     * @param record
     */
    void delFirstCommitOperate(WorkOrderOperate record);
    /**
     * 查询维修项详情
     * @param param
     * @return
     */
    List<RepairRecordDetailPojo> selectRepairRecordDetail(Map<String, Object> param);
    /**
     * 查询维修记录对应的图片
     *
     * @param woCode 工单号
     * @return 维修记录图片信息
     */
    List<WoRepairPhotoPojo> selectRepairPhotoByWoCode(String woCode);
    /**
     * 获取维修图片数量
     * @param woCode
     * @return
     */
    Integer selectRepairPhotoNumByWoCode(String woCode);
    /**
     * 查询检查记录对应的图片（处理方式固定-1）
     *
     * @param woCode 工单号
     * @return 检查记录图片信息
     */
    List<WoRepairPhotoPojo> selectInspectPhotoByWoCode(String woCode);
    /**
     * 获取检查图片数量
     * @param woCode
     * @return
     */
    Integer selectInspectPhotoNumByWoCode(String woCode);
    /**
     * 更新记录为可见
     *
     * @param list 主键list
     * @return 更新记录数
     */
    int updateRecordVisible(List<Long> list);

    /**
     * 更新操作记录外出救援次数
     *
     * @param record
     * @return
     */
    int updateTimesRescueNumber(WorkOrderOperate record);
    /**
     * 获取维修图片数量
     *
     * @param woCode
     * @param operateId
     * @return
     */
    Integer selectRepairPhotoNumByWoCodeAndOperateId(@Param("woCode") String woCode, @Param("operateId") String operateId);

}
