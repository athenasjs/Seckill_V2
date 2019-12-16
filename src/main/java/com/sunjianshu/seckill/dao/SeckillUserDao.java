package com.sunjianshu.seckill.dao;

import com.sunjianshu.seckill.domain.SeckillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SeckillUserDao {

     @Select("select * from seckill_user where id = #{id}")
     SeckillUser getById(@Param("id") long id);
}
