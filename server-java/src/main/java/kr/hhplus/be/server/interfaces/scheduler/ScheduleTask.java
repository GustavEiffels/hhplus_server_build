package kr.hhplus.be.server.interfaces.scheduler;

import kr.hhplus.be.server.application.queue_token.QueueTokenFacade;
import kr.hhplus.be.server.application.reservation.ReservationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleTask {
    private final QueueTokenFacade queueTokenFacade;
    private final ReservationFacade reservationFacade;

    @Value("${queue.max-active-token:20}")
    private long maxActiveToken;

    @Scheduled(fixedRate =  1_000)
    public void executeTokenActiveMaker(){
        queueTokenFacade.activate( new ScheduleDto.ActiveTokenRequest(maxActiveToken) );
    }

    @Scheduled(fixedRate =  1_000)
    public void executeReservationExpireMaker(){
        reservationFacade.expire();
    }
}
