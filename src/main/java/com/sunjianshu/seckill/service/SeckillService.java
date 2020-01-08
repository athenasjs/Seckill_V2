package com.sunjianshu.seckill.service;

import com.sunjianshu.seckill.domain.Goods;
import com.sunjianshu.seckill.domain.OrderInfo;
import com.sunjianshu.seckill.domain.SeckillOrder;
import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.redis.RedisService;
import com.sunjianshu.seckill.redis.SeckillKey;
import com.sunjianshu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeckillService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisService redisService;

    @Transactional  //原子操作 事务管理
    public OrderInfo miaosha(SeckillUser seckillUser, GoodsVo goodsVo) {
        OrderInfo orderInfo = null;
        //减库存
        int res = goodsService.reduceStock(goodsVo);
        //写入订单表和秒杀订单表  也是一个事务
        if(res > 0){
           orderInfo = orderService.createOrder(seckillUser, goodsVo);
        }else{
            setGoodsOver(goodsVo.getId());   //如果库存消耗完，减库存失败，标记商品0库存
        }
        return orderInfo;
    }

    //设置商品库存耗尽标志
    private void setGoodsOver(long id) {
        redisService.set(SeckillKey.isGoodsOver, ""+id, true);
    }
    //判断商品是否库存耗尽
    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, goodsId+"");
    }

    //客户端轮询 返回状态码或订单id
    public long getMiaoShaResult(long id, long goodsId) {
        SeckillOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(id, goodsId);
        if(order != null){  //秒杀成功
            return order.getOrderId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return -1;  //库存消耗完
            }else{
                return 0;  // 还有库存， 正在处理中
            }
        }
    }


    public void reset(List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }
}
