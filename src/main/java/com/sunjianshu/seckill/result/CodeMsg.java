package com.sunjianshu.seckill.result;


public class CodeMsg {

    private int code;
    private String msg;

    //所有的CodeMsg只需要在这里分模块定义 清晰优雅
    //通用异常
    public static final CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static final CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");

    //登录模块 5002XX

    //商品模块 5003XX

    //订单模块 5004XX

    //秒杀模块 5005XX



    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


}
