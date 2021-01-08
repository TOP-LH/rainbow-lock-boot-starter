package com.rainbow.lock.aop.aspect;

import com.rainbow.lock.aop.annotation.RedisLock;
import com.rainbow.lock.exception.LockFailureException;
import com.rainbow.lock.exception.ReleaseLockException;
import com.rainbow.lock.utils.DefaultRedisLockKeyGenerated;
import com.rainbow.lock.utils.LockInfo;
import com.rainbow.lock.utils.LockTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author An
 * @title: RedisLockAspect
 * @projectName rainbow-security-boot-starter
 * @description: redis stenx 锁切面
 * @date 2020/12/2918:58
 */
@Slf4j
@Component
@Aspect
public class RedisLockAspect {
    /**
     * 定义切面为RockLock这个注解
     * @return
     */
    @Pointcut("@annotation(com.rainbow.lock.aop.annotation.RedisLock)")
    public void pointCut(){};
    @Resource
    private DefaultRedisLockKeyGenerated defaultRedisLockKeyGenerated;
    @Resource
    private LockTemplate lockTemplate;

    /**
     * 环绕通知注解
     * @param joinPoint
     * @return
     */
    @Around("pointCut()")
    public Object RedisLockAround(ProceedingJoinPoint joinPoint) throws Throwable {
        LockInfo lockInfo=null;
        RedisLock redisLock=null;
        try{
            MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
            //注解参数
            redisLock= methodSignature.getMethod().getDeclaredAnnotation(RedisLock.class);
            //获取锁的时间单位
            TimeUnit timeUnit = redisLock.timeUnit();
            //获取key
            String key = redisLock.key();
            //获取value
            String value = redisLock.value();
            //获取过期时间
            Long expireTime = redisLock.expire();
            //等待超时时间
            Long lockOutTime = redisLock.lockTimeOut();
            if(StringUtils.isEmpty(key))
            {
                //如果key为空则自动生成next : 全类名.方法名成
                key=defaultRedisLockKeyGenerated.buildDefaultKey(methodSignature.getMethod(),redisLock.prefix());
            }
            if(lockOutTime==0)
            {
                lockInfo = lockTemplate.setLock(key, value, expireTime);
            }else
            {
                lockInfo = lockTemplate.setLock(key,value,lockOutTime,expireTime);
            }
            if(!lockInfo.getLockSuccess())
            {
                if(redisLock.throwsException())
                {
                    throw new LockFailureException(lockInfo.getMessage());
                }else
                {
                    log.error(lockInfo.getMessage());
                }
            }else
            {
                log.info(lockInfo.getMessage());
            }
            joinPoint.proceed();

        }finally {
            //判断锁是否不为空最终都需要释放锁
            if(lockInfo!=null)
            {
                final boolean releaseLock=lockTemplate.releaseLock(lockInfo.getLockKey(),lockInfo.getLockValue());
                if(!releaseLock)
                {
                    //log.error("lock: releaseLock fail, lockKey={},lockValue={}",lockInfo.getLockKey(),lockInfo.getLockValue());
                    return new ReleaseLockException(lockInfo.getMessage());
                }else
                {
                    log.info("lock: successfully unlocked lockKey={},lockValue={}",lockInfo.getLockKey(),lockInfo.getLockValue());
                }
            }
        }
        return null;
    }


}
