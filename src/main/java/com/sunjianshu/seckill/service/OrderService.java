package com.sunjianshu.seckill.service;

import com.sunjianshu.seckill.dao.OrderDao;
import com.sunjianshu.seckill.domain.OrderInfo;
import com.sunjianshu.seckill.domain.SeckillOrder;
import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    private OrderDao orderDao;

    public SeckillOrder getMiaoshaOrderByUserIdGoodsId(long id, long goodsId) {
        return orderDao.getMiaoshaOrderByUserIdGoodsId(id, goodsId);
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
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(seckillUser.getId());
        orderDao.insertMiaoshaOrder(seckillOrder);
        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);

    }
}
