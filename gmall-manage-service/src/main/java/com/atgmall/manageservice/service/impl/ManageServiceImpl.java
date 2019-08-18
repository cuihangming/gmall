package com.atgmall.manageservice.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atgmall.bean.*;
import com.atgmall.manageservice.constant.ManageConst;
import com.atgmall.manageservice.mapper.*;
import com.atgmall.service.ManageService;
import com.atgmall.serviceutil.RedisUtils.RedisUtil;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/7/24 19:05
 */
@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private Basecatalog1Mapper log1Mapper;
    @Autowired
    private Basecatalog2Mapper log2Mapper;
    @Autowired
    private Basecatalog3Mapper log3Mapper;
    @Autowired
    private BaseAttrInfoMapper InfoMapper;
    @Autowired
    private BaseAttrValueMapper valueMapper;
    @Autowired
    private SpuInfoMapper spuMapper;
    @Autowired
    private  BaseSaleAttrMapper SaleMapper;
    @Autowired
    private SpuImageMapper spuimgMapper;
    @Autowired
    private SpuSaleAttrMapper spusaleMapper;
    @Autowired
    private SpuSaleAttrValueMapper spusaleValueMapper;
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private RedisUtil redisUtil;





    @Override
    public List<BaseCatalog1> getBasecatalog1List() {
        return log1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getBasecatalog2List(String catalog1Id) {
        BaseCatalog2 catalog2 = new BaseCatalog2();
        catalog2.setCatalog1Id(catalog1Id);
        List<BaseCatalog2> list = log2Mapper.select(catalog2);
        return list;
    }

    @Override
    public List<BaseCatalog3> getBasecatalog3List(String catalog2Id) {
        BaseCatalog3 catalog3 = new BaseCatalog3();
        catalog3.setCatalog2Id(catalog2Id);
        List<BaseCatalog3> list = log3Mapper.select(catalog3);
        return list;
    }

    @Override
    public List<BaseAttrInfo> getBaseAttrInfoList(String catalog3Id) {
//        BaseAttrInfo attrInfo = new BaseAttrInfo();
//        attrInfo.setCatalog3Id(catalog3Id);
//        List<BaseAttrInfo> list = InfoMapper.select(attrInfo);
        List<BaseAttrInfo> list = InfoMapper.getBaseAttrInfoListByCatalog3Id(Long.parseLong(catalog3Id));
        return list;
    }

    @Override
    public List<BaseAttrValue> getBaseAttrValueList(String catalog4Id) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(catalog4Id);
        List<BaseAttrValue> list = valueMapper.select(baseAttrValue);
        return list;
    }

    @Transactional
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        // 修改操作！
        if (baseAttrInfo.getId()!=null && baseAttrInfo.getId().length()>0){
            InfoMapper.updateByPrimaryKeySelective(baseAttrInfo);
        }else {
            // 保存数据 baseAttrInfo
            InfoMapper.insertSelective(baseAttrInfo);
        }

        // baseAttrValue ？  先清空数据，在插入到数据即可！
        // 清空数据的条件 根据attrId 为依据
        // delete from baseAttrValue where attrId = baseAttrInfo.getId();
        BaseAttrValue baseAttrValueDel = new BaseAttrValue();
        baseAttrValueDel.setAttrId(baseAttrInfo.getId());
        valueMapper.delete(baseAttrValueDel);

        // baseAttrValue
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        // if ( attrValueList.size()>0  && attrValueList!=null){
        if (attrValueList!=null && attrValueList.size()>0){
            // 循环判断
            for (BaseAttrValue baseAttrValue : attrValueList) {
                //  private String id;
                //  private String valueName; 前台页面传递
                //  private String attrId; attrId=baseAttrInfo.getId();
                //  前提条件baseAttrInfo 对象中的主键必须能够获取到自增的值！
                //  baseAttrValue.setId("122"); 测试事务
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                valueMapper.insertSelective(baseAttrValue);
            }
        }
    }

    @Override
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo) {
        List<SpuInfo> list = spuMapper.select(spuInfo);
        return list;
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        List<BaseSaleAttr> list = SaleMapper.selectAll();
        return list;
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        // 什么情况下是保存，什么情况下是更新 spuInfo
        if (spuInfo.getId()==null || spuInfo.getId().length()==0){
            //保存数据
            spuInfo.setId(null);
            spuMapper.insertSelective(spuInfo);
            System.out.println("ID是："+spuInfo.getId());
        }else {
            spuMapper.updateByPrimaryKeySelective(spuInfo);
        }

        //  spuImage 图片列表 先删除，在新增
        //  delete from spuImage where spuId =?
        SpuImage spuImage = new SpuImage();

        spuImage.setSpuId(spuInfo.getId());
        spuimgMapper.delete(spuImage);

        // 保存数据，先获取数据
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList!=null && spuImageList.size()>0){
            // 循环遍历
            for (SpuImage image : spuImageList) {
                image.setId(null);
                image.setSpuId(spuInfo.getId());
                spuimgMapper.insertSelective(image);
            }
        }
        // 销售属性 删除，插入
        SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
        spuSaleAttr.setSpuId(spuInfo.getId());
        spusaleMapper.delete(spuSaleAttr);

        // 销售属性值 删除，插入
        SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
        spuSaleAttrValue.setSpuId(spuInfo.getId());
        spusaleValueMapper.delete(spuSaleAttrValue);

        // 获取数据
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList!=null && spuSaleAttrList.size()>0){
            // 循环遍历
            for (SpuSaleAttr saleAttr : spuSaleAttrList) {
                saleAttr.setId(null);
                saleAttr.setSpuId(spuInfo.getId());
                spusaleMapper.insertSelective(saleAttr);

                // 添加销售属性值
                List<SpuSaleAttrValue> spuSaleAttrValueList = saleAttr.getSpuSaleAttrValueList();
                if (spuSaleAttrValueList!=null && spuSaleAttrValueList.size()>0){
                    // 循环遍历
                    for (SpuSaleAttrValue saleAttrValue : spuSaleAttrValueList) {
                        saleAttrValue.setId(null);
                        saleAttrValue.setSpuId(spuInfo.getId());
                        spusaleValueMapper.insertSelective(saleAttrValue);
                    }
                }

            }
        }
    }
