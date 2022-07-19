package com.nut.driver.app.service;

import org.springframework.scheduling.annotation.Async;

public interface CommonCustomMaintainService {
    /**
     * 更新用户自定义保养数据
     */
    void updateUserCustomMaintain(Long userId);
}
