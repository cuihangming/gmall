package com.atgmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/7/24 16:45
 */
@Data
public class BaseAttrInfo implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //获取主键自增的id值
    private String id;

    @Column
    private String attrName;

    @Column
    private String catalog3Id;

    @Transient
    private List<BaseAttrValue> attrValueList;



}
