package com.nut.driver.app.service;

import com.nut.driver.app.entity.CustomMaintainItemEntity;

import java.util.List;

/**
 * @author liuBing
 * @Classname CustomMaintainItemService
 * @Description TODO
 * @Date 2021/6/24 15:21
 */
public interface CustomMaintainItemService {
    List<CustomMaintainItemEntity> queryItemListByMaintainId(Long id);

    String validateItem(String maintainItemIdList);
}
