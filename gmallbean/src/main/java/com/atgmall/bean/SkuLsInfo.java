package com.atgmall.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/7/31 19:55
 */
@Data
public class SkuLsInfo implements Serializable{

    String id;

    BigDecimal price;

    String skuName;

    String catalog3Id;

    String skuDefaultImg;

    Long hotScore=0L;

    List<SkuLsAttrValue> skuAttrValueList;


}
