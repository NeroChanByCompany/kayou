package com.jac.app.job.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * @Description: 积分解冻
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.jac.app.job.mapper
 * @Author: yzl
 * @CreateTime: 2021-10-26 11:29
 * @Email: yzl379131121@163.com
 * @Version: 1.0
 */
@Mapper
public interface IntegralMapper {

    void unfreezeStatus(String ucId);
}
