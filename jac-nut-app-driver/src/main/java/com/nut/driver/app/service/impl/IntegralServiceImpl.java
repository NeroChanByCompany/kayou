package com.nut.driver.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.dao.CarDao;
import com.nut.driver.app.dao.IntegralAlterRecordDao;
import com.nut.driver.app.dao.IntegralDao;
import com.nut.driver.app.dao.UserDao;
import com.nut.driver.app.domain.IntegralAlterRecord;
import com.nut.driver.app.domain.NewUserJobResult;
import com.nut.driver.app.dto.IntegralDetailDto;
import com.nut.driver.app.entity.*;
import com.nut.driver.app.form.IntegralOperationForm;
import com.nut.driver.app.form.IntegralOperationFreezeForm;
import com.nut.driver.app.form.QueryIntegralForm;
import com.nut.driver.app.pojo.CarInfoPojo;
import com.nut.driver.app.pojo.CarRolePojo;
import com.nut.driver.app.service.DuibaService;
import com.nut.driver.app.service.IntegralService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service("integralService")
@Slf4j
public class IntegralServiceImpl extends ServiceImpl<IntegralDao, IntegralEntity> implements IntegralService {

    @Autowired
    private IntegralConsumeInfoServiceImpl integralConsumeInfoService;

    @Autowired
    private IntegralDao integralDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CarDao carDao;

    @Autowired
    private IntegralAlterRecordDao integralAlterRecordDao;

    /**
     * 新手任务，每日评论给积分的次数
     */
    public static final int NEW_JOB_3_TIME = 20;

    public static final int LIMIT_4_TIME = 10;
    /**
     * 积分日志
     */
    @Autowired
    private DuibaService duibaService;


    @Value("${midAutumnStart}")
    private String midAutumnStart;

    @Value("${midAutumnEnd}")
    private String midAutumnEnd;

    @Value("${guoQingStart}")
    private String guoQingStart;

    @Value("${guoQingEnd}")
    private String guoQingEnd;


    /*
     * 用户积分添加埋点
     *
     * @param ucId    用户唯一id
     * @param addType 用户添加积分类型
     * @return
     * @throws Exception
     *
    @Override
   public HttpCommandResultWithData addAction(String ucId, Integer addType) throws Exception {
        log.info("IntegralService---addAction:---begin-----");
        log.info("IntegralService---addAction==> Parameters:ucId=" + ucId + "||addType=" + addType);
        HttpCommandResultWithData resultWithData = new HttpCommandResultWithData();
        resultWithData.setResultCode(ECode.SUCCESS.code());
        //查看添加积分的规则

        ScoreTaskRule scoreTaskRule = this.baseMapper.getScoreTaskRule(addType.toString());

        if (scoreTaskRule == null) {
            resultWithData.setResultCode(1023);
            resultWithData.setMessage("未查询到对应添加积分规则");
            log.info("未查询到对应添加积分规则,规则id：" + addType);
            return resultWithData;
        }
        //如果积分规则被禁用直接返回
        if (scoreTaskRule.getAvailFlag() == 2) {
            resultWithData.setMessage("该操作对应积分规则已被禁用，无法添加积分");
            log.info("该操作对应积分规则已被禁用，无法添加积分,规则id：" + addType);
            return resultWithData;
        }
        IntegralOperationForm form = new IntegralOperationForm();

        form.setUcId(ucId);//添加业务id
        form.setOperationId(0);
        form.setIntegralCounts(scoreTaskRule.getAddScore());
        form.setActionId(addType.toString());
        resultWithData = addIntegralCounts(form);

        return resultWithData;
    }*/

