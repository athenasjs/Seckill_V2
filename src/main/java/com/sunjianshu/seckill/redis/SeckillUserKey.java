package com.sunjianshu.seckill.redis;

public class SeckillUserKey extends BasePrefix{

    //token 有效时间设为2天，和redis中token的过期时间一致
    public static final int TOKEN_EXPIRE = 3600*24*2;

    public SeckillUserKey(String prefix,int expireSeconds) {
        super(expireSeconds,prefix);
    }

    public static SeckillUserKey token = new SeckillUserKey("tk", TOKEN_EXPIRE);
}
