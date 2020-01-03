package com.sunjianshu.seckill.controller;

import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.redis.GoodsKey;
import com.sunjianshu.seckill.redis.KeyPrefix;
import com.sunjianshu.seckill.redis.RedisService;
import com.sunjianshu.seckill.result.Result;
import com.sunjianshu.seckill.service.GoodsService;
import com.sunjianshu.seckill.service.SeckillUserService;
import com.sunjianshu.seckill.service.UserService;
import com.sunjianshu.seckill.vo.GoodsDetailVo;
import com.sunjianshu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value="/to_list", produces = "text/html")  //@CookieValue取出cookie
    @ResponseBody  //加入页面缓存，直接返回html源代码
    public String toList(HttpServletResponse response, Model model,
                          /*@CookieValue(value= SeckillUserService.COOKIE_NAME_TOKEN, required =false) String cookieToken,
                          @RequestParam(value= SeckillUserService.COOKIE_NAME_TOKEN, required = false) String paramToken,*/
                          SeckillUser seckillUser,
                          HttpServletRequest request
    ){
        /*if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return  "login";  //如果参数里和cookie中都没有token，则返回登录界面
        }*/
        //这些获取session的语句被自定义参数解析器给取代，更优雅的实现方式，直接注入方法参数中
        //定义一个优先级
       /* String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken ;
        SeckillUser user = seckillUserService.getByToken(response,token); //从缓存中拿出User信息*/
        //这里加入页面缓存  先查redis
        //如果redis中有列表页面，则直接返回，不用查询数据库
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
       //查询商品列表
        List<GoodsVo> goods = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goods);

        //手动渲染
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //手动获取模板引擎，传入webContext渲染页面
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", webContext);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;  //手动渲染的页面写到输出中
    }

    /*//商品详情页  也进行页面缓存
    @RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail(Model model, SeckillUser seckillUser,
                         @PathVariable("goodsId") long goodsId,
                         HttpServletResponse response,
                         HttpServletRequest request){
        model.addAttribute("user", seckillUser);
        //取缓存 不同商品有不同的缓存页面
        String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
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
        //手动渲染
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //手动获取模板引擎，传入webContext渲染页面
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", webContext);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
        }

        return html;
    }
*/
    //页面静态化处理
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(Model model, SeckillUser seckillUser,
                                         @PathVariable("goodsId") long goodsId,
                                         HttpServletResponse response,
                                         HttpServletRequest request){
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
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
        GoodsDetailVo vo = new GoodsDetailVo(); //填充vo对象用于静态页面渲染 全部改造成这种形式
        vo.setGoods(goodsVo);
        vo.setMiaoshaStatus(miaoshaStatus);
        vo.setRemainSeconds(remainSeconds);
        vo.setUser(seckillUser);
        return Result.success(vo);
    }

    //页面缓存方法抽象出来，步骤：1.redis中获取 2.没有则手动渲染并存redis
    private String pageCache(KeyPrefix keyPrefix, String key, WebContext webContext, String pageName){
        String html = redisService.get(keyPrefix, key, String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        html = thymeleafViewResolver.getTemplateEngine().process(pageName,webContext );
        if(!StringUtils.isEmpty(html)){
            redisService.set(keyPrefix, key, html);
        }
        return html;
    }
}
