package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.driver.app.entity.FeedbackEntity;
import com.nut.driver.app.form.FeedbackForm;

import java.util.Map;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 09:57:39
 */
public interface FeedbackService extends IService<FeedbackEntity> {

    long addFeedback(FeedbackForm form);
}

