package com.nut.servicestation.app.service.impl;

import com.nut.common.constant.ServiceStationVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.dao.UserDao;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.form.StaffListForm;
import com.nut.servicestation.app.service.RescueInfoQueryService;
import com.nut.servicestation.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@Service("RescueInfoQueryService")
public class RescueInfoQueryServiceImpl implements RescueInfoQueryService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Value("${database_name}")
    private String DatabaseName;


    @Override
    public HttpCommandResultWithData staffList(StaffListForm command) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        // 用户信息查询
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto != null) {
            if (userInfoDto.getRoleCode() != ServiceStationVal.JOB_TYPE_ADMIN) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("此用户没有操作权限");
                return result;
            }
            result.setData(userDao.queryStaffList(DatabaseName, userInfoDto.getServiceStationId()));
            result.setResultCode(ECode.SUCCESS.code());
            result.setMessage(ECode.SUCCESS.message());
        } else {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此用户不存在");
        }
        return result;
    }
}
