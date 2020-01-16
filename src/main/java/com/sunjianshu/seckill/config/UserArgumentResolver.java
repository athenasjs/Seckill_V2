package com.sunjianshu.seckill.config;

import com.sunjianshu.seckill.access.UserContext;
import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private SeckillUserService userService;
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        //判断参数类型，只有是SeckillUser类型才做处理
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == SeckillUser.class;
    }
    //如果以后获取session的方法改变了，只需要在这个方法里做调整就可以
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        return UserContext.getUser();  //这里直接从ThreadLocal中拿出拦截器放入的用户对象
    }
}
