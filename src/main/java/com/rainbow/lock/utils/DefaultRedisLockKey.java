package com.rainbow.lock.utils;

import java.lang.reflect.Method;

/**
 * @author An
 * @title: DefaultRedisLockKey
 * @projectName redisNx
 * @description: TODO
 * @date 2020/12/3010:52
 */
public interface DefaultRedisLockKey {
    /**
     *
     * @param method
     * @return
     */
    String buildDefaultKey(Method method,String prefix);
}