/*
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        if(spuInfo.getId()==null || StringUtils.isEmpty(spuInfo.getId())){
            //保存Spu表

            spuMapper.insertSelective(spuInfo);
            //保存图片
            List<SpuImage> imageList = spuInfo.getSpuImageList();
            if(imageList!=null && imageList.size()>0){
                for (int i = 0; i <imageList.size() ; i++) {
                    SpuImage spuImage = imageList.get(i);
                    spuImage.setId(null);
                    spuImage.setSpuId(spuInfo.getId());
                    imgMapper.insertSelective(spuImage);
                }
            }

            List<SpuSaleAttr> saleAttrList = spuInfo.getSpuSaleAttrList();
            if(saleAttrList!=null && saleAttrList.size()>0){
                for (int i = 0; i <saleAttrList.size() ; i++) {
                    SpuSaleAttr saleAttr = saleAttrList.get(i);
                    //保存销售属性
                    saleAttr.setId(null);
                    saleAttr.setSpuId(spuInfo.getId());
                    saleMapper.insertSelective(saleAttr);



                    List<SpuSaleAttrValue> valueList = saleAttr.getSpuSaleAttrValueList();
                    if(valueList!=null && valueList.size()>0){
                        for (int j = 0; j <valueList.size() ; j++) {
                            SpuSaleAttrValue attrValue = valueList.get(j);
                            //保存销售属性值
                            attrValue.setId(null);
                            attrValue.setSpuId(spuInfo.getId());
                            saleValueMapper.insertSelective(attrValue);
                        }
                    }
                }
            }
        }
    }
*/






    @Override
    public List<SpuImage> getSpuImgAllById(String spuId) {
//        Example example = new Example(SpuImage.class);
//        Example id = example.selectProperties(spuId);
//        List<SpuImage> list1 = imgMapper.selectByExample(id);
        SpuImage image = new SpuImage();
        image.setSpuId(spuId);
        List<SpuImage> list = spuimgMapper.select(image);
        return list;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        List<SpuSaleAttr> list = spusaleMapper.getSpuSaleAttrListBySpuId(Long.parseLong(spuId));
        return list;
    }

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        // sku_info
        if (skuInfo.getId()==null || skuInfo.getId().length()==0){
            // 设置id 为自增
            skuInfo.setId(null);
            skuInfoMapper.insertSelective(skuInfo);
        }else {
            skuInfoMapper.updateByPrimaryKeySelective(skuInfo);
        }

        //        sku_img,
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuInfo.getId());
        skuImageMapper.delete(skuImage);

        // insert
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList!=null && skuImageList.size()>0){
            for (SkuImage image : skuImageList) {
                /* "" 区别 null*/
                if (image.getId()!=null && image.getId().length()==0){
                    image.setId(null);
                }
                // skuId 必须赋值
                image.setSkuId(skuInfo.getId());
                skuImageMapper.insertSelective(image);
            }
        }
