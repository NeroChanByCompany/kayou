package com.nut.locationservice.app.service;

import com.nut.common.result.HttpCommandResultWithData;

/**
 * @Description: 生成故障下载文件service
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service
 * @Author: yzl
 * @CreateTime: 2021-06-17 09:16
 * @Version: 1.0
 */
public interface ExportCarInfoFileService {

    HttpCommandResultWithData generateCarFaultInfoFile(String procDate);

    HttpCommandResultWithData generateCarTrackInfoFile(String procDate);

}
