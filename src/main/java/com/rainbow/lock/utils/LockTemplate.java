package com.rainbow.lock.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;


/**
 * @author An
 * @title: LockTemplate
 * @projectName redisNx
 * @description: TODO
 * @date 2020/12/3013:07
 */
@Slf4j
@Component
public class LockTemplate {
      //释放锁的lua脚本
      private static final RedisScript<String> UNLOCK_LUA;
      //setNx的脚本
      private static final RedisScript<String> SCRIPT_LOCK;

      private static final String LOCK_SUCCESS = "OK";

      @Autowired
      private DefaultRedisLockKeyGenerated defaultRedisLockKeyGenerated;
      @Autowired
      private RedisTemplate<String,String> redisTemplate;

      static {
        //构造脚本
        StringBuilder sb = new StringBuilder();
        //获取到resource_name 判断和程序 是否是当前程序上的锁 是否相等在去释放锁
        //ARGV[] 程序传来的值
        UNLOCK_LUA = new DefaultRedisScript<>("if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end",String.class);
        SCRIPT_LOCK=new DefaultRedisScript<>("return redis.call('set',KEYS[1],ARGV[1],'NX','PX',ARGV[2])", String.class);
      }
      //默认的锁value
      private static final String LOCK_VALUE = "1";

    /**
     *
     * @param key 锁key
     * @param value 锁value
     * @param expire 锁的等待时间
     * @return
     */
      public LockInfo setLock(String key,String value,Long expire)
      {
          if(StringUtils.isBlank(value))
          {
              value=LOCK_VALUE;
          }
          return setNx(key,value,expire);
      }
      public LockInfo setLock(String key,Long expire) {
            return setLock(key, "", expire);
      }
      public LockInfo setLock(String key,Long lockOutTime,Long expire)
      {
         return setLock(key,null,lockOutTime,expire);
      }
      public LockInfo setLock(String key,String value,Long lockOutTime,Long expire)
      {
          LockInfo lockInfo=new LockInfo();
          if(StringUtils.isBlank(value))
          {
              value=LOCK_VALUE;
          }
          //判断是否等待获取锁
          if(lockOutTime==0L)
          {
              //递归回调
              return setLock(key,value,expire);
          }
          //获取开始时间转为毫秒
          Long startTime= System.currentTimeMillis();
          while(true)
          {
              //判断是否等待超时
              if(System.currentTimeMillis()-startTime>lockOutTime)
              {
                  lockInfo.setLockSuccess(false);
                  lockInfo.setMessage("lock: Timeout waiting to acquire lock resource");
                  return lockInfo;
              }
              //未超时的情况下进行上锁
              lockInfo=setLock(key,value,expire);
              if(lockInfo.getLockSuccess())
              {
                  return lockInfo;
              }else
              {
                  continue;
              }
          }
      }
      //获取value
      public Optional<String> getLockValue(String key)
      {
          ValueOperations<String, String> value = redisTemplate.opsForValue();
          return Optional.ofNullable(value.get(key));
      }

    /**
     * 设置nx锁
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
      public LockInfo setNx(String key, String value, Long expireTime)
      {
          LockInfo lockInfo=new LockInfo();
          String lock = redisTemplate.execute(SCRIPT_LOCK,
                  redisTemplate.getStringSerializer(),
                  redisTemplate.getStringSerializer(),
                  Collections.singletonList(key),
                  value, String.valueOf(expireTime));
          if(!LOCK_SUCCESS.equals(lock))
          {
              lockInfo.setLockSuccess(false);
              lockInfo.setMessage("lock: The lock resource is now occupied, please try again later");
              return lockInfo;
          }
          lockInfo.setLockSuccess(true);
          lockInfo.setMessage("lock: Set lock successfully lockKey="+key+", lockValue="+value);
          lockInfo.setExpireTime(expireTime);
          lockInfo.setLockKey(key);
          lockInfo.setLockValue(value);
          return lockInfo;
      }

    /**
     * 释放锁
     * @param key
     * @return
     */
      public boolean releaseLock(String key)
      {
          return  releaseLock(key,LOCK_VALUE);
      }
      public boolean releaseLock(String key,String value)
      {
          try {
              //执行lua脚本删除lock锁
              Object execute = redisTemplate.execute(
                      (RedisConnection connection) -> connection.eval(
                              UNLOCK_LUA.getScriptAsString().getBytes(),
                              ReturnType.INTEGER,
                              1,
                              key.getBytes(),
                              value.getBytes()
                      )
              );
              //判断是否释放锁成功
              return execute.equals(1L);

          }catch (Exception e)
          {
              log.error("lock: releaseLock fail:"+e.getMessage());
          }
          return false;
      }

}
