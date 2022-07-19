package com.nut.driver.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.domain.MyCouponInfo;
import com.nut.driver.app.form.CouponBranchListForm;
import com.nut.driver.app.form.CouponDetailForm;
import com.nut.common.result.PagingInfo;
import com.nut.driver.app.form.CouponListForm;
import com.nut.driver.app.form.ScoreBindcarReceiveForm;

import java.text.ParseException;
import java.util.Map;


/**
 * @Description: 优惠券服务
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service
 * @Author: yzl
 * @CreateTime: 2021-06-18 15:06
 * @Version: 1.0
 */
public interface CouponService {

    /**
     * 优惠券列表
     * @param form
     * @return
     */
    PagingInfo<MyCouponInfo> list(CouponListForm form);

    /**
     * 优惠券详情
     * @param form
     * @return
     */
    MyCouponInfo detail(CouponDetailForm form) throws JsonProcessingException;

    int carIsBind(String vin);

    int insertIntoCouponCarBind(Map<String, Object> params);
    /**
     * 邦车增加优惠券
     *
     * @param command
     * @return
     */
    HttpCommandResultWithData scoreBindcarReceive(ScoreBindcarReceiveForm command) throws ParseException;

    PagingInfo branchList(CouponBranchListForm form);

}
