package com.jac.app.job.util;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @description: 生成uuid
 * @author: MengJinyue
 * @create: 2021-04-15 21:26
 **/
public class UUIDUtils {
    @Autowired
    public static final long UUID_FLAG_PAY_ORDER_ID = 0; // UUID标识（支付订单ID）
    public static final long UUID_FLAG_ORDER_ID = 28; // UUID标识（订单ID）
    public static final long UUID_FLAG_REFUND_ORDER_ID = 29; // UUID标识（退款订单ID）
    public static final long UUID_FLAG_OTHER_ID = 30; // UUID标识（其它ID）

    //获取Uuid使用
    private static AtomicLong seq = new AtomicLong(1000);

    /**
     * 本地ip后三位，不足三位用0补足
     */
    private static String LOCAL_IP = null;

    /**
     * 获取UUID
     * @param type UUIDUtils.UUID_FLAG
     * @return
     */
    public static synchronized String getUuid(long type) {
        SnowFlakeUtils idWorker = new SnowFlakeUtils(type, type);
        return idWorker.nextId();
    }
}
