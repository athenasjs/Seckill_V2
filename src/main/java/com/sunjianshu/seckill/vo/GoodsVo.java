package com.sunjianshu.seckill.vo;

import com.sunjianshu.seckill.domain.Goods;

import java.util.Date;

/*
展示商品列表时也希望把商品的秒杀信息查出来，在两张表中，因此创建这个类把表中数据合到一起
 */
public class GoodsVo extends Goods {
    private Double seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    public Double getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(Double seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
