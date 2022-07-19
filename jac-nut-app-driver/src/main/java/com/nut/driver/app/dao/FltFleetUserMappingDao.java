package com.nut.driver.app.dao;

import com.github.pagehelper.Page;
import com.nut.driver.app.domain.FltFleetUserMapping;
import com.nut.driver.app.entity.FltFleetUserMappingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.pojo.FleetInfoPojo;
import com.nut.driver.app.pojo.UserInfoPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车队与用户绑定关系表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-16 15:48:59
 */
@Mapper
public interface FltFleetUserMappingDao extends BaseMapper<FltFleetUserMappingEntity> {

    /**
     * 根据用户ID查询用户角色
     *
     * @param userId 车队ID
     * @return 用户角色
     */
    String selectRoleByUserId(@Param("userId") Long userId);
    /**
     * 根据车队ID和角色查询用户信息
     *
     * @param teamId 车队ID
     * @param role   角色
     * @return 用户信息
     */
    Page<UserInfoPojo> selectByTeamIdAndRole(@Param("teamId") Long teamId, @Param("role") Integer role, @Param("keyword") String keyword);
    /**
     * 根据车队ID查询
     *
     * @param teamId 车队ID
     * @return 绑定关系
     */
    List<FltFleetUserMappingEntity> selectByTeamId(@Param("teamId") Long teamId);

    int insertSelective(FltFleetUserMapping record);
    /**
     * 根据车队ID和用户ID删除
     *
     * @param teamId 车队ID
     * @param userId 用户ID
     * @return 删除条数
     */
    int deleteByTeamIdAndUserId(@Param("teamId") Long teamId, @Param("userId") Long userId);

    int deleteByPrimaryKey(Long id);

    /**
     * 根据用户ID查询车队信息
     *
     * @param userId 用户ID
     * @return 车队信息
     */
    List<FleetInfoPojo> selectByUserId(Long userId);
    /**
     * 根据用户ID和车队ID查询车队信息
     *
     * @param userId 用户ID
     * @param teamId 车队ID
     * @return 车队信息
     */
    List<FleetInfoPojo> selectByUserIdAndTeamId(@Param("userId") Long userId, @Param("teamId") Long teamId);
    /**
     * 根据主键批量删除
     *
     * @param ids 主键列表
     * @return 删除条数
     */
    int batchDelete(List<Long> ids);

    /**
     * （统计用）查询用户所有车队ID
     * @param userId
     * @return
     */
    List<Long> queryFleetIdsByUserId(@Param("userId") Long userId);


    int insertSelective(FltFleetUserMappingEntity record);

}
