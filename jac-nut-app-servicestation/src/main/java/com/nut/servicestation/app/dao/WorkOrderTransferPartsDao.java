package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.domain.WorkOrderTransferParts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/5
 */
@Mapper
public interface WorkOrderTransferPartsDao {

    /**
     * 查询维修项是否调件中
     *
     * @param record
     * @return
     */
    int selectByOperateIdAndStatus(WorkOrderTransferParts record);
    /**
     * 查询维修项的服务类型
     *
     * @param param
     * @return
     */
    Integer selectServiceTypeByOperateId(Map<String, Object> param);
    /**
     * 根据工单号和operate_id查询未到货确认的调件信息
     *
     * @param woCode    工单号
     * @param operateId operate_id（选填）
     * @return 调件信息
     */
    List<WorkOrderTransferParts> selectByWoCodeAndOperateId(@Param("woCode") String woCode, @Param("operateId") String operateId);
    /**
     * 批量插入
     * 重新生成mapper时注意一同修改此sql
     *
     * @param list 插入的数据列表
     * @return 插入记录数
     */
    int batchInsert(List<WorkOrderTransferParts> list);
}
