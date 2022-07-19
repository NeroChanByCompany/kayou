package com.nut.driver.app.dao;

import com.nut.driver.app.entity.Integral;
import com.nut.driver.app.entity.IntegralEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.entity.ScoreTaskRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-15 19:03:14
 */
@Mapper
public interface IntegralDao extends BaseMapper<IntegralEntity> {

    /**
     * 创建用户积分
     *
     * @param integral
     * @return
     */
    int createIntegral(Integral integral);

    /**
     * 积分操作
     *
     * @param integral
     * @return
     */
    int updateIntegralCounts(Integral integral);

    /**
     * 积分操作
     *
     * @param integral
     * @return
     */
    int updateSubIntegralCounts(Integral integral);

    /**
     * 积分操作
     *
     * @param integral
     * @return
     */
    int updateAddIntegralCounts(Integral integral);

    /**
     * 通过积分规则id查询对应积分值
     *
     * @param roleId
     * @return
     */
    ScoreTaskRule getScoreTaskRule(String roleId);

    /**
     * 通过ucId判断用户是否存在积分
     *
     * @param ucId
     * @return
     */
    Integral integralExist(String ucId);

    /**
     * 通过ucId获取手机号
     *
     * @param ucId
     * @return
     */
    String queryPhoneByUcId(String ucId);

    /**
     * 积分冻结
     * @param ucId
     */
    void FreezeStatus(String ucId);

    /**
     * 积分解冻
     */
    void unfreezeStatus(String ucId);

}
