package com.nut.driver.app.service.impl;

import com.nut.driver.app.dao.SmsListDao;
import com.nut.driver.app.service.SmsListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service.impl
 * @Author: yzl
 * @CreateTime: 2021-10-25 17:01
 * @Version: 1.0
 */
@Service
@Slf4j
public class SmsListServiceImpl implements SmsListService {

    @Autowired
    private SmsListDao smsListDao;

    @Override
    @Async
    public void recordPhone(String phone){
        Map<String, Object> map = new HashMap<>();
        map.put("phone",phone);
        map.put("date",new Date());
        try {
            smsListDao.insertIllegalUser(map);
        }catch (Exception e){
            log.error("{}",e.getMessage());
        }
    }
}