    /**
     * 积分增加操作
     *
     * @param form
     * @return HttpCommandResultWithData
     */
    public Integral addIntegralCounts(IntegralOperationForm form) {

        //查看添加积分的规则

        ScoreTaskRule scoreTaskRule = this.baseMapper.getScoreTaskRule(form.getActionId());

        if (scoreTaskRule == null) {
            ExceptionUtil.result(1023, "未查询到对应添加积分规则");
        }
        //如果积分规则被禁用直接返回
        if (scoreTaskRule.getAvailFlag() == 2) {
            ExceptionUtil.result(1023, "该操作对应积分规则已被禁用，无法添加积分");
        }
        //验证该用户是否存在积分
        Integral findIntegral = this.baseMapper.integralExist(form.getUcId());

        String integralItem = "";

        if (findIntegral == null) {
            //创建积分用户
            log.info("无对应积分用户，执行新增积分用户");
            createIntegral(form);
            log.info("执行新增积分结束：{}",form);
            integralItem = form.getActionId() == "" ? "注册" : form.getActionId();
        } else {
            //用户存在添加积分操作
            if (findIntegral.getIntegralState() == 1) {
                ExceptionUtil.result(1023, "用户积分被冻结");
            } else {
                //添加用户积分
                if (form.getActionId() != null && form.getIntegralCounts() == null) {
                    scoreTaskRule = this.baseMapper.getScoreTaskRule(form.getActionId());
                    form.setIntegralCounts(scoreTaskRule.getAddScore());
                } else if (form.getActionId() != null && form.getIntegralCounts() != null) {
                    if (form.getIntegralCounts() <= 0) {
                        ExceptionUtil.result(ECode.INTEGRAL_INVALID_ERROR.code(), ECode.INTEGRAL_INVALID_ERROR.message());
                    }
                    form.setIntegralCounts(form.getIntegralCounts());
                }

                addIntegral(findIntegral, form);
                integralItem = form.getActionId();
            }
        }
        /**
         * 创建/添加积分记录日志
         */
        IntegralAlterRecordEntity integralAlterRecord = new IntegralAlterRecordEntity();
        integralAlterRecord.setUid(form.getUcId());
        integralAlterRecord.setCredits(form.getIntegralCounts() + "");
        integralAlterRecord.setType("1");
        if (NumberUtils.isNumber(integralItem)) {
            Long iiL = Long.parseLong(integralItem);
            if (iiL >= 1000 && iiL < 1010) {
                integralItem = "兑吧虚拟商品消费，商品ID: " + integralItem;
            }
        }
        if (Objects.nonNull(findIntegral.getIntegralCounts())) {
            integralAlterRecord.setBalance(findIntegral.getIntegralCounts() + form.getIntegralCounts());
        }
        integralAlterRecord.setIntegralItem(integralItem);
        integralAlterRecord.setIntegralResource(scoreTaskRule.getPointDistribution() + "");
        integralAlterRecord.setCreateTime(new Date());
        integralAlterRecord.setOrderNum(form.getOrderNum());
        integralAlterRecordDao.insertIntegralAlterRecord(integralAlterRecord);
        findIntegral = this.baseMapper.integralExist(form.getUcId());//查询并返回添加后的积分
        return findIntegral;
    }

    /**
     * 减少操作
     *
     * @param form
     * @return HttpCommandResultWithData
     */
    @SneakyThrows
    @Transactional
    public Integral subtractionIntegralCounts(IntegralOperationForm form) {
        //验证该用户是否存在积分
        Integral findIntegral = integralDao.integralExist(form.getUcId());
        if (form.getIntegralCounts() == null || form.getIntegralCounts() <= 0) {
            ExceptionUtil.result(ECode.INTEGRAL_INVALID_ERROR.code(), ECode.INTEGRAL_INVALID_ERROR.message());
        }
        if (findIntegral == null) {
            ExceptionUtil.result(1022, "用户积分不存在");
        } else {
            //用户存在添加积分操作
            if (findIntegral.getIntegralState() == 1) {
                //用户积分被冻结，禁止操作
                ExceptionUtil.result(1021, "用户积分被冻结");
            } else {
                //用户积分扣除操作
                subIntegral(findIntegral, form);
                /**
                 * 减少积分记录日志
                 */
                IntegralAlterRecordEntity integralAlterRecord = new IntegralAlterRecordEntity();
                integralAlterRecord.setUid(form.getUcId());
                integralAlterRecord.setCredits(form.getIntegralCounts().toString());
                if ("29".equals(form.getActionId())) {
                    form.setActionId("积分退换");
                }
                if (StringUtils.isNotBlank(form.getOrderNum())) {
                    integralAlterRecord.setOrderNum(form.getOrderNum());
                }
                if (Objects.nonNull(findIntegral.getIntegralCounts())) {
                    integralAlterRecord.setBalance(findIntegral.getIntegralCounts() - form.getIntegralCounts());
                }
                integralAlterRecord.setIntegralItem(form.getActionId());//积分操作动作
                integralAlterRecord.setIntegralResource("3");
                integralAlterRecord.setCreateTime(new Date());
                integralAlterRecord.setType("0");
                if (form.getActionId().equals("8")) {
                    integralAlterRecord.setOrderNum(form.getConsumeOrder());
                }
                log.info("减少用户积分 form :{}", integralAlterRecord);
                integralAlterRecordDao.insertIntegralAlterRecord(integralAlterRecord);

            }
        }
        findIntegral = integralDao.integralExist(form.getUcId());//查询并返回扣除后的积分
        return findIntegral;
    }

