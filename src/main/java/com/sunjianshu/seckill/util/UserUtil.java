package com.sunjianshu.seckill.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunjianshu.seckill.domain.SeckillUser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
压测生成5000个并发用户，插入数据库以及登录生成token
 */
public class UserUtil {

    private static void createUser(int count) throws Exception{
        List<SeckillUser> users = new ArrayList<>(count);
        //生成用户
        for(int i = 0; i < count ;i++){
        SeckillUser user = new SeckillUser();
        user.setId(13000000000L + i);
        user.setLoginCount(1);
        user.setNickname("user" + i);
        user.setRegisterDate(new Date());
        user.setSalt("1a2b3c");
        user.setPassword(MD5Util.inputPassToDbPass("123456", user.getSalt()));
        users.add(user);
    }
    //插入数据库
        /*Connection conn = DBUtil.getConn();
        String sql = "insert into seckill_user(login_count, nickname, register_date, salt, password, id) " +
                "values (?,?,?,?,?,?)";
        PreparedStatement psg = conn.prepareStatement(sql);
        for(int i = 0; i < users.size(); i++){
            SeckillUser user = users.get(i);
            psg.setInt(1, user.getLoginCount());
            psg.setString(2, user.getNickname());
            psg.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
            psg.setString(4, user.getSalt());
            psg.setString(5, user.getPassword());
            psg.setLong(6, user.getId());
            psg.addBatch();
        }
        psg.executeBatch();
        psg.close();
        conn.close();
*/

        //登录，生成token
        String urlString = "http://localhost:8080/login/do_login";
        File file = new File("D:tokens.txt");
        if(file.exists()){
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for(int i = 0; i < users.size(); i++){
            SeckillUser user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection)url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassFormPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream in = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while((len = in.read(buff)) >= 0){
                bout.write(buff, 0, len);
            }
            in.close();
            bout.close();
            String response = new String(bout.toByteArray());
            JSONObject jo = JSON.parseObject(response);
            String token = jo.getString("data");
            System.out.println("create token : " + user.getId());
            String row = user.getId() + "," + token;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());

        }
        raf.close();
        System.out.println("over");
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
