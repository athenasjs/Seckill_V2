package com.sunjianshu.seckill.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

    public static final String QUEUE = "queue";
    public static final String TOPIC_QUEUE1 = "tpoic.queue1";
    public static final String TOPIC_QUEUE2 = "tpoic.queue2";
    public static final String TOPIC_EXCHANGE = "topicExchange";
    public static final String HEADER_QUEUE = "header.queue";
    public static final String HEADERS_EXCHANGE = "headersExchange";
    public static final String FANOUT_EXCHANGE = "fanoutExchange";
    public static final String MIAOSHA_QUEUE = "miaosha.queue";
    /*
    Direct模式
     */
    @Bean
    public Queue miaosahQueue(){
        return new Queue(MIAOSHA_QUEUE, true);
    }

    @Bean
    public Queue queue(){
        //消息队列，名称+是否持久化
        return new Queue("queue", true);
    }

    /*
    Topic 模式，通配符字符串匹配多个队列
     */
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1, true);
    }

    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2, true);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("topic.key1");
    }


    @Bean
    public Binding topicBinding2(){  //#匹配0个或多个字符，*匹配一个字符
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("topic.#");
    }

    /*
    Header模式 指定一组键值对规则绑定队列和路由器
     */
    @Bean
    public Queue headerQueue1(){
        return new Queue(HEADER_QUEUE, true);
    }

    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(HEADERS_EXCHANGE);
    }
    @Bean
    public Binding headerBinding(){
        Map<String , Object> map = new HashMap<>();
        map.put("header1", "value1");
        map.put("header2", "value2");
        //交换机和队列绑定时指定一组键值对规则，发送消息也指定一组规则
        return BindingBuilder.bind(headerQueue1()).to(headersExchange()).whereAll(map).match();
    }

    /*
    Fanout模式 路由广播
    把消息发送给绑定它的全部队列，即使设置了Key也会被忽略
     */

    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding FanoutBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }
    @Bean
    public Binding FanoutBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }


}