    @Override
    public HttpCommandResultWithData addAction(String ucId, Integer addType) throws Exception {
        log.info("IntegralService---addAction:---begin-----");
        log.info("IntegralService---addAction==> Parameters:ucId=" + ucId + "||addType=" + addType);
        HttpCommandResultWithData resultWithData = new HttpCommandResultWithData();
        resultWithData.setResultCode(ECode.SUCCESS.code());
        resultWithData.setMessage(ECode.SUCCESS.message());
        //查看添加积分的规则

        ScoreTaskRule scoreTaskRule = integralDao.getScoreTaskRule(addType.toString());

        if (scoreTaskRule == null) {
            resultWithData.setResultCode(1023);
            resultWithData.setMessage("未查询到对应添加积分规则");
            log.info("未查询到对应添加积分规则,规则id：" + addType);
            return resultWithData;
        }
        //如果积分规则被禁用直接返回
        if (scoreTaskRule.getAvailFlag() == 2) {
            resultWithData.setMessage("该操作对应积分规则已被禁用，无法添加积分");
            log.info("该操作对应积分规则已被禁用，无法添加积分,规则id：" + addType);
            return resultWithData;
        }
        IntegralOperationForm command = new IntegralOperationForm();
        command.setUcId(ucId);//添加业务id
        command.setOperationId(0);
        command.setIntegralCounts(scoreTaskRule.getAddScore());
        command.setActionId(addType.toString());
        resultWithData = addIntegralCounts1(command);
        log.info("IntegralService---addAction==>Result:" + JsonUtil.toJson(resultWithData));
        log.info("IntegralService---addAction:---end-----");
        return resultWithData;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData addIntegralCounts1(IntegralOperationForm command) throws Exception {
        log.info("IntegralService---addIntegralCounts:---begin-----");
        log.info("IntegralService---addIntegralCounts ==> Parameters:" + JsonUtil.toJson(command));

        HttpCommandResultWithData resultWithData = new HttpCommandResultWithData();
        resultWithData.setResultCode(ECode.SUCCESS.code());
        resultWithData.setMessage(ECode.SUCCESS.message());

        //查看添加积分的规则

        ScoreTaskRule scoreTaskRule = integralDao.getScoreTaskRule(command.getActionId());

        if (scoreTaskRule == null) {
            resultWithData.setMessage("未查询到对应添加积分规则");
            log.info("未查询到对应添加积分规则,规则id：" + command.getActionId());
            return resultWithData;
        }
        //如果积分规则被禁用直接返回
        if (scoreTaskRule.getAvailFlag() == 2) {
            resultWithData.setMessage("该操作对应积分规则已被禁用，无法添加积分");
            log.info("该操作对应积分规则已被禁用，无法添加积分,规则id：" + command.getActionId());
            return resultWithData;
        }

        //验证该用户是否存在积分
        Integral findIntegral = integralDao.integralExist(command.getUcId());

        String integralItem = "";

        if (findIntegral == null) {
            //创建积分用户
            log.info("无对应积分用户，执行新增积分用户");
            createIntegral(command);
            log.info("执行新增积分结束：{}",command);
            integralItem = command.getActionId() == "" ? "注册" : command.getActionId();
        } else {
            //用户存在添加积分操作
            if (findIntegral.getIntegralState() == 1) {
                //用户积分被冻结，禁止操作
                resultWithData.setMessage("用户积分被冻结");
                log.info("IntegralService---addIntegralCounts==>用户积分被冻结:" + JsonUtil.toJson(resultWithData));
                return resultWithData;
            } else {
                //添加用户积分
                if (command.getActionId() != null && command.getIntegralCounts() == null) {
                    scoreTaskRule = integralDao.getScoreTaskRule(command.getActionId());
                    command.setIntegralCounts(scoreTaskRule.getAddScore());
                } else if (command.getActionId() != null && command.getIntegralCounts() != null) {
                    command.setIntegralCounts(command.getIntegralCounts());
                }

                addIntegral(findIntegral, command);
                integralItem = command.getActionId();
            }
        }

        /**
         * 创建/添加积分记录日志
         */
        IntegralAlterRecord integralAlterRecord = new IntegralAlterRecord();
        integralAlterRecord.setUid(command.getUcId());
        integralAlterRecord.setCredits(command.getIntegralCounts() + "");
        integralAlterRecord.setType("1");
        if (NumberUtils.isNumber(integralItem)) {
            Long iiL = Long.parseLong(integralItem);
            if (iiL >= 1000 && iiL < 1010) {
                integralItem = "兑吧虚拟商品消费，商品ID: " + integralItem;
            }
        }
        if (Objects.nonNull(findIntegral) && Objects.nonNull(findIntegral.getIntegralCounts())) {
            integralAlterRecord.setBalance(findIntegral.getIntegralCounts() + command.getIntegralCounts());
        }
        integralAlterRecord.setIntegralItem(integralItem);
        integralAlterRecord.setIntegralResource(scoreTaskRule.getPointDistribution() + "");
        integralAlterRecord.setCreateTime(new Date());
        integralAlterRecord.setOrderNum(command.getOrderNum());
        duibaService.insertIntegralAlterRecord(integralAlterRecord);


        findIntegral = integralDao.integralExist(command.getUcId());//查询并返回添加后的积分
        resultWithData.setData(findIntegral);
        log.info("IntegralService---addIntegralCounts==>Result:" + JsonUtil.toJson(resultWithData));
        log.info("IntegralService---addIntegralCounts:---end-----");
        return resultWithData;
    }

    /**
     * 减少积分操作
     *
     * @param form
     */
    private void subIntegral(Integral findIntegral, IntegralOperationForm form) {
        log.info("[subIntegral]start");
        Integral integral = new Integral();
        integral.setUcId(form.getUcId());
        integral.setIntegralCounts(form.getIntegralCounts());//记录扣除后积分总数
        integral.setUpdateTime(new Date());//记录更新时间
        integral.setLastIntegralCounts(findIntegral.getIntegralCounts());//记录本次扣除积分后的积分总数
        integral.setLastIntegralOperatedCounts(form.getIntegralCounts());//记录本次扣除多少积分
        integral.setLastOperateType(2);//记录本次操作的操作类型--1扣除积分
//        integralDao.updateIntegralCounts(integral);
        integralDao.updateSubIntegralCounts(integral);
        log.info("[subIntegral]end");
    }

    /**
     * 创建积分用户
     *
     * @param form
     */
    private void createIntegral(IntegralOperationForm form) {
        log.info("[createIntegral]start");
        Integral integral = new Integral();
        integral.setUcId(form.getUcId());
        if (form.getActionId() != null && form.getIntegralCounts() == null) {
            ScoreTaskRule scoreTaskRule = this.baseMapper.getScoreTaskRule(form.getActionId());
            integral.setIntegralCounts(scoreTaskRule.getAddScore());
            form.setIntegralCounts(scoreTaskRule.getAddScore());
        } else if (form.getActionId() != null && form.getIntegralCounts() != null) {
            integral.setIntegralCounts(form.getIntegralCounts());
        }
        integral.setCreateTime(new Date());
        integral.setLastIntegralCounts(0);//初始积分上次余额为0
        integral.setLastIntegralOperatedCounts(0);//初始积分上次操作数量为0
        integral.setLastOperateType(4);
        integral.setIntegralState(0);//初始积分状态为可用
        String phone = this.baseMapper.queryPhoneByUcId(form.getUcId());
        integral.setPhone(phone);
        this.baseMapper.createIntegral(integral);
        log.info("[createIntegral]end");
    }

    /**
     * 添加积分操作
     *
     * @param form
     */
    private void addIntegral(Integral findIntegral, IntegralOperationForm form) {
        log.info("[addIntegral]start");
        Integral integral = new Integral();
        integral.setUcId(form.getUcId());
        integral.setIntegralCounts(form.getIntegralCounts());//记录添加后积分总数
        integral.setUpdateTime(new Date());//记录更新时间
        integral.setLastIntegralCounts(findIntegral.getIntegralCounts());//记录本次添加积分前的积分总数
        integral.setLastIntegralOperatedCounts(form.getIntegralCounts());//记录本次添加多少积分
        integral.setLastOperateType(0);//记录本次操作的操作类型--0添加积分
//        this.baseMapper.updateIntegralCounts(integral);
        this.baseMapper.updateAddIntegralCounts(integral);
        log.info("[addIntegral]end");
    }

    /**
     * 查看用户积分
     *
     * @param form
     * @return
     */
    public IntegralDetailDto queryIntegral(QueryIntegralForm form) {

        //验证该用户是否存在积分
        Integral findIntegral = integralDao.integralExist(form.getUserId());

        IntegralDetailDto integralDetailDto = new IntegralDetailDto();

        if (findIntegral == null) {
            integralDetailDto.setIntegralCounts(0);
        } else {
            integralDetailDto.setId(findIntegral.getId());
            integralDetailDto.setUcId(findIntegral.getUcId());
            integralDetailDto.setIntegralCounts(findIntegral.getIntegralCounts());
            integralDetailDto.setIntegralState(findIntegral.getIntegralState());
            if (findIntegral.getUpdateTime() == null) {
                integralDetailDto.setUpdateTime(findIntegral.getCreateTime().getTime());
            } else {

                integralDetailDto.setUpdateTime(findIntegral.getUpdateTime().getTime());
            }
        }
        log.info("queryIntegral end return:{}", integralDetailDto);
        return integralDetailDto;
    }

    public NewUserJobResult newUserJob(QueryIntegralForm form) {
        NewUserJobResult data = new NewUserJobResult();
        String userId = form.getUserId();
        UserEntity byUcId = userDao.findByUcId(userId);
        if (byUcId.getInfoOk() == null) {
            data.userInfo = false;
        } else if (byUcId.getInfoOk() == 1) {
            data.userInfo = true;
        } else {
            data.userInfo = false;
        }
        data.carInfo = getCarInfo(form);
        data.forumPost = false;  // 不限制
        data.forumComment = integralAlterRecordDao.countByUserIdAndItemAndToday(userId, "3") >= NEW_JOB_3_TIME;
        log.info("newUserJob end return:{}", data);
        return data;
    }

    private boolean getCarInfo(QueryIntegralForm form) {
        // 查询相关车辆ID
        List<CarRolePojo> carRelations = carDao.selectUserRelatedCar(form.getAutoIncreaseId());
        if (carRelations.isEmpty()) {
            return false;
        }

        // 全部车辆ID
        List<String> carIds = carRelations.stream().map(CarRolePojo::getCarId).distinct().collect(Collectors.toList());

        // 查询车辆基础信息
        List<CarInfoPojo> cars = carDao.selectByCarIdIn(carIds);
        for (CarInfoPojo c : cars) {
            if (c.getExtInfoOk() != null && c.getExtInfoOk() == 1) {
                return true;
            }
        }
        return false;
    }

    public Map queryIntegralHistory(QueryIntegralForm form) {
        Map map = new HashMap(5);
        // 分页查询
        if (Integer.valueOf(form.getPage_number()) >= 0 && Integer.valueOf(form.getPage_size()) > 0) {
            // 设置分页参数
            PageHelper.startPage(Integer.valueOf(form.getPage_number()), 999999);
        }
        Page<IntegralAlterRecordEntity> data = integralAlterRecordDao.findByUserId(form.getUserId());
        PagingInfo<IntegralAlterRecordEntity> resultDto = this.convertPagingToPage(data);
        map.put("total", resultDto.getTotal());
        map.put("page_total", resultDto.getPage_total());
        map.put("list", resultDto.getList());
        log.info("queryIntegralHistory end return:{}", map);
        return map;
    }

    private <E> PagingInfo<E> convertPagingToPage(Page<E> page) {
        PagingInfo<E> result = new PagingInfo<E>();
        result.setList(page.getResult());
        result.setPage_total(page.getPages());
        result.setTotal(page.getResult().size());
        return result;
    }

    public Integral integralOperation(IntegralOperationForm form) {

        Integral findIntegral = new Integral();
        if (StringUtil.isNotEmpty(form.getUserId())) {
            form.setUcId(form.getUserId());
        }

        //增加发帖10个，评论20个限制，需求为限制用户每日获取发帖100积分，评论100积分,覆盖新手任务
        if (form.getOperationId() == 0 && form.getUcId() != null && ("3".equals(form.getActionId()))) {
            // ---进行新手任务每天10次评论和发帖的检查
            // 3是评论
            int count = integralAlterRecordDao.countByUserIdAndItemAndToday(form.getUcId(), form.getActionId());
            if (count >= NEW_JOB_3_TIME) {

                findIntegral = integralDao.integralExist(form.getUserId());//查询并返回添加后的积分
                return findIntegral;
            }
        }

        if (form.getOperationId() == 0 && form.getUcId() != null && ("4".equals(form.getActionId()))) {
            // 4是发帖
            int count = integralAlterRecordDao.countByUserIdAndItemAndToday(form.getUcId(), form.getActionId());
            if (count >= LIMIT_4_TIME) {
                findIntegral = integralDao.integralExist(form.getUserId());//查询并返回添加后的积分
                return findIntegral;
            }
        }


        try {
            switch (form.getOperationId()) {
                case 0:
                    log.info("进入了积分增加操作");
                    findIntegral = this.addIntegralCounts(form);  // 添加基础的积分
                    /**
                     * 添加活动积分 时间2021/09/20-2021/09/26
                     */
                    log.info("用户参加中秋活动，添加积分");
                    switch (Integer.parseInt(form.getActionId())) {
                        case 21:
                            this.midAutumnByRuleId(form.getUcId(), 73);
                            this.FestivalByRuleId(form.getUcId(),83,guoQingStart,guoQingEnd);
                            log.info("用户活动发帖");
                            break;
                        case 4:
                            this.midAutumnByRuleId(form.getUcId(), 73);
                            this.FestivalByRuleId(form.getUcId(),83,guoQingStart,guoQingEnd);
                            log.info("用户发帖");
                            break;
                        default:
                            break;
                    }

                    break;
                case 1:
                    log.info("进入了积分减少操作");
                    findIntegral = this.subtractionIntegralCounts(form);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("积分操作异常原因：{}", e.toString());
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), form.getOperationId() == 0 ? "添加积分失败，请稍后重试" : "扣除积分失败，请稍后重试");
        }
        log.info("integralOperation end return:{}", findIntegral);
        return findIntegral;
    }


