package com.atgmall.serviceutil.RedisUtils;

/*
Redis缓存数据库用到的工具
 */

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/7/30 18:45
 */
public class RedisUtil {

    private JedisPool jedisPool;

    public void initJedisPool(String host,int post,int database){
        JedisPoolConfig config = new JedisPoolConfig();
        //设置最大连接数
        config.setMaxTotal(200);
        //设置最小连接数
        config.setMaxWaitMillis(10*1000);
        //设置最大等待时间
        config.setMinIdle(10);
        //达到最大连接数时是否进行阻塞《等待》
        config.setBlockWhenExhausted(true);
        //获取连接时检查连接是否有效
        config.setTestOnBorrow(true);
        //Jedis连接池
        jedisPool=new JedisPool(config,host,post,10*1000);
    }

    //Jedis的获取方法
    public Jedis getJedis(){
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

}
