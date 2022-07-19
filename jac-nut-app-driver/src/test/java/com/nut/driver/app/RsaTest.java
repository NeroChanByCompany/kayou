package com.nut.driver.app;

import com.nut.driver.common.component.RsaComponent;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuBing
 * @Classname RsaTest
 * @Description TODO
 * @Date 2021/8/26 11:17
 */
@Slf4j
public class RsaTest {
    private static Long TIMER;

    public static final String KEY_ALGORITHM = "RSA";
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    @Before
    public void test_start() {
        log.info("Case Test Start.....");
        TIMER = System.currentTimeMillis();
    }

    @After
    public void test_end() {
        log.info("Case Test End....., Execution Time {}ms", System.currentTimeMillis() - TIMER);
    }

    @Test
    public void encrypt_test() {
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxvTz6I2JUI2/vttXEldDlTuhlexDDleV2gokGNjQv4FhU5CWCQXwscO3umoLUFfEIT+XLI1zJtnpfdN01eTZ8sE1DmRhgMUIOf5SZWA+fSI3K3ofAtP78kFl6RsgjJbjSYBhxuo/agt5oxaRp2q0P52lVbevXbdrhEaRM+mwgLwIDAQAB";
        String message = "17647580471";
        System.out.println("原字符串为:" + message);
        try {
            String messageEn = encrypt(message, publicKey);
            System.out.println("公钥加密后的字符串为:" + messageEn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void decrypt_test() {
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALG9PPojYlQjb++21cSV0OVO6GV7EMOV5XaCiQY2NC/gWFTkJYJBfCxw7e6agtQV8QhP5csjXMm2el903TV5NnywTUOZGGAxQg5/lJlYD59Ijcreh8C0/vyQWXpGyCMluNJgGHG6j9qC3mjFpGnarQ/naVVt69dt2uERpEz6bCAvAgMBAAECgYEAoOOoGtWE4Jzjap6aisUCv0/aVmI8Ho/Fkq0+0/yk6OGvk8ihm3MjsCbZei8rVkg2U0vfuy5bg8xGobnrs8fxZRBRodCuUT5FcDoC0RMjLTsIRsFeirRbiZGcfRTEjzp1WmkzJfFYUW229NjVgAFJeVBzo4VGbGdximMC9rPA8KECQQDwXoKNox5U1+EGfT3NhFeK95G/EKoCvqMub+5kxiwPXYMtCulAfUFEc5x3rPp+dTbiqwBPUab/abD98+SH1pC7AkEAvUwcQomrPNvDLmgKRG2fvt/454Qnr540+Uib8pJAR2A1MXDbgDU2ZdKc3zEQ+0XwWOeaoE+3pateAaU3I1cBHQJAQGiCwSM5aHCkWpHKsrxInPMzuafkdnHQ1gumLJixi7h6AuLOl7o4d/gmsCbIxIPyHhDGN3rgrcYLaDEnRoLnKwJASIZhiWifb7ivBhecX200t/etsF3kqpdNqniULxKzh+UnXCLmJi+9ALP5oFV3MR4xoI5ToroIHXQTl0PKZULPTQJBAJAi/AvSYOxPd8PxtgUMoYowx8v50o+Q9GPmadilQmHwOQ0IrhNw0UcHdNb0qG7P5EaMCewYe1DS3fChNOT3+EE=";
        String message = "IzAWI2ssZrO3N3S8ZcDP6vz5cNCgjiam6vKbdknOEBWrlMZy+gGfbz/soXg0P0pG7KC9iOFP+liy5Fk6zNW1891I2Q78/fi1c2UxwY5cCv7warZlhdiwSKN5qVS311Lyq486bg49+cnURYQPgpjTtQYyhWVdcPyyn6ITp0Xh9s4=";
        System.out.println("加密字符串为:" + message);
        try {
            String messageEn = decrypt(message, privateKey);
            System.out.println("私钥解密后的字符串为:" + messageEn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rsa_key_test() {
        try {
            Map<String, Object> keyMap = this.initKey();
            String publicKey = getPublicKey(keyMap);
            System.out.println(publicKey);
            System.out.println("==========我是分割线==========");
            String privateKey = getPrivateKey(keyMap);
            System.out.println(privateKey);

            String message = "hello world";
            System.out.println("原字符串为:" + message);
            long start = System.currentTimeMillis();
            String messageEn = encrypt(message, publicKey);
            System.out.println("公钥加密后的字符串为:" + messageEn);
            log.info("加密用时{}ms", System.currentTimeMillis() - start);
            start = System.currentTimeMillis();
            String messageDe = decrypt(messageEn, privateKey);
            System.out.println("私钥解密后的字符串为:" + messageDe);
            log.info("解密用时{}ms", System.currentTimeMillis() - start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取公私钥
     */
    @Test
    public void getKey() throws Exception {
        Map<String, Object> map = initKey();
        String publicKey = getPublicKey(map);
        String privateKey = getPrivateKey(map);
        System.out.println("公钥："+publicKey+"\r\n"+"私钥："+privateKey);
    }

    public Map<String, Object> initKey() throws Exception {
        //获得对象 KeyPairGenerator 参数 RSA 1024个字节
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        //通过对象 KeyPairGenerator 获取对象KeyPair
        KeyPair keyPair = keyPairGen.generateKeyPair();

        //通过对象 KeyPair 获取RSA公私钥对象RSAPublicKey RSAPrivateKey
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //公私钥对象存入map中
        Map<String, Object> keyMap = new HashMap<>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    //获得公钥
    public String getPublicKey(Map<String, Object> keyMap) throws Exception {
        //获得map中的公钥对象 转为key对象
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        //编码返回字符串
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    //获得私钥
    public String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        //获得map中的私钥对象 转为key对象
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        //编码返回字符串
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public String encrypt(String str, String publicKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes()));
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 铭文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(str.getBytes());
        //base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }

}
