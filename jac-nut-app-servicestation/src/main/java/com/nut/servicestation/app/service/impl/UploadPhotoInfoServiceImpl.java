package com.nut.servicestation.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nut.common.domain.FsManagerGenericResponse;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.HttpUtil;
import com.nut.servicestation.app.dao.WorkOrderOperatePhotoDao;
import com.nut.servicestation.app.domain.FileInfo;
import com.nut.servicestation.app.domain.WorkOrderOperatePhoto;
import com.nut.servicestation.app.form.UploadPhotoInfoForm;
import com.nut.servicestation.app.form.UploadPhotoInfoListForm;
import com.nut.servicestation.app.service.AsyCalculateManCarStationDistanceService;
import com.nut.servicestation.app.service.AsyTriggerFaultyPartService;
import com.nut.servicestation.app.service.UploadPhotoInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static com.nut.common.utils.QRcodeUtil.fileStorageUrl;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@Service("UploadPhotoInfoService")
public class UploadPhotoInfoServiceImpl implements UploadPhotoInfoService {

    private static final String YES = "1";

    @Autowired
    private WorkOrderOperatePhotoDao workOrderOperatePhotoMapper;

    @Value("${obsurl1}")
    private String  obsurl1;
    @Value("${obsurl2}")
    private String  obsurl2;
    @Value("${cdnurl}")
    private String  cdnurl;
    @Autowired
    AsyCalculateManCarStationDistanceService asyCalculateManCarStationDistanceService;
    @Autowired
    AsyTriggerFaultyPartService asyTriggerFaultyPartService;

    @Override
    @Transactional(timeout = 180, rollbackFor = Exception.class)
    public HttpCommandResultWithData uploadPhoto(UploadPhotoInfoListForm commandList) throws IOException {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        if (commandList.getListCommand() == null) {
            return result;
        }
        for (UploadPhotoInfoForm command : commandList.getListCommand()) {
            // 图片类型
            String picType = command.getType();

            // 校验图片时间戳是否重复（根据工单号、操作ID、时间戳校验）
            int picCount = workOrderOperatePhotoMapper.queryPicNum(command);
            if (picCount > 0) {
                return result;
            }

            // 文件信息保存
            savePhotoInfo(command);
            // 异步计算人车站距离
            asyCalculateManCarStationDistanceService.calculateDistance(command.getType(), command.getWoCode(), command.getLon(), command.getLat(), commandList.getUserId());
            // 检查照片同步
            asyTriggerFaultyPartService.triggerFaultyPart(command.getType(), command.getWoCode(), command.getOperateId());
        }

        return result;
    }

    @Override
    public HttpCommandResultWithData uploadPhotoSimple(MultipartFile file) throws IOException {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        InputStream is = null;
        is = file.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) > -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        byteArrayOutputStream.flush();
        byte[] bufferedStream = byteArrayOutputStream.toByteArray();

        InputStream uploadStream = new ByteArrayInputStream(bufferedStream);
        FileInfo fileInfo = HttpUtil.streamingFsManager(fileStorageUrl, null, file.getOriginalFilename(), uploadStream,
                new TypeReference<FsManagerGenericResponse<FileInfo>>() {
                });
        result.setMessage(fileInfo.getFullPath());
        return result;
    }

    /**
     * 保存照片信息
     *
     * @param command 接口入参
     */
    private void savePhotoInfo(UploadPhotoInfoForm command) {
        WorkOrderOperatePhoto workOrderOperatePhoto = new WorkOrderOperatePhoto();
        workOrderOperatePhoto.setTimestamp(command.getTimestamp());
        workOrderOperatePhoto.setType(command.getType());
        log.info("url变更中：{}",command.getImgURL());
        String url = command.getImgURL();
        url = url.replace(obsurl1,cdnurl);
        url = url.replace(obsurl2,cdnurl);
        log.info("url变更完成：{}",url);
        workOrderOperatePhoto.setUrl(url);
        workOrderOperatePhoto.setOperateId(command.getOperateId());
        workOrderOperatePhoto.setWoCode(command.getWoCode());
        workOrderOperatePhoto.setLon(command.getLon());
        workOrderOperatePhoto.setLat(command.getLat());
        workOrderOperatePhoto.setAddr(command.getAddr());
        workOrderOperatePhoto.setDeviceNo(command.getDeviceNo());
        // 系统时间
        Date now = new Date();
        workOrderOperatePhoto.setCreateTime(now);
        workOrderOperatePhoto.setUpdateTime(now);
        // 离线标识，默认0
        if (YES.equals(command.getOfflineFlag())) {
            workOrderOperatePhoto.setIsOffline(1);
        } else {
            workOrderOperatePhoto.setIsOffline(0);
        }
        workOrderOperatePhotoMapper.insertSelective(workOrderOperatePhoto);
    }
}
