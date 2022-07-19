package com.nut.servicestation.app.service.impl;

import com.github.pagehelper.Page;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.dao.UserMessageRecordDao;
import com.nut.servicestation.app.domain.UserMessageRecord;
import com.nut.servicestation.app.dto.MsgsDto;
import com.nut.servicestation.app.form.MsgsForm;
import com.nut.servicestation.app.service.MsgsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@Service("MsgsService")
public class MsgsServiceImpl implements MsgsService {

    @Autowired
    private UserMessageRecordDao userMessageRecordMapper;



    @Override
    public PagingInfo<MsgsDto> msgs(MsgsForm command) throws Exception {
        log.info("[msgs]start");
        List<MsgsDto> dtoList = new ArrayList<>();
        UserMessageRecord userMessageRecord = new UserMessageRecord();
        userMessageRecord.setReceiveAppType(Integer.parseInt(PushStaticLocarVal.PUSH_RECEIVETYPE_SERVERSTATION));
        userMessageRecord.setPushShowType(Integer.parseInt(command.getPushShowType()));
        userMessageRecord.setReceiverId(command.getUserId());
        userMessageRecord.setSendTime(System.currentTimeMillis());
        ServiceStationBaseService.getPage(command, true);
        Page<MsgsDto> dtoPageList = userMessageRecordMapper.queryMsgsList(userMessageRecord);
        if (dtoPageList != null && dtoPageList.getResult() != null && !dtoPageList.getResult().isEmpty()) {
            dtoList = dtoPageList.getResult();
            for (MsgsDto msgsDto : dtoList) {
                String messageExtra = msgsDto.getMessageExtra();
                if (StringUtil.isNotEmpty(messageExtra)) {
                    Map<String, Object> messageExtraMap = JsonUtil.toMap(messageExtra);
                    msgsDto.setWoStatus(messageExtraMap.get(PushStaticLocarVal.MESSAGE_EXTRA_WO_STATUS) == null
                            ? "" : messageExtraMap.get(PushStaticLocarVal.MESSAGE_EXTRA_WO_STATUS).toString());
                    msgsDto.setWoType(messageExtraMap.get(PushStaticLocarVal.MESSAGE_EXTRA_WO_TYPE) == null
                            ? "" : messageExtraMap.get(PushStaticLocarVal.MESSAGE_EXTRA_WO_TYPE).toString());
                    msgsDto.setAppoType(messageExtraMap.get(PushStaticLocarVal.MESSAGE_EXTRA_APPO_TYPE) == null
                            ? "" : messageExtraMap.get(PushStaticLocarVal.MESSAGE_EXTRA_APPO_TYPE).toString());
                }
            }
        }
        PagingInfo<MsgsDto> resultPageInfo = ServiceStationBaseService.convertPagingToPage(dtoPageList);
        resultPageInfo.setList(dtoList);
        log.info("[msgs]end");
        return resultPageInfo;
    }
}
