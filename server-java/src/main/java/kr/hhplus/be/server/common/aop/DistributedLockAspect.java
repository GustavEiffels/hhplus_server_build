package kr.hhplus.be.server.common.aop;

import kr.hhplus.be.server.application.point.PointFacadeDto;
import kr.hhplus.be.server.common.config.redis.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile("redisson")
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {
    private final RedissonClient redissonClient;

    @Pointcut("@annotation(kr.hhplus.be.server.common.config.redis.DistributedLock)")
    public void distributedLockPointcut() {}

    @Around("distributedLockPointcut() && @annotation(distributedLock)")
    public Object handleDistributedLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {


        Object[] args = joinPoint.getArgs();
        Long userId = null;

        // 파라미터가 객체인 경우 (예: ChargeParam userId)
        for (Object arg : args) {
            if (arg instanceof PointFacadeDto.ChargeParam) {
                userId = ((PointFacadeDto.ChargeParam) arg).userId();
                break;
            }
        }

        if (userId == null) {
            throw new IllegalArgumentException("userId가 제공되지 않았습니다.");
        }

        String lockName = distributedLock.lockNm()+userId;
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
