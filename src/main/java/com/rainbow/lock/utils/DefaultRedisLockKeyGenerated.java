package com.rainbow.lock.utils;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author An
 * @title: DefaultRedisLockKeyGenerated
 * @projectName redisNx
 * @description: redis nx 默认key生成器
 * @date 2020/12/3010:28
 */
@Component
public class DefaultRedisLockKeyGenerated implements DefaultRedisLockKey {

        @Override
        public String buildDefaultKey(Method method,String prefix) {
            //为生成的key添加前缀
            StringBuilder bd=new StringBuilder(prefix);;
            //拼接方法的全包名加方法名称
            bd.append(":").append(method.getDeclaringClass().getName()).append(".").append(method.getName());
            return bd.toString();
        }
}
