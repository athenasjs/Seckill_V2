package com.sunjianshu.seckill.redis;

public class AccessKey extends BasePrefix  {
    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public AccessKey(String prefix) {
        super(prefix);
    }


    //根据方法注解中指定的限流时间动态生成Key
    public static AccessKey withExpire(int expireSeconds){
        return new AccessKey(expireSeconds, "access");
    }
}
