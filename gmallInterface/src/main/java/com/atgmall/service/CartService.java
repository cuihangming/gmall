package com.atgmall.service;

import com.atgmall.bean.CartInfo;

import java.util.List;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/8/5 18:06
 */
public interface CartService {

    //添加购物车，根据userId,商品id，商品数量
    public void addToCart(String userId,String skuId,Integer skuNum);

    List<CartInfo> getCartList(String userId);

    public List<CartInfo> loadCartCache(String userId);

    //合并购物车
    List<CartInfo> mergeToCartList(List<CartInfo> cartList1, String userId);
    //修改购物车中，已选中商品的状态
    void checkCart(String skuId, String userId, String isChecked);
    //获取已选中的购物车商品
    List<CartInfo> getCheckedCartList(String userId);
}
