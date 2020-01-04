package com.sunjianshu.seckill.config;

import com.sunjianshu.seckill.exception.GlobalException;
import com.sunjianshu.seckill.result.CodeMsg;
import com.sunjianshu.seckill.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private SeckillUserService userService;
    @Autowired
    private UserArgumentResolver resolver;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //被@RequestMapping注解修饰的Controller方法就是HandlerMethod
        if(!handler.getClass().isAssignableFrom(HandlerMethod.class)){
            return true;
        }
        HandlerMethod method = (HandlerMethod)handler;
        //获取方法上的注解
        NeedLogin needLogin = method.getMethodAnnotation(NeedLogin.class);
        if(null == needLogin){
            return true;
        }
        String cookieToken = resolver.getCookieValue(request, SeckillUserService.COOKIE_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken) || (null == userService.getByToken(response, cookieToken))){  //如果cookie没有token信息
/*            //直接重定向到登录页面
            response.sendRedirect("/login/to_login");*/
            throw new GlobalException(CodeMsg.SESSION_ERROR);
        }
        return true;
    }
}
