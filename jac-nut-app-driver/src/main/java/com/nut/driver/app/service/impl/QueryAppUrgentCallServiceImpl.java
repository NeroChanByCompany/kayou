package com.nut.driver.app.service.impl;

import com.github.pagehelper.Page;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.result.Result;
import com.nut.driver.app.dao.QueryAppUrgentCallDao;
import com.nut.driver.app.dao.UserDao;
import com.nut.driver.app.dto.QueryAppUrgentCallDto;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.form.AppUrgentCallForm;
import com.nut.driver.app.form.QueryAppUrgentCallForm;
import com.nut.driver.app.service.QueryAppUrgentCallService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-28 14:45
 * @Version: 1.0
 */
@Service
@Slf4j
public class QueryAppUrgentCallServiceImpl extends DriverBaseService implements QueryAppUrgentCallService {

    @Autowired
    private QueryAppUrgentCallDao queryAppUrgentCallDao;

    @Autowired
    private UserDao userDao;

    @SneakyThrows
    public PagingInfo query(QueryAppUrgentCallForm form) {
        Map<String, Object> param = new HashMap<>(4);
        param.put("name", form.getName());
        param.put("type", Integer.parseInt(form.getType()));
        // 查询数据库
        getPage(form);
        Page<QueryAppUrgentCallDto> queryPage = null;
        try {
            queryPage = queryAppUrgentCallDao.selectByTypeAndName(param);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "紧急电话服务查询失败!");
        }
        log.info("queryUrgentCall end return:{}",queryPage);
        return convertPagingToPage(queryPage);
    }

    public void urgentCall(AppUrgentCallForm form) {
        try {
            UserEntity user = userDao.findByUcId(form.getUserId());
            queryAppUrgentCallDao.insert(form.getId(), user.getPhone(), user.getId());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "一键呼救保存失败!");
        }
        log.info("UrgentCall end return:{null}");
    }
}