//        sku_attr_value,
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuInfo.getId());
        skuAttrValueMapper.delete(skuAttrValue);

        // 插入数据
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (skuAttrValueList!=null && skuAttrValueList.size()>0){
            for (SkuAttrValue attrValue : skuAttrValueList) {
                if (attrValue.getId()!=null && attrValue.getId().length()==0){
                    attrValue.setId(null);
                }
                // skuId
                attrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insertSelective(attrValue);
            }
        }
//        sku_sale_attr_value,
        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuInfo.getId());
        skuSaleAttrValueMapper.delete(skuSaleAttrValue);
//      插入数据
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (skuSaleAttrValueList!=null && skuSaleAttrValueList.size()>0){
            for (SkuSaleAttrValue saleAttrValue : skuSaleAttrValueList) {
                if (saleAttrValue.getId()!=null && saleAttrValue.getId().length()==0){
                    saleAttrValue.setId(null);
                }
                // skuId
                saleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insertSelective(saleAttrValue);
            }
        }
    }

//    @Override
//    public SkuInfo getSkuInfo(String skuId) {
//        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
//        SkuImage image = new SkuImage();
//        image.setSkuId(skuId);
//        List<SkuImage> imageList = skuImageMapper.select(image);
//        skuInfo.setSkuImageList(imageList);
//        return skuInfo;
//    }


    /*
    企业中最常用的方式就是：object:id:field
                                :表示根windows的 /一个意思
     */
    @Override
    public SkuInfo getSkuInfo(String skuId) {
        Jedis jedis = null;
        SkuInfo info;
//解决Redis的高并发问题{
        //new Rediss的Config对象
        Config config = new Config();                           //redis的端口
        config.useSingleServer().setAddress("redis://192.168.124.133:6379");
        RedissonClient redissonClient = Redisson.create(config);

        //redisson客户端获得锁       锁名
        RLock lock = redissonClient.getLock("YourLock");
        //上锁                  //时间单位
//  }
        try {
            lock.lock(10, TimeUnit.SECONDS);
            //获取jedis客户端            有获取就有关闭
            jedis = redisUtil.getJedis();
            //object:id:field
            String jedisSkuid = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            //redis数据库中是否存在该Key
            if (jedis.exists(jedisSkuid)) {
                String s = jedis.get(jedisSkuid);
                //判断字符串是否为空
                if (s != null && s.length() > 0) {
                    //将获得的Value值转成对象
                    info = JSON.parseObject(s, SkuInfo.class);
                    return info;
                }
            } else {
                //缓存数据库中没有该Key，则从服务器数据库中进行查询，并放入Redis数据库中
                info = getSkuInfoDB(skuId);

                //将SkuInfo的值放入Redis数据库中
                String stringSkuInfoValue = JSON.toJSONString(info);  //对象==》String的value值
                //将字符串转变成Key,存入Redis
                //key          超时时间                  value值
                jedis.setex(jedisSkuid, ManageConst.SKUKEY_TIMEOUT, stringSkuInfoValue);  //存放操作
                return info;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                //关闭jedis
                jedis.close();
            }
            //必须解锁
            if(lock!=null) {
                lock.unlock();
            }
        }
        return getSkuInfoDB(skuId);
    }

    public SkuInfo getSkuInfoDB(String skuId) {
        // 单纯的信息
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        // 查询图片
        SkuImage image = new SkuImage();
        image.setSkuId(skuId);
        List<SkuImage> imageList = skuImageMapper.select(image);
        System.out.println("集合是"+image);

        skuInfo.setSkuImageList(imageList);

        SkuAttrValue attrValue = new SkuAttrValue();
        attrValue.setSkuId(skuId);
        List<SkuAttrValue> attrValues = skuAttrValueMapper.select(attrValue);
        skuInfo.setSkuAttrValueList(attrValues);

        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuId);
        List<SkuSaleAttrValue> skusalelist = skuSaleAttrValueMapper.select(skuSaleAttrValue);
        skuInfo.setSkuSaleAttrValueList(skusalelist);

        return skuInfo;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo info) {
        List<SpuSaleAttr> list= spusaleMapper.getSpuSaleBySku(info.getSpuId(),info.getId());
        return list;
    }


}
