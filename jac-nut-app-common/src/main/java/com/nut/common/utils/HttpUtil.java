package com.nut.common.utils;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nut.common.domain.FsManagerGenericResponse;
import com.nut.common.exception.FsManagerException;
import com.nut.common.exception.LocalCloudException;
import com.nut.common.result.HttpCommandResultWithData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.utils
 * @Author: yzl
 * @CreateTime: 2021-06-16 19:43
 * @Version: 1.0
 */
@Slf4j
public class HttpUtil {

    static final String BOUNDARY = "----MyFormBoundarySMFEtUYQG6r5B920";
    private static final String APPLICATION_JSON = "application/json";

    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

    private static final String CHARSET_UTF_8 = "UTF-8";

    private static final int RESULT_SUCCESS_CODE = 200;

    private static final int CONNECT_TIMEOUT = 800 * 1000;

    private static final int SOCKET_TIMEOUT = 800 * 1000;

    public static String get(String url, String param, String token) throws IOException {
        Map<String, String> headers = new HashMap<String, String>();
        if (!StringUtil.isEmpty(token)) {
            headers.put("token", token);
        }
        return get(url, param, headers);
    }

    public static String getHttps(String url, String param, String token) throws IOException {
        Map<String, String> headers = new HashMap<String, String>();
        if (!StringUtil.isEmpty(token)) {
            headers.put("token", token);
        }
        return getHttps(url, param, headers);
    }
    public static String postJson4Authorization(String url, String json, Map<String, String> headers) throws IOException {
        log.info("post json request url : {}, param : {}", url, json);
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(300000);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("accept", "application/json");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

        String encoding = new String(Base64.encode(new String("dayun-dms"+":"+"dayun123").getBytes()));
        conn.setRequestProperty( "Authorization","Basic "+encoding);
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (!StringUtils.isEmpty(json)) {
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();
        }

        conn.connect();

        int code = conn.getResponseCode();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String msg = null;
            while ((msg = br.readLine()) != null) {
                sb.append(msg).append("\n");
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } finally {
            if (br != null)
                br.close();

            conn.disconnect();
        }

        if (code != 200) {
            log.error("request url [ " + url + " ] returns code [ " + code + " ]");
            throw new IOException("request url [ "
                    + url + " ] returns code [ " + code + " ], message [ " + sb.toString() + " ]");
        }

        log.info("get request url : {}, response : {}", url, sb.toString());
        return sb.toString();
    }

