package com.nut.driver.app.service.impl;

import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.driver.app.dao.UserDao;
import com.nut.driver.app.domain.UserForForum;
import com.nut.driver.app.form.ForumForGetUserListForm;
import com.nut.driver.app.service.ForumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuBing
 * @Classname ForumServiceImpl
 * @Description TODO
 * @Date 2021/7/23 15:25
 */
@Slf4j
@Service
public class ForumServiceImpl implements ForumService {
    @Autowired
    private UserDao userDao;
    @Override
    public Map<Long, UserForForum> getUserListByIdList(ForumForGetUserListForm form) {
        if (form.getIdList().isEmpty()||form.getIdList()==null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "需查询的用户信息为空，请传入用户信息");
        }
        List<UserForForum> userList = userDao.findUserListByIdList(form.getIdList());

        Map<Long, UserForForum> stringUserForForumMap = new HashMap<>();
        if (null != userList) {
            for (UserForForum userForForum : userList) {
                stringUserForForumMap.put(userForForum.getId(),userForForum);
            }
        }
        return stringUserForForumMap;
    }
}
