package com.nut.driver.app.service.impl;/**
 * Created by Administrator on 2021/11/24.
 */

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.Page;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.result.Result;
import com.nut.driver.app.dao.CarDao;
import com.nut.driver.app.dao.OrderDao;
import com.nut.driver.app.dao.PayOrderDao;
import com.nut.driver.app.dao.SimCardPlaDao;
import com.nut.driver.app.dto.SimCardPlaDto;
import com.nut.driver.app.entity.CarEntity;
import com.nut.driver.app.entity.OrderEntity;
import com.nut.driver.app.entity.PayOrderEntity;
import com.nut.driver.app.form.BuySetMealForm;
import com.nut.driver.app.form.InsertOrderForm;
import com.nut.driver.app.form.SimCardPlaForm;
import com.nut.driver.app.pojo.SetMealPojo;
import com.nut.driver.app.service.SimCardPlaService;
import com.nut.driver.app.vo.InsertOrderVo;
import com.nut.driver.app.vo.SimOrderVo;
import com.nut.driver.common.config.CMBConfig;
import com.nut.driver.common.config.SimFlowConfig;
import com.nut.driver.common.em.DictEnum;
import com.nut.driver.common.utils.HttpUtil;
import com.nut.driver.common.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @version v1.0.0
 * @desc
 * @auther wangshuai
 * @create 2021/11/24
 * @Company esv
 */
@Service
@Slf4j
public class SimCardPlaServiceImpl  implements SimCardPlaService {

    @Autowired
    SimCardPlaDao simCardPlaDao;
    @Autowired
    OrderDao orderDao;
    @Autowired
    PayOrderDao payOrderDao;
    @Resource
    private CMBConfig cmbConfig;
    @Resource
    private SimFlowConfig simFlowConfig;
    @Resource
    private CarDao carDao;

    public static final String MEALS_STATUS_ON = "100202";
    public static final String MEALS_PAGE_URL = "/api/nsc/simflow/iotSetMeal/iotSetMeals/page";
    public static final String CHECK_ORDER_URL = "/api/nsc/simflow/iotDataUsage/orderPlaceChecking";
    public static final String FIND_LEFTFLOW_URL = "/api/nsc/simflow/iotDataUsage/findResidualFlowTotal";
    public static final String FIND_FLOW_LIMIT_URL = "/api/nsc/simflow/iotDataUsage/findFlowLimit";

