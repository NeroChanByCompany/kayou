package com.nut.driver.app.service;

import com.nut.driver.app.domain.UserForForum;
import com.nut.driver.app.form.ForumForGetUserListForm;

import java.util.Map;

/**
 * @author liuBing
 * @Classname ForumService
 * @Description TODO
 * @Date 2021/7/23 15:25
 */
public interface ForumService {
    /**
     * @Author liuBing
     * @Description //TODO 查询用户信息集合
     * @Date 15:28 2021/7/23
     * @Param [form]
     * @return java.util.Map<java.lang.Long,com.nut.driver.app.domain.UserForForum>
     **/
    Map<Long, UserForForum> getUserListByIdList(ForumForGetUserListForm form);
}
