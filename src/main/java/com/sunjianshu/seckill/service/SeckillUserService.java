package com.sunjianshu.seckill.service;

import com.sunjianshu.seckill.dao.SeckillUserDao;
import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.exception.GlobalException;
import com.sunjianshu.seckill.redis.RedisService;
import com.sunjianshu.seckill.redis.SeckillUserKey;
import com.sunjianshu.seckill.result.CodeMsg;
import com.sunjianshu.seckill.util.MD5Util;
import com.sunjianshu.seckill.util.UUIDUtil;
import com.sunjianshu.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class SeckillUserService {
    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    SeckillUserDao seckillUserDao;

    @Autowired
    RedisService redisService;

    //改造根据ID获取用户对象的方法，对象缓存 (登录时调用)
    //用户对象缓存时间设为永久有效（在没有变更的情况下）
    public SeckillUser getById(long id){
        //取缓存
        SeckillUser user = redisService.get(SeckillUserKey.getById, "" + id, SeckillUser.class);
        if(user != null){
            return user;
        }
        //取数据库
        user = seckillUserDao.getById(id);
        if(user != null){
            redisService.set(SeckillUserKey.getById, "" + id, user);
        }
        return user;
    }

    //修改密码
    public boolean updatePassword(long id, String passwordNew, String token){
        //取user
        SeckillUser user = getById(id);
        if(user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        SeckillUser toBeUpdate = new SeckillUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(passwordNew, user.getSalt()));
        seckillUserDao.update(toBeUpdate);
        //处理缓存  否则出现缓存不一致
        redisService.delete(SeckillUserKey.getById, "" + id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(SeckillUserKey.token, token, user);//token作为key的缓存需要更新，直接删除会无法登陆
        return true;
    }

    //登录方法  业务方法返回CodeMsg不合适，所以用业务异常来优化，直接抛出业务异常
    public String/*CodeMsg*/ login(LoginVo loginVo, HttpServletResponse httpServletResponse) {
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
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(httpServletResponse, seckillUser, token);
        //return true;  //返回真正具有业务含义的内容而不是CodeMsg
        return token;
    }

    //根据token获取用户信息   这种方式就是对象级缓存
    //public方法首先第一步要做参数校验
    public SeckillUser getByToken(HttpServletResponse response, String token) {
        if(null == token){
            return null;
        }  //这里就成功把一个token映射成一个用户
        SeckillUser user = redisService.get(SeckillUserKey.token, token, SeckillUser.class);
        //延长有效期
        if(user != null){
            addCookie(response, user, token);
        }
        return user;
    }


    //把更新session的操作单独封装
    //第一步往缓存中设置用户信息，第二步重新生成cookie，达到更新cookie生存期的效果，延长有效期
    private void addCookie(HttpServletResponse response, SeckillUser user, String token){
        redisService.set(SeckillUserKey.token, token, user); //不用每次生成一个token，后面更新就可以
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(SeckillUserKey.TOKEN_EXPIRE);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
