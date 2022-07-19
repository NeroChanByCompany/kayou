package com.nut.driver.app.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.driver.app.dao.CarDao;
import com.nut.driver.app.dao.CarExtDao;
import com.nut.driver.app.dao.QueryBrandListDao;
import com.nut.driver.app.dto.BrandInfoDto;
import com.nut.driver.app.entity.CarEntity;
import com.nut.driver.app.entity.CarExtEntity;
import com.nut.driver.app.form.IntegralOperationForm;
import com.nut.driver.app.form.UpdateCarExtForm;
import com.nut.driver.app.service.CarExtService;
import com.nut.driver.app.service.IntegralService;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@Slf4j
public class CarExtServiceImpl implements CarExtService {

    @Value("${database_name}")
    private String DbName;


    @Autowired
    private IntegralService integralService;
    @Autowired
    private CarExtDao carExtMapper;
    @Autowired
    private QueryBrandListDao brandListMapper;
    @Autowired
    private CarDao carDao;


    private CarExtEntity merge(CarExtEntity carExtEntity, CarEntity car) {
        if (carExtEntity == null) {
            carExtEntity = new CarExtEntity();
        }

        if (carExtEntity.getCarId() == null) {
            carExtEntity.setCarId(Long.parseLong(car.getId()));
        }
        if (carExtEntity.getVin() == null) {
            carExtEntity.setVin(car.getCarVin());
        }
        if (carExtEntity.getLicense() == null) {
            carExtEntity.setLicense(car.getCarNumber());
        }
        if (carExtEntity.getBrand() == null) {
            if (StringUtils.isNotBlank(car.getCarBrand()) && NumberUtils.isNumber(car.getCarBrand())) {
                BrandInfoDto brandInfoDto = brandListMapper.selectBrandInfoById(Long.parseLong(car.getCarBrand()));
                carExtEntity.setBrand(brandInfoDto.getBrandName());
            }
        }
        if (carExtEntity.getSeries() == null) {
            carExtEntity.setSeries(car.getCarSeriesName());
        }
        if (carExtEntity.getEngineNum() == null) {
            carExtEntity.setEngineNum(car.getEngineNo());
        }
        return carExtEntity;
    }

    @Override
    public CarExtEntity findById(Long carId) {
        CarExtEntity byCarId = carExtMapper.findByCarId(carId);
        CarEntity car = carDao.selectByPrimaryKey(carId + "");
        return merge(byCarId, car);
    }

    @Override
    public CarExtEntity findByClassisNum(String chassisNum) {
        CarExtEntity byClassesNum = carExtMapper.findByClassesNum(chassisNum);
        CarEntity car = carDao.selectByChassisNumber(chassisNum);
        return merge(byClassesNum, car);
    }

