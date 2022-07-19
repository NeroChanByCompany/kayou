package com.nut.common.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: 生成二维码
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.utils
 * @Author: yzl
 * @CreateTime: 2021-06-21 10:33
 * @Version: 1.0
 */
@Component
@Slf4j
public class QRcodeUtil {

    public static String fileStorageUrl;

    /**
     * 二维码尺寸
     */
    public static final int QRCODE_SIZE = 300;

    public static String generateQRcodePic(String content) {
        return QRcodeUtil.generateQRcodePic(content, QRcodeUtil.QRCODE_SIZE, QRcodeUtil.QRCODE_SIZE);
    }

    public static String generateQRcodePic(String content, int width, int height) {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        String fileName = "coupon_"+DateUtil.getNowDate_yyyyMMddHHmmss() + ".jpg";
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            Path file = new File(fileName).toPath();
            MatrixToImageWriter.writeToPath(bitMatrix, "jpg", file);
        } catch (IOException | WriterException e) {
            e.printStackTrace();
        }
        return QRcodeUtil.uploadFile(fileName);
    }

    public static String uploadFile(String fileName) {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("name", fileName);
        param.add("file", new FileSystemResource(fileName));
        RestTemplate restTemplate = new RestTemplate();
        String httpResult = restTemplate.postForObject(QRcodeUtil.fileStorageUrl, param, String.class);
        File f = new File(fileName);
        if(f.exists()){
            f.delete();
        }
        log.info("upload result:{}", httpResult);
        if (StringUtils.isNotBlank(httpResult)) {
            Map map = null;
            try {
                map = JsonUtil.toMap(httpResult);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            if (map != null && "200".equals(map.get("status").toString()) && map.get("data") != null) {
                Map resultMap = (LinkedHashMap) map.get("data");
                if (resultMap != null && resultMap.get("fullPath") != null) {
                    return (String)resultMap.get("fullPath");
                }
            }
        }
        return null;
    }

    @Value("${file.storage.url:dummy}")
    public void setFileStorageUrl(String fileStorageUrl) {
        QRcodeUtil.fileStorageUrl = fileStorageUrl;
    }
}
