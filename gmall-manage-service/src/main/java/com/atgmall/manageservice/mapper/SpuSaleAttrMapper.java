package com.atgmall.manageservice.mapper;

import com.atgmall.bean.SpuSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/7/27 0:49
 */
public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
    List<SpuSaleAttr> getSpuSaleAttrListBySpuId(Long spuid);

    List<SpuSaleAttr> getSpuSaleBySku(@Param("spuId") String spuId, @Param("skuid") String skuid);
}
