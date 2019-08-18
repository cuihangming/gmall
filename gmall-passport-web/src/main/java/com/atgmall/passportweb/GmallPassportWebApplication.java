package com.atgmall.passportweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.atgmall.passportweb","com.atgmall.webutil.config"})	//配置拦截器
public class GmallPassportWebApplication {
	public static void main(String[] args) {
		SpringApplication.run(GmallPassportWebApplication.class, args);
	}

}
