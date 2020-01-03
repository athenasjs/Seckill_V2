package com.sunjianshu.seckill.dao;

import com.sunjianshu.seckill.domain.OrderInfo;
import com.sunjianshu.seckill.domain.SeckillOrder;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from seckill_order where user_id = #{id} and goods_id=#{goodsId}")
    SeckillOrder getMiaoshaOrderByUserIdGoodsId(@Param("id") long id, @Param("goodsId") long goodsId);
    //这里需要把插入生成的id给返回出去 SelectKey注解
    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date) value(" +
            "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel}, #{status}, #{createDate})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    long insert(OrderInfo orderInfo);
    @Insert("insert into seckill_order(user_id, goods_id, order_id) value(#{userId}, #{goodsId}, #{orderId})")
    int insertMiaoshaOrder(SeckillOrder seckillOrder);

    @Select("select * from order_info where id= #{orderId}")
    OrderInfo getOrderById(@Param("orderId")long orderId);
}
