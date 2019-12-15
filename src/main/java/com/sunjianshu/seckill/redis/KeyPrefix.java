package com.sunjianshu.seckill.redis;

/*
对缓存Key做统一封装 模板模式
 */
public interface KeyPrefix {

     int expireSeconds();  //过期时间

     String getPrefix();  //前缀
}
