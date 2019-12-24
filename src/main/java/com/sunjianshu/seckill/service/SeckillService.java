package com.sunjianshu.seckill.service;

import com.sunjianshu.seckill.domain.Goods;
import com.sunjianshu.seckill.domain.OrderInfo;
import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeckillService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Transactional  //原子操作 事务管理
    public OrderInfo miaosha(SeckillUser seckillUser, GoodsVo goodsVo) {
        //减库存
        goodsService.reduceStock(goodsVo);
        //写入订单表和秒杀订单表  也是一个事务
        OrderInfo orderInfo = orderService.createOrder(seckillUser, goodsVo);
        return orderInfo;
    }
}
