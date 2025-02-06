package kr.hhplus.be.server.interfaces.scheduler;

import kr.hhplus.be.server.application.queue_token.QueueTokenFacade;
import kr.hhplus.be.server.application.queue_token.QueueTokenFacadeImpl;
import kr.hhplus.be.server.application.queue_token.QueueTokenFacadeDto;
import kr.hhplus.be.server.application.reservation.ReservationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleTask {
    private final QueueTokenFacade queueTokenFacade;
    private final ReservationFacade reservationFacade;


    @Scheduled(fixedRate =  10_000)
    public void executeExpireTokenRemover(){
        String uuid = "SCHEDULER-" + UUID.randomUUID();
        SchedulerContext.setUuid(uuid);
        long startTime = System.currentTimeMillis();

        try {
            queueTokenFacade.activate();
            log.info("UUID - [{}] | active token schedule ", uuid);
        } finally {
            log.info("UUID - [{}] | schedule processed in {} ms", uuid, (System.currentTimeMillis() - startTime));
            SchedulerContext.clear();
        }
    }

    @Scheduled(fixedRate =  10_000)
    public void executeTokenActiveMaker(){
        String uuid = "SCHEDULER-" + UUID.randomUUID();
        SchedulerContext.setUuid(uuid);
        long startTime = System.currentTimeMillis();

        try {
            queueTokenFacade.activate();
            log.info("UUID - [{}] | active token schedule ", uuid);
        } finally {
            log.info("UUID - [{}] | schedule processed in {} ms", uuid, (System.currentTimeMillis() - startTime));
            SchedulerContext.clear();
        }
    }

    @Scheduled(fixedRate =  60_000)
    public void executeReservationExpireMaker(){
        String uuid = "SCHEDULER-" + UUID.randomUUID();
        SchedulerContext.setUuid(uuid);
        long startTime = System.currentTimeMillis();
        log.info("UUID - [{}] | reservation expire schedule ", uuid);
        try {
            reservationFacade.expire();
        }finally {
            log.info("UUID - [{}] | schedule processed in {} ms", uuid, (System.currentTimeMillis() - startTime));
            SchedulerContext.clear();
        }
    }
}
