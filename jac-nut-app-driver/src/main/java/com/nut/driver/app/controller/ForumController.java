package com.nut.driver.app.controller;


import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.form.ForumForGetUserListForm;
import com.nut.driver.app.service.ForumService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 论坛相关接口
 *
 */
@Slf4j
@RestController
public class ForumController {

    @Autowired
    private ForumService forumService;

    /**
     * 通过id集合查询用户信息集合
     */
    @PostMapping(value = "/forum/getUserListByIdList")
    @CrossOrigin
    public @ResponseBody
    HttpCommandResultWithData getUserListByIdList(@RequestBody ForumForGetUserListForm form) {
        log.info("==== [ForumController]--[forum/getUserList] begin ====");
        return Result.ok(forumService.getUserListByIdList(form));
    }
}
