package kr.hhplus.be.server.interfaces.scheduler;

import kr.hhplus.be.server.application.queue_token.QueueTokenFacade;
import kr.hhplus.be.server.application.queue_token.QueueTokenFacadeDto;
import kr.hhplus.be.server.application.reservation.ReservationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleTask {
    private final QueueTokenFacade queueTokenFacade;
    private final ReservationFacade reservationFacade;

    @Value("${queue.max-active-token:20}")
    private long maxActiveToken;

    @Scheduled(fixedRate =  60_000)
    public void executeTokenActiveMaker(){
        String uuid    = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();
        log.info("<<--------------------------------------------------------------------------");
        log.info("UUID - [{}] | ACTIVE_TOKEN_SCHEDULER | START TIME : {}",uuid, LocalDateTime.now());
        queueTokenFacade.activate( new QueueTokenFacadeDto.ActivateParam(maxActiveToken) );
        log.info("UUID - [{}] | schedule processed in {} ms : {}",uuid, (System.currentTimeMillis()-startTime),LocalDateTime.now());
        log.info("UUID - [{}] | END TIME : {}",uuid, LocalDateTime.now());
        log.info("-------------------------------------------------------------------------->>");
    }

    @Scheduled(fixedRate =  60_000)
    public void executeReservationExpireMaker(){
        String uuid    = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();
        log.info("<<--------------------------------------------------------------------------");
        log.info("UUID - [{}] | RESERVATION_EXPIRE_SCHEDULER | START TIME : {}",uuid, LocalDateTime.now());
        reservationFacade.expire();
        log.info("UUID - [{}] | schedule processed in {} ms : {}",uuid, (System.currentTimeMillis()-startTime),LocalDateTime.now());
        log.info("UUID - [{}] | END TIME : {}",uuid, LocalDateTime.now());
        log.info("-------------------------------------------------------------------------->>");
    }
}
