package kr.hhplus.be.server.common.aop;

import kr.hhplus.be.server.common.filter.LogFilter;
import kr.hhplus.be.server.interfaces.scheduler.SchedulerContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {


    @Around("execution(* kr.hhplus.be.server.domain.concert.ConcertRepository.*(..)) || " +
            "execution(* kr.hhplus.be.server.domain.payment.PaymentRepository.*(..)) || " +
            "execution(* kr.hhplus.be.server.domain.point_history.PointHistoryRepository.*(..)) || " +
            "execution(* kr.hhplus.be.server.domain.queue_token.QueueTokenRepository.*(..)) || " +
            "execution(* kr.hhplus.be.server.domain.reservation.ReservationRepository.*(..)) || " +
            "execution(* kr.hhplus.be.server.domain.schedule.ConcertScheduleRepository.*(..)) || " +
            "execution(* kr.hhplus.be.server.domain.seat.SeatRepository.*(..)) || " +
            "execution(* kr.hhplus.be.server.domain.user.UserRepository.*(..))")
    public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String uuid = getCurrentUuid();
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("UUID - [{}] | Executed: {} in {} ms", uuid, methodName, endTime - startTime);
        }
    }

    private String getCurrentUuid() {
        if (SchedulerContext.getUuid() != null) {
            return SchedulerContext.getUuid();
        } else {
            return LogFilter.getCurrentUUID();
        }
    }
}