    /**
     *@Auther wangshuai
     *@Description 可用流量包查询
     *@param
     *@return
     */
    @Override
    public HttpCommandResultWithData query(SimCardPlaForm form) throws IOException {


        // 设置请求参数,默认设置为查询全部的
        String params = "?pageNum=0&pageSize=9999&keyword&status="+MEALS_STATUS_ON;
        String strResult = HttpUtil.get(simFlowConfig.simservicerootpath+ MEALS_PAGE_URL + params, "", simFlowConfig.getMap());
        log.info("MEALS_PAGE_URL result={}",strResult);
        JSONObject jsonObject = JSONObject.parseObject(strResult);
        JSONObject data = JSONObject.parseObject(jsonObject.get("data").toString());
        JSONArray detaildata = JSONObject.parseArray(data.get("data").toString());

        List<Map> lsm = new ArrayList<>();
        if(!data.isEmpty()){
            ArrayList<SetMealPojo> setMealList  = JSON.parseObject(detaildata.toString(), new TypeReference<ArrayList<SetMealPojo>>(){});
            List<SetMealPojo> addMealList = new ArrayList<>(); //加油包
            List<SetMealPojo> common2MealList = new ArrayList<>();//基础包2年20G
            List<SetMealPojo> common6MealList = new ArrayList<>();//基础包6年2G
            List<SetMealPojo> common8MealList = new ArrayList<>();//基础8年2G

            for(SetMealPojo setMealPojo:setMealList){
                //加油包
                if("jyb".equals(setMealPojo.getSetMealType())){
                    addMealList.add(setMealPojo);
                }
                //基础包2年20G
                else if("jcb2".equals(setMealPojo.getSetMealType())){
                    common2MealList.add(setMealPojo);
                }
                //基础包6年2G
                else if("jcb6".equals(setMealPojo.getSetMealType())){
                    common6MealList.add(setMealPojo);
                }
                //基础8年2G
                else if("jcb8".equals(setMealPojo.getSetMealType())){
                    common8MealList.add(setMealPojo);
                }
            }
            if(addMealList.size()>0){
                Map<String,Object> addMealMap = new HashMap<>();
                addMealMap.put("mealType",addMealList.get(0).getSetMealTypeName());
                addMealMap.put("mealList",addMealList);
                lsm.add(addMealMap);
            }
            if(common2MealList.size()>0){
                Map<String,Object> common2MealMap = new HashMap<>();
                common2MealMap.put("mealType",common2MealList.get(0).getSetMealTypeName());
                common2MealMap.put("mealList",common2MealList);
                lsm.add(common2MealMap);
            }
            if(common6MealList.size()>0){
                Map<String,Object> common6MealMap = new HashMap<>();
                common6MealMap.put("mealType",common6MealList.get(0).getSetMealTypeName());
                common6MealMap.put("mealList",common6MealList);
                lsm.add(common6MealMap);
            }
            if(common8MealList.size()>0){
                Map<String,Object> common8MealMap = new HashMap<>();
                common8MealMap.put("mealType",common8MealList.get(0).getSetMealTypeName());
                common8MealMap.put("mealList",common8MealList);
                lsm.add(common8MealMap);
            }

        }

        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        result.setData(lsm);
        return result;
    }

    /**
     *@Auther wangshuai
     *@Description 获取当前用户车辆表
     *@param
     *@return
     */
    @Override
    public HttpCommandResultWithData getUserCar(SimCardPlaForm form) {

        List<SimCardPlaDto> lsc= simCardPlaDao.getUserCar(form.getUserId());
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        result.setData(lsc);
        return result;
    }

    /**
     *@Auther wangshuai
     *@Description 购买相关套餐
     *@param
     *@return
     */
    @Transactional(rollbackFor=Exception.class)
    @Override
    public HttpCommandResultWithData buySetMeal(BuySetMealForm form) throws IOException {
        /**1、验证是否可下订；否，弹窗提示“该车辆不支持购买此套餐”。*/
        //处理参数
        Map<String,Object> mappa =new HashMap<String,Object>();
        mappa.put("iotCardNumber",form.getSimNum());
        mappa.put("iotSetMealId",form.getPacId());
        String pa = JSON.toJSONString(mappa);
        //获取返回值
        Map<String, Object> result = HttpUtil.postForEntity(simFlowConfig.simservicerootpath+ CHECK_ORDER_URL , pa, simFlowConfig.getMap());
        String code = Objects.isNull(result.get("code")) ? "" : result.get("code").toString();
        if(null == result.get("data") || !"10000".equals(code)){
            return Result.result(ECode.NO_DATA.code(),"该车辆不支持购买此套餐!");
        }
        /**2、查一下对应车辆流量上限，上限为2G的车，不支持用户购买新流量套餐，提示”该车辆不支持购买此套餐”*/
        String params = "?iotCardNumber="+form.getSimNum();
        try {
            String strResult = HttpUtil.get(simFlowConfig.simservicerootpath+ FIND_FLOW_LIMIT_URL + params, "", simFlowConfig.getMap());
            JSONObject jsonObject = JSONObject.parseObject(strResult);
            code = Objects.isNull(jsonObject.get("code")) ? "" : jsonObject.get("code").toString();
            if(null == jsonObject.get("data") || !"10000".equals(code)){
                return Result.result(ECode.NO_DATA.code(),"该车辆不支持购买此套餐!");
            }
        } catch (IOException e) {
            log.error("底盘号为{}的车辆，当月物联卡流量上限查询失败 e={}",form.getSimNum(),e);
            return Result.result(ECode.NO_DATA.code(),"该车辆不支持购买此套餐!");
        }

        /**3、创建订单：进入购买页面，开始购买*/
        return this.insertOrder(form);
    }

