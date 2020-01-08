package com.sunjianshu.seckill.rabbitmq;

import com.sunjianshu.seckill.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//消息发送者
@Service
public class MQSender {

    private static Logger logger = LoggerFactory.getLogger(MQSender.class);
    @Autowired
    AmqpTemplate amqpTemplate;  //操作队列的类
    //消息投放
    public void sendMiaoShaMessage(SeckillMessage message) {
        String msg = RedisService.beanToString(message);
        logger.info("send message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }

/*    public void send(Object message){
        //往队列中放数据  对象转化为字符串  发送时候指定发送到哪个queue
        //Queue中自带一些方法
        String msg = RedisService.beanToString(message);
        logger.info("send:" + msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
    }

    public void sendTopic(Object object){
        String msg = RedisService.beanToString(object);
        logger.info("send topic message:" + msg);
        //发送指定key,匹配路由器和队列绑定的通配符字符串
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic:key1", msg+"1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic:key2", msg+"2");
    }

    public void sendHeader(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("send header message:" + msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("header1", "value1");
        properties.setHeader("header2", "value2");
        Message obj = new Message(msg.getBytes(),properties );
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);
    }

    public void sendFanout(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("send fanout message:" + msg);
        //Fanout模式消息发送给路由器绑定的所有队列
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
    }*/



}
