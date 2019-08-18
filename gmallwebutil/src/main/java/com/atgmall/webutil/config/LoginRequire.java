package com.atgmall.webutil.config;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/8/4 16:55
 */
@Target(ElementType.METHOD) //注解使用在方法上
@Retention(RetentionPolicy.RUNTIME) //类运行时使用
public @interface LoginRequire {

    boolean autoRedirect() default true;
}
