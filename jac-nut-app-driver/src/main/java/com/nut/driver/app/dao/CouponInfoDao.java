package com.nut.driver.app.dao;

import com.github.pagehelper.Page;
import com.nut.driver.app.domain.CouponExchangeBranch;
import com.nut.driver.app.domain.CouponInfo;
import com.nut.driver.app.domain.MyCouponInfo;
import com.nut.driver.app.entity.CouponInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 优惠券主表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-18 15:50:56
 */
@Mapper
public interface CouponInfoDao extends BaseMapper<CouponInfoEntity> {

    Page<MyCouponInfo> selectList(Map<String, Object> param);

    List<String> selectApplicableModelName(Map<String, Object> param);

    List<CouponInfo> selectCouponInfoList(Map<String, Object> param);
    // 获取可添加首保券
    List<CouponInfo> selectCouponInfoListByMaintenance(Map<String, Object> param);

    Page<CouponExchangeBranch> queryBranchList(Map<String, Object> param);

}
