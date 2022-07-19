package com.jac.app.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jac.app.job.entity.CustomMaintainEntity;
import com.jac.app.job.entity.MaintainSendEntity;

import java.util.List;

/**
 * @author liuBing
 * @Classname customMaintainService
 * @Description TODO 自定义保养服务
 * @Date 2021/8/12 16:20
 */
public interface CustomMaintainService extends IService<CustomMaintainEntity> {
    /**
     * 查询出所有需要自定义保养的数据
     * @return
     */
    List<MaintainSendEntity> selectIsMaintains();

    /**
     * 修改发送状态为已推送
     * @param maintainInfoId
     * @param type
     */
    void updatePushed(Long maintainInfoId, String type);

    /**
     * 存储信息到消息记录当中
     * @param maintain
     */
    void addMessage(MaintainSendEntity maintain);
}
