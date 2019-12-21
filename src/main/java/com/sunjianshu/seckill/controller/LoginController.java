package com.sunjianshu.seckill.controller;

import com.sunjianshu.seckill.result.CodeMsg;
import com.sunjianshu.seckill.result.Result;
import com.sunjianshu.seckill.service.SeckillUserService;
import com.sunjianshu.seckill.util.ValidatorUtil;
import com.sunjianshu.seckill.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private SeckillUserService seckillUserService;
    private static Logger log = LoggerFactory.getLogger(LoginController.class);
    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }


    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(@Valid LoginVo loginVo, HttpServletResponse httpServletResponse){ //引入JSR303参数校验
        log.info(loginVo.toString());
        //参数校验
       /* String passInput = loginVo.getPassword();
        String mobile = loginVo.getMobile();
        if(StringUtils.isEmpty(passInput)){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        if(StringUtils.isEmpty(mobile)){
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        if(!ValidatorUtil.isMobile(mobile)){
            return Result.error(CodeMsg.MOBILE_ERROR);
        }*/
        //登录 todo

        seckillUserService.login(loginVo, httpServletResponse);
       /* if(codeMsg.getCode() == 0){
            return Result.success(true);
        }*/
        return Result.success(true);
    }


}
