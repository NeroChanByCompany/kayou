package com.nut.servicestation.app.service;

import com.nut.servicestation.app.dto.LatLngDto;
import com.nut.servicestation.app.dto.WoStatusDto;
import com.nut.servicestation.app.form.UploadRescueRoutePointForm;

import java.io.IOException;
import java.util.List;

/*
 *  @author wuhaotian 2021/7/6
 */
@Deprecated
public interface RescueRoutePointService {
/*    *//**
     * 验证入参
     * 因为后期有补传的点 所以点的时间顺序在上传时不检验 查询时再将不符合时间顺序的点去掉
     *
     * @param command 接口参数
     * @return 错误信息
     *//*
    String validateSaveCommand(UploadRescueRoutePointForm command);

    Integer checkWorkOrder(String woCode);

    void uploadPoint(String woCode, List<LatLngDto> points, String userId) throws IOException;

    List<WoStatusDto> queryWoStatus(List<String> list);*/
}
