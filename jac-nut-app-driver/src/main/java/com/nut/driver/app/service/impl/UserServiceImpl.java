package com.nut.driver.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.dozermapper.core.Mapper;
import com.nut.common.enums.LoginTypeEnum;
import com.nut.common.enums.UserListenerEnum;
import com.nut.common.result.Result;
import com.nut.common.utils.*;
import com.nut.driver.app.dao.*;
import com.nut.driver.app.domain.InviterMessage;
import com.nut.driver.app.dto.AppLoginDTO;
import com.nut.driver.app.dto.RegisterDTO;
import com.nut.driver.app.dto.StarSignDto;
import com.nut.driver.app.entity.UserFreezeConfigEntity;
import com.nut.driver.app.entity.UserFreezeRecordsEntity;
import com.nut.driver.app.entity.Integral;
import com.nut.driver.app.entity.IntegralEntity;
import com.nut.driver.app.entity.UserListenerLogEntity;
import com.nut.driver.app.enums.StatusEnum;
import com.nut.driver.app.form.*;
import com.nut.common.constant.AppTypeEnum;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.dto.UserInfoDTO;
import com.nut.driver.app.pojo.AppVersion;
import com.nut.driver.app.service.*;
import com.nut.driver.app.util.SmsUtil;
import com.nut.driver.common.component.AppVersionComponent;
import com.nut.driver.common.component.RsaComponent;
import com.nut.driver.common.component.TokenComponent;
import com.nut.driver.common.constants.CommonConstants;
import com.nut.driver.common.constants.RedisConstant;
import com.nut.driver.common.utils.*;
import com.nut.driver.common.utils.HttpUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nut.driver.app.entity.UserEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service("userService")
@Transactional(rollbackFor = Exception.class, readOnly = true)
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private IntegralServiceImpl integralService;

    @Autowired
    private SendSmsService sendSmsService;

    @Autowired
    private SmsListService smsListService;

    @Autowired
    private WorkOrderService workOrderService;

    @Autowired
    private FltFleetUserMappingDao fltFleetUserMappingDao;

    @Autowired
    private TokenComponent tokenComponent;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SignDao signDao;

    @Autowired
    private SmsListDao smsListDao;

    @Autowired
    private IntegralDao integralDao;

    @Autowired
    private Mapper convert;

    @Autowired
    private RedisComponent redisComponent;
    @Autowired
    private RsaComponent rsaComponent;

    @Autowired
    HttpServletRequest request;

    @Value("${guoQingStart}")
    private String guoQingStart;

    @Value("${guoQingEnd}")
    private String guoQingEnd;

    @Value("${shuang11Start}")
    private String shuang11Start;

    @Value("${shuang11End}")
    private String shuang11End;

    @Autowired
    private AppVersionComponent appVersionComponent;

    @Resource
    UserListenerLogService logService;

    @Resource
    FltCarOwnerMappingDao fltCarOwnerMappingDao;

    @Resource
    FltFleetCarMappingDao fltFleetCarMapping;


    @Value("${register.private.key:''}")
    private String privateKey;


    @Autowired
    private InviteDao inviteDao;

    @Value("${bbs-limit-record:jac_bbs:limit_record:}")
    private String bbsKey;

    @Resource
    UserFreezeConfigService userFreezeConfigService;

    @Resource
    UserFreezeRecordsService userFreezeRecordsService;


    /**
     * 最大发送次数 从0 开始
     */
    private final static Integer SEND_SMS_COUNT = 14;


    /**
     * 用户注册
     *
     * @param form
     * @return
     */
    @Override
    @SneakyThrows
    @Transactional(timeout = Constants.REGISTER_TIMEOUT, rollbackFor = Exception.class)
    public RegisterDTO register(RegisterForm form) {
//        String decrypt = rsaComponent.decrypt(form.getSign(), privateKey);
//        String phone = "jac+" + form.getPhone();
//        if (!phone.equals(decrypt)) {
//            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "用户信息校验失败，请重新输入");
//        }


        if (StringUtil.isEmpty(form.getAppType())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "客户端类型不能为空");
        }
        // 验证输入的密码和确认密码是否一致
        if (!StringUtils.equals(form.getPassword().trim(), form.getConfirmPassword().trim())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "两次输入密码不一致");
        }

        if (this.checkSmsCode(form.getPhone())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "当前账户验证已到最大次数，请稍后再试!");
        }

        String smsCode = redisTemplate.boundValueOps(RedisConstant.SMS_CODE_VALID_PERIOD + form.getPhone()).get();
        // 验证短信验证码
        if (!StringUtils.equals(form.getSmsCode(), smsCode)) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "短信验证码不正确");
        }

        String ipArr = this.getHttpClientIp(request);
        String ip = ipArr.split(",")[0];
        //判断是否是老用户注册
        UserEntity userEntity = userDao.selectOne(new QueryWrapper<UserEntity>()
                .eq("app_type", form.getAppType())
                .eq("phone", form.getPhone())
                .eq("status", StatusEnum.CANCELLATION.getCode()));
        if (userEntity != null) {
            if (StringUtils.isNotBlank(userEntity.getUcPassword())) {
                userEntity.setUcPassword(form.getPassword());
            } else {
                userEntity.setPassword(form.getPassword());
            }
            userEntity.setStatus(StatusEnum.NORMAL.getCode());
            userEntity.setIp(ip);
            userDao.updateById(userEntity);
            RegisterDTO dto = new RegisterDTO();
            dto.setMobile(userEntity.getPhone());
            dto.setToken(UUID.randomUUID().toString().replaceAll("-", ""));
            dto.setId(userEntity.getId());
            return dto;
        }
        JSONObject userInfo = userCenterService.register(form);
        if (userInfo == null || !userInfo.containsKey("code")) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "注册失败！");
        }
        int code = userInfo.getInteger("code");
        // 失败
        if (code != 200) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), userInfo.getString("message"));
        }
        JSONObject user = userInfo.getJSONObject("data");

        // 设置返回信息
        String userId = user.getString("userId");
        RegisterDTO dto = new RegisterDTO();
        dto.setUserId(userId);
        dto.setMobile(user.getString("mobile"));
        dto.setToken(user.getString("token"));
        // 判断是否有添加过用户（添加管理员）
        UserEntity userTemp = this.baseMapper.findByPhoneAndType(form.getPhone(), form.getAppType());
        Long id = null;
        UserEntity saveEntity = new UserEntity();
        saveEntity.setIp(ip);
        saveEntity.setRegisterFlag(form.getRegisterFlag());
        Integer createType = null;
        // 当邀请人ucid不为空的时候，用户邀请注册
        if (StringUtils.isNotEmpty(form.getInviterUcid())) {
            createType = AppTypeEnum.INVITE.code();
        } else {
            createType = AppTypeEnum.APP_C.code();
        }
        if (userTemp == null) {
            UserEntity contentUser = this.baseMapper.findNoRegisterPhone(form.getPhone());
            if (contentUser == null) {
                saveEntity.setUcId(userId);
                saveEntity.setUcPassword(form.getPassword());
                saveEntity.setName(form.getName());
                saveEntity.setPhone(form.getPhone());
                saveEntity.setOrgCode(form.getOrgCode());
                saveEntity.setCustTag("散户");
                saveEntity.setAppType(form.getAppType());
                saveEntity.setCreateTime(DateUtil.getNowDate(DateUtil.time_pattern));
                saveEntity.setCreateType(createType);
                //需要新手指引标志
                saveEntity.setNoviceGuideTag(Constants.NEED_NOVICE_TAG);
                saveEntity.setDeviceId(form.getDeviceId());
                saveEntity.setUserType(1);
                saveEntity.setDeviceType(form.getDeviceType());
                this.baseMapper.insertSelective(saveEntity);
                id = saveEntity.getId();
            } else {
                saveEntity.setId(contentUser.getId());
                saveEntity.setUcId(userId);
                saveEntity.setUcPassword(form.getPassword());
                saveEntity.setName(form.getName());
                saveEntity.setPhone(form.getPhone());
                saveEntity.setOrgCode(form.getOrgCode());
                saveEntity.setCustTag("散户");
                saveEntity.setAppType(form.getAppType());
                saveEntity.setCreateTime(DateUtil.getNowDate(DateUtil.time_pattern));
                saveEntity.setCreateType(createType);
                //需要新手指引标志
                saveEntity.setNoviceGuideTag(Constants.NEED_NOVICE_TAG);
                saveEntity.setDeviceId(form.getDeviceId());
                saveEntity.setUserType(1);
                saveEntity.setDeviceType(form.getDeviceType());
                this.baseMapper.updateByPrimaryKeySelective(saveEntity);
                id = saveEntity.getId();

            }

            //用户注册积分埋点
            log.info("[----新用户注册，添加积分----]Start");

            try {
                HttpCommandResultWithData integralResult = integralService.addAction(dto.getUserId(), 1);
                if (integralResult.getResultCode() != ECode.SUCCESS.code()) {
                    //result.fillResult(ReturnCode.CLIENT_ERROR);//即便积分添加失败也继续接下来操作
                    //result.setMessage(userInfo.getString("message"));
                    log.info("[----新用户注册，添加积分----]ErrResult:" + JsonUtil.toJson(integralResult));
                    //return result;
                } else {
                    log.info("[----新用户注册，添加积分----]Success");
                }
                if (form.getInviterUcid() != null) {
                    log.info("被邀请人注册，邀请人添加积分开始");
                    InviterMessage message = userDao.selectUserTypeByucId(form.getInviterUcid());
                    if (StringUtils.isNotBlank(message.getPhone()) && StringUtils.isNotBlank(message.getUserType())) {
                        Integer userType = Integer.parseInt(message.getUserType());
                        // 应对用户身份user_type数据库中为空，现检验，如果为0，强制设置为1
                        if (userType == 0) {
                            userType = 1;
                        }
                        switch (userType) {
                            case 1:
                                // 普通用户邀请
                                log.info("邀请角色ucId：{}，被邀请角色ucId：{}", form.getInviterUcid(), userId);
                                integralService.addAction(form.getInviterUcid(), 56);
                                log.info("被邀请人注册，邀请人[身份-普通用户]添加积分结束");
                                break;
                            case 2:
                                //  水军邀请
                                log.info("邀请角色ucId：{}，被邀请角色ucId：{}", form.getInviterUcid(), userId);
                                integralService.addAction(form.getInviterUcid(), 51);
                                log.info("被邀请人注册，邀请人[身份-水军]添加积分结束");
                                break;
                            case 3:
                                //  网红邀请
                                log.info("邀请角色ucId：{}，被邀请角色ucId：{}", form.getInviterUcid(), userId);
                                log.info("被邀请人注册，邀请人[身份-网红]不添加积分");
                                break;
                            case 4:
                                //  官方号邀请
                                log.info("邀请角色ucId：{}，被邀请角色ucId：{}", form.getInviterUcid(), userId);
                                integralService.addAction(form.getInviterUcid(), 52);
                                log.info("被邀请人注册，邀请人[身份-官方号]添加积分结束");
                                break;
                            case 5:
                                //  认证号邀请
                                log.info("邀请角色ucId：{}，被邀请角色ucId：{}", form.getInviterUcid(), userId);
                                integralService.addAction(form.getInviterUcid(), 53);
                                log.info("被邀请人注册，邀请人[身份-认证号]添加积分结束");
                                break;
                            case 6:
                                //  经销商认证号邀请
                                log.info("邀请角色ucId：{}，被邀请角色ucId：{}", form.getInviterUcid(), userId);
                                integralService.addAction(form.getInviterUcid(), 54);
                                log.info("被邀请人注册，邀请人[身份-经销商认证号]添加积分结束");
                                break;
                            case 7:
                                //  服务站认证号邀请
                                log.info("邀请角色ucId：{}，被邀请角色ucId：{}", form.getInviterUcid(), userId);
                                integralService.addAction(form.getInviterUcid(), 55);
                                log.info("被邀请人注册，邀请人[身份-服务站认证号]添加积分结束");
                                break;
                            default:
                                break;
                        }
                    }
                    /**
                     * 邀请活动发放积分：2021/09/20-2021/09/26
                     */
                    /** ***********************************************************  */
                    log.info("中秋邀请活动发放积分：2021/09/20-2021/09/26   当前时间：{}", LocalDateTime.now());
                    integralService.midAutumnByRuleId(form.getInviterUcid(), 76);
                    log.info("国庆邀请活动发放积分：2021/10/01-2021/10/07   当前时间：{}", LocalDateTime.now());
                    integralService.FestivalByRuleId(form.getInviterUcid(), 86, guoQingStart, guoQingEnd);
                    /** ***********************************************************  */


                    /**
                     * 针对最新版本用户，发放活动积分
                     * 修改时间：2021/11/02
                     * 双十一邀请活动发放积分：2021/11/08-2021/11/18
                     */
                    AppVersion appVersion = new AppVersion();
                    log.info("APP类型：{},苹果/安卓：{},当前内部版本号：{}", form.getAppType(), form.getVersionType(), form.getVersion());
                    appVersion.setActionCode0(form.getAppType());
                    appVersion.setType(form.getVersionType());
                    appVersion.setVersion(form.getParamVersion());
                    log.info("appVersion:{}", appVersion);
                    if (appVersionComponent.checkVersion(appVersion)) {
                        log.info("双十一邀请活动发放积分：2021/11/08-2021/11/18   当前时间：{}", LocalDateTime.now());
                        integralService.FestivalByRuleId(form.getInviterUcid(), 94, shuang11Start, shuang11End);
                    }
                    /** *************************************** */


//                    inviteDao.insertMessage(form.getInviterUcid(), userId, message.getPhone(), form.getPhone(), LocalDateTime.now());
//                    log.info("被邀请人注册，邀请人添加积分结束");

//                     二级邀请注册，发放积分
                    log.info("开始执行二级邀请积分发放");
                    String inviter2 = inviteDao.invite2invite(form.getInviterUcid());
                    InviterMessage message2 = new InviterMessage();
                    if (StringUtils.isNotBlank(inviter2)) {
                        message2 = userDao.selectUserTypeByucId(inviter2);
                        log.info("message2 info ：{}", message2);
                        if (StringUtils.isNotBlank(message2.getPhone()) && StringUtils.isNotBlank(message2.getUserType())) {
                            log.info("******  二级邀请人身份代码：{}  ******", message2.getUserType());
                            switch (message2.getUserType()) {
                                case "1":
                                    // 普通用户邀请
                                    log.info("一级邀请人角色ucId：{}，二级邀请人角色ucId：{}", userId, inviter2);
                                    integralService.addAction(inviter2, 66);
                                    log.info("二级邀请人邀请注册成功，一级邀请人[身份-普通用户]添加积分结束");
                                    break;
                                case "2":
                                    //  水军邀请
                                    log.info("一级邀请人角色ucId：{}，二级邀请人角色ucId：{}", userId, inviter2);
                                    integralService.addAction(inviter2, 61);
                                    log.info("二级邀请人邀请注册成功，一级邀请人[身份-水军]添加积分结束");
                                    break;
                                case "3":
                                    //  网红邀请
                                    log.info("一级邀请人角色ucId：{}，二级邀请人角色ucId：{}", userId, inviter2);
                                    log.info("二级邀请人邀请注册成功，一级邀请人[身份-网红]不添加积分");
                                    break;
                                case "4":
                                    //  官方号邀请
                                    log.info("一级邀请人角色ucId：{}，二级邀请人角色ucId：{}", userId, inviter2);
                                    integralService.addAction(inviter2, 62);
                                    log.info("二级邀请人邀请注册成功，一级邀请人[身份-官方号]添加积分结束");
                                    break;
                                case "5":
                                    //  认证号邀请
                                    log.info("一级邀请人角色ucId：{}，二级邀请人角色ucId：{}", userId, inviter2);
                                    integralService.addAction(inviter2, 63);
                                    log.info("二级邀请人邀请注册成功，一级邀请人[身份-认证号]添加积分结束");
                                    break;
                                case "6":
                                    //  经销商认证号邀请
                                    log.info("一级邀请人角色ucId：{}，二级邀请人角色ucId：{}", userId, inviter2);
                                    integralService.addAction(inviter2, 64);
                                    log.info("二级邀请人邀请注册成功，一级邀请人[身份-经销商认证号]添加积分结束");
                                    break;
                                case "7":
                                    //  服务站认证号邀请
                                    log.info("一级邀请人角色ucId：{}，二级邀请人角色ucId：{}", userId, inviter2);
                                    integralService.addAction(inviter2, 65);
                                    log.info("二级邀请人邀请注册成功，一级邀请人[身份-服务站认证号]添加积分结束");
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    log.info("二级邀请积分发放执行结束");

                    log.info("邀请数据入库");
                    inviteDao.insertMessage(
                            form.getInviterUcid(), userId,
                            message.getPhone(), form.getPhone(),
                            LocalDateTime.now(),
                            inviter2, message2.getPhone());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("[----新用户注册，添加积分----]End");
        } else {
            if (userTemp.getStatus().equals(StatusEnum.FREEZE.getCode())) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "当前用户已被冻结，无法注册");
            } else {
                ExceptionUtil.result(ECode.PHONE_EXIST.code(), ECode.PHONE_EXIST.message());
            }
        }
        dto.setId(id);
        log.info("register end return:{}", dto);
        //this.setUserTokenToRedis(form.getAppType(), userId, id, dto.getToken());
        return dto;
    }

    /**
     * 忘记密码
     *
     * @param form
     * @return
     */
    @Override
    @Transactional(timeout = Constants.REGISTER_TIMEOUT, rollbackFor = Exception.class)
    public String forgetPassword(ForgetPasswordForm form) {
        UserEntity user = this.baseMapper.findByPhoneAndType(form.getPhone(), form.getAppType());
        if (null == user) {
            ExceptionUtil.result(ECode.NO_USERID.code(), "用户不存在");
        }
        if (user.getStatus().equals(StatusEnum.FREEZE.getCode())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "当前用户已被冻结");
        }

        if (!StringUtils.equals(form.getPassword(), form.getConfirmPassword())) {
            ExceptionUtil.result(ECode.PASSWORD_NOT_EQUAL.code(), "两次密码输入不一致");
        }
        if (this.checkSmsCode(form.getPhone())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "当前账户验证已到最大次数，请稍后再试!");
        }
        String redisSmsCode = redisTemplate.opsForValue().get(RedisConstant.SMS_CODE_VALID_PERIOD + form.getPhone());
        if (!StringUtils.equals(form.getSmsCode(), redisSmsCode)) {
            ExceptionUtil.result(ECode.SMSCODE_ERROR.code(), "验证码不正确");
        }
        redisTemplate.delete("SMS_VERIFICATION_CODE" + form.getPhone());
        user.setUcPassword(form.getPassword());
        this.baseMapper.updateByPrimaryKey(user);
        log.info("forgetPassword end return:{}", form.getPassword());
        return form.getPassword();
    }

    /**
     * 发送短信验证码
     *
     * @param form
     * @return
     */
    @Override
    @Transactional(readOnly = false)
    public void smsVerificationCode(SmsVerificationCodeForm form) {
        /**
         * 21/10/25 新增短信发送黑名单与白名单功能   Start
         */
        String sign = form.getSign();  // sign = 0 的时候为注册时候所用验证码
        List<String> list = smsListDao.phoneList();
        if (sign.equals("0")) {
            // 先查询白名单
            Integer whiteNum = smsListDao.isWhiteList(form.getPhone()); // 判断号码是否在白名单
            if (whiteNum == 0) {
                Integer blackNum = 0;
                //不在白名单
                for (String phone : list) {
                    if (form.getPhone().startsWith(phone, 0)) {
                        blackNum = 1;
                        break;
                    }
                }
                if (blackNum > 0) {
                    smsListService.recordPhone(form.getPhone());  // 信息录入存储
                    log.info("号码不合法");
                    ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "号码不合法");
                } else {
                    log.info("号码合法");
                }
            } else {
                log.info("号码合法");
            }
        }
        /**
         * 21/10/25 新增短信发送黑名单与白名单功能   End
         */


        String phoneTimeCode = redisTemplate.opsForValue().get(RedisConstant.SMS_CODE_TIME + form.getPhone());
        if (StringUtils.isNotBlank(phoneTimeCode)) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "短信发送失败，发送频率过于频繁，请一分钟之后再试！");
        }
        //一天当中所能发送的短信次数限制
        Boolean flag = sendCount(RedisConstant.SMS_CODE_FREQUENCY + form.getPhone(), SEND_SMS_COUNT);
        if (!flag) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "您今天短信发送次数已达上限！");
        }
        SendSmsForm com = new SendSmsForm();
        com.setPhone(form.getPhone());
        Random random = new Random();
        String smsCode = "";
        for (int i = 0; i < 6; i++) {
            smsCode += random.nextInt(10);
        }
        com.setContent(smsCode);
        //请求短信平台发送验证码
        com.setSign(form.getSign());
