package com.nut.driver.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nut.driver.app.domain.FileInfo;
import com.nut.driver.app.domain.FsManagerGenericResponse;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.common.utils.HttpUtil;
import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author liuBing
 * @Classname CommonController
 * @Description TODO 公共文件上传模块
 * @Date 2021/6/17 16:57
 */
@Slf4j
@RestController
@EnableAutoConfiguration
@Api(tags ="公共文件上传模块")
public class CommonController {

    @Value("${file.storage.url:http://192.168.31.241:14001/file/upload/single?account=www}")
    private String fileStorageUrl;

    @Value("${endPoint.obs}")
    private String endPointOBS;
    @Value("${ak.obs}")
    private String akOBS;
    @Value("${sk.obs}")
    private String skOBS;
    @Value("${bucket.app.obs}")
    private String bucketAppOBS;
    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 上传图片或者文件
     * @Date 17:00 2021/6/17
     * @Param [file]
     **/
    @SneakyThrows
    @ApiOperation(value = "上传图片或者文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file",value = "文件",dataType = "File"),
    })
    @PostMapping(value = "/upload")
    public HttpCommandResultWithData uploadPhoto(@ApiIgnore @RequestParam(value = "file") MultipartFile file) {
        InputStream is = null;
        boolean lockGet = false;
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
        FileInfo fileInfo = HttpUtil.streamingFsManager(fileStorageUrl, null, file.getOriginalFilename(), uploadStream,new TypeReference<FsManagerGenericResponse<FileInfo>>(){});
        if (StringUtils.isEmpty(fileInfo.getFullPath())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "文件服务器返回空url");
        }
        is.close();
        return Result.ok(fileInfo.getFullPath());
    }


    /**
     * 上传APP到OBS
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadApp")
    public HttpCommandResultWithData uploadApp(@RequestParam(value = "file") MultipartFile file) {

        String apkUrl = "";
        try {
            // 创建ObsClient实例
            ObsClient obsClient = new ObsClient(akOBS, skOBS, endPointOBS);
            // 上传apk文件到指定桶
            PutObjectResult putObjectResult = obsClient.putObject(bucketAppOBS, file.getOriginalFilename(), file.getInputStream(), null);
             apkUrl = putObjectResult.getObjectUrl();
            // 关闭obsClient
            obsClient.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("上传APP文件到OBS出现问题");
        }

        log.info("[uploadApp]end");
        return Result.ok(apkUrl);
    }
}
