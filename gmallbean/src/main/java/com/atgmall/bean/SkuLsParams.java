package com.atgmall.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/8/1 14:29
 */
@Data
public class SkuLsParams implements Serializable {

    //相当于skuName
    String  keyword;

    String catalog3Id;

    String[] valueId;

    int pageNo=1;

    int pageSize=20;

}
