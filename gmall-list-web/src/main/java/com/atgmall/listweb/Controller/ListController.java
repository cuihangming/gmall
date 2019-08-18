package com.atgmall.listweb.Controller;
/*
         调用销售属性值列表的页面
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.atgmall.bean.SkuLsParams;
import com.atgmall.bean.SkuLsResult;
import com.atgmall.service.ListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/8/1 23:56
 */
@Controller
public class ListController {

    @Reference
    ListService listService;

//    @LoginRequire
    @RequestMapping("list.html")
    //@ResponseBody   //不让其响应数据
    public String getList(SkuLsParams skuLsParams, Model model){
        SkuLsResult search = listService.search(skuLsParams);
        //将查询结果放在作用域中，在页面进行获取
        model.addAttribute("skuLsInfoList",search.getSkuLsInfoList());
        //转发跳转页面
        return "list";
    }
}
