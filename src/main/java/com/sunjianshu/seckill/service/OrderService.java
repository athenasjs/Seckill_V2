package com.sunjianshu.seckill.service;

import com.sunjianshu.seckill.dao.OrderDao;
import com.sunjianshu.seckill.domain.OrderInfo;
import com.sunjianshu.seckill.domain.SeckillOrder;
import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.redis.OrderKey;
import com.sunjianshu.seckill.redis.RedisService;
import com.sunjianshu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private RedisService redisService;

    //这里秒杀订单信息加入缓存处理，小的优化
    //不设置过期时间，不查数据库了
    public SeckillOrder getMiaoshaOrderByUserIdGoodsId(long id, long goodsId) {
        return redisService.get(OrderKey.getMiaoShaOrderByUidGid, ""+id + "_" + goodsId, SeckillOrder.class);
        //return orderDao.getMiaoshaOrderByUserIdGoodsId(id, goodsId);
    }

    @Transactional  //这里也做成一个事务
    public OrderInfo createOrder(SeckillUser seckillUser, GoodsVo goodsVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L); //收货地址
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getSeckillPrice()); //这里是秒杀价格
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);//支付状态
        orderInfo.setUserId(seckillUser.getId());
        orderDao.insert(orderInfo);  //插入订单表，返回主键,返回到orderInfo对象中的id属性
        //插入之后，Mybatis会把生成的id塞到对象里面
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(seckillUser.getId());
        //生成订单完成之后要写入redis
        redisService.set(OrderKey.getMiaoShaOrderByUidGid, ""+seckillUser.getId() +"_" + goodsVo.getId(), seckillOrder);
        orderDao.insertMiaoshaOrder(seckillOrder);
        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);

    }

    public void deleteOrders() {
        orderDao.deleteOrders();
        orderDao.deleteSeckillOrders();
    }
}
