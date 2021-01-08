package com.rainbow.lock.exception;

/**
 * @author An
 * @title: ReleaseLockException
 * @projectName redisNx
 * @description: 释放锁失败异常类
 * @date 2020/12/3110:38
 */
public class ReleaseLockException extends LockException {
    public ReleaseLockException()
    {

    }
    public ReleaseLockException(String message)
    {
        super(message);
    }
}
