package com.nut.servicestation.app.client.fallback;

import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.servicestation.app.client.TspBusinessClient;
import com.nut.servicestation.app.entity.CarGasInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author zhangling
 * @Date 2022/5/17 17:45
 * @Description
 */
@Slf4j
@Component
public class TspBusinessFallback implements TspBusinessClient {
    @Override
    public HttpCommandResultWithData<CarGasInfo> getByChassisNum(String chassisNum) {
        return Result.result(ECode.FALLBACK.code(), ECode.FALLBACK.message());
    }
}
