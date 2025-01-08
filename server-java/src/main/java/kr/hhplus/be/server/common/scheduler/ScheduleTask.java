package kr.hhplus.be.server.common.scheduler;

import kr.hhplus.be.server.application.queue_token.QueueTokenFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleTask {
    private final QueueTokenFacade queueTokenFacade;

    @Value("${queue.max-active-token:20}")
    private long maxActiveToken;

    @Scheduled(fixedRate =  5_000)
    public void executeTokenActiveMaker(){
//        System.out.println(maxActiveToken);
        queueTokenFacade.activate(maxActiveToken);
    }
}
