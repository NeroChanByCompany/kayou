package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.driver.app.entity.AuthLogEntity;
import com.nut.driver.app.form.AuthLogForm;
import com.nut.driver.app.form.AuthStateForm;
import org.apache.ibatis.annotations.Param;

/**
 * @author liuBing
 * @Classname AuthLogService
 * @Description TODO 实名认证接口
 * @Date 2021/8/10 13:25
 */
public interface AuthLogService extends IService<AuthLogEntity> {
    /**
     * 查询认证回调
     * @param form
     * @return
     */
    Integer callback(AuthLogForm form);

    /**
     * 根据iccId更改认证状态
     * @param form
     */
    void state(AuthStateForm form);

    /**
     *根据vin删除用户实名认证
     * @param carVin 车辆vin
     */
    Integer deleteAuthlog(@Param("carVin") String carVin);
}
