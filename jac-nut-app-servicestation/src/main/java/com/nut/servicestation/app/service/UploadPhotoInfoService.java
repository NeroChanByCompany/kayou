package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.UploadPhotoInfoListForm;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/*
 *  @author wuhaotian 2021/7/8
 */
public interface UploadPhotoInfoService {
    /**
     * 上传图片
     */
    HttpCommandResultWithData uploadPhoto(UploadPhotoInfoListForm commandList) throws IOException;

    /*
    单纯的上传图片
     */
    HttpCommandResultWithData uploadPhotoSimple(MultipartFile file) throws IOException;
}
