package com.rainbow.lock.aop.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author An
 * @title: RedisLock
 * @projectName rainbow-security-boot-starter
 * @description: redis分布式锁注解
 * @date 2020/12/2917:24
 */

@Target(ElementType.METHOD)//定义此注解作用于方法上
@Retention(RetentionPolicy.RUNTIME)//生命周期
@Documented
public @interface RedisLock {
    /**
     * 锁过期时间 默认是 3秒
     * @return 单位是毫秒
     */
    long expire() default 30000;


    /**
     * 时间单位 默认是毫秒
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * springEl 表达式
     * 为空值取方法名加包名
     * @return
     */
    String  key() default "";

    /**
     * 防止key名称相同 前缀
     * @return
     */
    String  prefix() default "rainbow";

    /**
     * 获取锁的超时时间 未设置值时为默认3秒、为0时为不等待
     * @return 单位为毫秒
     */
    long lockTimeOut() default 0;
    /**
     * 锁 vlue
     * @return
     */
    String value() default "";


    boolean throwsException() default false;
}
