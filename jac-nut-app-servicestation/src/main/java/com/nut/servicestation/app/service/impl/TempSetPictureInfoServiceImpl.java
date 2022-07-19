package com.nut.servicestation.app.service.impl;

import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.dao.TempSetPictureInfoDao;
import com.nut.servicestation.app.dto.TempActionPictureSetDto;
import com.nut.servicestation.app.form.GetSetPictureInfoForm;
import com.nut.servicestation.app.service.TempSetPictureInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/7
 */
@Slf4j
@Service("TempSetPictureInfoService")
public class TempSetPictureInfoServiceImpl implements TempSetPictureInfoService {

    @Autowired
    private TempSetPictureInfoDao tempSetPictureInfoMapper;


    @Override
    public HttpCommandResultWithData getPictureInfoList(GetSetPictureInfoForm command) {
        log.info(" ===== getPictureInfoList start==========");
        HttpCommandResultWithData result = new HttpCommandResultWithData();

        if(command.getType()==null){
            command.setType("1");
        }

        List<TempActionPictureSetDto> list = tempSetPictureInfoMapper.tempGetSetPictureInfo(command.getActionCode(), command.getType());
        result.setData(list);
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        log.info(" ===== getPictureInfoList end==========");
        return result;
    }
}
