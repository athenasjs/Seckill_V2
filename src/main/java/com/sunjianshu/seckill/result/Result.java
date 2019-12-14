package com.sunjianshu.seckill.result;

/**
 * 对返回结果的封装  本类中为了更好地封装不提供set方法
 * @author:sjs
 * @date: 2019-12-14 17:20:52
 * @description:
 **/
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    //构造方法用private修饰，避免在程序中其他位置调用
    private Result(T data){
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    private Result(CodeMsg cm){
        if(cm == null){
            return;
        }
        this.code = cm.getCode();
        this.msg = cm.getMsg();
    }
    //提供一些静态方法获取封装好的结果
    /*
    成功时候的调用
     */
    public static <T> Result<T> success(T data){
        return new Result<T>(data);
    }

    /*
    失败时候的调用
     */
    public static <T> Result<T> error(CodeMsg cm){
        return new Result<T>(cm);
    }

    public int getCode() {
        return code;
    }


    public String getMsg() {
        return msg;
    }


    public T getData() {
        return data;
    }

}
