package com.atgmall.bean;
/*
        用于封装购物车结算时，选中的商品生成订单信息     OrderInfo==>OrderDetail
 */
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/8/8 10:26
 */
@Data
public class OrderDetail implements Serializable {


    @Id
    @Column
    private String id;
    @Column
    private String orderId;
    @Column
    private String skuId;
    @Column
    private String skuName;
    @Column
    private String imgUrl;
    @Column
    private BigDecimal orderPrice;
    @Column
    private Integer skuNum;

    @Transient
    private String hasStock;
}
