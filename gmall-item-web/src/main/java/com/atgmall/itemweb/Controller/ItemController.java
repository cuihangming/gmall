package com.atgmall.itemweb.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atgmall.bean.SkuInfo;
import com.atgmall.bean.SpuSaleAttr;
import com.atgmall.service.ManageService;
import com.atgmall.webutil.config.LoginRequire;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/7/29 19:31
 */
@Controller
public class ItemController{

    @Reference
    private ManageService manageService;

    @RequestMapping("{skuId}.html")
    @LoginRequire   //该方法必须为登陆状态                            model模型对象实质上是放在request域中
    public String skuInfoPage(@PathVariable(value = "skuId") String skuId,Model model){
        SkuInfo info= manageService.getSkuInfo(skuId);
        //将数据放入Model视图解析器中
        model.addAttribute("skuInfo",info);
        List<SpuSaleAttr> SaleList=manageService.getSpuSaleAttrListCheckBySku(info);
        model.addAttribute("SpuSaleAttr",SaleList);
        //转发
        return "item";
    }
}
