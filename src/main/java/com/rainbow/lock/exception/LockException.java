package com.rainbow.lock.exception;

/**
 * @author An
 * @title: LockException
 * @projectName redisNx
 * @description: 锁异常类
 * @date 2020/12/3110:33
 */
public class LockException extends RuntimeException {
    public LockException()
    {
        super();
    }

    /**
     * 返回错误信息
     * @param message
     */
    public LockException(String message)
    {
        super(message);
    }
}
