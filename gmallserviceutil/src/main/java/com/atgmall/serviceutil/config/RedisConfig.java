package com.atgmall.serviceutil.config;
/*
在启动类启动时，按该自定义配置进行装配
 */
import com.atgmall.serviceutil.RedisUtils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/7/30 18:29
 */
@Configuration
public class RedisConfig {


    //从配置文件application.properties中取值
    //                          :<默认值>
    @Value("${spring.redis.host:disabled}")
    private String host;
    @Value("${spring.redis.port:0}")
    private int post;
    @Value("${spring.redis.database:0}")
    private int database;


    //将上边的参数设置到RedisUtil对象中，将RedisUtil 纳入Spring容器中，装配bean
    @Bean
    public RedisUtil setRedisUtil(){
        if(host.equals("disabled")){
            return null;
        }else {
            RedisUtil util = new RedisUtil();
            util.initJedisPool(host, post, database);
            return util;
        }
    }




}
