package com.nut.driver.app.service.impl;

import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.dao.ScoreTaskRuleDao;
import com.nut.driver.app.form.InviteIntegralForm;
import com.nut.driver.app.service.H5Service;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.function.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: H5页面调用服务实现、
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service.impl
 * @Author: yzl
 * @CreateTime: 2021-11-17 15:48
 * @Version: 1.0
 */
@Service
@Slf4j
public class H5ServiceImpl implements H5Service {

    @Autowired
    ScoreTaskRuleDao scoreTaskRuleDao;

    @Override
    public HttpCommandResultWithData inviteIntegral(InviteIntegralForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        try {
            List<String> list = form.getList();
            List<Map<String ,Object>> mapList = new ArrayList<>();
            for (String id : list ) {
                Map<String ,Object> map = scoreTaskRuleDao.inviteIntegral(id);
                mapList.add(map);
            }
            log.info("mapList:{}", mapList);
            result.setData(mapList);
        }catch (Exception e){
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "数据查询失败");
        }
        return result;
    }
}
