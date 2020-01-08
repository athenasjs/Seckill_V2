package com.sunjianshu.seckill.rabbitmq;

import com.sunjianshu.seckill.domain.SeckillOrder;
import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.redis.RedisService;
import com.sunjianshu.seckill.service.GoodsService;
import com.sunjianshu.seckill.service.OrderService;
import com.sunjianshu.seckill.service.SeckillService;
import com.sunjianshu.seckill.service.SeckillUserService;
import com.sunjianshu.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//秒杀请求消费者
@Service
public class MQReceiver {

    @Autowired
    private RedisService redisService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SeckillService seckillService;
    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    //消费秒杀请求
    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message){
        logger.info("receive message:" + message);
        SeckillMessage seckillMessage = RedisService.stringToBean(message, SeckillMessage.class);
        SeckillUser seckillUser = seckillMessage.getSeckillUser();
        long goodsId = seckillMessage.getGoodsId();

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getStockCount();
        if(stock <= 0){
            return ;  //这里直接查数据库，因为此时的请求已经很少了
            //如果没有库存直接返回
        }

        //判断是否重复秒杀  数据库也有唯一索引约束  这里查一下redis，没什么性能损耗
        SeckillOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if(order != null){
            return;
        }
        //执行秒杀
        //减库存 下订单 生成秒杀订单
        seckillService.miaosha(seckillUser, goodsVo);
    }

   /* @RabbitListener(queues = MQConfig.QUEUE)  //指定监听的Queue
    public void receive(String message){
        logger.info("receive message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message){
        logger.info("topic queue1 message:" + message);
    }
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String msg){
        logger.info("topic queue2 message" + msg);
    }

    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
    public void receiveHeaderQueue(byte[] message){
        logger.info("header queue message:" + new String(message));
    }*/



}
