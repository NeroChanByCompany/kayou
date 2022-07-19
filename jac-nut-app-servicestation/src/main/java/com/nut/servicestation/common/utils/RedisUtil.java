package com.nut.servicestation.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
public class RedisUtil
{
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static String redisCode = "utf-8";

    /**
     *  删除
     * @param keys 键
     */
    public long del(final String... keys) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (int i = 0; i < keys.length; i++) {
                    result = connection.del(keys[i].getBytes());
                }
                return result;
            }
        });
    }

    /**
     *  插入key并设置有效期
     * @param key 键
     * @param value 值
     * @param liveTime 有效期（毫秒）
     */
    public void set(final byte[] key, final byte[] value, final long liveTime) {
        redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                if(value.length > 0 && value != null){
                    connection.set(key, value);
                }
                if (liveTime > 0) {
                    connection.expire(key, liveTime);
                }
                return 1L;
            }
        });
    }



    /**
     *  插入key并设置有效期
     * @param key 键
     * @param value 值
     * @param liveTime 有效期 秒
     */
    public void set(String key, String value, long liveTime) {
        this.set(key.getBytes(), value.getBytes(), liveTime);
    }

    /**
     *  插入值
     * @param key 键
     * @param value 值
     */
    public void set(String key, String value) {
        this.set(key, value, 0L);
    }

    /**
     *  插入值
     * @param key 键
     * @param value 值
     */
    public void set(byte[] key, byte[] value) {
        this.set(key, value, 0L);
    }

    /**
     *  获取值
     * @param key 键
     * @return String
     */
    public String get(final String key) {
        return redisTemplate.execute(new RedisCallback<String>() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                try {
                    return new String(connection.get(key.getBytes()), redisCode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    /**
     *  判断是否存在
     * @param key 键
     * @return boolean
     */
    public boolean exists(final String key) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.exists(key.getBytes());
            }
        });
    }

    /**
     *@param key 键
     *@param liveTime 有效期 毫秒
     * 设置有效期
     * */
    public Boolean expire(final String key ,final Long liveTime) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.expire(key.getBytes(), liveTime);
            }
        });
    }
    /**
     * @param key 键
     * @param name hash的key
     * @param value hash的value
     *  插入hash
     * */
    public Boolean hset(final byte [] key,final byte [] name,final byte [] value)
    {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.hSet(key, name, value);
            }
        });
    }

    /**
     * @param key 键
     * @param name hash的键
     * @param value hash的value
     *  插入hash
     * */
    public Boolean hset(String key,String name,String value)
    {
        return this.hset(key.getBytes(), name.getBytes(), value.getBytes());
    }
    /**
     * @param key 键
     * @param name hash的键
     *  获取hash的1条数据
     * */
    public String hget(final byte [] key,final byte [] name)
    {
        return redisTemplate.execute(new RedisCallback<String>() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                try {
                    return new String(connection.hGet(key, name), redisCode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    /**
     * @param key 键
     * @param name hash的键
     *  获取hash的1条数据
     * */
    public String hget(String key,String name)
    {
        return this.hget(key.getBytes(), name.getBytes());
    }

    /**
     * @param key 键
     *  获取hash的所有数据
     * */
    public Map<String,String> hgetAll(final byte [] key)
    {
        return redisTemplate.execute(new RedisCallback<Map>() {
            public Map doInRedis(RedisConnection connection) throws DataAccessException {
                Map<byte[], byte[]> map = connection.hGetAll(key);
                Map<String, String> result = new HashMap<String, String>();
                Set<Map.Entry<byte[], byte[]>> set = map.entrySet();
                try {
                    for (Map.Entry<byte[], byte[]> entry : set) {
                        result.put(new String(entry.getKey(), redisCode), new String(entry.getValue(), redisCode));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return result;
            }
        });
    }

    /**
     * @param key 键
     *  获取hash的所有数据
     * */
    public Map<String,String> hgetAll(String key)
    {
        return this.hgetAll(key.getBytes());
    }

    /**
     * @param key 键
     *  获取hash的所有key值
     * */
    public Set<String> hkeys(final byte [] key)
    {
        return redisTemplate.execute(new RedisCallback<Set<String>>() {
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                try {
                    Set<byte[]> set = connection.hKeys(key);
                    Set<String> newSet = new HashSet<String>();
                    for (byte[] bytes : set) {
                        newSet.add(new String(bytes, redisCode));
                    }
                    return newSet;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
    public Set<String> hkeys(String key)
    {
        return this.hkeys(key.getBytes());
    }
    /**
     * @param key 键
     *  获取hash的所有key值
     * */




    /**
     * ping
     */
    public String ping() {
        return redisTemplate.execute(new RedisCallback<String>() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {

                return connection.ping();
            }
        });
    }

    /**
     * 从redis中获取map类型存储中的数据
     * @return
     */
    public String getStringValueFromRedisMap(String mainKey, String subKey){
        try {
            BoundHashOperations newestHashOps = redisTemplate.boundHashOps(mainKey);
            return (String) newestHashOps.get(subKey);
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }

    }


    /**
     * 根据key进行自增
     * @author wujianbo
     * @param key
     * @param increment
     * @return
     */
    public Long incr(String key,Long increment){
        return redisTemplate.opsForValue().increment(key,increment);
    }

    /**
     * 设置失效时间
     * @param key
     * @param timeout
     * @param timeUnit
     * @return
     */
    public Boolean expire(String key,Long timeout, TimeUnit timeUnit){
        return redisTemplate.boundValueOps(key).expire(timeout, timeUnit);
    }

    public Set getHashKeys(String key) {
        Set res = this.redisTemplate.boundHashOps(key).keys();
        return res;
    }

    private static final long LOCK_TIMEOUT = 20 * 1000; //加锁超时时间 单位毫秒  意味着加锁期间内执行完操作 如果未完成会有并发现象

    /**
     * 取到锁加锁 取不到锁一直等待直到获得锁
     */
    public boolean lock(String lockKey,long lock_timeout) {
        boolean blnFlag = redisTemplate.boundValueOps(lockKey).setIfAbsent(String.valueOf(lock_timeout));
        if(blnFlag){
            redisTemplate.expire(lockKey, LOCK_TIMEOUT, TimeUnit.MILLISECONDS); //设置超时时间，释放内存
        }
        return  blnFlag;
    }

    public void unlock(String lockKey, long lockvalue) {
        Long currt_lock_timeout_Str = Long.valueOf( redisTemplate.opsForValue().get(lockKey));
        if (currt_lock_timeout_Str != null && currt_lock_timeout_Str == lockvalue) {
            redisTemplate.delete(lockKey); //删除键
        }
    }

    public boolean keyValueCompare(String lockKey, long lockvalue) {
        Long currt_lock_timeout_Str = Long.valueOf( redisTemplate.opsForValue().get(lockKey));
        if (currt_lock_timeout_Str != null && currt_lock_timeout_Str == lockvalue) {//如果是加锁者 则删除锁 如果不是则等待自动过期 重新竞争加锁
            return true;
        }
        return false;
    }
}
