package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.nut.driver.app.dto.MaintainItemListoDTO;
import com.nut.driver.app.entity.MaintainItemInfoEntity;
import com.nut.driver.app.form.QueryMaintainItemListForm;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuBing
 * @Classname MaintainItemInfoDao
 * @Description TODO
 * @Date 2021/6/24 15:31
 */
@Mapper
@Repository
public interface MaintainItemInfoDao extends BaseMapper<MaintainItemInfoEntity> {

    MaintainItemInfoEntity selectByPrimaryKey(Integer maintainItemId);

    List<String> queryMaintainItemName(List<Integer> collect);

    Page<MaintainItemListoDTO> queryMaintainItemListForPage(QueryMaintainItemListForm form);

    List<MaintainItemInfoEntity> queryMaintainItemList(List<Integer> collect);
}
