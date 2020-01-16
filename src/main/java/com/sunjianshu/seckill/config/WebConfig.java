package com.sunjianshu.seckill.config;

import com.sunjianshu.seckill.access.AccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    @Autowired
    UserArgumentResolver userArgumentResolver;
    @Autowired
    AccessInterceptor accessInterceptor;

    @Override  //加入自定义的参数解析器
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }

    @Override    //加入自定义的拦截器（判断用户登录）
    public void addInterceptors(InterceptorRegistry registry) {
       /* String[] excludes = new String[]{"/", "/login/to_login", "/templates/**", "/static/**"};
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**")
        .excludePathPatterns(excludes);*/
        registry.addInterceptor(accessInterceptor);
    }
}
