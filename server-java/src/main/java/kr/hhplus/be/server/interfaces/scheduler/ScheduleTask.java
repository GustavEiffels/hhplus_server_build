package kr.hhplus.be.server.interfaces.scheduler;

import kr.hhplus.be.server.application.queue_token.QueueTokenFacade;
import kr.hhplus.be.server.application.queue_token.QueueTokenFacadeDto;
import kr.hhplus.be.server.application.reservation.ReservationFacade;
import kr.hhplus.be.server.domain.outbox.OutBoxService;
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
    private final OutBoxService     outBoxService;

    @Value("${queue.max-active-token}")
    private Long maxActiveToken;

    @Scheduled(fixedRate =  10_000)
    public void executeExpireTokenRemover(){
        String uuid = "SCHEDULER-" + UUID.randomUUID();
        SchedulerContext.setUuid(uuid);
        long startTime = System.currentTimeMillis();

        try {
            queueTokenFacade.expire();
            log.info("UUID - [{}] | expire token schedule ", uuid);
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
            if(maxActiveToken==null){
                maxActiveToken = 30L;
            }
            queueTokenFacade.activate(new QueueTokenFacadeDto.ActivateParam(maxActiveToken));
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

    @Scheduled(fixedRate =  60_000)
    public void executeOutBoxRemover(){
        String uuid = "SCHEDULER-" + UUID.randomUUID();
        SchedulerContext.setUuid(uuid);
        long startTime = System.currentTimeMillis();
        log.info("UUID - [{}] | reservation expire schedule ", uuid);
        try {
            outBoxService.deleteExpired();
        }finally {
            log.info("UUID - [{}] | schedule processed in {} ms", uuid, (System.currentTimeMillis() - startTime));
            SchedulerContext.clear();
        }
    }
}
