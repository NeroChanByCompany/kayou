package com.nut.driver.app.service.impl;

import com.nut.driver.app.form.FeedbackForm;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nut.driver.app.dao.FeedbackDao;
import com.nut.driver.app.entity.FeedbackEntity;
import com.nut.driver.app.service.FeedbackService;

import java.util.Date;

@Service("feedbackService")
@Slf4j
public class FeedbackServiceImpl extends ServiceImpl<FeedbackDao, FeedbackEntity> implements FeedbackService {
    @Override
    @SneakyThrows
    public long addFeedback(FeedbackForm form){
        form.setCreateTime(new Date());
        this.baseMapper.insertSelective(form);
        long lineId = form.getId();
        log.info("addFeedbace end return:{}",form);
        return lineId;
    }
}
