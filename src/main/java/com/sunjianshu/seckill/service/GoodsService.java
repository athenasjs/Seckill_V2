package com.sunjianshu.seckill.service;

import com.sunjianshu.seckill.dao.GoodsDao;
import com.sunjianshu.seckill.domain.Goods;
import com.sunjianshu.seckill.domain.SeckillGoods;
import com.sunjianshu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public int reduceStock(GoodsVo goodsVo) {
        SeckillGoods goods = new SeckillGoods();
        goods.setGoodsId(goodsVo.getId());
        return goodsDao.reduceStock(goods);
    }
}
