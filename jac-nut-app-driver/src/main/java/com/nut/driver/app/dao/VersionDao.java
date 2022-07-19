package com.nut.driver.app.dao;

import com.nut.driver.app.entity.VersionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.pojo.VersionPojo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 版本更新说明表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 14:17:55
 */
@Mapper
public interface VersionDao extends BaseMapper<VersionEntity> {

    /**
     * 查询帮助手册列表
     *
     * @return 帮助手册列表
     */
    List<VersionPojo> queryVersionList(String appCode);
}
