package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.domain.NewUserJobResult;
import com.nut.driver.app.dto.IntegralDetailDto;
import com.nut.driver.app.entity.Integral;
import com.nut.driver.app.entity.IntegralEntity;
import com.nut.driver.app.form.IntegralOperationForm;
import com.nut.driver.app.form.IntegralOperationFreezeForm;
import com.nut.driver.app.form.QueryIntegralForm;

import java.util.Map;

/**
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-15 19:03:14
 */
public interface IntegralService extends IService<IntegralEntity> {

    /**
     * 积分查询
     * @param form
     * @return
     */
    IntegralDetailDto queryIntegral(QueryIntegralForm form);

    /**
     * 用户积分任务
     * @param form
     * @return
     */
    NewUserJobResult newUserJob(QueryIntegralForm form);

    /**
     * 用户积分历史记录
     * @param form
     * @return
     */
    Map queryIntegralHistory(QueryIntegralForm form);

    /**
     * 积分操作
     * @param form
     * @return
     */
    Integral integralOperation(IntegralOperationForm form);

    /**
     * 加积分
     * @param form
     * @return
     */
    Integral addIntegralCounts(IntegralOperationForm form);

    /**
     * 减积分
     * @param form
     * @return
     */
    Integral subtractionIntegralCounts(IntegralOperationForm form);
    /**
     * 用户积分添加埋点
     *
     * @param ucId    用户唯一id
     * @param addType 用户添加积分类型
     * @return
     * @throws Exception
     */
    HttpCommandResultWithData addAction(String ucId, Integer addType) throws Exception;
    /**
     * 积分增加操作
     *
     * @param command
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData addIntegralCounts1(IntegralOperationForm command) throws Exception;

    /**
     * 中秋活动，应对积分操作
     */
    HttpCommandResultWithData midAutumnByRuleId(String ucId, Integer ruleId);

    /**
     * 节日活动，应对积分操作
     */
    HttpCommandResultWithData FestivalByRuleId(String ucId, Integer ruleId, String statTime, String endTime);

    /**
     * 冻结积分操作
     * @param form
     * @return
     */
    void integralOperationFreeze(IntegralOperationFreezeForm form);


}

