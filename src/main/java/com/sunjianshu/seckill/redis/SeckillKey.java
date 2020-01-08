package com.sunjianshu.seckill.redis;

//秒杀key, 判断商品库存是否消耗完
public class SeckillKey  extends BasePrefix{

    public SeckillKey(String prefix) {
        super(prefix);
    }

    public static SeckillKey isGoodsOver = new SeckillKey("go");

}
