package com.atgmall.cartweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atgmall.bean.CartInfo;
import com.atgmall.bean.SkuInfo;
import com.atgmall.service.ManageService;
import com.atgmall.webutil.config.CookieUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/8/5 20:01
 */
@Component
public class CartCookieHandler {
        private String cookieCartName="Cart";

        @Reference
        private ManageService manageService;

    // 设置cookie 过期时间
    private int COOKIE_CART_MAXAGE=7*24*3600;


    /*
    和service模块的方法类似
        1、 先查询出来在cookie中的购物车，反序列化成列表。

        2、 通过循环比较有没有该商品

        3、 如果有，增加数量

        4、 如果没有，增加商品

        5、 然后把列表反序列化，利用之前最好的CookieUtil保存到cookie中。
     */

    public void addToCart(HttpServletRequest request, HttpServletResponse response, String skuId, String userId, int skuNum) {
//从cookie中获取值
        String value = CookieUtil.getCookieValue(request, cookieCartName, true);

        boolean ifExist=false;
        List<CartInfo> cartInfos=new ArrayList<>();
        if(value!=null) {
             cartInfos = JSON.parseArray(value, CartInfo.class);
            for (int i = 0; i <cartInfos.size() ; i++) {
                CartInfo info = cartInfos.get(i);
                if(info.getSkuId().equals(skuId)){
                    //Cookie里有该商品
                    info.setSkuNum(info.getSkuNum()+skuNum); //更改商品数量
                    info.setSkuPrice(info.getCartPrice());
                    ifExist=true;
                    break;
                }
            }
        }
        //cookie中没有购物车，或者没有该商品
        if(!ifExist){
            //从数据库查出该商品，放到Cookie中
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            CartInfo cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);
            cartInfo.setSkuNum(skuNum);
            cartInfo.setUserId(userId);
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuPrice(skuInfo.getPrice());

            //商品放入购物车集合中
            cartInfos.add(cartInfo);
        }
            String cartInfoJsonList = JSON.toJSONString(cartInfos);
            //将购物车商品集合放入Cookie中
            CookieUtil.setCookie(request,response,cookieCartName,cartInfoJsonList,COOKIE_CART_MAXAGE,true);

    }

    public void deleteCartCookie(HttpServletRequest request,HttpServletResponse response){
        CookieUtil.deleteCookie(request,response,cookieCartName);
    }


    public List<CartInfo> getCartList(HttpServletRequest request){
        String cartJson = CookieUtil.getCookieValue(request, cookieCartName, true);
        List<CartInfo> cartInfoList = JSON.parseArray(cartJson, CartInfo.class);
        return cartInfoList;
    }


    public void checkCart(HttpServletRequest request, HttpServletResponse response, String skuId, String isChecked) {
        //修改为登陆时，购物车商品的勾选状态
        List<CartInfo> cartList = getCartList(request); //从Cookie中获取数据CartList
        for (int i = 0; i < cartList.size(); i++) {
            CartInfo cartInfo = cartList.get(i);
            if(cartInfo.getSkuId().equals(skuId)){
                cartInfo.setIsChecked(isChecked);
            }
        }
        //修改后，再次反序列化回去
        String jsonCartList = JSON.toJSONString(cartList);
        //存到cookie中
        CookieUtil.setCookie(request,response,cookieCartName,jsonCartList,COOKIE_CART_MAXAGE,true);
    }
}
