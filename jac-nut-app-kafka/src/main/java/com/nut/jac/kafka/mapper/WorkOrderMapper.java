package com.nut.jac.kafka.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.jac.kafka.domain.WorkOrder;
import com.nut.jac.kafka.dto.WoStatusDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author liuBing
 * @Classname WorkOrderMapper
 * @Description TODO
 * @Date 2021/8/31 14:06
 */
@Mapper
@Repository
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {
    @Select("select * from work_order where wo_code = #{woCode}")
    WorkOrder selectByWoCode(@Param("woCode") String woCode);

    /**
     * 查询工单状态
     * @param list
     * @return
     */
    @Select("<script>" +
            "SELECT\n" +
            "        two.wo_code AS woCode,\n" +
            "        two.wo_status AS woStatus\n" +
            "        FROM\n" +
            "        work_order two\n" +
            "        WHERE\n" +
            "        two.wo_code IN\n" +
            "        <foreach item='item' index='index' collection='list' open='(' separator=',' close=')' >\n" +
            "            #{item}\n" +
            "        </foreach>" +
            "</script>")
    List<WoStatusDTO> queryWoStatusByWoCode(@Param("list") List<String> list);
}
