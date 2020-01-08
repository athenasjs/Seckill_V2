package com.sunjianshu.seckill.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisService {
    @Autowired
    private JedisPool jedisPool;

    /**
     * 获取单个对象
     * @param keyPrefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix keyPrefix,String key, Class<T> clazz){
        Jedis jedis = null;
        try{
           jedis =  jedisPool.getResource();
           String realKey = keyPrefix.getPrefix() + key;
           String str = jedis.get(realKey);
           T t = stringToBean(str, clazz);
           return t;
        }finally{
            returnToPool(jedis); //释放连接，否则可能导致连接不够
        }

    }

    /**
     * 设置对象
     * @param keyPrefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix keyPrefix, String key, T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            //生成真正的key
            String realKey = keyPrefix.getPrefix() +key;
            int seconds = keyPrefix.expireSeconds(); //设置过期时间
            if(str == null || str.length() <=0){
                return false;
            }
            if(seconds <= 0){
                jedis.set(realKey, str);
            }else{
                jedis.setex(realKey, seconds, str);
            }
            return true;
        }finally{
            returnToPool(jedis);
        }
    }

    /*
    bean对象转化为字符串
     */
    public static <T> String beanToString(T value) {
        if(value == null){
            return null;
        }
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz==Integer.class){  //这里做一些判断，如果为一些基本类型，直接返回
            return "" + value;
        }else if(clazz == String.class){
            return (String)value;
        }else if(clazz == long.class || clazz == Long.class){
            return "" + value;
        }else if(clazz == Boolean.class || clazz == boolean.class)
            return "" + value;
        else {
            return JSON.toJSONString(value);  //其他类型，bean转化为一个json
        }
    }

    /*
    字符串转化为Bean对象,用fastjson把对象转化成json字符串写到redis中
     */
    @SuppressWarnings("unchecked")
    public static  <T> T stringToBean(String str, Class<T> clazz) {
        //参数校验必不可少
        if(str == null || str.length() <=0 || clazz == null){
            return null;
        }
        if(clazz == int.class || clazz == Integer.class){
            return (T)Integer.valueOf(str);
        }else if(clazz == String.class){
            return (T)str;
        }else if(clazz == long.class || clazz == Long.class){
            return (T)Long.valueOf(str);
        }else if(clazz == Boolean.class || clazz == boolean.class){
            return (T)Boolean.valueOf(str);
        }else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    /**
     * 判断key是否存在
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exists(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.exists(realKey);
        }finally{
            returnToPool(jedis);
        }
    }

    /**
     * 增加值
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.incr(realKey);
        }finally{
            returnToPool(jedis);
        }
    }

    /**
     * 减少值
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.decr(realKey);
        }finally{
            returnToPool(jedis);
        }
    }

    /*
    删除
     */
    public boolean delete(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            long ret = jedis.del(realKey);  //返回一个long
            return ret > 0 ;
        }finally{
            returnToPool(jedis);
        }
    }

    public boolean delete(KeyPrefix prefix){
        if(null == prefix){
            return false;
        }
        List<String> keys = scanKeys(prefix.getPrefix());
        if(null == keys || keys.size() <= 0){
            return true;
        }
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.del(keys.toArray(new String[0]));  //删除一个字符串key数组
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }finally{
            returnToPool(jedis);
        }

    }

    public List<String> scanKeys(String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            List<String> keys = new ArrayList<>();
            String cursor = "0";
            ScanParams sp = new ScanParams();
            sp.match("*" + key + "*");
            sp.count(100);
            do{
                ScanResult<String> ret = jedis.scan(cursor, sp);
                List<String> result = ret.getResult();
                if(result != null && result.size() > 0){
                    keys.addAll(result);
                }
                //再处理cursor
            }while(!cursor.equals("0"));
            return keys;
        }finally {
            if(null != jedis){
                returnToPool(jedis);
            }
        }
    }
    private void returnToPool(Jedis jedis) {
        if(jedis != null){
            jedis.close(); //返回到连接池
        }
    }



}
