package com.sunjianshu.seckill.dao;

import com.sunjianshu.seckill.domain.Goods;
import com.sunjianshu.seckill.domain.SeckillGoods;
import com.sunjianshu.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    /*
    查询秒杀商品列表 连接查询
     */
    @Select("select g.*, sg.stock_count, sg.start_date, sg.end_date, sg.seckill_price from seckill_goods sg left join goods  g on sg.goods_id = g.id")
    List<GoodsVo> listGoodsVo();

    @Select("select g.*,sg.stock_count, sg.start_date, sg.end_date, sg.seckill_price from seckill_goods sg left join goods g on sg.goods_id = g.id where g.id = #{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    //减库存
    @Update("update seckill_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
    int reduceStock(SeckillGoods goods);
}