    @Override
    public void save(UpdateCarExtForm updateCarExtCommand){
        CarEntity car = carDao.selectByPrimaryKey(updateCarExtCommand.getCarId() + "");
        // 同步car表中的车牌号
        if (StringUtils.isNotBlank(updateCarExtCommand.getLicense())) {
            car.setCarNumber(updateCarExtCommand.getLicense());
            car.setCarSeriesName(updateCarExtCommand.getBrand());
            carDao.updateByPrimaryKeySelective(car);
            String chassisNum = null;

            int length = car.getCarVin().length();
            if (length > 8) {
                chassisNum = StringUtils.substring(car.getCarVin(), length - 8);
            } else {
                chassisNum = updateCarExtCommand.getLicense();
            }
            carDao.updateTspCarNumber(DbName, chassisNum, updateCarExtCommand.getLicense());
        }else{
            car.setCarSeriesName(updateCarExtCommand.getBrand());
            carDao.updateByPrimaryKeySelective(car);
        }


        int iCount = 0;
        if (carExtMapper.countByCarId(updateCarExtCommand.getCarId()) == 0) {
            if (StringUtils.isNotBlank(updateCarExtCommand.getLicense())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, license(车牌号)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getIdentity())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, identity(车辆身份)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getBrand())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, brand(品牌)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getSeries())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, series(车系)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getEngineNum())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, engineNum(发动机号)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getColor())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, color(车辆颜色)", updateCarExtCommand.getUserId());
            }
            if (updateCarExtCommand.getVehicleUse() != null) {
                iCount++;
                log.info("完善车辆资料，userId: {}, vehicleUse(车辆用途)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getIndustry())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, industry(行业)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getTypeOfGoods())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, typeOfGoods(货物类型)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getTypeOfVan())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, typeOfVan(货车类型)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getRatedLoad())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, ratedLoad(核定载重)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getWeight())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, weight(货车重量)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getLength())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, length(货车长度)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getWeight())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, width(货车宽度)", updateCarExtCommand.getUserId());
            }
            if (updateCarExtCommand.getInsuranceDate() != null) {
                iCount++;
                log.info("完善车辆资料，userId: {}, insuranceDate(保险到期时间)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getInsuredAmount())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, insuredAmount(保险金额)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getInsuranceCompany())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, insuranceCompany(保险公司)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isNotBlank(updateCarExtCommand.getTransferCycle())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, transferCycle(换车周期)", updateCarExtCommand.getUserId());
            }
            carExtMapper.insert(updateCarExtCommand);
        } else {
            CarExtEntity ext = carExtMapper.findByCarId(updateCarExtCommand.getCarId());
            if (StringUtils.isEmpty(ext.getLicense()) && StringUtils.isNotBlank(updateCarExtCommand.getLicense())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, license(车牌号)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getIdentity()) && StringUtils.isNotBlank(updateCarExtCommand.getIdentity())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, identity(车辆身份)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getBrand()) && StringUtils.isNotBlank(updateCarExtCommand.getBrand())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, brand(品牌)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getSeries()) && StringUtils.isNotBlank(updateCarExtCommand.getSeries())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, series(车系)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getEngineNum()) && StringUtils.isNotBlank(updateCarExtCommand.getEngineNum())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, engineNum(发动机号)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getColor()) && StringUtils.isNotBlank(updateCarExtCommand.getColor())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, color(车辆颜色)", updateCarExtCommand.getUserId());
            }
            if (ext.getVehicleUse() == null && updateCarExtCommand.getVehicleUse() != null) {
                iCount++;
                log.info("完善车辆资料，userId: {}, vehicleUse(车辆用途)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getIndustry()) && StringUtils.isNotBlank(updateCarExtCommand.getIndustry())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, industry(行业)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getTypeOfGoods()) && StringUtils.isNotBlank(updateCarExtCommand.getTypeOfGoods())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, typeOfGoods(货物类型)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getTypeOfVan()) && StringUtils.isNotBlank(updateCarExtCommand.getTypeOfVan())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, typeOfVan(货车类型)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getRatedLoad()) && StringUtils.isNotBlank(updateCarExtCommand.getRatedLoad())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, ratedLoad(核定载重)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getWeight()) && StringUtils.isNotBlank(updateCarExtCommand.getWeight())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, weight(货车重量)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getLength()) && StringUtils.isNotBlank(updateCarExtCommand.getLength())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, length(货车长度)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getWidth()) && StringUtils.isNotBlank(updateCarExtCommand.getWidth())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, width(货车宽度)", updateCarExtCommand.getUserId());
            }
            if (ext.getInsuranceDate() == null && updateCarExtCommand.getInsuranceDate() != null) {
                iCount++;
                log.info("完善车辆资料，userId: {}, insuranceDate(保险到期时间)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getInsuredAmount()) && StringUtils.isNotBlank(updateCarExtCommand.getInsuredAmount())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, insuredAmount(保险金额)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getInsuranceCompany()) && StringUtils.isNotBlank(updateCarExtCommand.getInsuranceCompany())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, insuranceCompany(保险公司)", updateCarExtCommand.getUserId());
            }
            if (StringUtils.isEmpty(ext.getTransferCycle()) && StringUtils.isNotBlank(updateCarExtCommand.getTransferCycle())) {
                iCount++;
                log.info("完善车辆资料，userId: {}, transferCycle(换车周期)", updateCarExtCommand.getUserId());
            }
            // 更新车辆资料
                updateCarExtCommand.setUpdateTime(new Date());
                carExtMapper.update(updateCarExtCommand);

        }


        boolean all = true;
        if (StringUtils.isAnyEmpty(updateCarExtCommand.getLicense(),
                updateCarExtCommand.getIdentity(),
                updateCarExtCommand.getBrand(),
                updateCarExtCommand.getSeries(),
                updateCarExtCommand.getEngineNum(),
                updateCarExtCommand.getColor(),
                updateCarExtCommand.getIndustry(),
                updateCarExtCommand.getTypeOfGoods(),
                updateCarExtCommand.getTypeOfVan(),
                updateCarExtCommand.getRatedLoad(),
                updateCarExtCommand.getWeight(),
                updateCarExtCommand.getLength(),
                updateCarExtCommand.getWidth(),
                updateCarExtCommand.getInsuranceCompany(),
                updateCarExtCommand.getInsuredAmount()) ||
                ObjectUtil.isEmpty(updateCarExtCommand.getInsuranceDate()) ||
                ObjectUtil.isEmpty(updateCarExtCommand.getVehicleUse())) {
            all = false;
        }
        if (all) {
            carDao.updateExtInfoOk(updateCarExtCommand.getCarId());
        }

        // 给积分
        if (iCount > 0){
            for(int i = 0; i < iCount; i++) {
                IntegralOperationForm integralOperationCommand = new IntegralOperationForm();
                integralOperationCommand.setActionId("101");
                integralOperationCommand.setOperationId(0);
                integralOperationCommand.setUcId(updateCarExtCommand.getUserId());
                integralService.addIntegralCounts(integralOperationCommand);
            }
        }

    }
}