    private HttpCommandResultWithData insertOrder(BuySetMealForm form) {
        /**初始化数据*/
        String ucId = form.getUserId();//用户ucid
        Long userId = form.getAutoIncreaseId();//用户的主键id
        BigDecimal primeCost = BigDecimal.ZERO;//总原价
        BigDecimal discountCost = BigDecimal.ZERO;//总优惠价
        BigDecimal realCost = BigDecimal.ZERO;//真实付款价格
        String orderStatus = DictEnum.ORDER_STATUS_WAIT_PAY.getCode();//订单状态
        Integer serviceFeePercentage = 2;//服务费百分比
        String payOrderNumber = UUIDUtils.getUuid(UUIDUtils.UUID_FLAG_PAY_ORDER_ID);//支付订单号
        String orderNumber = UUIDUtils.getUuid(UUIDUtils.UUID_FLAG_ORDER_ID);//订单号


        /**保存订单*/
        realCost = form.getPacPrice();
        form.setUserId(null);
        OrderEntity orderEntity = BeanUtil.copyProperties(form,OrderEntity.class);
        orderEntity.setRealCost(realCost.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.valueOf(0.01) : realCost);
        orderEntity.setOrderNumber(orderNumber);//订单编号
        orderEntity.setPayOrderNumber(payOrderNumber);//支付订单编号
        orderEntity.setUserId(userId);//用户主键id
        orderEntity.setUcId(ucId);//用户ucid,32位
        orderEntity.setOrderStatus(orderStatus);//订单状态
        //查询车牌号
        CarEntity carEntity = this.carDao.getByAutoTerminal(form.getSimNum());
        String carNumber = Objects.isNull(carEntity) ? "" : carEntity.getCarNumber();
        orderEntity.setCarNumber(carNumber);
        orderDao.insert(orderEntity);

        /**保存支付订单*/
        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setPayOrderNumber(payOrderNumber);
        payOrderEntity.setUserId(userId);
        payOrderEntity.setUcId(ucId);
        payOrderEntity.setPrimeCost(primeCost);
        payOrderEntity.setOrderTime(new Date());
        realCost = realCost.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.valueOf(0.01) : realCost;
        payOrderEntity.setRealCost(realCost);
        BigDecimal serviceFee = serviceFeePercentage == null ? null : realCost.multiply(new BigDecimal(serviceFeePercentage.doubleValue() / 100)).setScale(2, BigDecimal.ROUND_HALF_UP);//服务费=实际付款 * 服务费百分比
        payOrderEntity.setServiceFee(serviceFee);
        payOrderEntity.setServiceFeePercentage(serviceFeePercentage);
        payOrderEntity.setMerId(cmbConfig.MER_ID);
        payOrderEntity.setMerUserId(cmbConfig.USER_ID);
        payOrderDao.insert(payOrderEntity);

        //返回结果参数
        InsertOrderVo insertOrderVo = new InsertOrderVo();
        insertOrderVo.setPayOrderNumber(payOrderNumber);

        insertOrderVo.setRealCost(realCost.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString());
        return Result.ok(insertOrderVo);
    }

    /**
     *@Auther wangshuai
     *@Description 获取当前车辆的剩余流量
     *@param
     *@return
     */
    @Override
    public HttpCommandResultWithData getSimLeftMeals(SimCardPlaForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());