    public static String get(String url, String param, Map<String, String> headers) throws IOException {
        log.info("get request url : {}, param : {}", url, param);
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(30000);
        conn.setRequestProperty("contentType", "utf-8");
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (!StringUtils.isEmpty(param)) {
            OutputStream os = conn.getOutputStream();
            os.write(param.getBytes("UTF-8"));
            os.close();
        }

        conn.connect();

        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        int code;
        try {
            code = conn.getResponseCode();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String msg = null;
            while ((msg = br.readLine()) != null) {
                sb.append(msg).append("\n");
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }

        if (code != 200) {
            log.error("request url [ " + url + " ] returns code [ " + code + " ]");
            throw new IOException("request url [ "
                    + url + " ] returns code [ " + code + " ], message [ " + sb.toString() + " ]");
        }

        log.info("get request url : {}, response : {}", url, sb.toString());
        return sb.toString();
    }


    public static String getWithAll(String url, String param, String token) throws IOException {
        Map<String, String> headers = new HashMap<String, String>();
        if (!StringUtil.isEmpty(token)) {
            headers.put("token", token);
        }
        return getWithAll(url, param, headers);
    }

    public static String getWithAll(String url, String param, Map<String, String> headers) throws IOException {
        log.info("get request url : {}, param : {}", url, param);
        URL uri = new URL(url + param);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(30000);
        conn.setRequestProperty("contentType", "utf-8");
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

//        if (!StringUtils.isEmpty(param)) {
//            OutputStream os = conn.getOutputStream();
//            os.write(param.getBytes("UTF-8"));
//            os.close();
//        }

        conn.connect();

        StringBuilder sb = new StringBuilder();
        int code = conn.getResponseCode();
        BufferedReader br = null;
        try {
            if (code == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }
            String msg = null;
            while ((msg = br.readLine()) != null) {
                sb.append(msg).append("\n");
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }


        log.info("get request url : {}, response : {}", url, sb.toString());
        return sb.toString();
    }

    //调用TD
    public static String getTdWithAll(String url, String param, Map<String, String> headers, int timeOut) throws IOException {
        log.info("get request url : {}, param : {}", url, param);
        URL uri = new URL(url + param);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(timeOut);
        conn.setRequestProperty("contentType", "utf-8");
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

//        if (!StringUtils.isEmpty(param)) {
//            OutputStream os = conn.getOutputStream();
//            os.write(param.getBytes("UTF-8"));
//            os.close();
//        }

        conn.connect();

        StringBuilder sb = new StringBuilder();
        int code = conn.getResponseCode();
        BufferedReader br = null;
        try {
            if (code == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }
            String msg = null;
            while ((msg = br.readLine()) != null) {
                sb.append(msg).append("\n");
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }


        log.info("get request url : {}, response : {}", url, sb.toString());
        return sb.toString();
    }

    public static String getHttps(String url, String param, Map<String, String> headers) throws IOException {
        log.info("get request url : {}, param : {}", url, param);
        URL uri = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) uri.openConnection();

        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(30000);
        conn.setRequestProperty("contentType", "utf-8");
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (!StringUtils.isEmpty(param)) {
            OutputStream os = conn.getOutputStream();
            os.write(param.getBytes("UTF-8"));
            os.close();
        }

        conn.connect();

        StringBuilder sb = new StringBuilder();
        int code = conn.getResponseCode();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String msg = null;
            while ((msg = br.readLine()) != null) {
                sb.append(msg).append("\n");
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } finally {
            if (br != null) {
                br.close();
            }

            conn.disconnect();
        }

        if (code != 200) {
            log.error("request url [ " + url + " ] returns code [ " + code + " ]");
            throw new IOException("request url [ "
                    + url + " ] returns code [ " + code + " ], message [ " + sb.toString() + " ]");
        }

        log.info("get request url : {}, response : {}", url, sb.toString());
        return sb.toString();
    }

    public static InputStream getInputStream(String url, String param, Map<String, String> headers) {
        log.info("get request url : {}, param : {}", url, param);
        InputStream result = null;
        URL uri = null;
        HttpURLConnection conn = null;

        try {
            uri = new URL(url);
            conn = (HttpURLConnection) uri.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setReadTimeout(30000);
            conn.setRequestProperty("contentType", "utf-8");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (!StringUtils.isEmpty(param)) {
                OutputStream os = conn.getOutputStream();
                os.write(param.getBytes("UTF-8"));
                os.close();
            }

            conn.connect();

            int code = conn.getResponseCode();

            if (code == 200) {
                result = conn.getInputStream();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
//            conn.disconnect();
        }

        return result;
    }


    public static String post(String url, String param, String token) throws IOException {
        Map<String, String> headers = new HashMap<String, String>();
        if (!StringUtil.isEmpty(token)) {
            headers.put("token", token);
        }
        return post(url, param, headers);
    }

    public static String mapToParams(Map<String, Object> params) {
        if (params == null || params.size() == 0)
            return "";

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append("&").append(entry.getKey()).append("=").append(String.valueOf(entry.getValue()));
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(0);
            return sb.toString();
        }

        return "";
    }

    public static String post(String url, String param, Map<String, String> headers) throws IOException {
        log.info("post request url : {}, param : {}", url, param);
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(30000);
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("contentType", "utf-8");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (!StringUtils.isEmpty(param)) {
            OutputStream os = conn.getOutputStream();
            os.write(param.getBytes("UTF-8"));
            os.close();
        }

        conn.connect();

        int code = conn.getResponseCode();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String msg = null;
            while ((msg = br.readLine()) != null) {
                sb.append(msg).append("\n");
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } finally {
            if (br != null)
                br.close();

            conn.disconnect();
        }

        if (code != 200) {
            log.error("request url [ " + url + " ] returns code [ " + code + " ]");
            throw new IOException("request url [ "
                    + url + " ] returns code [ " + code + " ], message [ " + sb.toString() + " ]");
        }

        log.info("get request url : {}, response : {}", url, sb.toString());
        return sb.toString();
    }

    public static String postJson(String url, String json, String token) throws IOException {
        Map<String, String> headers = new HashMap<String, String>();
        if (!StringUtil.isEmpty(token)) {
            headers.put("token", token);
        }
        return postJson(url, json, headers);
    }

    public static String postJson(String url, String json, Map<String, String> headers) throws IOException {
        log.info("post json request url : {}, param : {}", url, json);
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(300000);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("accept", "application/json");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (!StringUtils.isEmpty(json)) {
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();
        }

        conn.connect();

        int code = conn.getResponseCode();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String msg = null;
            while ((msg = br.readLine()) != null) {
                sb.append(msg).append("\n");
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } finally {
            if (br != null)
                br.close();

            conn.disconnect();
        }

        if (code != 200) {
            log.error("request url [ " + url + " ] returns code [ " + code + " ]");
            throw new IOException("request url [ "
                    + url + " ] returns code [ " + code + " ], message [ " + sb.toString() + " ]");
        }

        log.info("get request url : {}, response : {}", url, sb.toString());
        return sb.toString();
    }

    /**
     * 文件上传
     *
     * @param contentByte 文件内容
     * @param fileName    文件名称
     * @param mimetype    文件类型
     * @param uuid
     * @param type        类型
     * @return
     */
    public static String uploadFile(String requestUrl, byte[] contentByte, String fileName, String mimetype, String uuid, String type) {
        String httpResult = null;
        String url = null;
        Map<String, String> mapParam = new HashMap<String, String>();
        try {
            //{uuid}/create
            String uploadUrl = requestUrl + uuid + "/" + type;
            mapParam.put("data", Base64Utils.encodeToString(contentByte));
            Map<String, String> attributesParam = new HashMap<String, String>();
            attributesParam.put("filename", fileName);
            Map<String, String> headerpParam = new HashMap<String, String>();
            headerpParam.put("Accept", "application/json");
            headerpParam.put("mimetype", mimetype);
            httpResult = postJson(uploadUrl, JsonUtil.toJson(mapParam), headerpParam);
            Map<String, Object> mapResult = JsonUtil.toMap(httpResult);
            if (mapResult.containsKey("resultCode") && (Integer) mapResult.get("resultCode") == 200) {
                url = (String) mapResult.get("url");
                if (StringUtil.isEmpty(url)) {
                    httpResult = url;
                    log.debug(fileName + " upload fail!" + httpResult);
                } else {
                    url = url + "?mimetype=" + mimetype;
                    log.debug(fileName + " upload success!" + httpResult);
                }
            } else {
                log.debug(fileName + " upload fail!" + httpResult);
            }
            log.debug(JsonUtil.toJson(mapParam));
        } catch (Exception e) {
            try {
                log.error(JsonUtil.toJson(mapParam));
            } catch (JsonProcessingException e1) {
                log.error(fileName + " json formatter error!", e);
            }
            log.error(fileName + " upload fail!" + httpResult, e);
        }
        return url;
    }



    public static String postJsonWithAll(String url, String json, String token) throws IOException {
        Map<String, String> headers = new HashMap<String, String>();
        if (!StringUtil.isEmpty(token)) {
            headers.put("token", token);
        }
        return postJsonWithAll(url, json, headers);
    }

    public static String postJsonWithAll(String url, String json, Map<String, String> headers) throws IOException {
        log.info("post json request url : {}, param : {}", url, json);
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(30000);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (!StringUtils.isEmpty(json)) {
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();
        }

        conn.connect();

        int code = conn.getResponseCode();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            if (code == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }
            String msg = null;
            while ((msg = br.readLine()) != null) {
                sb.append(msg).append("\n");
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } finally {
            if (br != null)
                br.close();

            conn.disconnect();
        }
//        if (code != 200) {
//            log.error("request url [ " + url + " ] returns code [ " + code + " ]");
//            log.error("request param [ " + json + " ]");
//            throw new IOException("request url [ "
//                    + url + " ] returns code [ " + code + " ], message [ " + sb.toString() + " ]");
//        }

        log.info("get request url : {}, response : {}", url, sb.toString());
        return sb.toString();
    }

    public static String postWithRequest(String url, Map<String, String> param, Map<String, String> headers) throws Exception {
        log.info("post json request url : {}, param : {}", url, JsonUtil.toJson(param));
        URL uri = new URL(url);
        StringBuffer params = new StringBuffer();
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(30000);
//        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
//        conn.setRequestProperty("accept", "*/*");
//        conn.setRequestProperty("connection", "Keep-Alive");
//        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded; charset=UTF-8");

        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        if (param != null) {
            OutputStream os = conn.getOutputStream();
            for(String key : param.keySet()) {
                params.append("&").append(key).append("=").append(URLEncoder.encode(param.get(key), "UTF-8"));
            }
            params.deleteCharAt(0);
            String sendInfo = params.toString();
            os.write(sendInfo.getBytes("UTF-8"));
            os.flush();
            os.close();
        }

        conn.connect();

        int code = conn.getResponseCode();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            if (code == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                log.info("[postWithRequest]code:"+code);
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }
            String msg = null;
            while ((msg = br.readLine()) != null) {
                sb.append(msg).append("\n");
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } finally {
            if (br != null)
                br.close();

            conn.disconnect();
        }

        if (code != 200) {
            log.error("request url [ " + url + " ] returns code [ " + code + " ]");
            log.error("request param [ " + params + " ]");
            throw new IOException("request url [ "
                    + url + " ] returns code [ " + code + " ], message [ " + sb.toString() + " ]");
        }
        log.info("get request url : {}, response : {}", url, sb.toString());
        return sb.toString();
    }

    public static String postWithRequestIntegral(String url, Map<String, String> param, Map<String, String> headers) throws Exception {
        log.info("post json request url : {}, param : {}", url, JsonUtil.toJson(param));
        URL uri = new URL(url);
        StringBuffer params = new StringBuffer();
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(30000);

        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        if (param != null) {
            OutputStream os = conn.getOutputStream();
            for(String key : param.keySet()) {
                params.append("&").append(key).append("=").append(URLEncoder.encode(param.get(key), "UTF-8"));
            }
            params.deleteCharAt(0);
            String sendInfo = params.toString();
            os.write(sendInfo.getBytes("UTF-8"));
            os.flush();
            os.close();
        }

        conn.connect();

        int code = conn.getResponseCode();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            if (code == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }
            String msg = null;
            while ((msg = br.readLine()) != null) {
                sb.append(msg).append("\n");
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } finally {
            if (br != null)
                br.close();

            conn.disconnect();
        }

        if (code != 200) {
            log.error("request url [ " + url + " ] returns code [ " + code + " ]");
            log.error("request param [ " + params + " ]");
            throw new IOException("request url [ "
                    + url + " ] returns code [ " + code + " ], message [ " + sb.toString() + " ]");
        }
        log.info("get request url : {}, response : {}", url, sb.toString());
        return sb.toString();
    }


    public static String postWithRequestNoSSL(String url, Map<String, String> param, Map<String, String> headers) throws Exception {
        log.info("post json request url : {}, param : {}", url, JsonUtil.toJson(param));
        URL uri = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) uri.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(30000);
//        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
//        conn.setRequestProperty("accept", "*/*");
//        conn.setRequestProperty("connection", "Keep-Alive");
//        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded; charset=UTF-8");

        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        if (param != null) {
            OutputStream os = conn.getOutputStream();
            StringBuffer params = new StringBuffer();
            for(String key : param.keySet()) {
                params.append("&").append(key).append("=").append(URLEncoder.encode(param.get(key), "UTF-8"));
            }
            params.deleteCharAt(0);
            String sendInfo = params.toString();
            os.write(sendInfo.getBytes("UTF-8"));
            os.flush();
            os.close();
        }

        conn.connect();

        int code = conn.getResponseCode();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            if (code == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }
            String msg = null;
            while ((msg = br.readLine()) != null) {
                sb.append(msg).append("\n");
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } finally {
            if (br != null)
                br.close();

            conn.disconnect();
        }


        log.info("get request url : {}, response : {}", url, sb.toString());
        return sb.toString();
    }

    /**
     * REST风格get请求
     * @param url
     * @param param
     * @return
     */
    public static String getRequestByRest(String url, String param) {
        String jsonStr = null;
        try {
            log.info("请求地址：" + url + param);
            RestTemplate restTemplate = new RestTemplate();
            jsonStr = restTemplate.getForObject(url + param, String.class);
        } catch (Exception e) {
            log.error("请求地址：" + url + param);
            log.error("REST请求接口异常：" + e);
        } finally {
            return jsonStr;
        }
    }


    /**
     * REST风格post请求
     * @param url
     * @param params
     * @return
     */
    public static String postRequestByRest(String url, String params) {
        String jsonStr = null;
        try {
            RestTemplate restTemplate = new RestTemplate();

            log.info("请求地址：" + url);
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());

            HttpEntity<String> formEntity = new HttpEntity<String>(params, headers);
            jsonStr = restTemplate.postForObject(url, formEntity, String.class);
        } catch (Exception e) {
            log.error("请求地址：" + url);
            log.error("请求参数：" + params);
            log.error("REST请求接口异常：" + e);
        } finally {
            return jsonStr;
        }
    }

