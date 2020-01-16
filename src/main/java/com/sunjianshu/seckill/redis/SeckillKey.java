package com.sunjianshu.seckill.redis;

//秒杀key, 判断商品库存是否消耗完
public class SeckillKey  extends BasePrefix{

    public SeckillKey(int expireSeconds, String prefix) {
        super(expireSeconds,prefix);
    }

    public static SeckillKey isGoodsOver = new SeckillKey(0,"go");
    public static SeckillKey getMiaoShaPath = new SeckillKey(60, "mp");
    public static SeckillKey getMiaoShaVerifyCode = new SeckillKey(300, "vc");

}
