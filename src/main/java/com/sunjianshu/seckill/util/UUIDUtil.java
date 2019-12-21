package com.sunjianshu.seckill.util;

import java.util.UUID;

public class UUIDUtil {
    /*
    生成随机的UUID
     */
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
