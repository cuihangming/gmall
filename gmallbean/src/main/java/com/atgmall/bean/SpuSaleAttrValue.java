package com.atgmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/7/27 0:45
 */
@Data
public class SpuSaleAttrValue  implements Serializable{


    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String id ;

    @Column
    String spuId;

    @Column
    String saleAttrId;

    @Column
    String saleAttrValueName;

    @Transient
    String isChecked;

}
