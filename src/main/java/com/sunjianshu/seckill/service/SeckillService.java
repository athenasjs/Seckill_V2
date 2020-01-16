package com.sunjianshu.seckill.service;

import com.sunjianshu.seckill.domain.Goods;
import com.sunjianshu.seckill.domain.OrderInfo;
import com.sunjianshu.seckill.domain.SeckillOrder;
import com.sunjianshu.seckill.domain.SeckillUser;
import com.sunjianshu.seckill.redis.RedisService;
import com.sunjianshu.seckill.redis.SeckillKey;
import com.sunjianshu.seckill.util.MD5Util;
import com.sunjianshu.seckill.util.UUIDUtil;
import com.sunjianshu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

@Service
public class SeckillService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisService redisService;

    @Transactional  //原子操作 事务管理
    public OrderInfo miaosha(SeckillUser seckillUser, GoodsVo goodsVo) {
        OrderInfo orderInfo = null;
        //减库存
        int res = goodsService.reduceStock(goodsVo);
        //写入订单表和秒杀订单表  也是一个事务
        if(res > 0){
           orderInfo = orderService.createOrder(seckillUser, goodsVo);
        }else{
            setGoodsOver(goodsVo.getId());   //如果库存消耗完，减库存失败，标记商品0库存
        }
        return orderInfo;
    }

    //设置商品库存耗尽标志
    private void setGoodsOver(long id) {
        redisService.set(SeckillKey.isGoodsOver, ""+id, true);
    }
    //判断商品是否库存耗尽
    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, goodsId+"");
    }

    //客户端轮询 返回状态码或订单id
    public long getMiaoShaResult(long id, long goodsId) {
        SeckillOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(id, goodsId);
        if(order != null){  //秒杀成功
            return order.getOrderId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return -1;  //库存消耗完
            }else{
                return 0;  // 还有库存， 正在处理中
            }
        }
    }


    public void reset(List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }
    //path校验，取缓存比较
    public boolean checkPath(SeckillUser seckillUser, Long goodsId, String path) {
        if(seckillUser == null || path == null){
            return false;
        }
        String pathOld = redisService.get(SeckillKey.getMiaoShaPath,  "" +seckillUser.getId() +"_"+goodsId, String.class);
        return path.equals(pathOld);
    }

    public String createMiaoShaPath(SeckillUser user, long goodsId) {
        if(user == null || goodsId <= 0){ //加一些验证
            return null;
        }
        String path = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(SeckillKey.getMiaoShaPath, "" + user.getId() + "_"+goodsId, path);
        return path;
    }

    public BufferedImage createVerifyCode(SeckillUser user, long goodsId) {
        if(user == null || goodsId <= 0){
            return null;
        }
        int width = 80;
        int height = 32;
        //用BufferedImage内存图像的方式生成图形验证码
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();//画布
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0,0,width,height);
        g.setColor(Color.black);
        g.drawRect(0,0,width-1,height-1);
        Random rdm = new Random();
        //生成一些干扰的点
        for(int i = 0; i < 50; i++){
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0,100,0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        //画上验证码
        g.drawString(verifyCode, 8, 24);
        //把验证码存到redis
        int rnd = calc(verifyCode);
        redisService.set(SeckillKey.getMiaoShaVerifyCode, user.getId() + ","+goodsId, rnd);
        //输出图片
        return image;

    }
    //计算结果  输入一个数学公式
    private int calc(String verifyCode) {
        try{
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(verifyCode);
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    //运算符数组
    private static char[] ops = new char[]{'+','-', '*'};
    //生成数学公式验证码  随机生成三个数字做运算
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = "" +num1 + op1 + num2 + op2 +num3;
        return exp;
    }

    public boolean checkVerifyCode(SeckillUser user, long goodsId, int verifyCode) {
        if(user == null || goodsId <= 0){
            return false;
        }
        Integer codeOld = redisService.get(SeckillKey.getMiaoShaVerifyCode, user.getId() + "," + goodsId, Integer.class);
        if(codeOld == null || codeOld-verifyCode != 0){
            return false;
        }
        //校验过后删除旧的验证码值
        redisService.delete(SeckillKey.getMiaoShaVerifyCode, user.getId() + "," + goodsId);
        return true;
    }
}
