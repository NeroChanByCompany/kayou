package com.nut.driver.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nut.common.exception.FsManagerException;
import com.nut.common.utils.JsonUtil;
import com.nut.driver.app.domain.FsManagerGenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);


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
                Integer code = httpConn.getResponseCode();
                System.out.println(code);
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
            }
        }
        return null;
    }

    public static String get(String url, String param, Map<String, String> headers) throws IOException {
        logger.info("get request url : {}, param : {}", url, param);
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(30000);
        conn.setRequestProperty("contentType", "application/x-www-form-urlencoded");
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
            logger.error("request url [ " + url + " ] returns code [ " + code + " ]");
            throw new IOException("request url [ "
                    + url + " ] returns code [ " + code + " ], message [ " + sb.toString() + " ]");
        }

        logger.info("get request url : {}, response : {}", url, sb.toString());
        return sb.toString();
    }

    public static String post(String url, String param, Map<String, String> headers) throws IOException {
        logger.info("post request url : {}, param : {}", url, param);
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(30000);
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("contentType", "application/json");
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
            logger.error("request url [ " + url + " ] returns code [ " + code + " ]");
            throw new IOException("request url [ "
                    + url + " ] returns code [ " + code + " ], message [ " + sb.toString() + " ]");
        }

        logger.info("get request url : {}, response : {}", url, sb.toString());
        return sb.toString();
    }

    /**
     * postForEntity方式
     * @param url
     * @param requestBody
     * @return
     * @author MengJinyue
     */
    public static Map<String, Object> postForEntity(String url, String requestBody,Map<String, String> requstHeader){
        log.info("===请求地址："+url);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        HttpHeaders headers = new HttpHeaders();//请求头
        headers.setContentType(MediaType.APPLICATION_JSON);// 以json的方式提交
        if (!Objects.isNull(requstHeader)){//请求头自定义参数
            Iterator it = requstHeader.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry entry = (Map.Entry) it.next() ;
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                headers.add(key,value);
                log.info("请求头"+key+"："+value);
            }
        }
        // 将请求头部和参数合成一个请求
        org.springframework.http.HttpEntity<String> requestEntity = new org.springframework.http.HttpEntity<>(requestBody, headers);
        // 执行HTTPS请求
        Map<String,Object> response = restTemplate.postForEntity(url,requestEntity,Map.class).getBody();
        log.info("===入参报文，"+ requestBody);
        log.info("===响应结果，"+GsonUtils.objectToJson(response));
        return response;
    }

}