        List<SimCardPlaDto> lsc= simCardPlaDao.getUserCar(form.getUserId());
        if(null!=lsc&&lsc.size()>0){
            for(SimCardPlaDto simCardPlaDto : lsc){
                // 设置请求参数,默认设置为查询全部的
                String params = "?iotCardId="+simCardPlaDto.getAutoTerminal();
                String strResult = null;
                try {
                    strResult = HttpUtil.get(simFlowConfig.simservicerootpath+ FIND_LEFTFLOW_URL + params, "", simFlowConfig.getMap());
                    JSONObject jsonObject = JSONObject.parseObject(strResult);
                    if(null!=jsonObject.get("data")){
                        simCardPlaDto.setSimLeftMeals(jsonObject.get("data").toString());
                    }else{
                        simCardPlaDto.setSimLeftMeals("");
                    }
                } catch (IOException e) {
                    log.error("getSimLeftMeals params={} e",params,e);
                    simCardPlaDto.setSimLeftMeals("");
                    log.info("底盘号为{}的车辆，剩余流量查询失败",simCardPlaDto.getChassisNumber());
                }


            }
        }

        result.setData(lsc);
        return result;
    }


    /**
     *@Auther wangshuai
     *@Description 获取订单列表
     *@param
     *@return
     */
    @Override
    public HttpCommandResultWithData getOrderList(SimCardPlaForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        DriverBaseService.getPage(form);
        // 查询数据库
        Page<SimOrderVo> queryList = simCardPlaDao.getOrderList(form.getUserId());
        log.info("[query]queryList.size:{}", queryList.size());

        PagingInfo<SimOrderVo> pagingInfo = new PagingInfo<>();

        pagingInfo.setTotal(queryList.getTotal());
        pagingInfo.setPage_total(queryList.getPages());
        pagingInfo.setList(doChange(queryList));

        result.setData(pagingInfo);
        log.info("fleetAdmins end return:{}", pagingInfo);
        return result;

    }

    /**
     *@Auther wangshuai
     *@Description 基于sim卡号进行剩余流量查询
     *@param
     *@return
     */
    @Override
    public HttpCommandResultWithData getLeftMealBySimnum(SimCardPlaForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        String params = "?iotCardId="+form.getSimNumber();
        String strResult = null;
        try {
            strResult = HttpUtil.get(simFlowConfig.simservicerootpath+ FIND_LEFTFLOW_URL + params, "", simFlowConfig.getMap());
            log.info("getLeftMealBySimnum FIND_LEFTFLOW_URL strResult={}",strResult);
            JSONObject jsonObject = JSONObject.parseObject(strResult);
            if(null!=jsonObject.get("data")){
                result.setData(jsonObject.get("data").toString());
            }else{
                result.setData("");
            }
        } catch (IOException e) {
            e.printStackTrace();
            result.setData("");
            log.info("底盘号为{}的车辆，剩余流量查询失败",form.getSimNumber());
        }


        return result;
    }


    /**
     *@Auther wangshuai
     *@Description 进行变量内容转换
     *@param
     *@return
     */
    private List<SimOrderVo> doChange(List<SimOrderVo> queryList) {
        List<SimOrderVo> lso= new ArrayList<>();
        for(SimOrderVo simOrderVo: queryList){
            if(DictEnum.ORDER_STATUS_WAIT_PAY.getCode().equals(simOrderVo.getOrderStatus())){
                simOrderVo.setOrderStatus("待付款");
            }else if(DictEnum.ORDER_STATUS_WAIT_RECEIVE.getCode().equals(simOrderVo.getOrderStatus())){
                simOrderVo.setOrderStatus("运营商处理中");
            }else if(DictEnum.ORDER_STATUS_WAIT_COMPLETE.getCode().equals(simOrderVo.getOrderStatus())){
                simOrderVo.setOrderStatus("已完成");
            }else if(DictEnum.ORDER_STATUS_CANCEL.getCode().equals(simOrderVo.getOrderStatus())){
                simOrderVo.setOrderStatus("已取消（未付款）");
            }
            lso.add(simOrderVo);
        }
        return lso;
    }

}
