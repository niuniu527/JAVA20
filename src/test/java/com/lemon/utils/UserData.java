package com.lemon.utils;

import cn.binarywang.tools.generator.*;
import cn.binarywang.tools.generator.util.ChineseCharUtils;
import utils.RandomPersonInfoUtil;

import java.util.HashMap;
import java.util.Map;

public class UserData {
    //存储接口响应变量
    public static Map<String,Object> VARS=new HashMap<>();
    //存储默认请求头
    public static Map<String,String> DEFAULT_HEADERS = new HashMap<>();
    static {
        //静态代码块，类在加载时自动就会加载一次此代码
        //创建默认请求头对象，添加"X-Lemonban-Media-Type", "lemonban.v1"（接口文档中要求必须）
        //DEFAULT_HEADERS.put("X-Lemonban-Media-Type", "lemonban.v2");
        DEFAULT_HEADERS.put("Content-Type", "application/json");
        //注册：存储随机用户名，固定密码，固定email
        VARS.put("${username}",ChineseIDCardNumberGenerator.getInstance().generate());
        VARS.put("${pwd}",RandomPersonInfoUtil.getRandomNumBySize(8));
        VARS.put("${email}",RandomPersonInfoUtil.getEmail());
    }
}
