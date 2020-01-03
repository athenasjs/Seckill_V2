package com.sunjianshu.seckill.controller;

import com.sunjianshu.seckill.domain.OrderInfo;
import com.sunjianshu.seckill.domain.SeckillOrder;
import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.redis.RedisService;
import com.sunjianshu.seckill.result.CodeMsg;
import com.sunjianshu.seckill.result.Result;
import com.sunjianshu.seckill.service.GoodsService;
import com.sunjianshu.seckill.service.OrderService;
import com.sunjianshu.seckill.service.SeckillService;
import com.sunjianshu.seckill.service.SeckillUserService;
import com.sunjianshu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/*
秒杀的核心功能
 */
@Controller
@RequestMapping("/miaosha")
public class SeckillController {

    @Autowired
    private SeckillUserService seckillUserService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SeckillService seckillService;
    //限定只能用POST提交
    //GET POST区别  GET是幂等的，无论调用多少次产生结果是一样的，对服务端数据没有影响
    //而POST请求对服务端数据产生影响  不是幂等的
    @RequestMapping(value="/do_miaosha", method = RequestMethod.POST)   //页面静态化处理后直接返回订单信息
    @ResponseBody
    public Result<OrderInfo> seckill(Model model, SeckillUser seckillUser,
                       @RequestParam("goodsId")Long goodsId){
        if(seckillUser == null){
            return Result.error(CodeMsg.SESSION_ERROR);  //用户还没有登录
        }
        model.addAttribute(seckillUser);
        //判断库存 如果没有库存则返回秒杀错误页面
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getStockCount();
        if(stock <= 0){
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER);
            return Result.error(CodeMsg.MIAO_SHA_OVER);  //秒杀结束
        }
        //判断是否已经秒杀到了
        SeckillOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if(order != null){
            model.addAttribute("errmsg", CodeMsg.REPEAT_MIAOSHA);
            return Result.error(CodeMsg.REPEAT_MIAOSHA); //重复秒杀
        }
        //减库存 下订单 写入秒杀订单
        //这几个操作要放到一个事务里面 一步失败需要全部回滚 原子操作
        //返回秒杀信息是因为秒杀成功后直接进入订单详情页
        OrderInfo orderInfo = seckillService.miaosha(seckillUser, goodsVo);
        return Result.success(orderInfo); //返回订单

    }



}
