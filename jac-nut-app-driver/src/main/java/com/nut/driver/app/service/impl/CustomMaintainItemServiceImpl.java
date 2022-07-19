package com.nut.driver.app.service.impl;

import com.nut.driver.app.dao.CustomMaintainItemDao;
import com.nut.driver.app.dao.MaintainItemInfoDao;
import com.nut.driver.app.entity.CustomMaintainItemEntity;
import com.nut.driver.app.service.CustomMaintainItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author liuBing
 * @Classname CustomMaintainItemServiceImpl
 * @Description TODO
 * @Date 2021/6/24 15:21
 */
@Service
@Slf4j
@Transactional(readOnly = true,rollbackFor = Exception.class)
public class CustomMaintainItemServiceImpl implements CustomMaintainItemService {
    @Autowired
    private CustomMaintainItemDao customMaintainItemDao;

    @Autowired
    MaintainItemInfoDao maintainItemInfoDao;

    @Override
    public List<CustomMaintainItemEntity> queryItemListByMaintainId(Long id) {
        return customMaintainItemDao.selectItemListByMaintainId(id);
    }

    @Override
    public String validateItem(String maintainItemIdList) {
        long checkFailCount = Stream.of(maintainItemIdList.split(","))
                .filter(sp -> maintainItemInfoDao.selectByPrimaryKey(Integer.parseInt(sp)) == null)
                .count();
        if (checkFailCount > 0) {
            return "保养项目部分或全部信息未查询到，请重新确认保养项目是否正确！";
        }
        return null;
    }
}
