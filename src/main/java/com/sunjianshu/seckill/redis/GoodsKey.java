package com.sunjianshu.seckill.redis;

//商品列表缓存
public class GoodsKey  extends BasePrefix{

    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60, "gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");
    //商品库存数量初始化缓存，永不失效
    public static GoodsKey getMiaoShaGoodsStock = new GoodsKey(0, "gs");
}
