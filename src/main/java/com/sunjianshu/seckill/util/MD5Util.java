package com.sunjianshu.seckill.util;


import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    private static final String salt = "1a2b3c4d";

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    public static String inputPassFormPass(String inputPass){
        String str = "" + salt.charAt(0)  + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String formPassToDBPass(String formPass, String salt ){ //这里的salt是随机的，也存入DB
        String str = "" + salt.charAt(0)  + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDbPass(String input, String salt){
        String formPass = inputPassFormPass(input);
        return formPassToDBPass(formPass, salt);
    }

    public static void main(String[] args) {
        //System.out.println(inputPassFormPass("123456")); //b4f517607836e4992482b5402ac3f69f
        //01075dcec00573e6d8a5388722ab155d 实际存入数据库的是这个串
        //System.out.println(formPassToDBPass(inputPassFormPass("123213"),"1a2b3c4d"));
        System.out.println(inputPassToDbPass("123456", "1a2b3c4d"));
    }
}