    /**
     * REST风格post请求
     * @param url
     * @param params
     * @return
     */
    public static HttpCommandResultWithData postRequestByRestForHcrwd(String url, String params) {

        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            RestTemplate restTemplate = new RestTemplate();

            log.info("请求地址：" + url);
            log.info("请求参数：" + params);
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());

            HttpEntity<String> formEntity = new HttpEntity<String>(params, headers);
            result = restTemplate.postForObject(url, formEntity, HttpCommandResultWithData.class);
        } catch (Exception e) {
            log.error("请求地址：" + url);
            log.error("请求参数：" + params);
            log.error("REST请求接口异常：" + e);
        } finally {
            return result;
        }
    }

    /**
     * 获取Http请求结果
     *
     * @param url
     * @param param
     * @return
     */
    public static JSONObject getHttpResult(String url, String param) {
        System.out.println(url + param);
        JSONObject resultObject = null;
        try {
            Map headers = new HashMap();
            headers.put("Accept", "application/json");
            String resultStr = HttpUtil.postJsonWithAll(url, param, headers);
            resultObject = (JSONObject) JSONObject.parse(resultStr);
        } catch (HttpClientErrorException e) {
            log.error("调用HTTP请求异常:" + url + " \n异常信息:" + e.getMessage(), e);
            resultObject = (JSONObject) JSONObject.parse(e.getResponseBodyAsString());
        } finally {
            return resultObject;
        }
    }

    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    /**
     * https 忽略证书
     */
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * 忽略证书的https请求
     * @param url
     * @param param
     * @param headers
     * @return
     * @throws Exception
     */
    public static String postHttpsWithRequest(String url, Map<String, String> param, Map<String, String> headers) throws Exception {
        log.info("post json request url : {}, param : {}", url, JsonUtil.toJson(param));
        //设置忽略证书
//        HttpsURLConnection.setDefaultHostnameVerifier(DO_NOT_VERIFY);
        URL uri = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) uri.openConnection();
        //设置忽略证书
