package com.nut.driver.app.dao;

import com.nut.driver.app.dto.ActionPictureSetDTO;
import com.nut.driver.app.entity.ActionPictureSetEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-18 13:35:38
 */
@Mapper
public interface ActionPictureSetDao extends BaseMapper<ActionPictureSetEntity> {

    /**
     * 查询信息
     * @param actionCode
     * @param type
     * @return
     */
    List<ActionPictureSetDTO> getSetPictureInfo(@Param("actionCode") String actionCode, @Param("type") String type);


}
