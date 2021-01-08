package com.rainbow.lock.exception;

/**
 * @author An
 * @title: LockFailure
 * @projectName redisNx
 * @description: 上锁失败异常
 * @date 2020/12/3110:36
 */
public class LockFailureException extends LockException {
    public LockFailureException() {

    }

    public LockFailureException(String message) {
        super(message);
    }
}
