package com.sunjianshu.seckill.controller;

import com.sunjianshu.seckill.domain.OrderInfo;
import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.redis.RedisService;
import com.sunjianshu.seckill.result.CodeMsg;
import com.sunjianshu.seckill.result.Result;
import com.sunjianshu.seckill.service.GoodsService;
import com.sunjianshu.seckill.service.OrderService;
import com.sunjianshu.seckill.service.SeckillUserService;
import com.sunjianshu.seckill.vo.GoodsVo;
import com.sunjianshu.seckill.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private RedisService redisService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SeckillUserService userService;
    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(SeckillUser user,
                                      @RequestParam("orderId")long orderId){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(null == order){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setGoods(goods);
        vo.setOrder(order);
        vo.setUser(user);
        return Result.success(vo);
    }

}
