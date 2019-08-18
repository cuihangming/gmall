package com.atgmall.listweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.atgmall.webutil.config","com.atgmall.listweb"})
public class GmallListWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallListWebApplication.class, args);
	}

}
