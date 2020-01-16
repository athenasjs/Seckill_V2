package com.sunjianshu.seckill.access;

import com.sunjianshu.seckill.domain.SeckillUser;

public class UserContext {
    //ThreadLocal维护用户信息，由拦截器保存信息进来，参数解析器从中获取
    private static ThreadLocal<SeckillUser> userHolder = new ThreadLocal<>();

    public static void setUser(SeckillUser user){
        userHolder.set(user);
    }

    public static SeckillUser getUser(){
        return userHolder.get();
    }
}
