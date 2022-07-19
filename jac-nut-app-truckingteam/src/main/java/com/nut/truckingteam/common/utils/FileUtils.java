package com.nut.truckingteam.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @description: 文件工具类
 * @author: hcb
 * @createTime: 2021/01/21 14:42
 * @version:1.0
 */
@Slf4j
public class FileUtils {

    /**
     * 校验是否有效图片
     * @param input
     * @return
     */
    public static boolean isValidImage(InputStream input) {
        boolean isValid;
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(input);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        if (null == bufferedImage) {
            isValid = false;
        } else {
            isValid = true;
        }
        return isValid;
    }

    /**
     * 校验是否有效图片
     * @param bytes
     * @return
     */
    public static boolean isValidImage(byte[] bytes) {
        return isValidImage(new ByteArrayInputStream(bytes));
    }

    /**
     * 校验是否有效图片
     * @param file
     * @return
     */
    public static boolean isValidImage(File file) {
        InputStream inputStream;
        try {
            inputStream = org.apache.commons.io.FileUtils.openInputStream(file);
        } catch (IOException e) {
            return false;
        }
        return isValidImage(inputStream);
    }
}
