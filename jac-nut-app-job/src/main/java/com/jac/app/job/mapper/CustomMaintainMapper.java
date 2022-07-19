package com.jac.app.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jac.app.job.domain.CustomMaintainInfo;
import com.jac.app.job.entity.CustomMaintainEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuBing
 * @Classname CustomMaintainMapper
 * @Description TODO
 * @Date 2021/8/12 16:22
 */
@Mapper
@Repository
public interface CustomMaintainMapper extends BaseMapper<CustomMaintainEntity> {
    /**
     * 查询当前记录是否已经推送
     * @return
     */
    List<CustomMaintainInfo> queryMaintanceInfos();

    /**
     * 查询出自定义保养项
     * @param id 保养项id
     * @return
     */
    List<String> selectByMaintainId(Long id);

    /**
     * 修改消息状态
     * @param record
     */
    void updateByPrimaryKeySelective(CustomMaintainInfo record);
}
