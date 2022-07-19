package com.nut.servicestation.app.service.impl;

import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.dao.AreaStationDao;
import com.nut.servicestation.app.dto.AreaListDto;
import com.nut.servicestation.app.dto.StationDto;
import com.nut.servicestation.app.form.QueryStationForm;
import com.nut.servicestation.app.service.AreaStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.service.impl
 * @Author: yzl
 * @CreateTime: 2021-07-27 15:38
 * @Version: 1.0
 */
@Service
@Slf4j
public class AreaStationServiceImpl implements AreaStationService {

    @Autowired
    private AreaStationDao areaStationDao;

    @Value("${database_name}")
    private String DbName;

    @Override
    public HttpCommandResultWithData queryStationList(QueryStationForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            List<StationDto> list = areaStationDao.getStationList(DbName, form.getCityID());
            result.setData(list);
        }catch (Exception e){
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询对应城市服务站信息失败");
        }
        return result;
    }
}
