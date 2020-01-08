package com.sunjianshu.seckill.controller;

import com.sunjianshu.seckill.config.NeedLogin;
import com.sunjianshu.seckill.domain.OrderInfo;
import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.domain.User;
import com.sunjianshu.seckill.rabbitmq.MQSender;
import com.sunjianshu.seckill.redis.RedisService;
import com.sunjianshu.seckill.redis.UserKey;
import com.sunjianshu.seckill.result.CodeMsg;
import com.sunjianshu.seckill.result.Result;
import com.sunjianshu.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class DemoController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MQSender sender;
    //1.rest api Json输出  2.页面
    @RequestMapping("/")
    @ResponseBody
    String home(){
        return "Hello World";
    }

    @RequestMapping("/thymeleaf")
    public String hello(Model model){
        //这里不用new对象，只关心数据这样的方式更简单
        model.addAttribute("name", "thymeleafyo");
        return "thymeleaf";
    }

    @ResponseBody
    @RequestMapping("/error")
    public Result<String> error(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/tx")
    @ResponseBody
    public Result<Boolean> tx(){
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){
        User user = redisService.get(UserKey.getById,"4", User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<User> redisSet(){
        User user = new User();
        user.setId(4);
        user.setName("pipio");
        redisService.set(UserKey.getById,"4", user);//UserKey:id4
        user = redisService.get(UserKey.getById,"4", User.class);
        return Result.success(user);
    }

    @RequestMapping("/info")  //测试QPS的接口，只有查询redis获取对象的操作
    @ResponseBody
    public Result<SeckillUser> info(Model model, SeckillUser seckillUser){
        return Result.success(seckillUser);
    }


    @RequestMapping("/test1")
    @ResponseBody
    @NeedLogin
    public Result<OrderInfo> test1(){
        OrderInfo info = new OrderInfo();
        info.setUserId(2L);
        return Result.success(info);
    }

   /* @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq(){
        String msg = "hello, imooc";
        sender.send(msg);
        return Result.success(msg);
    }*/
}
