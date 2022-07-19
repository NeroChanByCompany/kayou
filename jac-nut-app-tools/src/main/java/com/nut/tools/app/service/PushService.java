package com.nut.tools.app.service;

import com.alibaba.fastjson.JSONObject;
import com.nut.tools.app.form.PushMesForm;

/**
 * @author liuBing
 * @Classname PushService
 * @Description TODO
 * @Date 2021/6/22 17:51
 */
public interface PushService {

    void push2Device(PushMesForm from);

    void JPushMgs(JSONObject params);

    /**
     * 单独推送服务站的消息出去
     * @param from
     */
    void push2Station(PushMesForm from);
}
