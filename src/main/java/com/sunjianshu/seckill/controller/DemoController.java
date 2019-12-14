package com.sunjianshu.seckill.controller;

import com.sunjianshu.seckill.result.CodeMsg;
import com.sunjianshu.seckill.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class DemoController {

    //1.rest api Json输出  2.页面
    @RequestMapping("/")
    @ResponseBody
    String home(){
        return "Hello World";
    }

    @RequestMapping("/thymeleaf")
    public String hello(Model model){
        //这里不用new对象，只关心数据这样的方式更简单
        model.addAttribute("name", "thymeleafyo");
        return "thymeleaf";
    }

    @ResponseBody
    @RequestMapping("/error")
    public Result<String> error(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }


}
