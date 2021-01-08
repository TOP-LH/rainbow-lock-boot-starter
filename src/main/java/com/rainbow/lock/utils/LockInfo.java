package com.rainbow.lock.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author An
 * @title: LockInfo
 * @projectName redisNx
 * @description: 锁信息
 * @date 2020/12/319:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LockInfo {
    /**
     * 锁 key
     */
    private String lockKey;

    /**
     * 锁 value
     */
    private String lockValue;


    /**
     * 锁 过期时间
     */
    private Long expireTime;


    /**
     * 锁 等待超时时间
     */
    private Long lockOutTime;


    /**
     * 锁 尝试获取次数
     */
    private int acquireCount;
    /**
     * 是否成功
     */
    private Boolean lockSuccess;
    /**
     * 锁 返回信息
     * */
    private String message;

}