//        Map resultMap = sendSmsService.sendSms(com);
//        {"result":[{"originTo":"+8617621082342","createTime":"2022-05-27T02:04:24Z","from":"8821120233031","smsMsgId":"5262925e-62b5-40b2-977d-b381d93b4fd9_293369116","status":"000000"}],"code":"000000","description":"Success"}
        log.info("start sms  com = {}",com);
        String result = null;
        try{
            result = SmsUtil.send(com);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(result == null){
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "短信发送失败");
            return;
        }
        log.info("end sms result = {}",result);
        JSONObject resultMap = JSONObject.parseObject(result);
        log.info("smsCode:{},resultMap:{}", smsCode,result);
        if (StringUtils.equals("000000", String.valueOf(resultMap.get("code")))) {
            //验证码存放在redis中供后续验证
            redisTemplate.boundValueOps(RedisConstant.SMS_CODE_VALID_PERIOD + form.getPhone()).set(smsCode);
            redisTemplate.boundValueOps(RedisConstant.SMS_CODE_VALID_PERIOD + form.getPhone()).expire(10, TimeUnit.MINUTES);
            //发送频率限制
            redisTemplate.opsForValue().set(RedisConstant.SMS_CODE_TIME + form.getPhone(), smsCode, 1, TimeUnit.MINUTES);
            //次数限制
            redisTemplate.opsForList().rightPush(RedisConstant.SMS_CODE_FREQUENCY + form.getPhone(), LocalDate.now().toString());
        } else {
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "短信发送失败");
        }
    }


    /**
     * 同一个手机号一小时内发送短讯是否超过count次
     *
     * @param key   key 的拼接是 key + 手机号 + 业务类型
     * @param count 次数
     * @author
     */
    public Boolean sendCount(String key, int count) {
        Boolean boo = true;
        long size = redisTemplate.opsForList().size(key);
        if (size <= count) {
            boo = true;
        } else {
            List<String> t = redisTemplate.opsForList().range(key, 0, (int) size);
            if (!t.get(0).equals(LocalDate.now().toString())) {
                //最开始的一条距现在超过一小时就移除左边的，并添加一条
                redisTemplate.delete(key);
                boo = true;
            } else {
                //最左的一条也在n小时内，不能发送短信
                boo = false;
            }
        }
        return boo;
    }

    /**
     * 用户登录
     *
     * @param form
     * @return
     */
    @Override
    @Transactional(timeout = Constants.APPLOGIN_TIMEOUT)
    public AppLoginDTO appLogin(AppLoginForm form) {
        UserEntity findUser = this.baseMapper.findByPhoneAndType(form.getPhone(), form.getAppType());
        AppLoginDTO dto = userCenterService.login(form, findUser);
        dto.setId(findUser.getId());
        //更新推送消息key
        UserEntity updateUser4MessageKey = new UserEntity();
        updateUser4MessageKey.setId(findUser.getId());
        updateUser4MessageKey.setSendMessageKey(form.getSendMessageKey());
        this.baseMapper.updateByPrimaryKeySelective(updateUser4MessageKey);
        log.info("login end return:{}", dto);
        try {
            UserListenerLogEntity entity = new UserListenerLogEntity()
                    .setToken(dto.getToken())
                    .setVersion(form.getVersion())
                    .setAction(UserListenerEnum.LOGIN.getCode())
                    .setAppType(Integer.parseInt(form.getAppType()))
                    .setUcId(dto.getUserId())
                    .setVersionType(Integer.parseInt(StringUtils.isBlank(form.getVersionType()) ? "1" : form.getVersionType()));
            if (StringUtils.isNotBlank(form.getSmsCode())) {
                entity.setLoginType(LoginTypeEnum.SMS_CODE.getCode());
            } else if (StringUtils.isNotBlank(form.getPassword())) {
                entity.setLoginType(LoginTypeEnum.PASSWORD.getCode());
            } else {
                entity.setLoginType(LoginTypeEnum.UNDEFINED.getCode());
            }
            logService.saveActionListener(entity);
        } catch (Exception e) {
            log.info("登录日志存储异常,异常原因:{}", e.getMessage());
        }
        return dto;
    }


    /**
     * 用户详情
     */
    @Override
    public UserInfoDTO getUserInfo(GetUserInfoForm form) {
        UserEntity entity = this.baseMapper.findByUcId(form.getUserId());
        if (entity == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "没有该用户信息！");
        }
        //类型转换
        UserInfoDTO dto = convert.map(entity, UserInfoDTO.class);
        //获得用户角色信息
        String role = fltFleetUserMappingDao.selectRoleByUserId(entity.getId());
        dto.setRole(role == null ? "" : role);
        //在途工单数
        dto.setOtwOrderNum(workOrderService.queryOtwOrderNum(form.getAutoIncreaseId()));
        String ucId = userDao.findUcId(form.getAutoIncreaseId());
        dto.setUcId(ucId);
        LocalDate localDate = LocalDate.now();
        dto.setIsSign(signDao.selectByDate(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), ucId));
        Integer starSign = userDao.userStar(form.getAutoIncreaseId().toString());
        log.info("网红属性数据库查询结果：", starSign);
        if (starSign > 0) {
            dto.setStartSign(1);
        } else {
            dto.setStartSign(0);
        }
        log.info("网红属性输出结果：", dto.getStartSign());
        log.info("getUserInfo end return:{}", dto);
        return dto;
    }

    @Override
    @Transactional(timeout = Constants.LOGOUT_TIMEOUT, rollbackFor = Exception.class)
    public void logout(AppLogoutForm form) {
        UserEntity userEntity = userDao.selectOne(new QueryWrapper<UserEntity>().eq("uc_id", form.getUserId()));
        userEntity.setSendMessageKey("");
        userDao.updateById(userEntity);
        tokenComponent.destroyToken(form.getToken());
        try {
            UserListenerLogEntity entity = new UserListenerLogEntity()
                    .setToken(form.getToken())
                    .setVersion(form.getVersion())
                    .setAction(UserListenerEnum.LOGOUT.getCode())
                    .setAppType(Integer.parseInt(form.getAppType()))
                    .setUcId(form.getUserId())
                    .setVersionType(Integer.parseInt(StringUtils.isBlank(form.getVersionType()) ? "1" : form.getVersionType()));
            logService.saveActionListener(entity);
        } catch (Exception e) {
            log.info("登录日志存储异常,异常原因:{}", e.getMessage());
        }
        log.info(" logout end return:{null}");
    }

    @Override
    @Transactional(timeout = Constants.UPDPASSWORD_TIMEOUT, rollbackFor = Exception.class)
    public String updatePassword(UpdatePasswordForm form) {
        UserEntity user = this.baseMapper.findByUcId(form.getUserId());
        if (null == user) {
            ExceptionUtil.result(ECode.NO_USERID.code(), "没有找到用户");
        }
        if (!StringUtils.equals(form.getOldPassword(), user.getPassword()) && !StringUtils.equals(form.getOldPassword(), user.getUcPassword())) {
            ExceptionUtil.result(ECode.OLDPASSWORD_NOT_RIGHT.code(), "原始密码不正确");
        }
        if (StringUtils.equals(form.getOldPassword(), form.getNewPassword())) {
            ExceptionUtil.result(ECode.NEWPASSWORD_EQUAL_OLDPASSWORD.code(), "新密码与旧密码一样");

        }
        user.setUcPassword(form.getNewPassword());
        user.setPassword(form.getNewPassword());
        this.baseMapper.resetPasswordInfo(form);
        //更新redis
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUcId());
        map.put("id", user.getId());
        map.put("appType", form.getAppType());
        map.put("phone", user.getPhone());
        String token = tokenComponent.generateToken(map);

        //把老用户信息放进新token里
        //删除老token
        tokenComponent.destroyToken(form.getToken());
        log.info(" updatePassword end return:{}", form.getNewPassword());
        return form.getNewPassword();
    }

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUserInfo(ModifyUserInfoForm form) {
        UserEntity userEntity = this.baseMapper.findByUcId(form.getUserId());
        if (null == userEntity) {
            ExceptionUtil.result(ECode.PARAM_FAIL.code(), "此用户在数据库中不存在");
        }

        int count = UserUtil.setUser(form, userEntity);
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                IntegralOperationForm integralOperationForm = new IntegralOperationForm();
                integralOperationForm.setOperationId(0);
                integralOperationForm.setActionId("100");
                integralOperationForm.setUcId(userEntity.getUcId());
                integralService.addIntegralCounts(integralOperationForm);
            }
        }

        if (UserUtil.isInfoOk(userEntity)) {
            userEntity.setInfoOk(1);
        }
        userEntity.setUpdateTime(DateUtil.getNowDate(DateUtil.time_pattern));
        log.info(" modifyUserInfo end return:{null}");
        this.baseMapper.updateByPrimaryKey(userEntity);
    }

    @Override
    @Transactional
    public void modifyPhone(ModifyPhoneForm form) {

        String smsCode = redisTemplate.boundValueOps(RedisConstant.SMS_CODE_VALID_PERIOD + form.getPhone()).get();
        // 验证短信验证码
        if (!StringUtils.equals(form.getSmsCode(), smsCode)) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "短信验证码不正确");
        }

        UserEntity user = this.baseMapper.findByPhoneAndType(form.getPhone(), form.getType());
        if (null != user) {
            ExceptionUtil.result(ECode.PARAM_FAIL.code(), "手机号已存在");

        }
        UserEntity user4ModifyPhone = this.baseMapper.findByUcId(form.getUserId());
        if (null == user4ModifyPhone) {
            ExceptionUtil.result(ECode.PARAM_FAIL.code(), "此用户在数据库中不存在");
        }
        user4ModifyPhone.setPhone(form.getPhone());
        this.baseMapper.updateByPrimaryKey(user4ModifyPhone);

        log.info("modifyPhone end return:{null}");
    }

    @Override
    @Transactional
    public String uploadUserPicUrl(UploadUserPicUrlForm form) {
        UserEntity user = this.baseMapper.findByUcId(form.getUserId());
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setUserPicUrl(form.getUserPicUrl());
        this.updateById(userEntity);
        log.info("uploadUserPicUrl end return:{}", userEntity.getUserPicUrl());
        return userEntity.getUserPicUrl();
    }

    @Override
    @Transactional
    public HttpCommandResultWithData updateUserSignature(UpdateUserSignatureForm form) {
        try {
            UserEntity user = userDao.selectByPrimaryKey(form.getAutoIncreaseId());
            if (user == null) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "用户名不存在!");
            }
            userDao.updateSignature(String.valueOf(form.getAutoIncreaseId()), form.getSignature());
            if (StringUtils.isNotBlank(form.getSignature()) && StringUtils.isBlank(user.getSignature())) {
                IntegralOperationForm integralOperationform = new IntegralOperationForm();
                integralOperationform.setActionId("100");
                integralOperationform.setOperationId(0);
                integralOperationform.setUcId(user.getUcId());
                integralService.addIntegralCounts(integralOperationform);
            }

            user = userDao.selectByPrimaryKey(form.getAutoIncreaseId());
            if (isInfoOk(user)) {
                user.setInfoOk(1);
            }
            user.setUpdateTime(DateUtil.getNowDate(DateUtil.time_pattern));
            userDao.updateByPrimaryKey(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), e.getMessage());
        }
        log.info("updateUserSignature end return:{null}");
        return Result.ok();
    }

    @Override
    public String selectUcIdById(String userId) {
        return userDao.selectById(userId);
    }

    /**
     * 注销用户信息
     *
     * @param form ucid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancellation(AppLogoutForm form) {
        UserEntity user = userDao.selectOne(new QueryWrapper<UserEntity>().eq(org.apache.commons.lang3.StringUtils.isNotBlank(form.getUserId()), "uc_id", form.getUserId()));
        if (user == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "当前用户信息不存在");
        }

        //查询用户是否存在绑定车辆

        List<String> cars = fltCarOwnerMappingDao.queryOwnerCars(user.getId());
        if (CollectionUtils.isNotEmpty(cars) && cars.size() > 0) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "当前账户已绑定车辆，请先解绑！");
        }
        List<String> fltCars = fltFleetCarMapping.queryOwnerCars(user.getId());
        if (CollectionUtils.isNotEmpty(fltCars) && fltCars.size() > 0) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "当前账户已绑定车辆，请先解绑！");
        }

        //修改用户状态为已注销
        user.setStatus(StatusEnum.CANCELLATION.getCode());
        userDao.updateById(user);
        //删除token
        tokenComponent.destroyToken(form.getToken());
        try {
            logService.saveActionListener(
                    new UserListenerLogEntity()
                            .setToken(form.getToken())
                            .setAction(UserListenerEnum.CANCELLATION.getCode())
                            .setAppType(Integer.parseInt(form.getAppType()))
                            .setUcId(form.getUserId())
                            .setVersion(form.getVersion())
                            .setVersionType(Integer.parseInt(StringUtils.isBlank(form.getVersionType()) ? "1" : form.getVersionType()))
            );
        } catch (Exception e) {
            log.info("注销日志存储异常,异常原因:{}", e.getMessage());
        }
    }

    /**
     * 拉黑用户
     * 1、查询是否存在当前户信息
     * 2、更改用户状态为冻结
     * 3、删除token
     *
     * @param form
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freezeUser(AppFreezeForm form) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getPhone, form.getPhone())
                .eq(UserEntity::getAppType, form.getAppType())
                .ne(UserEntity::getStatus, StatusEnum.CANCELLATION.getCode());
        wrapper.last("limit 1");
        UserEntity entity = userDao.selectOne(wrapper);
        if (Objects.isNull(entity)) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "暂无当前用户信息");
        }
        entity.setStatus(StatusEnum.FREEZE.getCode())
                .setRemark(form.getRemark())
                .setUpdateTime(new Date());
        userDao.updateById(entity);
        String preToken = CommonConstants.TOKEN_NAME + ":userToken:" + entity.getUcId();
        String token = (String) redisComponent.get(preToken);
        if (StringUtils.isNotBlank(token)) {
            tokenComponent.destroyToken(token);
        }
    }

    /**
     * 激活用户
     * 1、查询是否存在当前户信息
     * 2、更改用户状态为冻结
     * 3、删除token
     * 4、删除用户发帖记录
     *
     * @param form
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateUser(AppFreezeForm form) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getPhone, form.getPhone())
                .eq(UserEntity::getAppType, form.getAppType())
                .eq(UserEntity::getStatus, StatusEnum.FREEZE.getCode());
        wrapper.last("limit 1");
        UserEntity entity = userDao.selectOne(wrapper);
        integralDao.unfreezeStatus(entity.getUcId());
        if (Objects.isNull(entity)) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "暂无当前用户信息");
        }
        entity.setStatus(StatusEnum.NORMAL.getCode())
                .setRemark(form.getRemark())
                .setUpdateTime(new Date());
        userDao.updateById(entity);
        List<String> redisKeys = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            log.info("bbsKey:{}", bbsKey + entity.getId() + ":" + i);
            redisKeys.add(bbsKey + entity.getId() + ":" + i);
        }
        redisComponent.delList(redisKeys);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freezeByUcId(AppFreezeTokenForm form) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getId, form.getId())
                .ne(UserEntity::getStatus, StatusEnum.CANCELLATION.getCode());
        wrapper.last("limit 1");
        UserEntity entity = userDao.selectOne(wrapper);
        if (Objects.isNull(entity)) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "暂无当前用户信息");
        }
        Integer freezeNum = Objects.isNull(entity.getFreezeNum()) ? 0 : entity.getFreezeNum();
        entity.setStatus(StatusEnum.FREEZE.getCode())
                .setRemark(form.getRemark())
                .setFreezeNum(freezeNum + 1)
                .setUpdateTime(new Date());
        userDao.updateById(entity);
        //设置冻结状态
        UserFreezeConfigEntity userFreezeConfig = userFreezeConfigService.getOne(new LambdaQueryWrapper<UserFreezeConfigEntity>().eq(UserFreezeConfigEntity::getFreezeNum, entity.getFreezeNum()).last("limit 1"));
        if (Objects.nonNull(userFreezeConfig) && StringUtils.isNotBlank(userFreezeConfig.getThawTime())) {
            UserFreezeRecordsEntity recordsEntity = new UserFreezeRecordsEntity();
            recordsEntity.setFreezeTime(new Date());
            recordsEntity.setCreateTime(new Date());
            recordsEntity.setUpdateTime(new Date());
            recordsEntity.setTimeFormat(userFreezeConfig.getTimeFormat());
            recordsEntity.setUcId(entity.getUcId());
            recordsEntity.setFreezeNum(userFreezeConfig.getFreezeNum());
            recordsEntity.setThawTime(userFreezeConfig.getThawTime());
            userFreezeRecordsService.saveOrUpdate(recordsEntity, new LambdaUpdateWrapper<UserFreezeRecordsEntity>().eq(UserFreezeRecordsEntity::getUcId, entity.getUcId()));
        }
        Integral integral = integralDao.integralExist(entity.getUcId());
        if (integral == null) {
            log.info("用户无积分");
        } else {
            integralDao.FreezeStatus(entity.getUcId());
        }
        String preToken = CommonConstants.TOKEN_NAME + ":userToken:" + entity.getUcId();
        String token = (String) redisComponent.get(preToken);
        if (StringUtils.isNotBlank(token)) {
            tokenComponent.destroyToken(token);
        }
    }

    @Override
    public StarSignDto starSign(StarSignForm form) {
        StarSignDto dto = new StarSignDto();
        dto.setPromotion(0);
        dto.setBill(0);
        // 判断账号是否是网红账号
        Integer i = userDao.isStat(form.getAutoIncreaseId().toString());
        log.info("i = {}", i);
        if (i >= 1) {
            dto.setBill(1);
            // 判断账号是否启用（所有协议都禁用才算停用，有一个启用都不算停用）
            i = userDao.isDisable(form.getAutoIncreaseId().toString());
            if (i >= 1) {
                dto.setPromotion(1);
            }
        }
        return dto;
    }

    private boolean isInfoOk(UserEntity userEntity) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getName())) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getUserPicUrl())) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getSignature())) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getSex())) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getInterest())) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getRealName())) {
            return false;
        }
        if (null == userEntity.getBirthday()) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getProvinceDesc())) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getCityDesc())) {
            return false;
        }
        if (null == userEntity.getDrivingAge()) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getEmail())) {
            return false;
        }
        if (null == userEntity.getAnnualIncome()) {
            return false;
        }
        return true;
    }

    /**
     * 校验当前验证码是否达到最大次数
     *
     * @return
     */
    protected boolean checkSmsCode(String phone) {
        // 判断redis
        Long count = redisComponent.incr(RedisConstant.SMS_CODE_CHECK + phone, 1L);
        // key为初始值，需要设置失效时间:10分钟
        if (count == 1L) {
            redisComponent.expire(RedisConstant.SMS_CODE_CHECK + phone, 10L, TimeUnit.MINUTES);
        }
        return count > 5;
    }

    /**
     * 获得Http客户端的ip
     *
     * @param req
     * @return
     */
    private String getHttpClientIp(HttpServletRequest req) {
        String ip = req.getHeader(CommonConstants.HTTP_HEADER_X_FORWARDED_FOR);
        if (ip == null || ip.length() == 0 || CommonConstants.UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = req.getHeader(CommonConstants.HTTP_HEADER_PROXY_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || CommonConstants.UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = req.getHeader(CommonConstants.HTTP_HEADER_WL_PROXY_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || CommonConstants.UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        return ip;
    }

}
