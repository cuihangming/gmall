package com.atgmall.usermanage.controller;

import com.atgmall.bean.UserInfo;
import com.atgmall.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/7/23 19:42
 */

@RestController
public class UserInfoController {

    @Autowired
    private UserInfoService uservice;

    @RequestMapping("getuserall")
    public List<UserInfo>  getuserInfoAll(){
        List<UserInfo> all = uservice.getUserInfoAll();
        return all;
    }




}
