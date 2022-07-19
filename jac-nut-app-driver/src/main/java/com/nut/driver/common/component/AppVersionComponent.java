package com.nut.driver.common.component;

import com.nut.driver.app.entity.ActionPictureSetEntity;
import com.nut.driver.app.pojo.AppVersion;
import com.nut.driver.app.service.ActionPictureSetService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author liuBing
 * @Classname AppVersionComponent
 * @Description TODO
 * @Date 2021/11/2 16:39
 */
@Component
public class AppVersionComponent {

    @Resource
    ActionPictureSetService actionPictureSetService;
    /**
     * @Author liuBing
     * @Description //TODO  判断当前app版本号
     * @Date 16:52 2021/11/2
     * @Param [version]
     *  actionCode  kehu_version 客户版本 fleet_version 车队版本 service_version 服务版本
     *  type 当前app类型 1 android 2 ios
     *  version 当前app版本号
     * @return boolean
     **/
    public boolean checkVersion(AppVersion version){

        if (Objects.isNull(version) || StringUtils.isBlank(version.getVersion())){
            return false;
        }

       ActionPictureSetEntity entity =  actionPictureSetService.getVersion(version);

       if (Objects.isNull(entity)){
           return false;
       }

        if (Long.parseLong(version.getVersion()) >= Long.parseLong(entity.getInVersion())){
            return true;
        }

       return false;
    }

}
