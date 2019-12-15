package com.sunjianshu.seckill.redis;

public abstract class BasePrefix implements KeyPrefix {

    private int expireSeconds;  //过期时间
    private String prefix;

    public BasePrefix(int expireSeconds, String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public BasePrefix(String prefix){
        this.expireSeconds = 0;  //永不过期
        this.prefix = prefix;
    }
    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className +":" + prefix;
    }
}
