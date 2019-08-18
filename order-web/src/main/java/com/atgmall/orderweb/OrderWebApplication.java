package com.atgmall.orderweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.atgmall.orderweb","com.atgmall.webutil.config"})
public class OrderWebApplication {
	public static void main(String[] args) {
		SpringApplication.run(OrderWebApplication.class, args);
	}

}
