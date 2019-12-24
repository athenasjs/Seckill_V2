package com.sunjianshu.seckill.controller;

import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.redis.RedisService;
import com.sunjianshu.seckill.service.GoodsService;
import com.sunjianshu.seckill.service.SeckillUserService;
import com.sunjianshu.seckill.service.UserService;
import com.sunjianshu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping(value="/goods")
public class GoodsController {
    @Autowired
    private SeckillUserService seckillUserService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private GoodsService goodsService;

    @RequestMapping(value="/to_list")  //@CookieValue取出cookie
    public String toLogin(HttpServletResponse response, Model model,
                          /*@CookieValue(value= SeckillUserService.COOKIE_NAME_TOKEN, required =false) String cookieToken,
                          @RequestParam(value= SeckillUserService.COOKIE_NAME_TOKEN, required = false) String paramToken,*/
                          SeckillUser seckillUser
    ){
        /*if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return  "login";  //如果参数里和cookie中都没有token，则返回登录界面
        }*/
        //这些获取session的语句被自定义参数解析器给取代，更优雅的实现方式，直接注入方法参数中
        //定义一个优先级
       /* String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken ;
        SeckillUser user = seckillUserService.getByToken(response,token); //从缓存中拿出User信息*/
       //查询商品列表
        List<GoodsVo> goods = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goods);
        return "goods_list";
    }

    //商品详情页
    @RequestMapping(value = "/to_detail/{goodsId}")
    public String detail(Model model, SeckillUser seckillUser,
                         @PathVariable("goodsId") long goodsId){
        model.addAttribute("user", seckillUser);
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goodsVo);
        //秒杀时间判断
        long start = goodsVo.getStartDate().getTime();
        long end = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int remainSeconds = 0;
        int miaoshaStatus = 0;  //秒杀状态，传到页面显示
        if(now < start){  //秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)((start - now)/1000);
        }else if(now > end){ //秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else{ //正在进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        //将两个状态传到页面 显示对应的文案
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        return "goods_detail";
    }

}