    @Override
    @SneakyThrows
    @Transactional
    public HttpCommandResultWithData midAutumnByRuleId(String ucId, Integer ruleId) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(midAutumnStart, sdf);
        LocalDateTime endDate = LocalDateTime.parse(midAutumnEnd, sdf);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowMax = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime nowMin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        log.info("进入中秋活动验证，当前时间：{}", sdf.format(LocalDateTime.now()));
        log.info("中秋活动时间有效期：2021-09-20 00:00:00  ~  2021-09-26 23:59:59");
        log.info("{}", startDate.isBefore(now) && endDate.isAfter(now));
        // 判断时间是否处于两者之间
        if (startDate.isBefore(now) && endDate.isAfter(now)) {
            // 判断今天执行过此积分规则的操作
            Integer i = integralAlterRecordDao.countByRuleId(ucId, ruleId, nowMin, nowMax);
            log.info("今天添加活动积分次数：{}", i);
            // 未执行
            if (i == 0) {
                result = addAction(ucId, ruleId);
                log.info("活动积分添加结束");
            } else {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("今天已执行过此规则");
            }
            return result;
        } else {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            if (startDate.isAfter(now)) {
                result.setMessage("活动未开始");
            } else {
                result.setMessage("活动已结束");
            }
            return result;
        }
    }


    @Override
    @SneakyThrows
    @Transactional
    public HttpCommandResultWithData FestivalByRuleId(String ucId, Integer ruleId, String startTime, String endTime) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(startTime, sdf);
        LocalDateTime endDate = LocalDateTime.parse(endTime, sdf);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowMax = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime nowMin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        log.info("进入活动功能验证，当前时间：{}", sdf.format(LocalDateTime.now()));
        log.info("当前活动活动时间有效期：" + startTime + "  ~ " + endTime);
        log.info("{}", startDate.isBefore(now) && endDate.isAfter(now));
        // 判断时间是否处于两者之间
        if (startDate.isBefore(now) && endDate.isAfter(now)) {
            // 判断今天执行过此积分规则的操作
            Integer i = integralAlterRecordDao.countByRuleId(ucId, ruleId, nowMin, nowMax);
            log.info("今天添加活动积分次数：{}", i);
            // 未执行
            if (i == 0) {
                result = addAction(ucId, ruleId);
                log.info("活动积分添加结束");
            } else {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("今天已执行过此规则");
            }
            return result;
        } else {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            if (startDate.isAfter(now)) {
                result.setMessage("活动未开始");
            } else {
                result.setMessage("活动已结束");
            }
            return result;
        }
    }

    @Override
    @SneakyThrows
    @Transactional
    public void integralOperationFreeze(IntegralOperationFreezeForm form) {
        LocalDateTime nowMax = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime nowMin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        String ucId = userDao.findUcId(form.getAutoIncreaseId());
        // 当日 评论/发帖 操作获取的积分数值
        Integer integralCount = integralAlterRecordDao.integralCount(ucId,nowMin,nowMax);
        if (integralCount != null){
            // 用户总积分
            Integer integralAll = integralDao.integralExist(ucId).getIntegralCounts();
            log.info("**********总积分减去**********");
            log.info("对用户：{} 进行积分冻结退换操作 Start", ucId);
            Integral integral = new Integral();
            integral.setUcId(ucId);
            if (integralAll > integralCount){
                integral.setIntegralCounts(integralAll - integralCount);
            }else {
                integral.setIntegralCounts(0);
            }
            integral.setUpdateTime(new Date());
            integral.setLastIntegralCounts(integralAll);//记录本次扣除积分后的积分总数
            integral.setLastIntegralOperatedCounts(integralCount);//记录本次扣除多少积分
            integral.setLastOperateType(2);
            integralDao.updateIntegralCounts(integral);
            log.info("对用户：{} 进行积分冻结退换操作 End", ucId);
            log.info("**********积分减去记录**********");
            IntegralAlterRecordEntity integralAlterRecord = new IntegralAlterRecordEntity();
            integralAlterRecord.setUid(ucId);
            if (integralAll > integralCount){
                integralAlterRecord.setBalance(integralAll - integralCount);
                integralAlterRecord.setCredits(integralCount.toString());
            }else {
                integralAlterRecord.setBalance(0);
                integralAlterRecord.setCredits(integralAll.toString());
            }
            integralAlterRecord.setType("0");
            integralAlterRecord.setIntegralItem("1100");
            integralAlterRecord.setIntegralResource("3");
            integralAlterRecord.setCreateTime(new Date());
            integralAlterRecordDao.insertIntegralAlterRecord(integralAlterRecord);
            // 当次冻结积分标识
            integralAlterRecordDao.updateFreeze(ucId,nowMin,nowMax);
        }else {

        }
    }
}
