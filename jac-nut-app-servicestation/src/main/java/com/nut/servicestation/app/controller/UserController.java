package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.enums.LoginCheckEnum;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.common.utils.RedisComponent;
import com.nut.servicestation.app.form.*;
import com.nut.servicestation.app.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author liuBing
 * @Classname UserController
 * @Description TODO 用户中心
 * @Date 2021/6/28 15:42
 */
@Slf4j
@RestController
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Resource
    HttpServletRequest request;

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private RedisComponent redisComponent;


    /**
     * @Author liuBing
     * @Description //TODO 用户登录
     * @Date 15:42 2021/6/28
     * @Param [form]
     * @return com.nut.common.result.HttpCommandResultWithData
     **/
    @PostMapping(value = "/login")
    public HttpCommandResultWithData login(@RequestJson LoginForm form) throws Exception {
        this.formValidate(form);
        String version = request.getHeader("version");
        if (StringUtils.isNotBlank(version)){
            form.setVersion(version);
        }
        return Result.ok(userService.login(form));
    }
    /**
     * @Author liuBing
     * @Description //TODO 获取用户信息
     * @Date 21:48 2021/6/28
     * @Param [form]
     * @return com.nut.common.result.HttpCommandResultWithData
     **/
    @PostMapping(value = "/getUserInfo")
    @LoginRequired
    public HttpCommandResultWithData getUserInfo(@RequestJson GetUserInfoForm form) {
        return Result.ok(userService.getUserInfo(form));
    }

    /**
     * @Author liuBing
     * @Description //TODO 修改密码
     * @Date 21:48 2021/6/28
     * @Param [form]
     * @return com.nut.common.result.HttpCommandResultWithData
     **/
    @PostMapping(value = "/updatePassword")
    @LoginRequired
    public HttpCommandResultWithData updatePassword(@RequestJson UpdatePasswordForm form) throws Exception {
        this.formValidate(form);
        return Result.ok(userService.updatePassword(form));
    }


    /**
     * @Author liuBing
     * @Description //TODO 修改用户登录信息
     * @Date 21:48 2021/6/28
     * @Param [form]
     * @return com.nut.common.result.HttpCommandResultWithData
     **/
    @PostMapping(value = "/updateUserInfo")
    @LoginRequired
    public HttpCommandResultWithData updateUserInfo(@RequestJson UpdateUserInfoForm form) {
        return Result.ok(userService.updateUserInfo(form));
    }


    /**
     * @Author liuBing
     * @Description //TODO 退出登录
     * @Date 21:48 2021/6/28
     * @Param [form]
     * @return com.nut.common.result.HttpCommandResultWithData
     **/
    @PostMapping(value = "/logout")
    @LoginRequired
    public HttpCommandResultWithData logout(@ApiParam @RequestJson LogoutForm form) {
        userService.logout(form);
        return Result.ok();
    }


    @PostMapping(value = "/cancellation")
    @LoginRequired
    @ApiOperation(value = "注销当前账户")
    public HttpCommandResultWithData cancellation(@ApiIgnore LogoutForm form) {
        log.info("cancellation start param:{}",form);
        //userService.cancellation(form);
        userService.logout(form);
        return Result.ok();
    }

    /**
     * 服务站关闭时，调用接口，删除所有该站下用户token
     * @param tokenList 参数传进来的所有用户id集合
     * @return
     */
    @PostMapping("/deleteToken")
    @ApiOperation(value = "注销个人token")
    public boolean deleteToken(@ApiIgnore @RequestBody TokenForm tokenList){
        if(tokenList != null){
            List<Long> accountIdList = tokenList.getAccoutIdList();
        if( !accountIdList.isEmpty() && accountIdList.size() > 0){
            //遍历用户id集合
            for(int i = 0; i <accountIdList.size(); i++) {
                //拿到每一项即为一个单独的用户id
                Long accoutId = accountIdList.get(i);
                String userId = userService.queryUserNameById(accoutId);
                if(userId != null ){
                    //根据用户id拼接出缓存token的key值
                    String cacheKeyUserId = applicationName + ":userToken:" + userId;
                    //根据key值拿到用户先前缓存的token
                    String preToken = (String) redisComponent.get(cacheKeyUserId);
                    //根据token拼接出用户信息缓存的key值
                    String cacheKey = applicationName + ":userToken:" + preToken;
                    //根据key值拿到用户信息
                    Map<Object, Object> tokenMap = redisComponent.hmget(cacheKey);
                    if (Objects.nonNull(tokenMap) && !tokenMap.isEmpty()) {
                        String userId1 = (String) tokenMap.get("userId");
                        String cacheKeyUserId1 = applicationName + ":userToken:" + userId1;
                        redisComponent.del(cacheKeyUserId1);
                    }
                    redisComponent.del(cacheKey);
                }
            }
            //查询是否删除成功
            for(int i = 0; i <accountIdList.size(); i++) {
                //拿到每一项即为一个单独的用户id
                Long accoutId = accountIdList.get(i);
                String userId = userService.queryUserNameById(accoutId);
                if(userId != null ) {
                    //根据用户id拼接出缓存token的key值
                    String cacheKeyUserId = applicationName + ":userToken:" + userId;
                    //根据key值拿到用户先前缓存的token
                    String preToken = "";
                    preToken = (String) redisComponent.get(cacheKeyUserId);
                    if (preToken != ""&& preToken != null) {
                        return false;
                    }
                    //根据token拼接出用户信息缓存的key值
                    String cacheKey = applicationName + ":userToken:" + preToken;
                    //根据key值拿到用户信息
                    Map<Object, Object> tokenMap = redisComponent.hmget(cacheKey);
                    if (Objects.nonNull(tokenMap) && !tokenMap.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        }
        return true;
    }
}
