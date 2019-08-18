package com.atgmall.manageweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atgmall.bean.*;
import com.atgmall.service.ListService;
import com.atgmall.service.ManageService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/7/24 19:27
 */
@RestController
@CrossOrigin
public class ManageControll {

    @Reference
    private ManageService manavice;

    @Reference
    private ListService listService;

    // 查找一级分类
    @PostMapping("getCatalog1")
    public List<BaseCatalog1> getBaseCatalog1List(){
        return manavice.getBasecatalog1List();
    }

    // 查找二级分类
    @PostMapping("getCatalog2")
    public List<BaseCatalog2> getBaseCatalog2List(String catalog1Id){
       return manavice.getBasecatalog2List(catalog1Id);
    }
    // 查找三级分类
    @PostMapping("getCatalog3")
    public List<BaseCatalog3> getBaseCatalog3List(String catalog2Id){
        return manavice.getBasecatalog3List(catalog2Id);
    }
    // 根据三级分类id获取属性列表
    @GetMapping("attrInfoList")
    public List<BaseAttrInfo> getgetAttrInfoList(String catalog3Id){
        return manavice.getBaseAttrInfoList(catalog3Id);
    }

    // 根据属性id获取属性值列表,用于修改时的数据回显
    @PostMapping("getAttrValueList")
    public List<BaseAttrValue> getBaseAttrValueList(String attrId){
        return manavice.getBaseAttrValueList(attrId);
    }


    @PostMapping("saveAttrInfo")
    public void addAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
         manavice.saveAttrInfo(baseAttrInfo);
    }

    // 根据三级分类id获取属性列表
    @GetMapping("spuList")
    public List<SpuInfo> spuList(String catalog3Id){
        SpuInfo info = new SpuInfo();
        info.setCatalog3Id(catalog3Id);
        List<SpuInfo> list = manavice.getSpuInfoList(info);
        return list;
    }


    @PostMapping("baseSaleAttrList")
    public List<BaseSaleAttr> AddSpuInfo(){
        List<BaseSaleAttr> list = manavice.getBaseSaleAttrList();
        return list;
    }


    @PostMapping("saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo info){
        manavice.saveSpuInfo(info);
    }


    @GetMapping("spuImageList")
    public List<SpuImage> getSpuImgList(String spuId){
        List<SpuImage> allById = manavice.getSpuImgAllById(spuId);
        return allById;
    }

    @GetMapping("spuSaleAttrList")
    public List<SpuSaleAttr> getSaleAttrList(String spuId){
        return manavice.getSpuSaleAttrList(spuId);
    }

    @PostMapping("saveSkuInfo")
    public String saveSkuInfo(@RequestBody  SkuInfo skuInfo) {
        manavice.saveSkuInfo(skuInfo);
        return "OK";
    }



    @GetMapping("onSale")
    public void onSale(String skuid){
        SkuInfo skuInfo = manavice.getSkuInfo(skuid);
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        //将查出来的SkuInfo  拷贝赋值到新的用于封装ES信息的Bean中
        try {
            BeanUtils.copyProperties(skuInfo,skuLsInfo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        listService.saveSkuInfo(skuLsInfo);
    }
}
