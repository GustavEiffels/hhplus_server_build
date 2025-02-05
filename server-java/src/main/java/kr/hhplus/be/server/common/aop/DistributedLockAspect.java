package kr.hhplus.be.server.common.aop;

import kr.hhplus.be.server.common.config.redis.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonClient redissonClient;

    @Pointcut("@annotation(kr.hhplus.be.server.common.config.redis.DistributedLock)")
    public void distributedLockPointcut() {}

    @Around("distributedLockPointcut() && @annotation(distributedLock)")
    public Object handleDistributedLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {

        String methodName = joinPoint.getSignature().getName();
        String lockName = methodName;

        RLock lock = redissonClient.getLock(lockName);

        try {
            boolean locked = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (locked) {
                return joinPoint.proceed();
            } else {
                log.info("해당 락을 다른 사람이 사용하고 있음");
                throw new IllegalStateException("해당 작업은 현재 다른 프로세스에서 진행 중입니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 인터럽트 발생", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
