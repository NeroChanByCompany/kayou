package com.nut.driver.app.service.impl;

import com.baomidou.dynamic.datasource.annotation.Master;
import com.baomidou.dynamic.datasource.annotation.Slave;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dozermapper.core.Mapper;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.driver.app.dao.AuthLogDao;
import com.nut.driver.app.entity.AuthLogEntity;
import com.nut.driver.app.enums.AuthEnum;
import com.nut.driver.app.form.AuthLogForm;
import com.nut.driver.app.form.AuthStateForm;
import com.nut.driver.app.pojo.AuthStatePojo;
import com.nut.driver.app.service.AuthLogService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author liuBing
 * @Classname AuthLogServiceImpl
 * @Description TODO 实名认证实现类
 * @Date 2021/8/10 13:21
 */
@Slf4j
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class AuthLogServiceImpl extends ServiceImpl<AuthLogDao, AuthLogEntity> implements AuthLogService {

    @Autowired
    private Mapper convert;

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public Integer callback(AuthLogForm form) {
        AuthLogEntity entity = convert.map(form, AuthLogEntity.class);
        QueryWrapper<AuthLogEntity> wrapper = new QueryWrapper<AuthLogEntity>().eq(StringUtils.isNotBlank(entity.getOrderNo()), "order_no", entity.getOrderNo());
        wrapper.last("limit 1");
        AuthLogEntity logEntity = this.baseMapper.selectOne(wrapper);
        if (logEntity == null) {
            ExceptionUtil.result(ECode.NO_AUTH.code(), ECode.NO_AUTH.message());
        }
        entity.setUpdateTime(new Date());
        int key = this.baseMapper.update(entity, new UpdateWrapper<AuthLogEntity>().eq(StringUtils.isNotBlank(entity.getOrderNo()), "order_no", entity.getOrderNo()));
        log.info("auth callback return:{}", key);
        return key;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void state(AuthStateForm form) {

        if (CollectionUtils.isNotEmpty(form.getAuthStatePojos()) && form.getAuthStatePojos().size() > 0 ){
            List<AuthStatePojo> authStatePojos = form.getAuthStatePojos();
            for (AuthStatePojo pojo : authStatePojos) {
                if (StringUtils.isNotBlank(pojo.getCarVin())){
                    AuthLogEntity authLogEntity = new AuthLogEntity().setCarVin(pojo.getCarVin());
                    QueryWrapper<AuthLogEntity> wrapper = new QueryWrapper<AuthLogEntity>().eq(StringUtils.isNotBlank(authLogEntity.getCarVin()), "car_vin", authLogEntity.getCarVin());
                    wrapper.last("limit 1");
                    AuthLogEntity entity = this.baseMapper.selectOne(wrapper);
                    if (Objects.nonNull(entity) && StringUtils.isNotBlank(entity.getMsisdn()) && StringUtils.isNotBlank(pojo.getSimId())) {
                        if (!entity.getMsisdn().equals(pojo.getSimId())) {
                            entity.setIsAuth(String.valueOf(AuthEnum.FAIL.getCode()));
                            entity.setUpdateTime(new Date());
                            this.baseMapper.updateById(entity);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Integer deleteAuthlog(String carVin) {
        QueryWrapper<AuthLogEntity> wrapper = new QueryWrapper<AuthLogEntity>();
        wrapper.eq("car_vin",carVin);
        Integer count = this.baseMapper.delete(wrapper);
        return count;
    }


}
