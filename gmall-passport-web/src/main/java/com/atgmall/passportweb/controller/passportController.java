package com.atgmall.passportweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atgmall.bean.UserInfo;
import com.atgmall.passportweb.util.JwtUtil;
import com.atgmall.service.UserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/8/3 14:01
 */
@Controller
public class passportController {

    @Value("${token.key}")
    private String key;

    @Reference
    private UserInfoService uservice;


    @GetMapping("index")
    public String index(HttpServletRequest request){
        //获取Url地址中的传值参数
        String originUrl = request.getParameter("originUrl");
        //放到域中，在页面中使用
        request.setAttribute("originUrl",originUrl);
        return "index";
    }

    @PostMapping("login")
    @ResponseBody
    public String login(UserInfo info,HttpServletRequest request){
        UserInfo userInfo = uservice.loginUserInfo(info);

        //获取Nginx的服务器ip地址          nginx的代理地址的key值，可获得被代理的ip地址：192.168.124.1：8087
        String header = request.getHeader("X-forwarded-for");
        if(userInfo!=null){

            Map<String,Object> map=new HashMap<>();
            map.put("userId",userInfo.getId());
            map.put("nickName",userInfo.getNickName());
            //JWT编码，生成明文            公共的key,map封装的用户信息，服务器ip地址
            String token = JwtUtil.encode(key, map, header);
            //
            return token;
        }else {
            return "fail";
        }
    }

    //认证中心
    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request){
        /*
        1.获取服务器的Ip地址，以及token
        2.key+ip,解密token,得到NickName,id
        3.根据id  《key》查询redis中是否有userInfo <value>
        4.有，true，无，false
         */
        String serverIp = request.getParameter("sale");
        String token = request.getParameter("token");
        //解密token
        Map<String, Object> map = JwtUtil.decode(token, key, serverIp);
            System.out.println("Map对象："+map+key+serverIp);
        if(map!=null && map.size()>0){
            String userId = (String) map.get("userId");
            String nickName = (String) map.get("nickName");

            //判断在redis中是否存在
            UserInfo userInfo = uservice.userverify(userId);
            System.out.println("UserInfo对象："+userInfo+"/t"+userId);
            if(userInfo!=null){
                //存在
                return "success";
            }else {
                return "fail";
            }
        }
        return "fail";
    }




}
