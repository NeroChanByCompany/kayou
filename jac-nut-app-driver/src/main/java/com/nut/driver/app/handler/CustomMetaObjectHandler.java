package com.nut.driver.app.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.nut.driver.common.component.TokenComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author MengJinyue
 * @create 2021/1/27
 * @Describtion 自动填充create_user、create_time、update_user、update_time
 */
@Slf4j
@Component
public class CustomMetaObjectHandler implements MetaObjectHandler {

    @Autowired
    TokenComponent tokenComponent;

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("createUser", tokenComponent.getReqUserId(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateUser", tokenComponent.getReqUserId(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateUser", tokenComponent.getReqUserId(), metaObject);
    }

}
