package com.atgmall.orderservice.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atgmall.bean.OrderDetail;
import com.atgmall.bean.OrderInfo;
import com.atgmall.bean.enums.OrderStatus;
import com.atgmall.bean.enums.ProcessStatus;
import com.atgmall.orderservice.mapper.OrderDetailMapper;
import com.atgmall.orderservice.mapper.OrderInfoMapper;
import com.atgmall.service.OrderService;
import com.atgmall.serviceutil.RedisUtils.RedisUtil;
import config.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.*;


/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/8/8 22:30
 */

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private RedisUtil redisUtil;


    @Transactional  //数据库插入数据时，同时插入两张以上的+事物
    @Override
    public String saveOrder(OrderInfo orderInfo) {
        //总金额
        orderInfo.sumTotalAmount();
        //订单状态
        orderInfo.setOrderStatus(OrderStatus.UNPAID);
        //订单创建时间
        orderInfo.setCreateTime(new Date());

        Calendar calendar = Calendar.getInstance();//启动java系统类中的日历类Calender
        calendar.add(Calendar.DATE,1);
        //失效时间
        orderInfo.setExpireTime(calendar.getTime());   //设置失效时间

        //生成第三方支付编号
        String orderNum="ATGUIGU"+System.currentTimeMillis()+""+new Random().nextInt(1000);
        orderInfo.setOutTradeNo(orderNum);
        //进程状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);

        //插入订单;
        orderInfoMapper.insertSelective(orderInfo);

        //保存订单详情
        List<OrderDetail> detailList = orderInfo.getOrderDetailList();
        for (OrderDetail detail : detailList) {
                detail.setOrderId(orderInfo.getId());
                orderDetailMapper.insertSelective(detail);
        }

        Random random = new Random();
        int i = random.nextInt(1000);
        System.out.println("随机数为："+i);

                //返回插入OrderInfo的自增id
        return orderInfo.getId();
    }

    @Override
    public String createTradeNo(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String tradeNoKey="user:"+userId+":tradeCode";
        String  tradeCode = UUID.randomUUID().toString();      //全球唯一编号
        jedis.set(tradeNoKey,tradeCode);
        jedis.close();
        return tradeCode;
    }



    @Override
    public boolean checkTradeNo(String userId, String tradeCodeNo) {
        Jedis jedis = redisUtil.getJedis();
        String tradeNoKey="user:"+userId+":tradeCode";
        String tradeCode = jedis.get(tradeNoKey);
        jedis.close();
        if(tradeCode!=null && tradeCode.equals(tradeCodeNo)){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void deleleTradeNo(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String tradeNoKey="user:"+userId+":tradeCode";
        jedis.del(tradeNoKey);
        jedis.close();
    }

    @Override
    public boolean checkStock(String skuId, Integer skuNum) {
        //请求查库存的请求地址        购买商品数量   满足 库存要求，返回1，否则返回0
        String result = HttpClientUtil.doGet("http://www.gware.com/hasStock?skuId=" + skuId + "&num=" + skuNum);

        if ("1".equals(result)){
            return  true;
        }else {
            return  false;
        }
    }


}
