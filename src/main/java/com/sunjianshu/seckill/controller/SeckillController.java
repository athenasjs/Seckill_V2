package com.sunjianshu.seckill.controller;

import com.sunjianshu.seckill.config.NeedLogin;
import com.sunjianshu.seckill.domain.OrderInfo;
import com.sunjianshu.seckill.domain.SeckillOrder;
import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.rabbitmq.MQSender;
import com.sunjianshu.seckill.rabbitmq.SeckillMessage;
import com.sunjianshu.seckill.redis.GoodsKey;
import com.sunjianshu.seckill.redis.OrderKey;
import com.sunjianshu.seckill.redis.RedisService;
import com.sunjianshu.seckill.redis.SeckillKey;
import com.sunjianshu.seckill.result.CodeMsg;
import com.sunjianshu.seckill.result.Result;
import com.sunjianshu.seckill.service.GoodsService;
import com.sunjianshu.seckill.service.OrderService;
import com.sunjianshu.seckill.service.SeckillService;
import com.sunjianshu.seckill.service.SeckillUserService;
import com.sunjianshu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

/*
秒杀的核心功能
 */
@Controller
@RequestMapping("/miaosha")
public class SeckillController  implements InitializingBean {

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
    @Autowired
    private MQSender mqSender;
    //内存标记，redis中商品库存状态
    private volatile HashMap<Long, Boolean> localOverMap = new HashMap<>();
    //限定只能用POST提交
    //GET POST区别  GET是幂等的，无论调用多少次产生结果是一样的，对服务端数据没有影响
    //而POST请求对服务端数据产生影响  不是幂等的
    @RequestMapping(value="/do_miaosha", method = RequestMethod.POST)   //页面静态化处理后直接返回订单信息
    @ResponseBody
    public Result<Integer> seckill(Model model, SeckillUser seckillUser,
                       @RequestParam("goodsId")Long goodsId){
        if(seckillUser == null){
            return Result.error(CodeMsg.SESSION_ERROR);  //用户还没有登录
        }
        //异步下单改造
        //内存标记 减少redis访问
        boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //预减库存
        long stock = redisService.decr(GoodsKey.getMiaoShaGoodsStock, goodsId+"");
        if(stock < 0){
            localOverMap.put(goodsId, true);  //后面的请求不需要访问redis了，直接返回
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //判断是否已经秒杀到了  这里优化为查缓存，基本没有什么性能问题
        SeckillOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEAT_MIAOSHA); //重复秒杀
        }

        //请求入队
        //秒杀信息包含用户信息和商品
        SeckillMessage message = new SeckillMessage();
        message.setSeckillUser(seckillUser);
        message.setGoodsId(goodsId);
        mqSender.sendMiaoShaMessage(message);

        return Result.success(0);  //返回0表示排队中

        /*//判断库存 如果没有库存则返回秒杀错误页面
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getStockCount();
        if(stock <= 0){
            return Result.error(CodeMsg.MIAO_SHA_OVER);  //秒杀结束
        }
        //判断是否已经秒杀到了  这里优化为查缓存，基本没有什么性能问题
        SeckillOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEAT_MIAOSHA); //重复秒杀
        }
        //减库存 下订单 写入秒杀订单
        //这几个操作要放到一个事务里面 一步失败需要全部回滚 原子操作
        //返回秒杀信息是因为秒杀成功后直接进入订单详情页
        OrderInfo orderInfo = seckillService.miaosha(seckillUser, goodsVo);
        if(null != orderInfo){
            return Result.success(orderInfo); //返回订单
        }
        return Result.error(CodeMsg.MIAO_SHA_OVER);*/

    }

    //系统初始化， IOC容器初始化发现实现了InitializingBean接口会回调这个方法做一些初始化
    //加载商品数量到redis
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null ){
            return ;
        }
        for(GoodsVo goodsVo : goodsList){
            redisService.set(GoodsKey.getMiaoShaGoodsStock, "" + goodsVo.getId(), goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(), false);
        }
    }

    /*
        客户端轮询接口， 返回一个秒杀状态码
        orderId : 成功
        -1 :秒杀失败
        0 :排队中  继续轮询
     */
    @RequestMapping(value="/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(SeckillUser user,
                                  @RequestParam("goodsId") long goodsId){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = seckillService.getMiaoShaResult(user.getId(), goodsId);
        return Result.success(result);
    }

    //reset接口 还原redis和mysql中库存数据， 清空标志位， 清空订单数据
    @RequestMapping(value="/reset", method=RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(){
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for(GoodsVo goodsVo : goodsList){
            goodsVo.setStockCount(10);
            redisService.set(GoodsKey.getMiaoShaGoodsStock, "" + goodsVo.getId(), 10);
            localOverMap.put(goodsVo.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoShaOrderByUidGid);
        redisService.delete(SeckillKey.isGoodsOver);
        seckillService.reset(goodsList);
        return Result.success(true);
    }


}
