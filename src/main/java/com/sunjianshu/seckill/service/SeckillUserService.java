package com.sunjianshu.seckill.service;

import com.sunjianshu.seckill.dao.SeckillUserDao;
import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.exception.GlobalException;
import com.sunjianshu.seckill.result.CodeMsg;
import com.sunjianshu.seckill.util.MD5Util;
import com.sunjianshu.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeckillUserService {
    @Autowired
    SeckillUserDao seckillUserDao;

    public SeckillUser getById(long id){
        return seckillUserDao.getById(id);
    }

    //登录方法  业务方法返回CodeMsg不合适，所以用业务异常来优化，直接抛出业务异常
    public boolean/*CodeMsg*/ login(LoginVo loginVo) {
        if(loginVo == null){
//            return CodeMsg.SERVER_ERROR;
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //判断手机号是否存在
        SeckillUser seckillUser = getById(Long.parseLong(mobile));
        if(seckillUser == null){
//            return CodeMsg.MOBILE_NOT_EXIST;
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = seckillUser.getPassword();
        String dbSalt = seckillUser.getSalt();
        //这里作二次MD5，用DB中的salt
        String calcPass = MD5Util.formPassToDBPass(password, dbSalt);
        if(!calcPass.equals(dbPass)){
//            return CodeMsg.PASSWORD_ERROR;
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
//        return CodeMsg.SUCCESS;
        return true;  //返回真正具有业务含义的内容而不是CodeMsg

    }
}
