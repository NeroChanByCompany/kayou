package com.nut.servicestation.app.dao;

import com.github.pagehelper.Page;
import com.nut.servicestation.app.domain.*;
import com.nut.servicestation.app.dto.CouponInfoDto;
import com.nut.servicestation.app.dto.StationDto;
import com.nut.servicestation.app.form.BranchListForm;
import com.nut.servicestation.app.form.CouponDetailForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/7
 */
@Mapper
public interface CouponInfoDao {

    CouponInfoDetail queryCouponDetail(CouponDetailForm command);

    Map<String, String> queryCouponStatus(Map<String, Object> param);

    long countCouponBranch(Map<String, Object> param);

    Page<CouponApproval> queryApprovalList(Map<String, Object> param);

    CouponInfo selectByPrimaryKey(Long id);

    int insertSelective(CouponInfo record);

    // 插入 coupon_applicable 表
    int batchInsertApplicable(@Param("couponApplicable") CouponApplicable couponApplicable);
    // 从jac_tsp_dev数据库中查询服务站信息
    Page<BranchInfo> queryBranchList(@Param("DbName") String DbName);
    // 插入网点兑换表
    Integer batchInsertExchange(@Param("list") List<CouponExchange> list);
    // 插入优惠券与用户对应表
    int batchInsertCUM(@Param("cum") CouponUserMapping cum);

    List<CouponGiveInfo> queryListByServiceCode(@Param("serviceCode") String serviceCode);

    CouponGiveDetail queryDetailById(@Param("id") Integer id);


}
