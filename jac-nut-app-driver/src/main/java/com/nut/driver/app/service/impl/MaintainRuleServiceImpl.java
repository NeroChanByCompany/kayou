package com.nut.driver.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nut.driver.app.dao.MaintainRuleDao;
import com.nut.driver.app.entity.MaintainRuleEntity;
import com.nut.driver.app.service.MaintainRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author liuBing
 * @Classname MaintainRuleServiceImpl
 * @Description TODO 推荐保养
 * @Date 2021/8/23 14:05
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class,readOnly = true)
public class MaintainRuleServiceImpl extends ServiceImpl<MaintainRuleDao, MaintainRuleEntity> implements MaintainRuleService {
}