//        if("https".equalsIgnoreCase(uri.getProtocol())){
//            SslUtils.ignoreSsl();
//        }
        conn.setHostnameVerifier(DO_NOT_VERIFY);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(30000);
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded; charset=UTF-8");
//        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        if (param != null) {
            OutputStream os = conn.getOutputStream();
            StringBuffer params = new StringBuffer();
            for(String key : param.keySet()) {
                params.append("&").append(key).append("=").append(URLEncoder.encode(param.get(key), "UTF-8"));
            }
            params.deleteCharAt(0);
            String sendInfo = params.toString();
            os.write(sendInfo.getBytes("UTF-8"));
            os.flush();
            os.close();
        }

        conn.connect();

        int code = conn.getResponseCode();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            if (code == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }
            String msg = null;
            while ((msg = br.readLine()) != null) {
                sb.append(msg).append("\n");
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } finally {
            if (br != null)
                br.close();

            conn.disconnect();
        }

        log.info("get request url : {}, response : {}", url, sb.toString());
        return sb.toString();
    }

    /**
     * description Http请求get
     **/
    public static String http4Get(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            log.info("请求url：" + urlNameString);
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            log.info("请求返回的结果：" + result);
        } catch (Exception e) {
            log.error("发送GET请求出现异常！" + e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                log.error("关闭流异常，异常信息：" + e2);
            }
        }
        return result;
    }
    /**
     * 添加space
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }

    /**
     * 文件服务器上传文件
     * @param url
     * @param map
     * @param filePath
     * @param inputStream
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T  streamingFsManager(String url, Map<String, String> map,
                                            String filePath, InputStream inputStream, TypeReference<FsManagerGenericResponse<T>> typeReference){
        FsManagerGenericResponse<T> response = doPostSubmitBody(url, map, filePath, inputStream, typeReference);
        if (response == null || response.getStatus() != 200){
            throw new FsManagerException(response == null ? null : response.getMessage());
        }
        return response.getData();
    }


    /**
     * 上传文件公共方法
     * @param url
     * @param map
     * @param filePath
     * @param inputStream
     * @param tTypeReference
     * @param <T>
     * @return
     */
    public static <T> T doPostSubmitBody(String url, Map<String, String> map,
                                         String filePath, InputStream inputStream,
                                         TypeReference<T> tTypeReference) {
        // 设置三个常用字符串常量：换行、前缀、分界线（NEWLINE、PREFIX、BOUNDARY）；
        final String NEWLINE = "\r\n"; // 换行，或者说是回车
        final String PREFIX = "--"; // 固定的前缀
        final String BOUNDARY = "#"; // 分界线，就是上面提到的boundary，可以是任意字符串，建议写长一点，这里简单的写了一个#
        HttpURLConnection httpConn = null;
        BufferedInputStream bis = null;
        DataOutputStream dos = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte [] bytes = IOUtils.toByteArray(inputStream);
            // 实例化URL对象。调用URL有参构造方法，参数是一个url地址；
            URL urlObj = new URL(url);
            // 调用URL对象的openConnection()方法，创建HttpURLConnection对象；
            httpConn = (HttpURLConnection) urlObj.openConnection();
            // 调用HttpURLConnection对象setDoOutput(true)、setDoInput(true)、setRequestMethod("POST")；
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod("POST");
            // 设置Http请求头信息；（Accept、Connection、Accept-Encoding、Cache-Control、Content-Type、User-Agent），不重要的就不解释了，直接参考抓包的结果设置即可
            httpConn.setUseCaches(false);
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            httpConn.setRequestProperty("Cache-Control", "no-cache");
            // 这个比较重要，按照上面分析的拼装出Content-Type头的内容
            httpConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            // 这个参数可以参考浏览器中抓出来的内容写，用chrome或者Fiddler抓吧看看就行
            httpConn.setRequestProperty(
                    "User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30)");
            // 调用HttpURLConnection对象的connect()方法，建立与服务器的真实连接；
            httpConn.connect();

            // 调用HttpURLConnection对象的getOutputStream()方法构建输出流对象；
            dos = new DataOutputStream(httpConn.getOutputStream());
            // 获取表单中上传控件之外的控件数据，写入到输出流对象（根据上面分析的抓包的内容格式拼凑字符串）；
            if (map != null && !map.isEmpty()) { // 这时请求中的普通参数，键值对类型的，相当于上面分析的请求中的username，可能有多个
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey(); // 键，相当于上面分析的请求中的username
                    String value = map.get(key); // 值，相当于上面分析的请求中的sdafdsa
                    dos.writeBytes(PREFIX + BOUNDARY + NEWLINE); // 像请求体中写分割线，就是前缀+分界线+换行
                    dos.writeBytes("Content-Disposition: form-data; "
                            + "name=\"" + key + "\"" + NEWLINE); // 拼接参数名，格式就是Content-Disposition: form-data; name="key" 其中key就是当前循环的键值对的键，别忘了最后的换行
                    dos.writeBytes(NEWLINE); // 空行，一定不能少，键和值之间有一个固定的空行
                    dos.writeBytes(URLEncoder.encode(value, "UTF-8")); // 将值写入
                    // 或者写成：dos.write(value.toString().getBytes(charset));
                    dos.writeBytes(NEWLINE); // 换行
                } // 所有循环完毕，就把所有的键值对都写入了
            }

            // 获取表单中上传附件的数据，写入到输出流对象（根据上面分析的抓包的内容格式拼凑字符串）；
            if (bytes != null && bytes.length > 0) {
                dos.writeBytes(PREFIX + BOUNDARY + NEWLINE);// 像请求体中写分割线，就是前缀+分界线+换行
                String fileName = filePath.substring(filePath
                        .lastIndexOf(File.separatorChar) + 1); // 通过文件路径截取出来文件的名称，也可以作文参数直接传过来
                // 格式是:Content-Disposition: form-data; name="请求参数名"; filename="文件名"
                // 我这里吧请求的参数名写成了uploadFile，是死的，实际应用要根据自己的情况修改
                // 不要忘了换行
                dos.writeBytes("Content-Disposition: form-data; " + "name=\""
                        + "file" + "\"" + "; filename=\"" + fileName
                        + "\"" + NEWLINE);
                // 换行，重要！！不要忘了
                dos.writeBytes(NEWLINE);
                // 上传文件的内容
                dos.write(bytes);
                // 最后换行
                dos.writeBytes(NEWLINE);
            }
            // 最后的分割线，与前面的有点不一样是前缀+分界线+前缀+换行，最后多了一个前缀
            dos.writeBytes(PREFIX + BOUNDARY + PREFIX + NEWLINE);
            dos.flush();

            // 调用HttpURLConnection对象的getInputStream()方法构建输入流对象；
            byte[] buffer = new byte[8 * 1024];
            int c;

            StringBuilder sb = new StringBuilder();
            BufferedReader br = null;
            try {
                if (httpConn.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
                    String msg = null;
                    while ((msg = br.readLine()) != null) {
                        sb.append(msg).append("\n");
                    }

                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                } else {
                    throw new FsManagerException(httpConn.getResponseMessage());
                }
            } finally {
                if (br != null) {
                    br.close();
                }
            }

            return JsonUtil.fromJson(sb.toString(), tTypeReference);
        } catch (Exception e) {
            log.error("文件上传错误！", e);
        } finally {
            try {
                if (dos != null){
                    dos.close();
                }
                if (bis != null){
                    bis.close();
                }
                if (baos != null){
                    baos.close();
                }
                httpConn.disconnect();
            } catch (Exception e) {
                log.error("文件上传，关闭时错误！", e);
            }
        }
        return null;
    }

    /**
     * post json
     * @param url
     * @param requestBody
     * @param typeReference
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T,R> T postJsonRequest(String url, R requestBody,TypeReference<HttpCommandResultWithData<T>> typeReference){
        HttpCommandResultWithData<T> httpCommandResultWithData = new HttpCommandResultWithData<>();
        try {
            String json = JsonUtil.toJson(requestBody);
            String res = postJson(url, json, new HashMap<>());
            httpCommandResultWithData = JsonUtil.fromJson(res, typeReference);
            if (400 == httpCommandResultWithData.getResultCode()) {
                log.error("调用位置云服务器参数错误：{}", httpCommandResultWithData.toString());
                throw new LocalCloudException(httpCommandResultWithData.getMessage());
            } else if (500 == httpCommandResultWithData.getResultCode()) {
                throw new LocalCloudException();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return httpCommandResultWithData.getData();
    }

    /**
     * post 请求 https 接口
     * @param url
     * @param param
     * @return
     * @throws Exception
     */
    public static String postHttps(String url, JSONObject param) throws Exception {
        log.info("postHttps json request url : {}, param : {}", url, param);
        String resp = "";
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).setConnectTimeout(CONNECT_TIMEOUT)
                            .setSocketTimeout(SOCKET_TIMEOUT).build())
                    .build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            // 绑定到请求 Entry
            StringEntity se = new StringEntity(param.toString(), CHARSET_UTF_8);
            se.setContentType(CONTENT_TYPE_TEXT_JSON);
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
            httpPost.setEntity(se);
            // 发送请求
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == RESULT_SUCCESS_CODE) {
                // 得到应答的字符串，这也是一个 JSON 格式保存的数据
                resp = EntityUtils.toString(httpResponse.getEntity(), CHARSET_UTF_8);
            } else {
                log.warn("postHttps request url : {}, response's errorCode is : {}", url, httpResponse.getStatusLine()
                        .getStatusCode());
            }
        } catch (KeyManagementException e) {
            log.error("HttpUtil#postRequest#KeyManagementException is error", e);
            throw new Exception(e);
        } catch (KeyStoreException e) {
            log.error("HttpUtil#postRequest#KeyStoreException is error", e);
            throw new Exception(e);
        } catch (NoSuchAlgorithmException e) {
            log.error("HttpUtil#postRequest#NoSuchAlgorithmException is error", e);
            throw new Exception(e);
        } catch (UnsupportedEncodingException e) {
            log.error("HttpUtil#postRequest#UnsupportedEncodingException is error", e);
            throw new Exception(e);
        } catch (ClientProtocolException e) {
            log.error("HttpUtil#postRequest#ClientProtocolException is error", e);
            throw new Exception(e);
        } catch (IOException e) {
            log.error("HttpUtil#postRequest#IOException is error", e);
            throw new Exception(e);
        }
        log.info("postHttps request url : {}, response : {}", url, resp);
        return resp;
    }


}
