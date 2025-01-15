package kr.hhplus.be.server.common.aop;

import kr.hhplus.be.server.common.filter.LogFilter;
import kr.hhplus.be.server.interfaces.scheduler.SchedulerContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
//    @Before("execution(* kr.hhplus.be.server.domain.concert.ConcertRepository.*(..)) || " +
//            "execution(* kr.hhplus.be.server.domain.payment.PaymentRepository.*(..)) || " +
//            "execution(* kr.hhplus.be.server.domain.point_history.PointHistoryRepository.*(..)) || " +
//            "execution(* kr.hhplus.be.server.domain.queue_token.QueueTokenRepository.*(..)) || " +
//            "execution(* kr.hhplus.be.server.domain.reservation.ReservationRepository.*(..)) || " +
//            "execution(* kr.hhplus.be.server.domain.schedule.ConcertScheduleRepository.*(..)) || " +
//            "execution(* kr.hhplus.be.server.domain.seat.SeatRepository.*(..)) || " +
//            "execution(* kr.hhplus.be.server.domain.user.UserRepository.*(..))")
//    public void logMethodStart(JoinPoint joinPoint) {
//        String uuid = getCurrentUuid();
//        String methodName = joinPoint.getSignature().toShortString();
//        log.info("UUID - [{}] | Method Start: {}", uuid, methodName);
//    }
//
//    @After("execution(* kr.hhplus.be.server.domain.concert.ConcertRepository.*(..)) || " +
//            "execution(* kr.hhplus.be.server.domain.payment.PaymentRepository.*(..)) || " +
//            "execution(* kr.hhplus.be.server.domain.point_history.PointHistoryRepository.*(..)) || " +
//            "execution(* kr.hhplus.be.server.domain.queue_token.QueueTokenRepository.*(..)) || " +
//            "execution(* kr.hhplus.be.server.domain.reservation.ReservationRepository.*(..)) || " +
//            "execution(* kr.hhplus.be.server.domain.schedule.ConcertScheduleRepository.*(..)) || " +
//            "execution(* kr.hhplus.be.server.domain.seat.SeatRepository.*(..)) || " +
//            "execution(* kr.hhplus.be.server.domain.user.UserRepository.*(..))")
//    public void logMethodEnd(JoinPoint joinPoint) {
//        String uuid = getCurrentUuid();
//        String methodName = joinPoint.getSignature().toShortString();
//        log.info("UUID - [{}] | Method End: {}", uuid, methodName);
//    }

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
            log.info("UUID - [{}] | Executing: {}", uuid, methodName);
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
