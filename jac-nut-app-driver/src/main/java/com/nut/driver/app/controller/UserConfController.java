package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.dao.UserDao;
import com.nut.driver.app.entity.UserConfEntity;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.form.QueryUserConfForm;
import com.nut.driver.app.form.UpdateUserConfForm;
import com.nut.driver.app.service.UserConfService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Optional;

/**
 * @Description: 用户相关设置
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-27 15:43
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "用户相关设置")
public class UserConfController extends BaseController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserConfService userConfService;


    @PostMapping(value = "/user/conf")
    @ApiOperation("获取自定义入口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "topic", dataType = "String"),
            @ApiImplicitParam(name = "key", dataType = "String")
    })
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData findByUserId(@ApiIgnore @RequestJson QueryUserConfForm form) {
        log.info("findByUserId start param:{}", form);
        this.formValidate(form);
        UserConfEntity userConfEntity = new UserConfEntity();
        try {
            Optional<Long> userId = getUserId(form.getUserId());
            if (!userId.isPresent()) {
                return Result.result(ECode.CLIENT_ERROR.code(), ECode.SERVER_ERROR.message());
            }
            userConfEntity = userConfService.findByUserId(userId.get(),
                    form.getTopic(), form.getKey());
        } catch (Exception e) {
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("findByUserId end return:{}", userConfEntity);
        return Result.ok(userConfEntity);
    }

    @PostMapping(value = "/user/conf/save")
    @ApiOperation("保存自定义入口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "topic", dataType = "String"),
            @ApiImplicitParam(name = "key", dataType = "String"),
            @ApiImplicitParam(name = "value", dataType = "String")
    })
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData findByCarId(@ApiIgnore @RequestBody  UpdateUserConfForm form) {
        log.info("findByCarId start param:{}", form);
        try {
            Optional<Long> userId = getUserId(form.getUserId());
            if (!userId.isPresent()) {
                return Result.ok();
            }
            UserConfEntity userConfEntity = userConfService.findByUserId(userId.get(),
                    form.getTopic(), form.getKey());
            if (userConfEntity == null) {
                userConfEntity = new UserConfEntity();
            }
            userConfEntity.setTopic(form.getTopic());
            userConfEntity.setKey(form.getKey());
            userConfEntity.setValue(form.getValue());
            if (userConfEntity.getId() == null) {
                userConfEntity.setUserId(userId.get());
                userConfService.insert(userConfEntity);
            } else {
                userConfService.update(userConfEntity);
            }
        } catch (Exception e) {
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("findByCarId end return:{null}");
        return Result.ok();
    }


    private Optional<Long> getUserId(String ucId) {
        UserEntity byUcId = userDao.findByUcId(ucId);
        if (byUcId == null) return Optional.empty();
        return Optional.of(byUcId.getId());
    }

}
