package com.sunjianshu.seckill.controller;

import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.redis.RedisService;
import com.sunjianshu.seckill.service.SeckillUserService;
import com.sunjianshu.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value="/goods")
public class GoodsController {
    @Autowired
    private SeckillUserService seckillUserService;
    @Autowired
    private RedisService redisService;

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
        model.addAttribute("user", seckillUser);
        return "goods_list";
    }

}
