package com.nut.servicestation.common.constants;

/**
 * @description: 全局常量
 * @author: hcb
 * @createTime: 2021/01/20 10:58
 * @version:1.0
 */
public class CommonConstants {

    /**
     * 车厂默认租户ID
     */
    public static final Long FACTORY_DEFAULT_ORG_ID = 1L;
    /**
     * 默认系统用户ID
     */
    public static final Long SYSTEM_DEFAULT_USER_ID = 1L;

    /**
     * 访问端来源：1-浏览器端、2-Android端、3-iOS端、4-后台服务端
     **/
    public static final String REQ_SOURCE_TYPE_KEY = "Source-Type";
    public static final String REQ_SOURCE_TYPE_WEB = "1";
    public static final String REQ_SOURCE_TYPE_ANDROID = "2";
    public static final String REQ_SOURCE_TYPE_IOS = "3";
    public static final String REQ_SOURCE_TYPE_SERVICE = "4";

    /**
     * Feign调用返回参数
     **/
    public static final String FEIGN_RESULT_CODE = "code";
    public static final String FEIGN_RESULT_MESSAGE = "message";
    public static final String FEIGN_RESULT_DATA = "data";
    public static final int FEIGN_RESULT_SUCCESS = 200;

    /**
     * 日志ID
     **/
    public static final String LOG_TRACE_ID = "traceId";

    /**
     * 字符串"null"
     **/
    public static final String NULL_STRING = "null";

    /**
     * 字符串"unknown"
     **/
    public static final String UNKNOWN_STRING = "unknown";

    /**
     * 默认字符编码
     **/
    public static final String DEFAULT_CHARACTER_ENCODING = "utf-8";

    /**
     * Http请求方式
     **/
    public static final String HTTP_REQUEST_METHOD_GET = "GET";
    public static final String HTTP_REQUEST_METHOD_POST = "POST";

    /**
     * Http请求头
     **/
    public static final String HTTP_HEADER_X_FORWARDED_FOR = "x-forwarded-for";
    public static final String HTTP_HEADER_PROXY_CLIENT_IP = "Proxy-Client-IP";
    public static final String HTTP_HEADER_WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";


    /**
     * 首页轮播图 1
     * 精选好车 2
     */
    public static final Integer PHOTO_HOME = 1;
    public static final Integer PHOTO_GOOD_CAR = 2;

    /**
     * 购车方式
     * 1 全款购车 2 金融购车
     */
    public static final Integer SHOPPING_WAY_FULL = 1;
    public static final Integer SHOPPING_WAY_LOAN = 2;

    /**
     * 文件分类 1 购车合同 2 尾款凭证
     */
    public static final Integer FILE_CONTRACT = 1;
    public static final Integer FILE_VOUCHER = 2;

    /**
     * 账号类型
     * 0-经销商，1-车厂
     */
    public static final Integer USER_TYPE_DEALER = 0;
    public static final Integer USER_TYPE_CAR = 1;

    /**
     * 服务版本信息，每次发版时，修改此值
     */
    public static final String APP_INFO = "1.0.0_20210607";
}

