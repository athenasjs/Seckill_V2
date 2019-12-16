package com.sunjianshu.seckill.exception;

import com.sunjianshu.seckill.result.CodeMsg;

/*
定义的业务异常
 */
public class GlobalException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg){
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }


}
