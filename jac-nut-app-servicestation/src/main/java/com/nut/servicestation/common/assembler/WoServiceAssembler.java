package com.nut.servicestation.common.assembler;


import com.nut.common.utils.DateUtil;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.common.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 工单业务装配类
 */
@Slf4j
public class WoServiceAssembler {

    /**
     * 工单号获取
     *
     * @param redisUtil   Redis工具类
     * @param woType      工单类型
     * @param orderWay    预约方式
     * @param paddingBit  连番位数
     * @param stationId   服务站id
     * @param stationCode 服务站编码
     * @return 工单号
     */
    public static String getDfWoCode(RedisUtil redisUtil, String woType, String orderWay, Integer paddingBit, String stationId, String stationCode) {
        // 新增工单号前缀
        String redisKey = "Dongfeng_WoCode_" + stationId + ":" + DateUtil.getNowDate_yyyyMMdd();
        // 判断redis
        Long redisSeq = redisUtil.incr(redisKey, 1L);
        // key为初始值，需要设置失效时间:一天
        if (redisSeq == 1L) {
            redisUtil.expire(redisKey, 1L, TimeUnit.DAYS);
        }
        return woType + orderWay + stationCode + DateUtil.getNowDate_yyyyMMddHHmm() +
                StringUtil.padRight(String.valueOf(redisSeq), paddingBit, '0');
    }
}