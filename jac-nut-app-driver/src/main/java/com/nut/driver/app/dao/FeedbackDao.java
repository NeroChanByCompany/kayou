package com.nut.driver.app.dao;

import com.nut.driver.app.entity.FeedbackEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.form.FeedbackForm;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 09:57:39
 */
@Mapper
public interface FeedbackDao extends BaseMapper<FeedbackEntity> {
    int insertSelective(FeedbackForm form);

}
