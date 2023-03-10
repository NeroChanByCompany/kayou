package com.nut.driver.common.utils.cmb;

import com.nut.driver.common.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @author cmb demo
 */
@Slf4j
public class RestTemplateUtils {
    static {
        disableSslVerification();
    }

    private static void disableSslVerification() {
        try
        {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };
            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> postForEntity(String url, String requestBody, Map<String, String> apiHeader){
        log.info("===???????????????"+url);
        RestTemplate restTemplate = getRestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        HttpHeaders headers = new HttpHeaders();

        // ???json???????????????
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!Objects.isNull(apiHeader)){
            Iterator it = apiHeader.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry entry = (Map.Entry) it.next() ;
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                headers.add(key,value);
                log.info("?????????"+key+"???"+value);
            }
        }

        // ??????????????????????????????????????????
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        // ??????HTTPS??????
        Map<String,String> response = restTemplate.postForEntity(url,requestEntity,Map.class).getBody();
        log.info("===???????????????"+ requestBody);
        log.info("===???????????????"+ GsonUtils.objectToJson(response));
        return response;
    }
    public static RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    public static HttpHeaders getHttpHeaders(){
        return new HttpHeaders();
    }

    /**
     * ????????? URL ?????? POST??????????????????application/x-www-form-urlencoded???
     * url ??????????????? URL
     * params ?????????????????????
     * @return ???????????????????????????
     */
    @SuppressWarnings("unused")
    public static String sendPost(String url, Map<String,String> params) {
        log.info("===???????????????"+url);
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new    StringBuilder();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
            // ??????POST??????????????????????????????
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST??????
            conn.setRequestMethod("POST");
            // ???????????????????????????
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            // ??????URLConnection????????????????????????
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // ??????????????????
            if (params != null) {
                StringBuilder param = new    StringBuilder();
                for (Map.Entry<   String,    String> entry : params.entrySet()) {
                    if(param.length()>0){
                        param.append("&");
                    }
                    param.append(entry.getKey());
                    param.append("=");
                    param.append(entry.getValue());
                    log.info(entry.getKey()+":"+entry.getValue());
                }
                log.info("===???????????????"+ param.toString());
                out.write(param.toString());
            }
            // flush??????????????????
            out.flush();
            // ??????BufferedReader??????????????????URL?????????
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }

            log.info("===???????????????"+ GsonUtils.objectToJson(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //??????finally?????????????????????????????????
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result.toString();
    }
}
