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
            // ????????????
            String picType = command.getType();

            // ????????????????????????????????????????????????????????????ID?????????????????????
            int picCount = workOrderOperatePhotoMapper.queryPicNum(command);
            if (picCount > 0) {
                return result;
            }

            // ??????????????????
            savePhotoInfo(command);
            // ???????????????????????????
            asyCalculateManCarStationDistanceService.calculateDistance(command.getType(), command.getWoCode(), command.getLon(), command.getLat(), commandList.getUserId());
            // ??????????????????
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
     * ??????????????????
     *
     * @param command ????????????
     */
    private void savePhotoInfo(UploadPhotoInfoForm command) {
        WorkOrderOperatePhoto workOrderOperatePhoto = new WorkOrderOperatePhoto();
        workOrderOperatePhoto.setTimestamp(command.getTimestamp());
        workOrderOperatePhoto.setType(command.getType());
        log.info("url????????????{}",command.getImgURL());
        String url = command.getImgURL();
        url = url.replace(obsurl1,cdnurl);
        url = url.replace(obsurl2,cdnurl);
        log.info("url???????????????{}",url);
        workOrderOperatePhoto.setUrl(url);
        workOrderOperatePhoto.setOperateId(command.getOperateId());
        workOrderOperatePhoto.setWoCode(command.getWoCode());
        workOrderOperatePhoto.setLon(command.getLon());
        workOrderOperatePhoto.setLat(command.getLat());
        workOrderOperatePhoto.setAddr(command.getAddr());
        workOrderOperatePhoto.setDeviceNo(command.getDeviceNo());
        // ????????????
        Date now = new Date();
        workOrderOperatePhoto.setCreateTime(now);
        workOrderOperatePhoto.setUpdateTime(now);
        // ?????????????????????0
        if (YES.equals(command.getOfflineFlag())) {
            workOrderOperatePhoto.setIsOffline(1);
        } else {
            workOrderOperatePhoto.setIsOffline(0);
        }
        workOrderOperatePhotoMapper.insertSelective(workOrderOperatePhoto);
    }
}
