package com.nut.driver.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.entity.ActivityEntity;
import com.nut.driver.app.form.GuoQingForm;
import com.nut.driver.app.form.MidAutumnForm;
import com.nut.driver.app.form.Shuang11Form;

import java.util.List;

/**
 * @Description: 活动服务
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service
 * @Author: yzl
 * @CreateTime: 2021-06-29 10:40
 * @Version: 1.0
 */

public interface ActiveService {

    boolean active1Check(long userId);


    /**
     * @Author liuBing
     * @Description //TODO 查询所有活动列表-entity版
     * @Date 18:20 2021/5/27
     * @Param []
     **/
     List<ActivityEntity> checkList();

    HttpCommandResultWithData midAutumn(MidAutumnForm form);

    HttpCommandResultWithData guoqing(GuoQingForm form);

    HttpCommandResultWithData shuang11(Shuang11Form form);

    HttpCommandResultWithData timeIsEnd();

    HttpCommandResultWithData timeIsEndGuoQing();

    HttpCommandResultWithData timeIsEndBy11();

}
