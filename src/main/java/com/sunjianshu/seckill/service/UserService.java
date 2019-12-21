package com.sunjianshu.seckill.service;

import com.sunjianshu.seckill.dao.UserDao;
import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;


    public User getById(int id){
        return userDao.getById(id);
    }


    @Transactional
    public boolean tx(){
        User user1 = new User();
        user1.setId(2);
        user1.setName("aaa");
        userDao.insert(user1);


        user1.setId(1);
        user1.setName("ccc");
        userDao.insert(user1);

        return true;
    }
}
